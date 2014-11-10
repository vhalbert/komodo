/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.komodo.spi.annotation.Removed;
import org.komodo.spi.query.metadata.QueryMetadataInterface;
import org.komodo.spi.query.metadata.QueryNode;
import org.komodo.spi.query.metadata.StoredProcedureInfo;
import org.komodo.spi.query.sql.lang.ICommand;
import org.komodo.spi.query.sql.lang.ISPParameter;
import org.komodo.spi.runtime.version.TeiidVersion;
import org.komodo.spi.runtime.version.TeiidVersion.Version;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.language.SQLConstants;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.metadata.TempMetadataID.Type;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.TCQueryParser;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.validator.UpdateValidator.UpdateInfo;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


public abstract class ProcedureContainerResolver extends CommandResolver {

    /**
     * @param queryResolver
     */
    public ProcedureContainerResolver(TCQueryResolver queryResolver) {
        super(queryResolver);
    }

    public abstract void resolveProceduralCommand(Command command,
                                                  TempMetadataAdapter metadata) throws Exception;

    /**
     * Expand a command by finding and attaching all subcommands to the command.  If
     * some initial resolution must be done for this to be accomplished, that is ok, 
     * but it should be kept to a minimum.
     * @param procCcommand The command to expand
     * @param metadata Metadata access
     * 
     * @throws Exception
     */
    public Command expandCommand(ProcedureContainer procCommand, QueryMetadataInterface metadata)
    throws Exception {
        
        // Resolve group so we can tell whether it is an update procedure
        GroupSymbol group = procCommand.getGroup();

        Command subCommand = null;
        
        String plan = getPlan(metadata, procCommand);
        
        if (plan == null) {
            return null;
        }
        
        TCQueryParser parser = getQueryResolver().getQueryParser();
        try {
            subCommand = parser.parseProcedure(plan, !(procCommand instanceof StoredProcedure));
        } catch(Exception e) {
             throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID30060, group, procCommand.getClass().getSimpleName()));
        }
        
        return subCommand;
    }

    /** 
     * For a given resolver, this returns the unparsed command.
     * 
     * @param metadata
     * @param group
     * @return
     * @throws Exception
     * @throws Exception
     */
    protected abstract String getPlan(QueryMetadataInterface metadata,
                           GroupSymbol group) throws Exception;
        
	private static void addChanging(TeiidParser parser, TempMetadataStore discoveredMetadata,
			GroupContext externalGroups, List<ElementSymbol> elements) {
		List<ElementSymbol> changingElements = new ArrayList<ElementSymbol>(elements.size());
        for(int i=0; i<elements.size(); i++) {
            ElementSymbol virtualElmnt = elements.get(i);
            ElementSymbol changeElement = virtualElmnt.clone();
            changeElement.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
            changingElements.add(changeElement);
        }

        addScalarGroup(parser, ProcedureReservedWords.CHANGING, discoveredMetadata, externalGroups, changingElements, false);
	}
        
    /** 
     * @see org.teiid.query.resolver.CommandResolver#resolveCommand(org.teiid.query.sql.lang.Command, org.teiid.query.metadata.TempMetadataAdapter, boolean)
     */
    public void resolveCommand(Command command, TempMetadataAdapter metadata, boolean resolveNullLiterals) 
        throws Exception {
        
        ProcedureContainer procCommand = (ProcedureContainer)command;
        
        resolveGroup(metadata, procCommand);
        
        resolveProceduralCommand(procCommand, metadata);
        
        //getPlan(metadata, procCommand);
    }

	private String getPlan(QueryMetadataInterface metadata, ProcedureContainer procCommand)
			throws Exception {
		if(!procCommand.getGroup().isTempTable() && metadata.isVirtualGroup(procCommand.getGroup().getMetadataID())) {
            String plan = getPlan(metadata, procCommand.getGroup());
            if (plan == null && !metadata.isProcedure(procCommand.getGroup().getMetadataID())) {
            	int type = procCommand.getType();
            	//force validation
            	getUpdateInfo(procCommand.getGroup(), metadata, type, true);
            }
            return plan;
        }
		return null;
	}
	
	public UpdateInfo getUpdateInfo(GroupSymbol group, QueryMetadataInterface metadata, int type, boolean validate) throws Exception {
		UpdateInfo info = getUpdateInfo(group, metadata);
		
		if (info == null) {
			return null;
		}
    	if (validate || group.getTeiidVersion().isLessThan(Version.TEIID_8_0.get())) {
    		String error = validateUpdateInfo(group, type, info);
    		if (error != null) {
    			throw new QueryResolverException(error);
    		}
    	}
    	return info;
	}

	public static String validateUpdateInfo(GroupSymbol group, int type, UpdateInfo info) {
		String error = info.getDeleteValidationError();
		String name = "Delete"; //$NON-NLS-1$
		if (type == ICommand.TYPE_UPDATE) {
			error = info.getUpdateValidationError();
			name = "Update"; //$NON-NLS-1$
		} else if (type == ICommand.TYPE_INSERT) {
			error = info.getInsertValidationError();
			name = "Insert"; //$NON-NLS-1$
		}
		if (error != null) {
			return Messages.gs(Messages.TEIID.TEIID30061, group, name, error);
		}
		return null;
	}

	public UpdateInfo getUpdateInfo(GroupSymbol group,
			QueryMetadataInterface metadata) throws Exception {
		if (!getQueryResolver().isView(group, metadata)) {
			return null;
		}
		try {
			return getQueryResolver().resolveView(group, metadata.getVirtualPlan(group.getMetadataID()), SQLConstants.Reserved.SELECT, metadata).getUpdateInfo();
		} catch (Exception e) {
			 throw new QueryResolverException(e);
		}
	}
	
    /** 
     * @param metadata
     * @param procCommand
     * @throws Exception
     * @throws Exception
     */
    protected void resolveGroup(TempMetadataAdapter metadata,
                              ProcedureContainer procCommand) throws Exception {
        // Resolve group so we can tell whether it is an update procedure
        GroupSymbol group = procCommand.getGroup();
        ResolverUtil.resolveGroup(group, metadata);
        if (!group.isTempTable()) {
        	procCommand.setUpdateInfo(getUpdateInfo(group, metadata, procCommand.getType(), false));
        }
    }

    public static GroupSymbol addScalarGroup(TeiidParser teiidParser, String name, TempMetadataStore metadata, GroupContext externalGroups, List<? extends Expression> symbols) {
    	return addScalarGroup(teiidParser, name, metadata, externalGroups, symbols, true);
    }
    
	public static GroupSymbol addScalarGroup(TeiidParser teiidParser, String name, TempMetadataStore metadata, GroupContext externalGroups, List<? extends Expression> symbols, boolean updatable) {
		boolean[] updateArray = new boolean[symbols.size()];
		if (updatable) {
			Arrays.fill(updateArray, true);
		}
		return addScalarGroup(teiidParser, name, metadata, externalGroups, symbols, updateArray);
	}
	
	public static GroupSymbol addScalarGroup(TeiidParser teiidParser, String name, TempMetadataStore metadata, GroupContext externalGroups, List<? extends Expression> symbols, boolean[] updatable) {
	    GroupSymbol variables = teiidParser.createASTNode(ASTNodes.GROUP_SYMBOL);
		variables.setName(name);
	    externalGroups.addGroup(variables);
	    TempMetadataID tid = metadata.addTempGroup(name, symbols);
	    tid.setMetadataType(Type.SCALAR);
	    int i = 0;
	    for (TempMetadataID cid : tid.getElements()) {
			cid.setMetadataType(Type.SCALAR);
			cid.setUpdatable(updatable[i++]);
		}
	    variables.setMetadataID(tid);
	    return variables;
	}
	
	/**
	 * Set the appropriate "external" metadata for the given command
	 * @param queryResolver
	 * @param currentCommand 
	 * @param container 
	 * @param type 
	 * @param metadata 
	 * @param inferProcedureResultSetColumns 
	 * @throws Exception 
	 */
	public static void findChildCommandMetadata(TCQueryResolver queryResolver, Command currentCommand,
			GroupSymbol container, int type, QueryMetadataInterface metadata, boolean inferProcedureResultSetColumns)
			throws Exception {
	    TeiidParser parser = queryResolver.getQueryParser().getTeiidParser();
		//find the childMetadata using a clean metadata store
	    TempMetadataStore childMetadata = new TempMetadataStore();
	    TempMetadataAdapter tma = new TempMetadataAdapter(metadata, childMetadata);
	    GroupContext externalGroups = new GroupContext();

		if (currentCommand instanceof TriggerAction) {
			TriggerAction ta = (TriggerAction)currentCommand;
			ta.setView(container);
		    //TODO: it seems easier to just inline the handling here rather than have each of the resolvers check for trigger actions
		    List<ElementSymbol> viewElements = ResolverUtil.resolveElementsInGroup(ta.getView(), metadata);
		    if (type == ICommand.TYPE_UPDATE || type == ICommand.TYPE_INSERT) {
		    	addChanging(parser, tma.getMetadataStore(), externalGroups, viewElements);
		    	addScalarGroup(parser, SQLConstants.Reserved.NEW, tma.getMetadataStore(), externalGroups, viewElements, false);
		    }
		    if (type == ICommand.TYPE_UPDATE || type == ICommand.TYPE_DELETE) {
		    	addScalarGroup(parser, SQLConstants.Reserved.OLD, tma.getMetadataStore(), externalGroups, viewElements, false);
		    }
		} else if (currentCommand instanceof CreateUpdateProcedureCommand) {
            CreateUpdateProcedureCommand cupc = (CreateUpdateProcedureCommand)currentCommand;
            cupc.setVirtualGroup(container);

            if (type == ICommand.TYPE_STORED_PROCEDURE) {
                StoredProcedureInfo<ISPParameter, QueryNode> info = metadata.getStoredProcedureInfoForProcedure(container.getCanonicalName());
                // Create temporary metadata that defines a group based on either the stored proc
                // name or the stored query name - this will be used later during planning
                String procName = info.getProcedureCallableName();
                
                // Look through parameters to find input elements - these become child metadata
                List<ElementSymbol> tempElements = new ArrayList<ElementSymbol>(info.getParameters().size());
                boolean[] updatable = new boolean[info.getParameters().size()];
                int i = 0;
                for (ISPParameter param : info.getParameters()) {
                    if(param.getParameterType() != ISPParameter.ParameterInfo.RESULT_SET.index()) {
                        ElementSymbol symbol = (ElementSymbol) param.getParameterSymbol();
                        tempElements.add(symbol);
                        updatable[i++] = param.getParameterType() != ISPParameter.ParameterInfo.IN.index();
                    }
                }

                addScalarGroup(parser, procName, childMetadata, externalGroups, tempElements, updatable);
            } else if (type != ICommand.TYPE_DELETE) {
                createInputChangingMetadata(parser, childMetadata, tma, container, externalGroups);
            }
		} else if (currentCommand instanceof CreateProcedureCommand) {
			CreateProcedureCommand cupc = (CreateProcedureCommand)currentCommand;
			cupc.setVirtualGroup(container);

			if (type == ICommand.TYPE_STORED_PROCEDURE) {
				StoredProcedureInfo<ISPParameter, QueryNode> info = metadata.getStoredProcedureInfoForProcedure(container.getName());
		        // Create temporary metadata that defines a group based on either the stored proc
		        // name or the stored query name - this will be used later during planning
		        String procName = info.getProcedureCallableName();

		        // Look through parameters to find input elements - these become child metadata
		        List<ElementSymbol> tempElements = new ArrayList<ElementSymbol>(info.getParameters().size());
		        boolean[] updatable = new boolean[info.getParameters().size()];
		        int i = 0;
		        List<ElementSymbol> rsColumns = Collections.emptyList();
		        for (ISPParameter param : info.getParameters()) {
		            if(param.getParameterType() != ISPParameter.ParameterInfo.RESULT_SET.index()) {
		                ElementSymbol symbol = (ElementSymbol) param.getParameterSymbol();
		                tempElements.add(symbol);
		                updatable[i++] = param.getParameterType() != ISPParameter.ParameterInfo.IN.index();  
		                if (param.getParameterType() == ISPParameter.ParameterInfo.RETURN_VALUE.index()) {
		                	cupc.setReturnVariable(symbol);
		                }
		            } else {
		            	rsColumns = param.getResultSetColumns();
		            }
		        }
		        if (inferProcedureResultSetColumns) {
		        	rsColumns = null;
		        }
		        GroupSymbol gs = addScalarGroup(parser, procName, childMetadata, externalGroups, tempElements, updatable);
		        if (cupc.getReturnVariable() != null) {
		        	ResolverVisitor visitor = new ResolverVisitor(parser.getVersion());
		        	visitor.resolveLanguageObject(cupc.getReturnVariable(), Arrays.asList(gs), metadata);
		        }
		        cupc.setResultSetColumns(rsColumns);
		        //the relational planner will override this with the appropriate value
		        cupc.setProjectedSymbols(rsColumns);
			} else {
    			cupc.setUpdateType(type);
			}
		}

	    queryResolver.setChildMetadata(currentCommand, childMetadata, externalGroups);
	}

	@Removed(Version.TEIID_8_0)
    private static void createInputChangingMetadata(TeiidParser teiidParser, TempMetadataStore discoveredMetadata, QueryMetadataInterface metadata, GroupSymbol group, GroupContext externalGroups)
        throws Exception {
        //Look up elements for the virtual group
        List<ElementSymbol> elements = ResolverUtil.resolveElementsInGroup(group, metadata);

        // Create the INPUT variables
        List<ElementSymbol> inputElments = new ArrayList<ElementSymbol>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            ElementSymbol virtualElmnt = elements.get(i);
            ElementSymbol inputElement = virtualElmnt.clone();
            inputElments.add(inputElement);
        }

        addScalarGroup(teiidParser, ProcedureReservedWords.INPUT, discoveredMetadata, externalGroups, inputElments, false);
        addScalarGroup(teiidParser, ProcedureReservedWords.INPUTS, discoveredMetadata, externalGroups, inputElments, false);

        // Switch type to be boolean for all CHANGING variables
        addChanging(teiidParser, discoveredMetadata, externalGroups, elements);
    }
}

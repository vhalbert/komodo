/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.komodo.relational.vdb;

import org.komodo.relational.Messages;
import org.komodo.relational.Messages.Relational;
import org.komodo.relational.RelationalObject;
import org.komodo.relational.RelationalProperties;
import org.komodo.relational.TypeResolver;
import org.komodo.relational.internal.AdapterFactory;
import org.komodo.relational.vdb.internal.DataRoleImpl;
import org.komodo.repository.ObjectImpl;
import org.komodo.spi.KException;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.KomodoType;
import org.komodo.spi.repository.Repository;
import org.komodo.spi.repository.Repository.UnitOfWork;
import org.komodo.spi.repository.Repository.UnitOfWork.State;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;

/**
 * Represents a VDB data role.
 */
public interface DataRole extends RelationalObject {

    /**
     * The type identifier.
     */
    int TYPE_ID = DataRole.class.hashCode();

    /**
     * Identifier of this object
     */
    KomodoType IDENTIFIER = KomodoType.VDB_DATA_ROLE;

    /**
     * The default value for the <code>create temporary tables</code> property. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_CREATE_TEMP_TABLES = false;

    /**
     * The default value for the <code>any authenticated</code> property. Value is {@value} .
     */
    boolean DEFAULT_ANY_AUTHENTICATED = false;

    /**
     * The default value for the <code>grant all</code> property. Value is {@value} .
     */
    boolean DEFAULT_GRANT_ALL = false;

    /**
     * An empty array of data roles.
     */
    DataRole[] NO_DATA_ROLES = new DataRole[0];

    /**
     * The resolver of a {@link DataRole}.
     */
    public static final TypeResolver< DataRole > RESOLVER = new TypeResolver< DataRole >() {
    
        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.TypeResolver#create(org.komodo.spi.repository.Repository.UnitOfWork,
         *      org.komodo.spi.repository.Repository, org.komodo.spi.repository.KomodoObject, java.lang.String,
         *      org.komodo.relational.RelationalProperties)
         */
        @Override
        public DataRole create( final UnitOfWork transaction,
                                final Repository repository,
                                final KomodoObject parent,
                                final String id,
                                final RelationalProperties properties ) throws KException {
            final AdapterFactory adapter = new AdapterFactory( );
            final Vdb parentVdb = adapter.adapt( transaction, parent, Vdb.class );
    
            if ( parentVdb == null ) {
                throw new KException( Messages.getString( Relational.INVALID_PARENT_TYPE,
                                                          parent.getAbsolutePath(),
                                                          DataRole.class.getSimpleName() ) );
            }
    
            return parentVdb.addDataRole( transaction, id );
        }
    
        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.TypeResolver#identifier()
         */
        @Override
        public KomodoType identifier() {
            return IDENTIFIER;
        }
    
        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.TypeResolver#owningClass()
         */
        @Override
        public Class< DataRoleImpl > owningClass() {
            return DataRoleImpl.class;
        }
    
        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.TypeResolver#resolvable(org.komodo.spi.repository.Repository.UnitOfWork,
         *      org.komodo.spi.repository.KomodoObject)
         */
        @Override
        public boolean resolvable( final UnitOfWork transaction,
                                   final KomodoObject kobject ) throws KException {
            return ObjectImpl.validateType( transaction, kobject.getRepository(), kobject, VdbLexicon.DataRole.DATA_ROLE );
        }
    
        /**
         * {@inheritDoc}
         *
         * @see org.komodo.relational.TypeResolver#resolve(org.komodo.spi.repository.Repository.UnitOfWork,
         *      org.komodo.spi.repository.KomodoObject)
         */
        @Override
        public DataRole resolve( final UnitOfWork transaction,
                                 final KomodoObject kobject ) throws KException {
            return new DataRoleImpl( transaction, kobject.getRepository(), kobject.getAbsolutePath() );
        }
    
    };

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param roleName
     *        the name of the mapped role being added (cannot be empty)
     * @return all the mapped roles (never empty)
     * @throws KException
     *         if an error occurs
     */
    String[] addMappedRole( final UnitOfWork transaction,
                            final String roleName ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param permissionName
     *        the name of the permission being added (cannot be empty)
     * @return the new permission (never <code>null</code>)
     * @throws KException
     *         if an error occurs
     */
    Permission addPermission( final UnitOfWork transaction,
                              final String permissionName ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the value of the <code>description</code> property (can be empty)
     * @throws KException
     *         if an error occurs
     */
    String getDescription( final UnitOfWork transaction ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the mapped role names (never <code>null</code> but can be empty)
     * @throws KException
     *         if an error occurs
     */
    String[] getMappedRoles( final UnitOfWork transaction ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return the permissions (never <code>null</code> but can be empty)
     * @throws KException
     *         if an error occurs
     */
    Permission[] getPermissions( final UnitOfWork transaction ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return <code>true</code> if allows creating temporary tables
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_ALLOW_CREATE_TEMP_TABLES
     */
    boolean isAllowCreateTempTables( final UnitOfWork transaction ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return <code>true</code> if any authenticated
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_ANY_AUTHENTICATED
     */
    boolean isAnyAuthenticated( final UnitOfWork transaction ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @return <code>true</code> if grant all
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_GRANT_ALL
     */
    boolean isGrantAll( final UnitOfWork transaction ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param roleNameToRemove
     *        the name of the role being removed (cannot be empty)
     * @return all the mapped roles (never <code>null</code> but can be empty)
     * @throws KException
     *         if an error occurs
     */
    String[] removeMappedRole( final UnitOfWork transaction,
                               final String roleNameToRemove ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param permissionToRemove
     *        the name of the permission being removed (cannot be empty)
     * @throws KException
     *         if an error occurs
     */
    void removePermission( final UnitOfWork transaction,
                           final String permissionToRemove ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newAllowCreateTempTables
     *        the new value for the <code>allow creating temporary tables</code> property
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_ALLOW_CREATE_TEMP_TABLES
     */
    void setAllowCreateTempTables( final UnitOfWork transaction,
                                   final boolean newAllowCreateTempTables ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newAnyAuthenticated
     *        the new value for the <code>any authenticated</code> property
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_ANY_AUTHENTICATED
     */
    void setAnyAuthenticated( final UnitOfWork transaction,
                              final boolean newAnyAuthenticated ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newDescription
     *        the new value of the <code>description</code> property
     * @throws KException
     *         if an error occurs
     */
    void setDescription( final UnitOfWork transaction,
                         final String newDescription ) throws KException;

    /**
     * @param transaction
     *        the transaction (cannot be <code>null</code> or have a state that is not {@link State#NOT_STARTED})
     * @param newGrantAll
     *        the new value for the <code>grant all</code> property
     * @throws KException
     *         if an error occurs
     * @see #DEFAULT_GRANT_ALL
     */
    void setGrantAll( final UnitOfWork transaction,
                      final boolean newGrantAll ) throws KException;

}

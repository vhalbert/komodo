/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.rest.relational;

import java.net.URI;
import org.komodo.relational.vdb.DataRole;
import org.komodo.relational.vdb.Mask;
import org.komodo.relational.vdb.Permission;
import org.komodo.relational.vdb.Vdb;
import org.komodo.rest.KomodoService;
import org.komodo.rest.RestBasicEntity;
import org.komodo.rest.RestLink;
import org.komodo.rest.RestLink.LinkType;
import org.komodo.spi.KException;
import org.komodo.spi.repository.Repository.UnitOfWork;
import org.komodo.utils.ArgCheck;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;

/**
 * A condition that can be used by GSON to build a JSON document representation.
 */
public final class RestVdbMask extends RestBasicEntity {

    /**
     * Label used to describe name
     */
    public static final String NAME_LABEL = KomodoService.encode(VdbLexicon.DataRole.Permission.Mask.MASK);

    /**
     * Label used to describe order
     */
    public static final String ORDER_LABEL = KomodoService.encode(VdbLexicon.DataRole.Permission.Mask.ORDER);

    /**
     * An empty array of masks.
     */
    public static final RestVdbMask[] NO_MASKS = new RestVdbMask[ 0 ];

    /**
     * Constructor for use <strong>only</strong> when deserializing.
     */
    public RestVdbMask() {
        // nothing to do
    }

    /**
     * Constructor for use when serializing.
     * @param baseUri the base uri of the REST request
     * @param mask the mask
     * @param uow the transaction
     * @throws KException if error occurs
     */
    public RestVdbMask(URI baseUri, Mask mask, UnitOfWork uow) throws KException {
        super(baseUri, mask, uow);

        setName(mask.getName(uow));
        setOrder(mask.getOrder(uow));

        Permission permission = ancestor(mask, Permission.class, uow);
        ArgCheck.isNotNull(permission);
        String permName = permission.getName(uow);

        DataRole dataRole = ancestor(permission, DataRole.class, uow);
        ArgCheck.isNotNull(dataRole);
        String dataRoleName = dataRole.getName(uow);

        Vdb vdb = ancestor(dataRole, Vdb.class, uow);
        ArgCheck.isNotNull(vdb);
        String vdbName = vdb.getName(uow);

        addLink(new RestLink(LinkType.SELF, getUriBuilder()
                             .buildVdbPermissionChildUri(LinkType.SELF, vdbName, dataRoleName, permName, LinkType.MASKS, getId())));
        addLink(new RestLink(LinkType.PARENT, getUriBuilder()
                             .buildVdbPermissionChildUri(LinkType.PARENT, vdbName, dataRoleName, permName, LinkType.MASKS, getId())));
    }

    /**
     * @return the name (can be empty)
     */
    public String getName() {
        Object value = tuples.get(NAME_LABEL);
        return value != null ? value.toString() : null;
    }

    /**
     * @param newName
     *        the new mask name (can be empty)
     */
    public void setName( final String newName ) {
        tuples.put(NAME_LABEL, newName);
    }

    /**
     * @return the order
     */
    public String getOrder() {
        Object value = tuples.get(ORDER_LABEL);
        return value != null ? value.toString() : null;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        tuples.put(ORDER_LABEL, order);
    }
}
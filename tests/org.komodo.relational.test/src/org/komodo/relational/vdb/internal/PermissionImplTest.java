/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.komodo.relational.vdb.internal;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.komodo.relational.RelationalModelTest;
import org.komodo.relational.RelationalObject.Filter;
import org.komodo.relational.internal.RelationalObjectImpl;
import org.komodo.relational.vdb.Condition;
import org.komodo.relational.vdb.DataRole;
import org.komodo.relational.vdb.Mask;
import org.komodo.relational.vdb.Permission;
import org.komodo.relational.vdb.Vdb;
import org.komodo.spi.KException;
import org.komodo.spi.constants.StringConstants;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.KomodoType;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;

@SuppressWarnings( { "javadoc", "nls" } )
public final class PermissionImplTest extends RelationalModelTest {

    private Permission permission;

    @Before
    public void init() throws Exception {
        final Vdb vdb = createVdb();
        final DataRole dataRole = vdb.addDataRole( getTransaction(), "dataRole" );
        this.permission = dataRole.addPermission( getTransaction(), "permission" );
        commit();
    }

    @Test
    public void shouldAddCondition() throws Exception {
        final String name = "condition";
        final Condition condition = this.permission.addCondition( getTransaction(), name );
        assertThat( condition, is( notNullValue() ) );
        assertThat( this.permission.getConditions( getTransaction() ).length, is( 1 ) );

        final Condition added = this.permission.getConditions( getTransaction() )[0];
        assertThat( added, is( condition ) );
        assertThat( added.getName( getTransaction() ), is( name ) );
        assertThat( added.getPrimaryType( getTransaction() ).getName(), is( VdbLexicon.DataRole.Permission.Condition.CONDITION ) );
        assertThat( this.permission.getChildren( getTransaction() )[0], is( instanceOf( Condition.class ) ) );
    }

    @Test
    public void shouldAddMask() throws Exception {
        final String name = "mask";
        final Mask mask = this.permission.addMask( getTransaction(), name );
        assertThat( mask, is( notNullValue() ) );
        assertThat( this.permission.getMasks( getTransaction() ).length, is( 1 ) );

        final Mask added = this.permission.getMasks( getTransaction() )[0];
        assertThat( added, is( mask ) );
        assertThat( added.getName( getTransaction() ), is( name ) );
        assertThat( added.getPrimaryType( getTransaction() ).getName(), is( VdbLexicon.DataRole.Permission.Mask.MASK ) );
        assertThat( this.permission.getChildren( getTransaction() )[0], is( instanceOf( Mask.class ) ) );
    }

    @Test
    public void shouldHaveStrongTypedChildren() throws Exception {
        this.permission.addMask( getTransaction(), "mask" );
        this.permission.addCondition( getTransaction(), "condition" );
        assertThat( this.permission.getChildren( getTransaction() ).length, is( 2 ) );
        assertThat( this.permission.getChildren( getTransaction() )[0], is( instanceOf( Condition.class ) ) );
        assertThat( this.permission.getChildren( getTransaction() )[1], is( instanceOf( Mask.class ) ) );
    }

    @Test
    public void shouldFailConstructionIfNotPermission() {
        if ( RelationalObjectImpl.VALIDATE_INITIAL_STATE ) {
            try {
                new PermissionImpl( getTransaction(), _repo, this.permission.getParent( getTransaction() ).getAbsolutePath() );
                fail();
            } catch ( final KException e ) {
                // expected
            }
        }
    }

    @Test
    public void shouldHaveCorrectChildTypes() {
        assertThat( Arrays.asList( this.permission.getChildTypes() ), hasItems( Condition.IDENTIFIER, Mask.IDENTIFIER ) );
        assertThat( this.permission.getChildTypes().length, is( 2 ) );
    }

    @Test
    public void shouldHaveCorrectPrimaryType() throws Exception {
        assertThat( this.permission.getPrimaryType( getTransaction() ).getName(), is( VdbLexicon.DataRole.Permission.PERMISSION ) );
    }

    @Test
    public void shouldHaveCorrectTypeIdentifier() throws Exception {
        assertThat(this.permission.getTypeIdentifier( getTransaction() ), is(KomodoType.VDB_PERMISSION));
    }

    @Test
    public void shouldHaveDefaultAllowAlterValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowAlter( getTransaction() ), is( Permission.DEFAULT_ALLOW_ALTER ) );
    }

    @Test
    public void shouldHaveDefaultAllowCreateValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowCreate( getTransaction() ), is( Permission.DEFAULT_ALLOW_CREATE ) );
    }

    @Test
    public void shouldHaveDefaultAllowDeleteValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowDelete( getTransaction() ), is( Permission.DEFAULT_ALLOW_DELETE ) );
    }

    @Test
    public void shouldHaveDefaultAllowExecuteValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowExecute( getTransaction() ), is( Permission.DEFAULT_ALLOW_EXECUTE ) );
    }

    @Test
    public void shouldHaveDefaultAllowLanguageValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowLanguage( getTransaction() ), is( Permission.DEFAULT_ALLOW_LANGUAGE ) );
    }

    @Test
    public void shouldHaveDefaultAllowReadValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowRead( getTransaction() ), is( Permission.DEFAULT_ALLOW_READ ) );
    }

    @Test
    public void shouldHaveDefaultAllowUpdateValueAfterConstruction() throws Exception {
        assertThat( this.permission.isAllowUpdate( getTransaction() ), is( Permission.DEFAULT_ALLOW_UPDATE ) );
    }

    @Test
    public void shouldHaveMoreRawProperties() throws Exception {
        final String[] filteredProps = this.permission.getPropertyNames( getTransaction() );
        final String[] rawProps = this.permission.getRawPropertyNames( getTransaction() );
        assertThat( ( rawProps.length > filteredProps.length ), is( true ) );
    }

    @Test
    public void shouldHaveParentDataRole() throws Exception {
        assertThat( this.permission.getParent( getTransaction() ), is( instanceOf( DataRole.class ) ) );
    }

    @Test
    public void shouldHaveResourceName() throws Exception {
        assertThat( this.permission.getResourceName( getTransaction() ), is( notNullValue() ) );
        assertThat( this.permission.getResourceName( getTransaction() ), is( this.permission.getName( getTransaction() ) ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeAbleToAddEmptyCondition() throws Exception {
        this.permission.addCondition( getTransaction(), StringConstants.EMPTY_STRING );
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeAbleToAddEmptyMask() throws Exception {
        this.permission.addMask( getTransaction(), StringConstants.EMPTY_STRING );
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeAbleToAddNullCondition() throws Exception {
        this.permission.addCondition( getTransaction(), null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotBeAbleToAddNullMask() throws Exception {
        this.permission.addMask( getTransaction(), null );
    }

    @Test
    public void shouldNotContainFilteredProperties() throws Exception {
        final String[] filteredProps = this.permission.getPropertyNames( getTransaction() );
        final Filter[] filters = this.permission.getFilters();

        for ( final String name : filteredProps ) {
            for ( final Filter filter : filters ) {
                assertThat( filter.rejectProperty( name ), is( false ) );
            }
        }
    }

    @Test
    public void shouldNotHaveConditionsAfterConstruction() throws Exception {
        assertThat( this.permission.getConditions( getTransaction() ), is( notNullValue() ) );
        assertThat( this.permission.getConditions( getTransaction() ).length, is( 0 ) );
    }

    @Test
    public void shouldNotHaveMakssAfterConstruction() throws Exception {
        assertThat( this.permission.getMasks( getTransaction() ), is( notNullValue() ) );
        assertThat( this.permission.getMasks( getTransaction() ).length, is( 0 ) );
    }

    @Test
    public void shouldRemoveCondition() throws Exception {
        final String name = "condition";
        this.permission.addCondition( getTransaction(), name );
        assertThat( this.permission.getConditions( getTransaction() ).length, is( 1 ) );

        this.permission.removeCondition( getTransaction(), name );
        assertThat( this.permission.getConditions( getTransaction() ).length, is( 0 ) );
    }

    @Test
    public void shouldRemoveMask() throws Exception {
        final String name = "mask";
        this.permission.addMask( getTransaction(), name );
        assertThat( this.permission.getMasks( getTransaction() ).length, is( 1 ) );

        this.permission.removeMask( getTransaction(), name );
        assertThat( this.permission.getMasks( getTransaction() ).length, is( 0 ) );
    }

    @Test
    public void shouldSetAllowAlterValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_ALTER;
        this.permission.setAllowAlter( getTransaction(), newValue );
        assertThat( this.permission.isAllowAlter( getTransaction() ), is( newValue ) );
    }

    @Test
    public void shouldSetAllowCreateValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_CREATE;
        this.permission.setAllowCreate( getTransaction(), newValue );
        assertThat( this.permission.isAllowCreate( getTransaction() ), is( newValue ) );
    }

    @Test
    public void shouldSetAllowDeleteValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_DELETE;
        this.permission.setAllowDelete( getTransaction(), newValue );
        assertThat( this.permission.isAllowDelete( getTransaction() ), is( newValue ) );
    }

    @Test
    public void shouldSetAllowExecuteValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_EXECUTE;
        this.permission.setAllowExecute( getTransaction(), newValue );
        assertThat( this.permission.isAllowExecute( getTransaction() ), is( newValue ) );
    }

    @Test
    public void shouldSetAllowLanguageValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_LANGUAGE;
        this.permission.setAllowLanguage( getTransaction(), newValue );
        assertThat( this.permission.isAllowLanguage( getTransaction() ), is( newValue ) );
    }

    @Test
    public void shouldSetAllowReadValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_READ;
        this.permission.setAllowRead( getTransaction(), newValue );
        assertThat( this.permission.isAllowRead( getTransaction() ), is( newValue ) );
    }

    @Test
    public void shouldSetAllowUpdateValue() throws Exception {
        final boolean newValue = !Permission.DEFAULT_ALLOW_UPDATE;
        this.permission.setAllowUpdate( getTransaction(), newValue );
        assertThat( this.permission.isAllowUpdate( getTransaction() ), is( newValue ) );
    }

    /*
     * ********************************************************************
     * *****                  Resolver Tests                          *****
     * ********************************************************************
     */

    @Test
    public void shouldCreateUsingResolver() throws Exception {
        final String name = "blah";
        final KomodoObject kobject = Permission.RESOLVER.create( getTransaction(),
                                                                     _repo,
                                                                     this.permission.getParent( getTransaction() ),
                                                                     name,
                                                                     null );
        assertThat( kobject, is( notNullValue() ) );
        assertThat( kobject, is( instanceOf( Permission.class ) ) );
        assertThat( kobject.getName( getTransaction() ), is( name ) );
    }

    @Test( expected = KException.class )
    public void shouldFailCreateUsingResolverWithInvalidParent() throws Exception {
        final KomodoObject bogusParent = _repo.add( getTransaction(), null, "bogus", null );
        Permission.RESOLVER.create( getTransaction(), _repo, bogusParent, "blah", null );
    }

}

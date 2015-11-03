/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.komodo.relational.model.internal;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.komodo.relational.RelationalModelTest;
import org.komodo.relational.RelationalObject.Filter;
import org.komodo.relational.internal.RelationalObjectImpl;
import org.komodo.relational.model.DataTypeResultSet;
import org.komodo.relational.model.Model;
import org.komodo.relational.model.PushdownFunction;
import org.komodo.relational.model.SchemaElement.SchemaElementType;
import org.komodo.relational.model.TabularResultSet;
import org.komodo.spi.KException;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.KomodoType;

@SuppressWarnings( { "javadoc", "nls" } )
public final class PushdownFunctionImplTest extends RelationalModelTest {

    private PushdownFunction function;

    @Before
    public void init() throws Exception {
        final Model model = createModel();
        this.function = model.addPushdownFunction( getTransaction(), "function" );
        commit();
    }

    @Test
    public void shouldFailConstructionIfNotPushdownFunction() {
        if ( RelationalObjectImpl.VALIDATE_INITIAL_STATE ) {
            try {
                new PushdownFunctionImpl( getTransaction(), _repo, _repo.komodoLibrary( getTransaction() ).getAbsolutePath() );
                fail();
            } catch ( final KException e ) {
                // expected
            }
        }
    }

    @Test( expected = KException.class )
    public void shouldFailRemovingResultSetIfOneDoesNotExist() throws Exception {
        this.function.removeResultSet( getTransaction() );
    }

    @Test
    public void shouldGetOnlyResultSetWhenGettingChildren() throws Exception {
        final TabularResultSet resultSet = this.function.setResultSet( getTransaction(), TabularResultSet.class );
        assertThat( this.function.getChildren( getTransaction() ).length, is( 1 ) );
        assertThat( this.function.getChildren( getTransaction() )[0], is( ( KomodoObject )resultSet ) );
    }

    @Test
    public void shouldGetChildren() throws Exception {
        this.function.addParameter( getTransaction(), "param" );
        this.function.setResultSet( getTransaction(), DataTypeResultSet.class );
        assertThat( this.function.getChildren( getTransaction() ).length, is( 2 ) );
    }

    @Test
    public void shouldHaveCorrectSchemaElementType() throws Exception {
        assertThat( this.function.getSchemaElementType( getTransaction() ), is( SchemaElementType.FOREIGN ) );
    }

    @Test
    public void shouldHaveCorrectTypeIdentifier() throws Exception {
        assertThat(this.function.getTypeIdentifier( getTransaction() ), is(KomodoType.PUSHDOWN_FUNCTION));
    }

    @Test
    public void shouldHaveMoreRawProperties() throws Exception {
        final String[] filteredProps = this.function.getPropertyNames( getTransaction() );
        final String[] rawProps = this.function.getRawPropertyNames( getTransaction() );
        assertThat( ( rawProps.length > filteredProps.length ), is( true ) );
    }

    @Test
    public void shouldNotContainFilteredProperties() throws Exception {
        final String[] filteredProps = this.function.getPropertyNames( getTransaction() );
        final Filter[] filters = this.function.getFilters();

        for ( final String name : filteredProps ) {
            for ( final Filter filter : filters ) {
                assertThat( filter.rejectProperty( name ), is( false ) );
            }
        }
    }

    @Test
    public void shouldNotCountStatementOptionsAsChildren() throws Exception {
        this.function.setAggregate( getTransaction(), true );
        this.function.setStatementOption( getTransaction(), "sledge", "hammer" );
        assertThat( this.function.getChildren( getTransaction() ).length, is( 0 ) );
    }

    @Test
    public void shouldNotHaveResultSetAfterConstruction() throws Exception {
        assertThat( this.function.getResultSet( getTransaction() ), is( nullValue() ) );
    }

    @Test
    public void shouldRemoveResultSet() throws Exception {
        this.function.setResultSet( getTransaction(), TabularResultSet.class );
        this.function.removeResultSet( getTransaction() );
        assertThat( this.function.getResultSet( getTransaction() ), is( nullValue() ) );
    }

    @Test
    public void shouldSetDataTypeResultSet() throws Exception {
        assertThat( this.function.setResultSet( getTransaction(), DataTypeResultSet.class ), is( notNullValue() ) );
        assertThat( this.function.getResultSet( getTransaction() ), is( instanceOf( DataTypeResultSet.class ) ) );
    }

    @Test
    public void shouldSetTabularResultSet() throws Exception {
        assertThat( this.function.setResultSet( getTransaction(), TabularResultSet.class ), is( notNullValue() ) );
        assertThat( this.function.getResultSet( getTransaction() ), is( instanceOf( TabularResultSet.class ) ) );
    }

    /*
     * ********************************************************************
     * *****                  Resolver Tests                          *****
     * ********************************************************************
     */

    @Test
    public void shouldCreateUsingResolver() throws Exception {
        final String name = "blah";
        final KomodoObject kobject = PushdownFunction.RESOLVER.create( getTransaction(),
                                                                           _repo,
                                                                           this.function.getParent( getTransaction() ),
                                                                           name,
                                                                           null );
        assertThat( kobject, is( notNullValue() ) );
        assertThat( kobject, is( instanceOf( PushdownFunction.class ) ) );
        assertThat( kobject.getName( getTransaction() ), is( name ) );
    }

    @Test( expected = KException.class )
    public void shouldFailCreateUsingResolverWithInvalidParent() throws Exception {
        final KomodoObject bogusParent = _repo.add( getTransaction(), null, "bogus", null );
        PushdownFunction.RESOLVER.create( getTransaction(), _repo, bogusParent, "blah", null );
    }

}

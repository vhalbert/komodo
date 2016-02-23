/* Generated By:JJTree: Do not edit this line. Alter.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.komodo.modeshape.teiid.sql.lang;

import java.util.List;
import org.komodo.modeshape.teiid.parser.TeiidSeqParser;
import org.komodo.modeshape.teiid.parser.SQLanguageVisitorImpl;
import org.komodo.modeshape.teiid.sql.symbol.BaseExpression;
import org.komodo.modeshape.teiid.sql.symbol.GroupSymbolImpl;
import org.komodo.spi.lexicon.TeiidSqlLexicon;
import org.komodo.spi.query.sql.lang.Alter;

/**
 *
 * @param <T> type of child definition
 */
public abstract class AlterImpl<T extends CommandImpl> extends CommandImpl implements Alter<BaseExpression, SQLanguageVisitorImpl> {

    /**
     * @param p teiid parser
     * @param id node type id
     */
    public AlterImpl(TeiidSeqParser p, int id) {
        super(p, id);
    }

    /**
     * @return the target
     */
    public GroupSymbolImpl getTarget() {
        return getChildforIdentifierAndRefType(TeiidSqlLexicon.Alter.TARGET_REF_NAME, GroupSymbolImpl.class);
    }

    /**
     * @param target the target to set
     */
    public void setTarget(GroupSymbolImpl target) {
        setChild(TeiidSqlLexicon.Alter.TARGET_REF_NAME, target);
    }

    /**
     * @return the definition
     */
    public T getDefinition() {
        return (T) getChildforIdentifierAndRefType(TeiidSqlLexicon.Alter.DEFINITION_REF_NAME, CommandImpl.class);
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(T definition) {
        setChild(TeiidSqlLexicon.Alter.DEFINITION_REF_NAME, definition);
    }

    @Override
    public List<BaseExpression> getProjectedSymbols() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.getDefinition() == null) ? 0 : this.getDefinition().hashCode());
        result = prime * result + ((this.getTarget() == null) ? 0 : this.getTarget().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        AlterImpl other = (AlterImpl)obj;
        if (this.getDefinition() == null) {
            if (other.getDefinition() != null) return false;
        } else if (!this.getDefinition().equals(other.getDefinition())) return false;
        if (this.getTarget() == null) {
            if (other.getTarget() != null) return false;
        } else if (!this.getTarget().equals(other.getTarget())) return false;
        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(SQLanguageVisitorImpl visitor) {
        visitor.visit(this);
    }
}
/* JavaCC - OriginalChecksum=4c2a7e700d4af2b1569d4947a1d82223 (do not edit this line) */

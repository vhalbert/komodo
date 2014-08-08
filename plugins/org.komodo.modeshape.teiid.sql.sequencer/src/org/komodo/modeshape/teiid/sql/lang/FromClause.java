/* Generated By:JJTree: Do not edit this line. FromClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.komodo.modeshape.teiid.sql.lang;

import java.util.Collection;
import org.komodo.modeshape.teiid.parser.LanguageVisitor;
import org.komodo.modeshape.teiid.parser.TeiidParser;
import org.komodo.modeshape.teiid.sql.symbol.GroupSymbol;
import org.komodo.spi.annotation.Since;
import org.komodo.spi.query.sql.lang.IFromClause;
import org.komodo.spi.runtime.version.TeiidVersion.Version;

/**
 * A FromClause is an interface for subparts held in a FROM clause.  One 
 * type of FromClause is {@link UnaryFromClause}, which is the more common 
 * use and represents a single group.  Another, less common type of FromClause
 * is the {@link JoinPredicate} which represents a join between two FromClauses
 * and may contain criteria.
 */
public abstract class FromClause extends ASTNode implements IFromClause<LanguageVisitor> {

    /**
     * 
     */
    public static final String MAKEIND = "MAKEIND"; //$NON-NLS-1$

    /**
     * 
     */
    @Since(Version.TEIID_8_0)
    public static final String PRESERVE = "PRESERVE"; //$NON-NLS-1$

    /**
     * @param p
     * @param id
     */
    public FromClause(TeiidParser p, int id) {
        super(p, id);
    }

    /**
     * @return whether has any hints set
     */
    public boolean hasHint() {
        return false;
    }

    @Override
    public boolean isOptional() {
        return false;
    }
    
    @Override
    public void setOptional(boolean optional) {
    }
    
    /**
     * @return make ind flag
     */
    public boolean isMakeInd() {
        return false;
    }
    
    /**
     * @param makeInd
     */
    public void setMakeInd(boolean makeInd) {
    }

    /**
     * @param noUnnest
     */
    public void setNoUnnest(boolean noUnnest) {
    }
    
    /**
     * @return no unnest flag
     */
    public boolean isNoUnnest() {
        return false;
    }

    @Override
    public boolean isMakeDep() {
        return false;
    }

    /**
     * Both this and isMax() are components of the former MakeDep
     * class.
     *
     * @return join flag
     */
    public boolean isJoin() {
        return false;
    }

    public void setJoin(boolean join) {
    }

    /**
     * Both this and isJoin() are components of the former MakeDep
     * class.
     *
     * @return join flag
     */
    public boolean isMax() {
        return false;
    }

    public void setMax(boolean max) {
    }

    @Override
    public void setMakeDep(boolean makeDep) {
    }

    @Override
    public boolean isMakeNotDep() {
        return false;
    }

    @Override
    public void setMakeNotDep(boolean makeNotDep) {
    }
    
    /**
     * @return preserve flag
     */
    public boolean isPreserve() {
        return false;
    }
    
    /**
     * @param preserve
     */
    public void setPreserve(boolean preserve) {
    }

    /**
     * @param groups
     */
    public abstract void collectGroups(Collection<GroupSymbol> groups);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.isJoin() ? 1231 : 1237);
        result = prime * result + (this.isMax() ? 1231 : 1237);
        result = prime * result + (this.isMakeInd() ? 1231 : 1237);
        result = prime * result + (this.isMakeNotDep() ? 1231 : 1237);
        result = prime * result + (this.isNoUnnest() ? 1231 : 1237);
        result = prime * result + (this.isOptional() ? 1231 : 1237);
        result = prime * result + (this.isPreserve() ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        FromClause other = (FromClause)obj;

        if (this.isJoin() != other.isJoin()) return false;
        if (this.isMax() != other.isMax()) return false;
        if (this.isMakeInd() != other.isMakeInd()) return false;
        if (this.isMakeNotDep() != other.isMakeNotDep()) return false;
        if (this.isNoUnnest() != other.isNoUnnest()) return false;
        if (this.isOptional() != other.isOptional()) return false;
        if (this.isPreserve() != other.isPreserve()) return false;
        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public abstract FromClause clone();

}
/* JavaCC - OriginalChecksum=908130697ce6a37a6c778dfefda987bb (do not edit this line) */
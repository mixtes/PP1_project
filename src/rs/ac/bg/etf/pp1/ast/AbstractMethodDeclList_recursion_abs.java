// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class AbstractMethodDeclList_recursion_abs extends AbstractMethodDeclList {

    private AbstractMethodDeclList AbstractMethodDeclList;
    private AbstractMethodDecl AbstractMethodDecl;

    public AbstractMethodDeclList_recursion_abs (AbstractMethodDeclList AbstractMethodDeclList, AbstractMethodDecl AbstractMethodDecl) {
        this.AbstractMethodDeclList=AbstractMethodDeclList;
        if(AbstractMethodDeclList!=null) AbstractMethodDeclList.setParent(this);
        this.AbstractMethodDecl=AbstractMethodDecl;
        if(AbstractMethodDecl!=null) AbstractMethodDecl.setParent(this);
    }

    public AbstractMethodDeclList getAbstractMethodDeclList() {
        return AbstractMethodDeclList;
    }

    public void setAbstractMethodDeclList(AbstractMethodDeclList AbstractMethodDeclList) {
        this.AbstractMethodDeclList=AbstractMethodDeclList;
    }

    public AbstractMethodDecl getAbstractMethodDecl() {
        return AbstractMethodDecl;
    }

    public void setAbstractMethodDecl(AbstractMethodDecl AbstractMethodDecl) {
        this.AbstractMethodDecl=AbstractMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbstractMethodDeclList!=null) AbstractMethodDeclList.accept(visitor);
        if(AbstractMethodDecl!=null) AbstractMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractMethodDeclList!=null) AbstractMethodDeclList.traverseTopDown(visitor);
        if(AbstractMethodDecl!=null) AbstractMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractMethodDeclList!=null) AbstractMethodDeclList.traverseBottomUp(visitor);
        if(AbstractMethodDecl!=null) AbstractMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractMethodDeclList_recursion_abs(\n");

        if(AbstractMethodDeclList!=null)
            buffer.append(AbstractMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AbstractMethodDecl!=null)
            buffer.append(AbstractMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractMethodDeclList_recursion_abs]");
        return buffer.toString();
    }
}

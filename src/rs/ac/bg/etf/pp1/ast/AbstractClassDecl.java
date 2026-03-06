// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class AbstractClassDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private AbsClassName AbsClassName;
    private ExtendsOpt ExtendsOpt;
    private AbstractMethodDeclListOpt AbstractMethodDeclListOpt;

    public AbstractClassDecl (AbsClassName AbsClassName, ExtendsOpt ExtendsOpt, AbstractMethodDeclListOpt AbstractMethodDeclListOpt) {
        this.AbsClassName=AbsClassName;
        if(AbsClassName!=null) AbsClassName.setParent(this);
        this.ExtendsOpt=ExtendsOpt;
        if(ExtendsOpt!=null) ExtendsOpt.setParent(this);
        this.AbstractMethodDeclListOpt=AbstractMethodDeclListOpt;
        if(AbstractMethodDeclListOpt!=null) AbstractMethodDeclListOpt.setParent(this);
    }

    public AbsClassName getAbsClassName() {
        return AbsClassName;
    }

    public void setAbsClassName(AbsClassName AbsClassName) {
        this.AbsClassName=AbsClassName;
    }

    public ExtendsOpt getExtendsOpt() {
        return ExtendsOpt;
    }

    public void setExtendsOpt(ExtendsOpt ExtendsOpt) {
        this.ExtendsOpt=ExtendsOpt;
    }

    public AbstractMethodDeclListOpt getAbstractMethodDeclListOpt() {
        return AbstractMethodDeclListOpt;
    }

    public void setAbstractMethodDeclListOpt(AbstractMethodDeclListOpt AbstractMethodDeclListOpt) {
        this.AbstractMethodDeclListOpt=AbstractMethodDeclListOpt;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbsClassName!=null) AbsClassName.accept(visitor);
        if(ExtendsOpt!=null) ExtendsOpt.accept(visitor);
        if(AbstractMethodDeclListOpt!=null) AbstractMethodDeclListOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbsClassName!=null) AbsClassName.traverseTopDown(visitor);
        if(ExtendsOpt!=null) ExtendsOpt.traverseTopDown(visitor);
        if(AbstractMethodDeclListOpt!=null) AbstractMethodDeclListOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbsClassName!=null) AbsClassName.traverseBottomUp(visitor);
        if(ExtendsOpt!=null) ExtendsOpt.traverseBottomUp(visitor);
        if(AbstractMethodDeclListOpt!=null) AbstractMethodDeclListOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractClassDecl(\n");

        if(AbsClassName!=null)
            buffer.append(AbsClassName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExtendsOpt!=null)
            buffer.append(ExtendsOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AbstractMethodDeclListOpt!=null)
            buffer.append(AbstractMethodDeclListOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractClassDecl]");
        return buffer.toString();
    }
}

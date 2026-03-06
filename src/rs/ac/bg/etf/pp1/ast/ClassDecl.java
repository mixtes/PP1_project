// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class ClassDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private ClassName ClassName;
    private ExtendsOpt ExtendsOpt;
    private VarDeclListMultiple VarDeclListMultiple;
    private MethodDeclListOpt MethodDeclListOpt;

    public ClassDecl (ClassName ClassName, ExtendsOpt ExtendsOpt, VarDeclListMultiple VarDeclListMultiple, MethodDeclListOpt MethodDeclListOpt) {
        this.ClassName=ClassName;
        if(ClassName!=null) ClassName.setParent(this);
        this.ExtendsOpt=ExtendsOpt;
        if(ExtendsOpt!=null) ExtendsOpt.setParent(this);
        this.VarDeclListMultiple=VarDeclListMultiple;
        if(VarDeclListMultiple!=null) VarDeclListMultiple.setParent(this);
        this.MethodDeclListOpt=MethodDeclListOpt;
        if(MethodDeclListOpt!=null) MethodDeclListOpt.setParent(this);
    }

    public ClassName getClassName() {
        return ClassName;
    }

    public void setClassName(ClassName ClassName) {
        this.ClassName=ClassName;
    }

    public ExtendsOpt getExtendsOpt() {
        return ExtendsOpt;
    }

    public void setExtendsOpt(ExtendsOpt ExtendsOpt) {
        this.ExtendsOpt=ExtendsOpt;
    }

    public VarDeclListMultiple getVarDeclListMultiple() {
        return VarDeclListMultiple;
    }

    public void setVarDeclListMultiple(VarDeclListMultiple VarDeclListMultiple) {
        this.VarDeclListMultiple=VarDeclListMultiple;
    }

    public MethodDeclListOpt getMethodDeclListOpt() {
        return MethodDeclListOpt;
    }

    public void setMethodDeclListOpt(MethodDeclListOpt MethodDeclListOpt) {
        this.MethodDeclListOpt=MethodDeclListOpt;
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
        if(ClassName!=null) ClassName.accept(visitor);
        if(ExtendsOpt!=null) ExtendsOpt.accept(visitor);
        if(VarDeclListMultiple!=null) VarDeclListMultiple.accept(visitor);
        if(MethodDeclListOpt!=null) MethodDeclListOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassName!=null) ClassName.traverseTopDown(visitor);
        if(ExtendsOpt!=null) ExtendsOpt.traverseTopDown(visitor);
        if(VarDeclListMultiple!=null) VarDeclListMultiple.traverseTopDown(visitor);
        if(MethodDeclListOpt!=null) MethodDeclListOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassName!=null) ClassName.traverseBottomUp(visitor);
        if(ExtendsOpt!=null) ExtendsOpt.traverseBottomUp(visitor);
        if(VarDeclListMultiple!=null) VarDeclListMultiple.traverseBottomUp(visitor);
        if(MethodDeclListOpt!=null) MethodDeclListOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDecl(\n");

        if(ClassName!=null)
            buffer.append(ClassName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExtendsOpt!=null)
            buffer.append(ExtendsOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclListMultiple!=null)
            buffer.append(VarDeclListMultiple.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDeclListOpt!=null)
            buffer.append(MethodDeclListOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDecl]");
        return buffer.toString();
    }
}

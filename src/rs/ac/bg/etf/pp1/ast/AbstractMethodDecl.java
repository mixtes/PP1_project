// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class AbstractMethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethodTypeAndName MethodTypeAndName;
    private FormParsList FormParsList;

    public AbstractMethodDecl (MethodTypeAndName MethodTypeAndName, FormParsList FormParsList) {
        this.MethodTypeAndName=MethodTypeAndName;
        if(MethodTypeAndName!=null) MethodTypeAndName.setParent(this);
        this.FormParsList=FormParsList;
        if(FormParsList!=null) FormParsList.setParent(this);
    }

    public MethodTypeAndName getMethodTypeAndName() {
        return MethodTypeAndName;
    }

    public void setMethodTypeAndName(MethodTypeAndName MethodTypeAndName) {
        this.MethodTypeAndName=MethodTypeAndName;
    }

    public FormParsList getFormParsList() {
        return FormParsList;
    }

    public void setFormParsList(FormParsList FormParsList) {
        this.FormParsList=FormParsList;
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
        if(MethodTypeAndName!=null) MethodTypeAndName.accept(visitor);
        if(FormParsList!=null) FormParsList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodTypeAndName!=null) MethodTypeAndName.traverseTopDown(visitor);
        if(FormParsList!=null) FormParsList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodTypeAndName!=null) MethodTypeAndName.traverseBottomUp(visitor);
        if(FormParsList!=null) FormParsList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbstractMethodDecl(\n");

        if(MethodTypeAndName!=null)
            buffer.append(MethodTypeAndName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParsList!=null)
            buffer.append(FormParsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbstractMethodDecl]");
        return buffer.toString();
    }
}

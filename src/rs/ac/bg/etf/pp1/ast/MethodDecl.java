// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class MethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethodTypeAndName MethodTypeAndName;
    private FormParsList FormParsList;
    private VarDeclListMultiple VarDeclListMultiple;
    private StatementListOpt StatementListOpt;

    public MethodDecl (MethodTypeAndName MethodTypeAndName, FormParsList FormParsList, VarDeclListMultiple VarDeclListMultiple, StatementListOpt StatementListOpt) {
        this.MethodTypeAndName=MethodTypeAndName;
        if(MethodTypeAndName!=null) MethodTypeAndName.setParent(this);
        this.FormParsList=FormParsList;
        if(FormParsList!=null) FormParsList.setParent(this);
        this.VarDeclListMultiple=VarDeclListMultiple;
        if(VarDeclListMultiple!=null) VarDeclListMultiple.setParent(this);
        this.StatementListOpt=StatementListOpt;
        if(StatementListOpt!=null) StatementListOpt.setParent(this);
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

    public VarDeclListMultiple getVarDeclListMultiple() {
        return VarDeclListMultiple;
    }

    public void setVarDeclListMultiple(VarDeclListMultiple VarDeclListMultiple) {
        this.VarDeclListMultiple=VarDeclListMultiple;
    }

    public StatementListOpt getStatementListOpt() {
        return StatementListOpt;
    }

    public void setStatementListOpt(StatementListOpt StatementListOpt) {
        this.StatementListOpt=StatementListOpt;
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
        if(VarDeclListMultiple!=null) VarDeclListMultiple.accept(visitor);
        if(StatementListOpt!=null) StatementListOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodTypeAndName!=null) MethodTypeAndName.traverseTopDown(visitor);
        if(FormParsList!=null) FormParsList.traverseTopDown(visitor);
        if(VarDeclListMultiple!=null) VarDeclListMultiple.traverseTopDown(visitor);
        if(StatementListOpt!=null) StatementListOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodTypeAndName!=null) MethodTypeAndName.traverseBottomUp(visitor);
        if(FormParsList!=null) FormParsList.traverseBottomUp(visitor);
        if(VarDeclListMultiple!=null) VarDeclListMultiple.traverseBottomUp(visitor);
        if(StatementListOpt!=null) StatementListOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDecl(\n");

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

        if(VarDeclListMultiple!=null)
            buffer.append(VarDeclListMultiple.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementListOpt!=null)
            buffer.append(StatementListOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDecl]");
        return buffer.toString();
    }
}

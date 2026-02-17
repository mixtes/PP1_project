// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class FormParsList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private FormParOpt FormParOpt;

    public FormParsList (FormParOpt FormParOpt) {
        this.FormParOpt=FormParOpt;
        if(FormParOpt!=null) FormParOpt.setParent(this);
    }

    public FormParOpt getFormParOpt() {
        return FormParOpt;
    }

    public void setFormParOpt(FormParOpt FormParOpt) {
        this.FormParOpt=FormParOpt;
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
        if(FormParOpt!=null) FormParOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormParOpt!=null) FormParOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormParOpt!=null) FormParOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParsList(\n");

        if(FormParOpt!=null)
            buffer.append(FormParOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParsList]");
        return buffer.toString();
    }
}

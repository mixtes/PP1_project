// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class ElseOpt_else extends ElseOpt {

    private ElseMarker ElseMarker;
    private Statement Statement;

    public ElseOpt_else (ElseMarker ElseMarker, Statement Statement) {
        this.ElseMarker=ElseMarker;
        if(ElseMarker!=null) ElseMarker.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public ElseMarker getElseMarker() {
        return ElseMarker;
    }

    public void setElseMarker(ElseMarker ElseMarker) {
        this.ElseMarker=ElseMarker;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ElseMarker!=null) ElseMarker.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ElseMarker!=null) ElseMarker.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ElseMarker!=null) ElseMarker.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ElseOpt_else(\n");

        if(ElseMarker!=null)
            buffer.append(ElseMarker.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ElseOpt_else]");
        return buffer.toString();
    }
}

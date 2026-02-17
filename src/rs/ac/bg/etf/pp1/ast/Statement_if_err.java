// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class Statement_if_err extends Statement {

    private IfStart IfStart;
    private ThenMarker ThenMarker;
    private Statement Statement;
    private ElseOpt ElseOpt;

    public Statement_if_err (IfStart IfStart, ThenMarker ThenMarker, Statement Statement, ElseOpt ElseOpt) {
        this.IfStart=IfStart;
        if(IfStart!=null) IfStart.setParent(this);
        this.ThenMarker=ThenMarker;
        if(ThenMarker!=null) ThenMarker.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.ElseOpt=ElseOpt;
        if(ElseOpt!=null) ElseOpt.setParent(this);
    }

    public IfStart getIfStart() {
        return IfStart;
    }

    public void setIfStart(IfStart IfStart) {
        this.IfStart=IfStart;
    }

    public ThenMarker getThenMarker() {
        return ThenMarker;
    }

    public void setThenMarker(ThenMarker ThenMarker) {
        this.ThenMarker=ThenMarker;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public ElseOpt getElseOpt() {
        return ElseOpt;
    }

    public void setElseOpt(ElseOpt ElseOpt) {
        this.ElseOpt=ElseOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IfStart!=null) IfStart.accept(visitor);
        if(ThenMarker!=null) ThenMarker.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(ElseOpt!=null) ElseOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IfStart!=null) IfStart.traverseTopDown(visitor);
        if(ThenMarker!=null) ThenMarker.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(ElseOpt!=null) ElseOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IfStart!=null) IfStart.traverseBottomUp(visitor);
        if(ThenMarker!=null) ThenMarker.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(ElseOpt!=null) ElseOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_if_err(\n");

        if(IfStart!=null)
            buffer.append(IfStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ThenMarker!=null)
            buffer.append(ThenMarker.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ElseOpt!=null)
            buffer.append(ElseOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_if_err]");
        return buffer.toString();
    }
}

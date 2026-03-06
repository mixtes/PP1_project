// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class Statement_while extends Statement {

    private WhileInit WhileInit;
    private WhileConditionStart WhileConditionStart;
    private Condition Condition;
    private WhileBodyStart WhileBodyStart;
    private Statement Statement;

    public Statement_while (WhileInit WhileInit, WhileConditionStart WhileConditionStart, Condition Condition, WhileBodyStart WhileBodyStart, Statement Statement) {
        this.WhileInit=WhileInit;
        if(WhileInit!=null) WhileInit.setParent(this);
        this.WhileConditionStart=WhileConditionStart;
        if(WhileConditionStart!=null) WhileConditionStart.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.WhileBodyStart=WhileBodyStart;
        if(WhileBodyStart!=null) WhileBodyStart.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public WhileInit getWhileInit() {
        return WhileInit;
    }

    public void setWhileInit(WhileInit WhileInit) {
        this.WhileInit=WhileInit;
    }

    public WhileConditionStart getWhileConditionStart() {
        return WhileConditionStart;
    }

    public void setWhileConditionStart(WhileConditionStart WhileConditionStart) {
        this.WhileConditionStart=WhileConditionStart;
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public WhileBodyStart getWhileBodyStart() {
        return WhileBodyStart;
    }

    public void setWhileBodyStart(WhileBodyStart WhileBodyStart) {
        this.WhileBodyStart=WhileBodyStart;
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
        if(WhileInit!=null) WhileInit.accept(visitor);
        if(WhileConditionStart!=null) WhileConditionStart.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
        if(WhileBodyStart!=null) WhileBodyStart.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(WhileInit!=null) WhileInit.traverseTopDown(visitor);
        if(WhileConditionStart!=null) WhileConditionStart.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(WhileBodyStart!=null) WhileBodyStart.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(WhileInit!=null) WhileInit.traverseBottomUp(visitor);
        if(WhileConditionStart!=null) WhileConditionStart.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(WhileBodyStart!=null) WhileBodyStart.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_while(\n");

        if(WhileInit!=null)
            buffer.append(WhileInit.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileConditionStart!=null)
            buffer.append(WhileConditionStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileBodyStart!=null)
            buffer.append(WhileBodyStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_while]");
        return buffer.toString();
    }
}

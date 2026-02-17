// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class Statement_for extends Statement {

    private ForInit ForInit;
    private DesignatorStatementOpt DesignatorStatementOpt;
    private ForConditionStart ForConditionStart;
    private ConditionOpt ConditionOpt;
    private ForUpdateStart ForUpdateStart;
    private DesignatorStatementOpt DesignatorStatementOpt1;
    private ForBodyStart ForBodyStart;
    private Statement Statement;

    public Statement_for (ForInit ForInit, DesignatorStatementOpt DesignatorStatementOpt, ForConditionStart ForConditionStart, ConditionOpt ConditionOpt, ForUpdateStart ForUpdateStart, DesignatorStatementOpt DesignatorStatementOpt1, ForBodyStart ForBodyStart, Statement Statement) {
        this.ForInit=ForInit;
        if(ForInit!=null) ForInit.setParent(this);
        this.DesignatorStatementOpt=DesignatorStatementOpt;
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.setParent(this);
        this.ForConditionStart=ForConditionStart;
        if(ForConditionStart!=null) ForConditionStart.setParent(this);
        this.ConditionOpt=ConditionOpt;
        if(ConditionOpt!=null) ConditionOpt.setParent(this);
        this.ForUpdateStart=ForUpdateStart;
        if(ForUpdateStart!=null) ForUpdateStart.setParent(this);
        this.DesignatorStatementOpt1=DesignatorStatementOpt1;
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.setParent(this);
        this.ForBodyStart=ForBodyStart;
        if(ForBodyStart!=null) ForBodyStart.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public ForInit getForInit() {
        return ForInit;
    }

    public void setForInit(ForInit ForInit) {
        this.ForInit=ForInit;
    }

    public DesignatorStatementOpt getDesignatorStatementOpt() {
        return DesignatorStatementOpt;
    }

    public void setDesignatorStatementOpt(DesignatorStatementOpt DesignatorStatementOpt) {
        this.DesignatorStatementOpt=DesignatorStatementOpt;
    }

    public ForConditionStart getForConditionStart() {
        return ForConditionStart;
    }

    public void setForConditionStart(ForConditionStart ForConditionStart) {
        this.ForConditionStart=ForConditionStart;
    }

    public ConditionOpt getConditionOpt() {
        return ConditionOpt;
    }

    public void setConditionOpt(ConditionOpt ConditionOpt) {
        this.ConditionOpt=ConditionOpt;
    }

    public ForUpdateStart getForUpdateStart() {
        return ForUpdateStart;
    }

    public void setForUpdateStart(ForUpdateStart ForUpdateStart) {
        this.ForUpdateStart=ForUpdateStart;
    }

    public DesignatorStatementOpt getDesignatorStatementOpt1() {
        return DesignatorStatementOpt1;
    }

    public void setDesignatorStatementOpt1(DesignatorStatementOpt DesignatorStatementOpt1) {
        this.DesignatorStatementOpt1=DesignatorStatementOpt1;
    }

    public ForBodyStart getForBodyStart() {
        return ForBodyStart;
    }

    public void setForBodyStart(ForBodyStart ForBodyStart) {
        this.ForBodyStart=ForBodyStart;
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
        if(ForInit!=null) ForInit.accept(visitor);
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.accept(visitor);
        if(ForConditionStart!=null) ForConditionStart.accept(visitor);
        if(ConditionOpt!=null) ConditionOpt.accept(visitor);
        if(ForUpdateStart!=null) ForUpdateStart.accept(visitor);
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.accept(visitor);
        if(ForBodyStart!=null) ForBodyStart.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForInit!=null) ForInit.traverseTopDown(visitor);
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.traverseTopDown(visitor);
        if(ForConditionStart!=null) ForConditionStart.traverseTopDown(visitor);
        if(ConditionOpt!=null) ConditionOpt.traverseTopDown(visitor);
        if(ForUpdateStart!=null) ForUpdateStart.traverseTopDown(visitor);
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.traverseTopDown(visitor);
        if(ForBodyStart!=null) ForBodyStart.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForInit!=null) ForInit.traverseBottomUp(visitor);
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.traverseBottomUp(visitor);
        if(ForConditionStart!=null) ForConditionStart.traverseBottomUp(visitor);
        if(ConditionOpt!=null) ConditionOpt.traverseBottomUp(visitor);
        if(ForUpdateStart!=null) ForUpdateStart.traverseBottomUp(visitor);
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.traverseBottomUp(visitor);
        if(ForBodyStart!=null) ForBodyStart.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_for(\n");

        if(ForInit!=null)
            buffer.append(ForInit.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorStatementOpt!=null)
            buffer.append(DesignatorStatementOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForConditionStart!=null)
            buffer.append(ForConditionStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConditionOpt!=null)
            buffer.append(ConditionOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForUpdateStart!=null)
            buffer.append(ForUpdateStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorStatementOpt1!=null)
            buffer.append(DesignatorStatementOpt1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForBodyStart!=null)
            buffer.append(ForBodyStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_for]");
        return buffer.toString();
    }
}

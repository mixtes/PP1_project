// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class Statement_switch extends Statement {

    private SwitchInit SwitchInit;
    private Expression Expression;
    private CaseListOpt CaseListOpt;

    public Statement_switch (SwitchInit SwitchInit, Expression Expression, CaseListOpt CaseListOpt) {
        this.SwitchInit=SwitchInit;
        if(SwitchInit!=null) SwitchInit.setParent(this);
        this.Expression=Expression;
        if(Expression!=null) Expression.setParent(this);
        this.CaseListOpt=CaseListOpt;
        if(CaseListOpt!=null) CaseListOpt.setParent(this);
    }

    public SwitchInit getSwitchInit() {
        return SwitchInit;
    }

    public void setSwitchInit(SwitchInit SwitchInit) {
        this.SwitchInit=SwitchInit;
    }

    public Expression getExpression() {
        return Expression;
    }

    public void setExpression(Expression Expression) {
        this.Expression=Expression;
    }

    public CaseListOpt getCaseListOpt() {
        return CaseListOpt;
    }

    public void setCaseListOpt(CaseListOpt CaseListOpt) {
        this.CaseListOpt=CaseListOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SwitchInit!=null) SwitchInit.accept(visitor);
        if(Expression!=null) Expression.accept(visitor);
        if(CaseListOpt!=null) CaseListOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SwitchInit!=null) SwitchInit.traverseTopDown(visitor);
        if(Expression!=null) Expression.traverseTopDown(visitor);
        if(CaseListOpt!=null) CaseListOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SwitchInit!=null) SwitchInit.traverseBottomUp(visitor);
        if(Expression!=null) Expression.traverseBottomUp(visitor);
        if(CaseListOpt!=null) CaseListOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_switch(\n");

        if(SwitchInit!=null)
            buffer.append(SwitchInit.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expression!=null)
            buffer.append(Expression.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CaseListOpt!=null)
            buffer.append(CaseListOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_switch]");
        return buffer.toString();
    }
}

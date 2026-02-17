// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class Expression_condition extends Expression {

    private Condition Condition;
    private TernaryTrueStart TernaryTrueStart;
    private Expression Expression;
    private TernaryFalseStart TernaryFalseStart;
    private Expression Expression1;

    public Expression_condition (Condition Condition, TernaryTrueStart TernaryTrueStart, Expression Expression, TernaryFalseStart TernaryFalseStart, Expression Expression1) {
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.TernaryTrueStart=TernaryTrueStart;
        if(TernaryTrueStart!=null) TernaryTrueStart.setParent(this);
        this.Expression=Expression;
        if(Expression!=null) Expression.setParent(this);
        this.TernaryFalseStart=TernaryFalseStart;
        if(TernaryFalseStart!=null) TernaryFalseStart.setParent(this);
        this.Expression1=Expression1;
        if(Expression1!=null) Expression1.setParent(this);
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public TernaryTrueStart getTernaryTrueStart() {
        return TernaryTrueStart;
    }

    public void setTernaryTrueStart(TernaryTrueStart TernaryTrueStart) {
        this.TernaryTrueStart=TernaryTrueStart;
    }

    public Expression getExpression() {
        return Expression;
    }

    public void setExpression(Expression Expression) {
        this.Expression=Expression;
    }

    public TernaryFalseStart getTernaryFalseStart() {
        return TernaryFalseStart;
    }

    public void setTernaryFalseStart(TernaryFalseStart TernaryFalseStart) {
        this.TernaryFalseStart=TernaryFalseStart;
    }

    public Expression getExpression1() {
        return Expression1;
    }

    public void setExpression1(Expression Expression1) {
        this.Expression1=Expression1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Condition!=null) Condition.accept(visitor);
        if(TernaryTrueStart!=null) TernaryTrueStart.accept(visitor);
        if(Expression!=null) Expression.accept(visitor);
        if(TernaryFalseStart!=null) TernaryFalseStart.accept(visitor);
        if(Expression1!=null) Expression1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(TernaryTrueStart!=null) TernaryTrueStart.traverseTopDown(visitor);
        if(Expression!=null) Expression.traverseTopDown(visitor);
        if(TernaryFalseStart!=null) TernaryFalseStart.traverseTopDown(visitor);
        if(Expression1!=null) Expression1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(TernaryTrueStart!=null) TernaryTrueStart.traverseBottomUp(visitor);
        if(Expression!=null) Expression.traverseBottomUp(visitor);
        if(TernaryFalseStart!=null) TernaryFalseStart.traverseBottomUp(visitor);
        if(Expression1!=null) Expression1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Expression_condition(\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryTrueStart!=null)
            buffer.append(TernaryTrueStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expression!=null)
            buffer.append(Expression.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryFalseStart!=null)
            buffer.append(TernaryFalseStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expression1!=null)
            buffer.append(Expression1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Expression_condition]");
        return buffer.toString();
    }
}

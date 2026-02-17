// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class CondFact_relop extends CondFact {

    private SimpleExpression SimpleExpression;
    private Relop Relop;
    private SimpleExpression SimpleExpression1;

    public CondFact_relop (SimpleExpression SimpleExpression, Relop Relop, SimpleExpression SimpleExpression1) {
        this.SimpleExpression=SimpleExpression;
        if(SimpleExpression!=null) SimpleExpression.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.SimpleExpression1=SimpleExpression1;
        if(SimpleExpression1!=null) SimpleExpression1.setParent(this);
    }

    public SimpleExpression getSimpleExpression() {
        return SimpleExpression;
    }

    public void setSimpleExpression(SimpleExpression SimpleExpression) {
        this.SimpleExpression=SimpleExpression;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public SimpleExpression getSimpleExpression1() {
        return SimpleExpression1;
    }

    public void setSimpleExpression1(SimpleExpression SimpleExpression1) {
        this.SimpleExpression1=SimpleExpression1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SimpleExpression!=null) SimpleExpression.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(SimpleExpression1!=null) SimpleExpression1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SimpleExpression!=null) SimpleExpression.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(SimpleExpression1!=null) SimpleExpression1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SimpleExpression!=null) SimpleExpression.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(SimpleExpression1!=null) SimpleExpression1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_relop(\n");

        if(SimpleExpression!=null)
            buffer.append(SimpleExpression.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(SimpleExpression1!=null)
            buffer.append(SimpleExpression1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_relop]");
        return buffer.toString();
    }
}

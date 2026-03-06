// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class CondFact_single extends CondFact {

    private SimpleExpression SimpleExpression;

    public CondFact_single (SimpleExpression SimpleExpression) {
        this.SimpleExpression=SimpleExpression;
        if(SimpleExpression!=null) SimpleExpression.setParent(this);
    }

    public SimpleExpression getSimpleExpression() {
        return SimpleExpression;
    }

    public void setSimpleExpression(SimpleExpression SimpleExpression) {
        this.SimpleExpression=SimpleExpression;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SimpleExpression!=null) SimpleExpression.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SimpleExpression!=null) SimpleExpression.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SimpleExpression!=null) SimpleExpression.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_single(\n");

        if(SimpleExpression!=null)
            buffer.append(SimpleExpression.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_single]");
        return buffer.toString();
    }
}

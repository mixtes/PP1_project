// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class FormParsAdditional_epsilon extends FormParsAdditional {

    public FormParsAdditional_epsilon () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParsAdditional_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [FormParsAdditional_epsilon]");
        return buffer.toString();
    }
}

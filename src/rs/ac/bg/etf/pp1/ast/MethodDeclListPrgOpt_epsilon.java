// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclListPrgOpt_epsilon extends MethodDeclListPrgOpt {

    public MethodDeclListPrgOpt_epsilon () {
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
        buffer.append("MethodDeclListPrgOpt_epsilon(\n");

        buffer.append(tab);
        buffer.append(") [MethodDeclListPrgOpt_epsilon]");
        return buffer.toString();
    }
}

// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class Statement_return extends Statement {

    private ExpressionOpt ExpressionOpt;

    public Statement_return (ExpressionOpt ExpressionOpt) {
        this.ExpressionOpt=ExpressionOpt;
        if(ExpressionOpt!=null) ExpressionOpt.setParent(this);
    }

    public ExpressionOpt getExpressionOpt() {
        return ExpressionOpt;
    }

    public void setExpressionOpt(ExpressionOpt ExpressionOpt) {
        this.ExpressionOpt=ExpressionOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExpressionOpt!=null) ExpressionOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExpressionOpt!=null) ExpressionOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExpressionOpt!=null) ExpressionOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_return(\n");

        if(ExpressionOpt!=null)
            buffer.append(ExpressionOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_return]");
        return buffer.toString();
    }
}

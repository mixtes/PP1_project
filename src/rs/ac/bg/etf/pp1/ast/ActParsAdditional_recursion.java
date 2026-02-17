// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class ActParsAdditional_recursion extends ActParsAdditional {

    private ActPar ActPar;
    private ActParsAdditional ActParsAdditional;

    public ActParsAdditional_recursion (ActPar ActPar, ActParsAdditional ActParsAdditional) {
        this.ActPar=ActPar;
        if(ActPar!=null) ActPar.setParent(this);
        this.ActParsAdditional=ActParsAdditional;
        if(ActParsAdditional!=null) ActParsAdditional.setParent(this);
    }

    public ActPar getActPar() {
        return ActPar;
    }

    public void setActPar(ActPar ActPar) {
        this.ActPar=ActPar;
    }

    public ActParsAdditional getActParsAdditional() {
        return ActParsAdditional;
    }

    public void setActParsAdditional(ActParsAdditional ActParsAdditional) {
        this.ActParsAdditional=ActParsAdditional;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActPar!=null) ActPar.accept(visitor);
        if(ActParsAdditional!=null) ActParsAdditional.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActPar!=null) ActPar.traverseTopDown(visitor);
        if(ActParsAdditional!=null) ActParsAdditional.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActPar!=null) ActPar.traverseBottomUp(visitor);
        if(ActParsAdditional!=null) ActParsAdditional.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParsAdditional_recursion(\n");

        if(ActPar!=null)
            buffer.append(ActPar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActParsAdditional!=null)
            buffer.append(ActParsAdditional.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParsAdditional_recursion]");
        return buffer.toString();
    }
}

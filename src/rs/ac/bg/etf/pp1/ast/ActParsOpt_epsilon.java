// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class ActParsOpt_epsilon extends ActParsOpt {

    private ActParsListOpener ActParsListOpener;

    public ActParsOpt_epsilon (ActParsListOpener ActParsListOpener) {
        this.ActParsListOpener=ActParsListOpener;
        if(ActParsListOpener!=null) ActParsListOpener.setParent(this);
    }

    public ActParsListOpener getActParsListOpener() {
        return ActParsListOpener;
    }

    public void setActParsListOpener(ActParsListOpener ActParsListOpener) {
        this.ActParsListOpener=ActParsListOpener;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParsListOpener!=null) ActParsListOpener.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsListOpener!=null) ActParsListOpener.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsListOpener!=null) ActParsListOpener.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParsOpt_epsilon(\n");

        if(ActParsListOpener!=null)
            buffer.append(ActParsListOpener.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParsOpt_epsilon]");
        return buffer.toString();
    }
}

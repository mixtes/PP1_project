// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class ActParsOpt_exist extends ActParsOpt {

    private ActParsListOpener ActParsListOpener;
    private ActPar ActPar;
    private ActParsAdditional ActParsAdditional;

    public ActParsOpt_exist (ActParsListOpener ActParsListOpener, ActPar ActPar, ActParsAdditional ActParsAdditional) {
        this.ActParsListOpener=ActParsListOpener;
        if(ActParsListOpener!=null) ActParsListOpener.setParent(this);
        this.ActPar=ActPar;
        if(ActPar!=null) ActPar.setParent(this);
        this.ActParsAdditional=ActParsAdditional;
        if(ActParsAdditional!=null) ActParsAdditional.setParent(this);
    }

    public ActParsListOpener getActParsListOpener() {
        return ActParsListOpener;
    }

    public void setActParsListOpener(ActParsListOpener ActParsListOpener) {
        this.ActParsListOpener=ActParsListOpener;
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
        if(ActParsListOpener!=null) ActParsListOpener.accept(visitor);
        if(ActPar!=null) ActPar.accept(visitor);
        if(ActParsAdditional!=null) ActParsAdditional.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsListOpener!=null) ActParsListOpener.traverseTopDown(visitor);
        if(ActPar!=null) ActPar.traverseTopDown(visitor);
        if(ActParsAdditional!=null) ActParsAdditional.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsListOpener!=null) ActParsListOpener.traverseBottomUp(visitor);
        if(ActPar!=null) ActPar.traverseBottomUp(visitor);
        if(ActParsAdditional!=null) ActParsAdditional.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParsOpt_exist(\n");

        if(ActParsListOpener!=null)
            buffer.append(ActParsListOpener.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

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
        buffer.append(") [ActParsOpt_exist]");
        return buffer.toString();
    }
}

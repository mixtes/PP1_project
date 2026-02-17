// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class AdditionalEnumIDT_recursion extends AdditionalEnumIDT {

    private EnumIDT EnumIDT;
    private AdditionalEnumIDT AdditionalEnumIDT;

    public AdditionalEnumIDT_recursion (EnumIDT EnumIDT, AdditionalEnumIDT AdditionalEnumIDT) {
        this.EnumIDT=EnumIDT;
        if(EnumIDT!=null) EnumIDT.setParent(this);
        this.AdditionalEnumIDT=AdditionalEnumIDT;
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.setParent(this);
    }

    public EnumIDT getEnumIDT() {
        return EnumIDT;
    }

    public void setEnumIDT(EnumIDT EnumIDT) {
        this.EnumIDT=EnumIDT;
    }

    public AdditionalEnumIDT getAdditionalEnumIDT() {
        return AdditionalEnumIDT;
    }

    public void setAdditionalEnumIDT(AdditionalEnumIDT AdditionalEnumIDT) {
        this.AdditionalEnumIDT=AdditionalEnumIDT;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumIDT!=null) EnumIDT.accept(visitor);
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumIDT!=null) EnumIDT.traverseTopDown(visitor);
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumIDT!=null) EnumIDT.traverseBottomUp(visitor);
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AdditionalEnumIDT_recursion(\n");

        if(EnumIDT!=null)
            buffer.append(EnumIDT.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AdditionalEnumIDT!=null)
            buffer.append(AdditionalEnumIDT.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AdditionalEnumIDT_recursion]");
        return buffer.toString();
    }
}

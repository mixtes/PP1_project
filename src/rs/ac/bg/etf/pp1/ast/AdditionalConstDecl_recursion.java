// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class AdditionalConstDecl_recursion extends AdditionalConstDecl {

    private ConstDecl ConstDecl;
    private AdditionalConstDecl AdditionalConstDecl;

    public AdditionalConstDecl_recursion (ConstDecl ConstDecl, AdditionalConstDecl AdditionalConstDecl) {
        this.ConstDecl=ConstDecl;
        if(ConstDecl!=null) ConstDecl.setParent(this);
        this.AdditionalConstDecl=AdditionalConstDecl;
        if(AdditionalConstDecl!=null) AdditionalConstDecl.setParent(this);
    }

    public ConstDecl getConstDecl() {
        return ConstDecl;
    }

    public void setConstDecl(ConstDecl ConstDecl) {
        this.ConstDecl=ConstDecl;
    }

    public AdditionalConstDecl getAdditionalConstDecl() {
        return AdditionalConstDecl;
    }

    public void setAdditionalConstDecl(AdditionalConstDecl AdditionalConstDecl) {
        this.AdditionalConstDecl=AdditionalConstDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstDecl!=null) ConstDecl.accept(visitor);
        if(AdditionalConstDecl!=null) AdditionalConstDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstDecl!=null) ConstDecl.traverseTopDown(visitor);
        if(AdditionalConstDecl!=null) AdditionalConstDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstDecl!=null) ConstDecl.traverseBottomUp(visitor);
        if(AdditionalConstDecl!=null) AdditionalConstDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AdditionalConstDecl_recursion(\n");

        if(ConstDecl!=null)
            buffer.append(ConstDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AdditionalConstDecl!=null)
            buffer.append(AdditionalConstDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AdditionalConstDecl_recursion]");
        return buffer.toString();
    }
}

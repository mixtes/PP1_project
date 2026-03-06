// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class FactorList_recursion extends FactorList {

    private FactorList FactorList;
    private Mulop Mulop;
    private ExtensionFactor ExtensionFactor;

    public FactorList_recursion (FactorList FactorList, Mulop Mulop, ExtensionFactor ExtensionFactor) {
        this.FactorList=FactorList;
        if(FactorList!=null) FactorList.setParent(this);
        this.Mulop=Mulop;
        if(Mulop!=null) Mulop.setParent(this);
        this.ExtensionFactor=ExtensionFactor;
        if(ExtensionFactor!=null) ExtensionFactor.setParent(this);
    }

    public FactorList getFactorList() {
        return FactorList;
    }

    public void setFactorList(FactorList FactorList) {
        this.FactorList=FactorList;
    }

    public Mulop getMulop() {
        return Mulop;
    }

    public void setMulop(Mulop Mulop) {
        this.Mulop=Mulop;
    }

    public ExtensionFactor getExtensionFactor() {
        return ExtensionFactor;
    }

    public void setExtensionFactor(ExtensionFactor ExtensionFactor) {
        this.ExtensionFactor=ExtensionFactor;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FactorList!=null) FactorList.accept(visitor);
        if(Mulop!=null) Mulop.accept(visitor);
        if(ExtensionFactor!=null) ExtensionFactor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FactorList!=null) FactorList.traverseTopDown(visitor);
        if(Mulop!=null) Mulop.traverseTopDown(visitor);
        if(ExtensionFactor!=null) ExtensionFactor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FactorList!=null) FactorList.traverseBottomUp(visitor);
        if(Mulop!=null) Mulop.traverseBottomUp(visitor);
        if(ExtensionFactor!=null) ExtensionFactor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorList_recursion(\n");

        if(FactorList!=null)
            buffer.append(FactorList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Mulop!=null)
            buffer.append(Mulop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExtensionFactor!=null)
            buffer.append(ExtensionFactor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorList_recursion]");
        return buffer.toString();
    }
}

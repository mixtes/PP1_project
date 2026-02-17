// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class FactorList_single extends FactorList {

    private ExtensionFactor ExtensionFactor;

    public FactorList_single (ExtensionFactor ExtensionFactor) {
        this.ExtensionFactor=ExtensionFactor;
        if(ExtensionFactor!=null) ExtensionFactor.setParent(this);
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
        if(ExtensionFactor!=null) ExtensionFactor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExtensionFactor!=null) ExtensionFactor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExtensionFactor!=null) ExtensionFactor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorList_single(\n");

        if(ExtensionFactor!=null)
            buffer.append(ExtensionFactor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorList_single]");
        return buffer.toString();
    }
}

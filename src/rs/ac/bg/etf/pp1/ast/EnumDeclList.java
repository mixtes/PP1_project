// generated with ast extension for cup
// version 0.8
// 1/2/2026 21:4:38


package rs.ac.bg.etf.pp1.ast;

public class EnumDeclList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private EnumName EnumName;
    private EnumIDT EnumIDT;
    private AdditionalEnumIDT AdditionalEnumIDT;

    public EnumDeclList (EnumName EnumName, EnumIDT EnumIDT, AdditionalEnumIDT AdditionalEnumIDT) {
        this.EnumName=EnumName;
        if(EnumName!=null) EnumName.setParent(this);
        this.EnumIDT=EnumIDT;
        if(EnumIDT!=null) EnumIDT.setParent(this);
        this.AdditionalEnumIDT=AdditionalEnumIDT;
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.setParent(this);
    }

    public EnumName getEnumName() {
        return EnumName;
    }

    public void setEnumName(EnumName EnumName) {
        this.EnumName=EnumName;
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

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumName!=null) EnumName.accept(visitor);
        if(EnumIDT!=null) EnumIDT.accept(visitor);
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumName!=null) EnumName.traverseTopDown(visitor);
        if(EnumIDT!=null) EnumIDT.traverseTopDown(visitor);
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumName!=null) EnumName.traverseBottomUp(visitor);
        if(EnumIDT!=null) EnumIDT.traverseBottomUp(visitor);
        if(AdditionalEnumIDT!=null) AdditionalEnumIDT.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumDeclList(\n");

        if(EnumName!=null)
            buffer.append(EnumName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

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
        buffer.append(") [EnumDeclList]");
        return buffer.toString();
    }
}

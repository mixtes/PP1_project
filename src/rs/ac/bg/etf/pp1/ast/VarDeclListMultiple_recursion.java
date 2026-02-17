// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListMultiple_recursion extends VarDeclListMultiple {

    private VarDeclListMultiple VarDeclListMultiple;
    private VarDeclList VarDeclList;

    public VarDeclListMultiple_recursion (VarDeclListMultiple VarDeclListMultiple, VarDeclList VarDeclList) {
        this.VarDeclListMultiple=VarDeclListMultiple;
        if(VarDeclListMultiple!=null) VarDeclListMultiple.setParent(this);
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
    }

    public VarDeclListMultiple getVarDeclListMultiple() {
        return VarDeclListMultiple;
    }

    public void setVarDeclListMultiple(VarDeclListMultiple VarDeclListMultiple) {
        this.VarDeclListMultiple=VarDeclListMultiple;
    }

    public VarDeclList getVarDeclList() {
        return VarDeclList;
    }

    public void setVarDeclList(VarDeclList VarDeclList) {
        this.VarDeclList=VarDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclListMultiple!=null) VarDeclListMultiple.accept(visitor);
        if(VarDeclList!=null) VarDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclListMultiple!=null) VarDeclListMultiple.traverseTopDown(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclListMultiple!=null) VarDeclListMultiple.traverseBottomUp(visitor);
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListMultiple_recursion(\n");

        if(VarDeclListMultiple!=null)
            buffer.append(VarDeclListMultiple.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListMultiple_recursion]");
        return buffer.toString();
    }
}

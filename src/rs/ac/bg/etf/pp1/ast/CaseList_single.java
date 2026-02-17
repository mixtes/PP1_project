// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class CaseList_single extends CaseList {

    private NumConst NumConst;
    private CaseStart CaseStart;
    private StatementListOpt StatementListOpt;
    private CaseEnd CaseEnd;

    public CaseList_single (NumConst NumConst, CaseStart CaseStart, StatementListOpt StatementListOpt, CaseEnd CaseEnd) {
        this.NumConst=NumConst;
        if(NumConst!=null) NumConst.setParent(this);
        this.CaseStart=CaseStart;
        if(CaseStart!=null) CaseStart.setParent(this);
        this.StatementListOpt=StatementListOpt;
        if(StatementListOpt!=null) StatementListOpt.setParent(this);
        this.CaseEnd=CaseEnd;
        if(CaseEnd!=null) CaseEnd.setParent(this);
    }

    public NumConst getNumConst() {
        return NumConst;
    }

    public void setNumConst(NumConst NumConst) {
        this.NumConst=NumConst;
    }

    public CaseStart getCaseStart() {
        return CaseStart;
    }

    public void setCaseStart(CaseStart CaseStart) {
        this.CaseStart=CaseStart;
    }

    public StatementListOpt getStatementListOpt() {
        return StatementListOpt;
    }

    public void setStatementListOpt(StatementListOpt StatementListOpt) {
        this.StatementListOpt=StatementListOpt;
    }

    public CaseEnd getCaseEnd() {
        return CaseEnd;
    }

    public void setCaseEnd(CaseEnd CaseEnd) {
        this.CaseEnd=CaseEnd;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(NumConst!=null) NumConst.accept(visitor);
        if(CaseStart!=null) CaseStart.accept(visitor);
        if(StatementListOpt!=null) StatementListOpt.accept(visitor);
        if(CaseEnd!=null) CaseEnd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(NumConst!=null) NumConst.traverseTopDown(visitor);
        if(CaseStart!=null) CaseStart.traverseTopDown(visitor);
        if(StatementListOpt!=null) StatementListOpt.traverseTopDown(visitor);
        if(CaseEnd!=null) CaseEnd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(NumConst!=null) NumConst.traverseBottomUp(visitor);
        if(CaseStart!=null) CaseStart.traverseBottomUp(visitor);
        if(StatementListOpt!=null) StatementListOpt.traverseBottomUp(visitor);
        if(CaseEnd!=null) CaseEnd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CaseList_single(\n");

        if(NumConst!=null)
            buffer.append(NumConst.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CaseStart!=null)
            buffer.append(CaseStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementListOpt!=null)
            buffer.append(StatementListOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CaseEnd!=null)
            buffer.append(CaseEnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CaseList_single]");
        return buffer.toString();
    }
}

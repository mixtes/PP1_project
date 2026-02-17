// generated with ast extension for cup
// version 0.8
// 17/1/2026 16:4:33


package rs.ac.bg.etf.pp1.ast;

public class FormParOpt_exists extends FormParOpt {

    private FormPar FormPar;
    private FormParsAdditional FormParsAdditional;

    public FormParOpt_exists (FormPar FormPar, FormParsAdditional FormParsAdditional) {
        this.FormPar=FormPar;
        if(FormPar!=null) FormPar.setParent(this);
        this.FormParsAdditional=FormParsAdditional;
        if(FormParsAdditional!=null) FormParsAdditional.setParent(this);
    }

    public FormPar getFormPar() {
        return FormPar;
    }

    public void setFormPar(FormPar FormPar) {
        this.FormPar=FormPar;
    }

    public FormParsAdditional getFormParsAdditional() {
        return FormParsAdditional;
    }

    public void setFormParsAdditional(FormParsAdditional FormParsAdditional) {
        this.FormParsAdditional=FormParsAdditional;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FormPar!=null) FormPar.accept(visitor);
        if(FormParsAdditional!=null) FormParsAdditional.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormPar!=null) FormPar.traverseTopDown(visitor);
        if(FormParsAdditional!=null) FormParsAdditional.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormPar!=null) FormPar.traverseBottomUp(visitor);
        if(FormParsAdditional!=null) FormParsAdditional.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParOpt_exists(\n");

        if(FormPar!=null)
            buffer.append(FormPar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParsAdditional!=null)
            buffer.append(FormParsAdditional.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParOpt_exists]");
        return buffer.toString();
    }
}

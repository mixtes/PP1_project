package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import java.util.Stack;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

    private int mainPC;

    public int getMainPC() {
        return this.mainPC;
    }

    private Stack<Obj> designatorObjStack = new Stack<>();

    @Override
    public void visit(DesignatorName designatorName)
    {
        Obj baseObj = designatorName.obj;
        designatorObjStack.push(baseObj);

        // Only push the base onto the expression stack if the designator is complex (has suffixes)
        // but NOT for enum types — enum member access resolves to a constant, no load needed
        Designator parent = (Designator) designatorName.getParent();
        if (!(parent.getDesignatorSuffix() instanceof DesignatorSuffix_epsilon)
            && baseObj.getKind() != Obj.Type)
        {
            Code.load(baseObj);
        }
    }

    @Override
    public void visit(DesignatorSuffix_array suffix)
    {
        Obj curObj = designatorObjStack.peek();
        Struct elemType = curObj.getType().getElemType();
        Obj elemObj = new Obj(Obj.Elem, curObj.getName(), elemType);
        designatorObjStack.set(designatorObjStack.size() - 1, elemObj);

        // If this is an intermediate suffix (not the final one), resolve the access now
        if (!(suffix.getParent() instanceof Designator))
        {
            if (elemType == Tab.charType)
                Code.put(Code.baload);
            else
                Code.put(Code.aload);
        }
    }

    @Override
    public void visit(DesignatorSuffix_member suffix)
    {
        Obj curObj = designatorObjStack.peek();
        Struct ownerType = curObj.getType();
        Obj memberObj = null;
        for (Obj m : ownerType.getMembers())
        {
            if (m.getName().equals(suffix.getI2()))
            {
                memberObj = m;
                break;
            }
        }
        designatorObjStack.set(designatorObjStack.size() - 1, memberObj);

        // If this is an intermediate suffix, resolve the field access now
        if (!(suffix.getParent() instanceof Designator))
        {
            if (ownerType.getKind() == Struct.Enum)
            {
                // enum member is a constant — no field load, nothing to do here
            }
            else
            {
                Code.put(Code.getfield);
                Code.put2(memberObj.getAdr());
            }
        }
    }

    @Override
    public void visit(DesignatorSuffix_length suffix)
    {
        designatorObjStack.set(designatorObjStack.size() - 1, new Obj(Obj.Fld, "length", Tab.intType));

        // Always emit arraylength — .length is read-only so it's always a value load
        Code.put(Code.arraylength);
        designatorAlreadyLoaded = true;
    }

    private boolean designatorAlreadyLoaded = false;

    @Override
    public void visit(Designator designator)
    {
        designator.obj = designatorObjStack.pop();
    }

    /* MethodDecl */

    @Override
    public void visit(MethodTypeAndName_void methodTypeAndName)
    {
        if ("main".equalsIgnoreCase(methodTypeAndName.getI1()))
            this.mainPC = Code.pc;

        methodTypeAndName.obj.setAdr(Code.pc);

        Code.put(Code.enter);
        Code.put(methodTypeAndName.obj.getLevel());
        Code.put(methodTypeAndName.obj.getLocalSymbols().size());
    }

    @Override
    public void visit(MethodTypeAndName_type methodTypeAndName)
    {
        methodTypeAndName.obj.setAdr(Code.pc);

        Code.put(Code.enter);
        Code.put(methodTypeAndName.obj.getLevel());
        Code.put(methodTypeAndName.obj.getLocalSymbols().size());
    }

    @Override
    public void visit(MethodDecl methodDecl)
    {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    /* Statement */

    @Override
    public void visit(Statement_print statement)
    {
        if (statement.getNumConstOpt() instanceof NumConstOpt_exists)
        {
            NumConstOpt_exists numConstOpt = (NumConstOpt_exists) statement.getNumConstOpt();
            int width = numConstOpt.getNumConst().getN1();
            Code.loadConst(width);
        }
        else
        {
            Code.loadConst(0);
        }

        if (statement.getExpression().struct.equals(Tab.charType))
            Code.put(Code.bprint);
        else
            Code.put(Code.print);
    }

    @Override
    public void visit(Statement_read statement)
    {
        Obj designatorObj = statement.getDesignator().obj;
        if (designatorObj.getType().equals(Tab.charType))
            Code.put(Code.bread);
        else
            Code.put(Code.read);
        Code.store(designatorObj);
    }

    @Override
    public void visit(Statement_return statement)
    {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    /* DesignatorStatement */

    @Override
    public void visit(DesignatorStatement_assign designatorStatement)
    {
        Code.store(designatorStatement.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorStatement_inc designatorStatement)
    {
        Obj designatorObj = designatorStatement.getDesignator().obj;

        if (designatorObj.getKind() == Obj.Elem)
            Code.put(Code.dup2);
        else if (designatorObj.getKind() == Obj.Fld)
            Code.put(Code.dup);

        Code.load(designatorObj);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.store(designatorObj);
    }

    @Override
    public void visit(DesignatorStatement_dec designatorStatement)
    {
        Obj designatorObj = designatorStatement.getDesignator().obj;

        if (designatorObj.getKind() == Obj.Elem)
            Code.put(Code.dup2);
        else if (designatorObj.getKind() == Obj.Fld)
            Code.put(Code.dup);

        Code.load(designatorObj);
        Code.loadConst(1);
        Code.put(Code.sub);
        Code.store(designatorObj);
    }

    @Override
    public void visit(DesignatorStatement_method designatorStatement)
    {
        Obj methodObj = designatorStatement.getDesignator().obj;
        String name = methodObj.getName();

        if ("ord".equals(name) || "chr".equals(name))
        {
            Code.put(Code.pop); // discard result since called as statement
            return;
        }
        if ("len".equals(name))
        {
            Code.put(Code.arraylength);
            Code.put(Code.pop);
            return;
        }

        int offset = methodObj.getAdr() - Code.pc;
        Code.put(Code.call);
        Code.put2(offset);

        if (methodObj.getType() != Tab.noType)
            Code.put(Code.pop);
    }

    /* Factor */

    @Override
    public void visit(Factor_constant_num factor_constant)
    {
        Code.loadConst(factor_constant.getN1());
    }

    @Override
    public void visit(Factor_constant_chr factor_constant)
    {
        Code.loadConst(factor_constant.getC1());
    }

    @Override
    public void visit(Factor_constant_boo factor_constant)
    {
        Code.loadConst(factor_constant.getB1());
    }

    @Override
    public void visit(Factor_designator_base factor_designator)
    {
        if (designatorAlreadyLoaded)
        {
            designatorAlreadyLoaded = false;
            return;
        }
        Code.load(factor_designator.getDesignator().obj);
    }

    @Override
    public void visit(Factor_designator_method factor_designator)
    {
        Obj methodObj = factor_designator.getDesignator().obj;
        String name = methodObj.getName();

        // Built-in functions: ord and chr are no-ops (value already on stack), len emits arraylength
        if ("ord".equals(name) || "chr".equals(name))
        {
            // no-op: argument is already on the expression stack
            return;
        }
        if ("len".equals(name))
        {
            Code.put(Code.arraylength);
            return;
        }

        int offset = methodObj.getAdr() - Code.pc;
        Code.put(Code.call);
        Code.put2(offset);
    }

    @Override
    public void visit(Factor_array factor_array)
    {
        Code.put(Code.newarray);
        if (factor_array.struct.getElemType() == Tab.charType)
            Code.put(0); // byte array
        else
            Code.put(1); // word array
    }

    @Override
    public void visit(Factor_object factor_object)
    {
        Code.put(Code.new_);
        int numFields = factor_object.struct.getNumberOfFields();
        Code.put2(numFields * 4);
    }

    /* ExtensionFactor */

    @Override
    public void visit(ExtensionFactor extFactor)
    {
        if (extFactor.getMinusOpt() instanceof MinusOpt_minus)
        {
            Code.put(Code.neg);
        }
    }

    /* TermList */

    @Override
    public void visit(TermList_recursion termList)
    {
        if (termList.getAddop() instanceof Addop_pls)
            Code.put(Code.add);
        else
            Code.put(Code.sub);
    }

    /* FactorList */

    @Override
    public void visit(FactorList_recursion factorList)
    {
        if (factorList.getMulop() instanceof Mulop_mul)
            Code.put(Code.mul);
        else if (factorList.getMulop() instanceof Mulop_div)
            Code.put(Code.div);
        else
            Code.put(Code.rem);
    }

}

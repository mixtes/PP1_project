package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

    private int mainPC;
    
    private boolean designatorAlreadyLoaded = false;

    public int getMainPC() {
        return this.mainPC;
    }

    private Stack<Obj> designatorObjStack = new Stack<>();

    /* Designator */

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

        if ("ord".equals(name) || "chr".equals(name))
        {
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

    /* ============================================================ */
    /* Condition evaluation (shared by if/else, for, and ternary)   */
    /* ============================================================ */

    private Stack<List<Integer>> condFalseJumps = new Stack<>();
    private Stack<List<Integer>> condTrueJumps = new Stack<>();

    private Set<SyntaxNode> ternaryCondContextCreated = new HashSet<>();

    private SyntaxNode findTernaryOwner(SyntaxNode node)
    {
        SyntaxNode current = node.getParent();
        while (current != null)
        {
            if (current instanceof Statement_if || current instanceof Statement_for)
                return null;
            if (current instanceof Expression_condition)
                return current;
            current = current.getParent();
        }
        return null;
    }

    private void ensureCondContext(SyntaxNode condFactNode)
    {
        SyntaxNode ternaryOwner = findTernaryOwner(condFactNode);
        if (ternaryOwner != null && !ternaryCondContextCreated.contains(ternaryOwner))
        {
            ternaryCondContextCreated.add(ternaryOwner);
            condFalseJumps.push(new ArrayList<>());
            condTrueJumps.push(new ArrayList<>());
        }
    }

    @Override
    public void visit(CondFact_single condFact)
    {
        ensureCondContext(condFact);
        Code.loadConst(0);
        Code.putFalseJump(Code.ne, 0);
        condFalseJumps.peek().add(Code.pc - 2);
    }

    @Override
    public void visit(CondFact_relop condFact)
    {
        ensureCondContext(condFact);
        Relop relop = condFact.getRelop();
        if (relop instanceof Relop_eql)
            Code.putFalseJump(Code.eq, 0);
        else if (relop instanceof Relop_neq)
            Code.putFalseJump(Code.ne, 0);
        else if (relop instanceof Relop_let)
            Code.putFalseJump(Code.lt, 0);
        else if (relop instanceof Relop_grt)
            Code.putFalseJump(Code.gt, 0);
        else if (relop instanceof Relop_lte)
            Code.putFalseJump(Code.le, 0);
        else if (relop instanceof Relop_gre)
            Code.putFalseJump(Code.ge, 0);

        condFalseJumps.peek().add(Code.pc - 2);
    }

    @Override
    public void visit(OrConditionMarker marker)
    {
        Code.putJump(0);
        condTrueJumps.peek().add(Code.pc - 2);

        List<Integer> falseJumps = condFalseJumps.peek();
        for (int addr : falseJumps)
            Code.fixup(addr);
        falseJumps.clear();
    }

    private void fixupTrueJumps()
    {
        List<Integer> trueJumps = condTrueJumps.peek();
        for (int addr : trueJumps)
            Code.fixup(addr);
        trueJumps.clear();
    }

    private void fixupFalseJumps()
    {
        List<Integer> falseJumps = condFalseJumps.peek();
        for (int addr : falseJumps)
            Code.fixup(addr);
        falseJumps.clear();
    }

    /* If/Else */

    private Stack<List<Integer>> ifEndJumps = new Stack<>();

    @Override
    public void visit(IfStart ifStart)
    {
        condFalseJumps.push(new ArrayList<>());
        condTrueJumps.push(new ArrayList<>());
        ifEndJumps.push(new ArrayList<>());
    }

    @Override
    public void visit(ThenMarker thenMarker)
    {
        fixupTrueJumps();
    }

    @Override
    public void visit(ElseMarker elseMarker)
    {
        Code.putJump(0);
        ifEndJumps.peek().add(Code.pc - 2);
        fixupFalseJumps();
    }

    @Override
    public void visit(ElseOpt_epsilon elseOpt)
    {
        fixupFalseJumps();
    }

    @Override
    public void visit(Statement_if statementIf)
    {
        List<Integer> endJumps = ifEndJumps.pop();
        for (int addr : endJumps)
            Code.fixup(addr);
        condFalseJumps.pop();
        condTrueJumps.pop();
    }

    /* Switch/Case */

    private Stack<Integer> caseSkipAddr = new Stack<>();
    private Stack<Integer> pendingCaseFallThrough = new Stack<>(); // -1 = none

    private Stack<List<Integer>> breakJumps = new Stack<>(); // for both switch and for loops

    @Override
    public void visit(SwitchInit switchInit)
    {
        breakJumps.push(new ArrayList<>());
        pendingCaseFallThrough.push(-1);
    }

    @Override
    public void visit(CaseStart caseStart)
    {
        // Get the case constant value
        int caseValue;
        SyntaxNode parent = caseStart.getParent();
        if (parent instanceof CaseList_recursion)
            caseValue = ((CaseList_recursion) parent).getNumConst().getN1();
        else
            caseValue = ((CaseList_single) parent).getNumConst().getN1();

        // Duplicate switch expr value, load case constant, compare
        Code.put(Code.dup);
        Code.loadConst(caseValue);
        Code.putFalseJump(Code.eq, 0);
        caseSkipAddr.push(Code.pc - 2);

        // Match: pop the expression value (entering case body)
        Code.put(Code.pop);

        // Fix up previous case's fall-through jump to here (body start, after pop)
        int ft = pendingCaseFallThrough.pop();
        if (ft != -1)
            Code.fixup(ft);
        pendingCaseFallThrough.push(-1);
    }

    @Override
    public void visit(CaseEnd caseEnd)
    {
        // Emit fall-through jump to next case (or END if this is the last case)
        Code.putJump(0);
        pendingCaseFallThrough.pop();
        pendingCaseFallThrough.push(Code.pc - 2);

        // Fix up skip to here
        Code.fixup(caseSkipAddr.pop());
    }

    @Override
    public void visit(Statement_switch statementSwitch)
    {
        // Save last case's fall-through address
        int lastFt = pendingCaseFallThrough.pop();

        // No case matched: pop the expression value
        Code.put(Code.pop);

        // Fix up break jumps to here (past the cleanup pop)
        List<Integer> breaks = breakJumps.pop();
        for (int addr : breaks)
            Code.fixup(addr);

        // Fix up last case's fall-through to here
        if (lastFt != -1)
            Code.fixup(lastFt);
    }

    /* For Loop */

    private Stack<Integer> forCondAddr = new Stack<>();
    private Stack<Integer> forUpdateAddr = new Stack<>();
    private Stack<Integer> forBodyJumpAddr = new Stack<>();

    @Override
    public void visit(ForInit forInit)
    {
        condFalseJumps.push(new ArrayList<>());
        condTrueJumps.push(new ArrayList<>());
        breakJumps.push(new ArrayList<>());
    }

    @Override
    public void visit(ForConditionStart marker)
    {
        forCondAddr.push(Code.pc);
    }

    @Override
    public void visit(ForUpdateStart marker)
    {
        // Fix up true jumps from OR in condition
        fixupTrueJumps();

        // Jump over the update section to the body
        Code.putJump(0);
        forBodyJumpAddr.push(Code.pc - 2);

        // Loop back here after the body or a continue statement
        forUpdateAddr.push(Code.pc);
    }

    @Override
    public void visit(ForBodyStart marker)
    {
        // Jump back to condition
        Code.putJump(forCondAddr.peek());

        // Fix up jump from update to here (body start)
        Code.fixup(forBodyJumpAddr.pop());
    }

    @Override
    public void visit(Statement_for statementFor)
    {
        // Fix up jump from body to update
        Code.putJump(forUpdateAddr.peek());

        // Fix up condition false jumps to here (END)
        fixupFalseJumps();

        // Fix up break jumps to here (END)
        List<Integer> breaks = breakJumps.pop();
        for (int addr : breaks)
            Code.fixup(addr);

        forCondAddr.pop();
        forUpdateAddr.pop();
        condFalseJumps.pop();
        condTrueJumps.pop();
    }

    /* Break/Continue */

    @Override
    public void visit(Statement_break statementBreak)
    {
        Code.putJump(0);
        breakJumps.peek().add(Code.pc - 2);
    }

    @Override
    public void visit(Statement_continue statementContinue)
    {
        Code.putJump(forUpdateAddr.peek());
    }

    /* Ternary operator */

    private Stack<List<Integer>> ternaryEndJumps = new Stack<>();

    @Override
    public void visit(TernaryTrueStart marker)
    {
        fixupTrueJumps();
        ternaryEndJumps.push(new ArrayList<>());
    }

    @Override
    public void visit(TernaryFalseStart marker)
    {
        Code.putJump(0);
        ternaryEndJumps.peek().add(Code.pc - 2);

        fixupFalseJumps();
    }

    @Override
    public void visit(Expression_condition exprCond)
    {
        // Fix up end jumps to here
        List<Integer> endJumps = ternaryEndJumps.pop();
        for (int addr : endJumps)
            Code.fixup(addr);

        ternaryCondContextCreated.remove(exprCond);
        condFalseJumps.pop();
        condTrueJumps.pop();
    }
}

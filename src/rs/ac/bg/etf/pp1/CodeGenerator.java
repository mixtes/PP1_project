package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

    private int mainPC;
    private int currentDataAddr = 0;
    private Struct currentClassStruct = null;
    private Map<Struct, Integer> classVTableAddrs = new HashMap<>();
    private Map<Struct, LinkedHashMap<String, Obj>> classOwnMethods = new HashMap<>();
    private Map<Struct, LinkedHashMap<String, Obj>> classVTables = new HashMap<>();

    private int tempSlotBase = 0;
    private final int numTempSlots = 16;
    private int nextTempSlot = 0;
    private Stack<Integer> tempSlotStack = new Stack<>();
    
    private boolean designatorAlreadyLoaded = false;
    private Stack<Obj> designatorObjStack = new Stack<>();

    public CodeGenerator(int nVars)
    {
        this.tempSlotBase = nVars;
        this.currentDataAddr = nVars + numTempSlots;
    }

    public int getDataSize() 
    {
        return this.currentDataAddr;
    }

    public int getMainPC() 
    {
        return this.mainPC;
    }

    /* Designator */

    @Override
    public void visit(DesignatorName designatorName)
    {
        Obj baseObj = designatorName.obj;
        designatorObjStack.push(baseObj);

        Designator parent = (Designator) designatorName.getParent();
        boolean suffixIsEpsilon = parent.getDesignatorSuffix() instanceof DesignatorSuffix_epsilon;

        // Implicit this: bare field access inside class method needs 'this' on stack
        if (baseObj.getKind() == Obj.Fld)
        {
            Code.put(Code.load_n); // load_0 = this
        }

        if (!suffixIsEpsilon && baseObj.getKind() != Obj.Type)
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
        if (memberObj == null)
        {
            // Member not found — semantic analysis should have caught this;
            // push a decoy to keep the stack balanced and bail out.
            designatorObjStack.set(designatorObjStack.size() - 1, new Obj(Obj.Var, suffix.getI2(), Tab.noType));
            return;
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
        if (methodTypeAndName.getParent() instanceof AbstractMethodDecl)
            return;

        if ("main".equals(methodTypeAndName.getI1()))
            this.mainPC = Code.pc;

        methodTypeAndName.obj.setAdr(Code.pc);

        if (currentClassStruct != null)
        {
            classOwnMethods
                .computeIfAbsent(currentClassStruct, k -> new LinkedHashMap<>())
                .put(methodTypeAndName.getI1(), methodTypeAndName.obj);
        }

        Code.put(Code.enter);
        Code.put(methodTypeAndName.obj.getLevel());
        Code.put(methodTypeAndName.obj.getLocalSymbols().size());

        if ("main".equals(methodTypeAndName.getI1()))
        {
            generateVTableInit();
        }
    }

    @Override
    public void visit(MethodTypeAndName_type methodTypeAndName)
    {
        if (methodTypeAndName.getParent() instanceof AbstractMethodDecl)
            return;

        methodTypeAndName.obj.setAdr(Code.pc);

        if (currentClassStruct != null)
        {
            classOwnMethods
                .computeIfAbsent(currentClassStruct, k -> new LinkedHashMap<>())
                .put(methodTypeAndName.getI2(), methodTypeAndName.obj);
        }

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
        Struct exprType = statement.getExpression().struct;

        if (exprType.getKind() == Struct.Array)
        {
            // Array printing
            Struct elemType = exprType.getElemType();

            if (statement.getNumConstOpt() instanceof NumConstOpt_exists)
            {
                // print(arr, N) — print arr[N]
                NumConstOpt_exists numConstOpt = (NumConstOpt_exists) statement.getNumConstOpt();
                int index = numConstOpt.getNumConst().getN1();

                // stack: [arr_addr]
                Code.loadConst(index);
                if (elemType.equals(Tab.charType))
                    Code.put(Code.baload);
                else
                    Code.put(Code.aload);
                Code.loadConst(0);
                if (elemType.equals(Tab.charType))
                    Code.put(Code.bprint);
                else
                    Code.put(Code.print);
            }
            else
            {
                // print(arr) — print all elements
                // stack: [arr_addr]
                // Save arr_addr in a temp global slot
                int arrSlot = tempSlotBase + nextTempSlot++;
                int idxSlot = tempSlotBase + nextTempSlot++;

                // Store arr_addr
                Code.put(Code.dup);               // [arr_addr, arr_addr]
                Code.put(Code.putstatic);
                Code.put2(arrSlot);                // [arr_addr]  (saved to slot)

                // Get length, save for comparison
                Code.put(Code.arraylength);        // [len]

                // Store i = 0
                Code.loadConst(0);                 // [len, 0]
                Code.put(Code.putstatic);
                Code.put2(idxSlot);                // [len]  (i=0 saved)

                // LOOP_TOP:
                int loopTop = Code.pc;
                // stack: [len]
                Code.put(Code.getstatic);
                Code.put2(idxSlot);                // [len, i]

                // Compare: stack is [len, i]. putFalseJump(gt) tests len > i, jumps when false (len <= i)
                Code.putFalseJump(Code.gt, 0);     // if !(len > i) goto END
                int endPatch = Code.pc - 2;
                // stack: []

                // Load arr[i]
                Code.put(Code.getstatic);
                Code.put2(arrSlot);                // [arr_addr]
                Code.put(Code.getstatic);
                Code.put2(idxSlot);                // [arr_addr, i]
                if (elemType.equals(Tab.charType))
                    Code.put(Code.baload);
                else
                    Code.put(Code.aload);          // [arr[i]]

                // Print
                Code.loadConst(0);                 // [arr[i], 0]
                if (elemType.equals(Tab.charType))
                    Code.put(Code.bprint);
                else
                    Code.put(Code.print);          // []

                // i++
                Code.put(Code.getstatic);
                Code.put2(idxSlot);                // [i]
                Code.loadConst(1);                 // [i, 1]
                Code.put(Code.add);                // [i+1]
                Code.put(Code.putstatic);
                Code.put2(idxSlot);                // []  (i+1 saved)

                // Reload len and jump to top
                Code.put(Code.getstatic);
                Code.put2(arrSlot);                // [arr_addr]
                Code.put(Code.arraylength);        // [len]
                Code.putJump(loopTop);

                // END:
                Code.fixup(endPatch);

                nextTempSlot -= 2;
            }
        }
        else
        {
            // Scalar printing (original behavior)
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

            if (exprType.equals(Tab.charType))
                Code.put(Code.bprint);
            else
                Code.put(Code.print);
        }
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
        String methodName = methodObj.getName();

        if ("ord".equals(methodName) || "chr".equals(methodName)) {
            Code.put(Code.pop); // discard result since called as statement
            return;
        }
        if ("len".equals(methodName)) {
            Code.put(Code.arraylength);
            Code.put(Code.pop);
            return;
        }

        if (isVirtualMethod(methodObj))
        {
            int slot = tempSlotStack.pop();
            nextTempSlot--;

            Code.put(Code.getstatic);
            Code.put2(slot);

            Code.put(Code.getfield);
            Code.put2(0);

            Code.put(Code.invokevirtual);

            for (char c : methodName.toCharArray())
            {
                Code.put4((int) c);
            }

            Code.put4(-1);
        }
        else
        {
            int offset = methodObj.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }

        if (methodObj.getType() != Tab.noType)
            Code.put(Code.pop);
    }
    
    @Override
    public void visit(ActParsListOpener actParsListOpener)
    {
        Obj methodObj = null;
        Designator designator = null;

        if (actParsListOpener.getParent().getParent() instanceof DesignatorStatement_method)
        {
            DesignatorStatement_method dsm =
                    (DesignatorStatement_method) actParsListOpener.getParent().getParent();
            methodObj = dsm.getDesignator().obj;
            designator = dsm.getDesignator();
        }
        else if (actParsListOpener.getParent().getParent() instanceof Factor_designator_method)
        {
            Factor_designator_method fdm =
                    (Factor_designator_method) actParsListOpener.getParent().getParent();
            methodObj = fdm.getDesignator().obj;
            designator = fdm.getDesignator();
        }

        if (methodObj != null && isVirtualMethod(methodObj))
        {
            // Implicit this: bare method call inside class method (no explicit receiver)
            if (designator != null && designator.getDesignatorSuffix() instanceof DesignatorSuffix_epsilon)
            {
                Code.put(Code.load_n); // load_0 = this
            }

            Code.put(Code.dup);

            int slot = tempSlotBase + nextTempSlot;
            nextTempSlot++;
            tempSlotStack.push(slot);

            Code.put(Code.putstatic);
            Code.put2(slot);
        }
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
        String methodName = methodObj.getName();

        if ("ord".equals(methodName) || "chr".equals(methodName))
        {
            return;
        }
        if ("len".equals(methodName))
        {
            Code.put(Code.arraylength);
            return;
        }

        if (isVirtualMethod(methodObj))
        {
            int slot = tempSlotStack.pop();
            nextTempSlot--;

            Code.put(Code.getstatic);
            Code.put2(slot);

            Code.put(Code.getfield);
            Code.put2(0);

            Code.put(Code.invokevirtual);

            for (char c : methodName.toCharArray())
            {
                Code.put4((int) c);
            }

            Code.put4(-1);
        }
        else
        {
            int offset = methodObj.getAdr() - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }
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
        Code.put2((numFields + 1) * 4);

        Code.put(Code.dup);

        int vtableAddr = classVTableAddrs.get(factor_object.struct);
        Code.loadConst(vtableAddr);

        Code.put(Code.putfield);
        Code.put2(0);
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
            if (current instanceof Statement_if || current instanceof Statement_for
                || current instanceof Statement_while || current instanceof Statement_do_while)
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
        boolean isForeach = forInit.getParent() instanceof Statement_foreach;
        if (isForeach)
        {
            breakJumps.push(new ArrayList<>());
            continueTargetAddr.push(-1); // deferred
            continueJumps.push(new ArrayList<>());
        }
        else
        {
            condFalseJumps.push(new ArrayList<>());
            condTrueJumps.push(new ArrayList<>());
            breakJumps.push(new ArrayList<>());
        }
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
        continueTargetAddr.push(Code.pc);
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
        continueTargetAddr.pop();
        condFalseJumps.pop();
        condTrueJumps.pop();
    }
    
    /* While loop */
    
    private Stack<Integer> whileCondAddr = new Stack<>();
    
    @Override
    public void visit(WhileInit whileInit)
    {
    	condFalseJumps.push(new ArrayList<>());
        condTrueJumps.push(new ArrayList<>());
        breakJumps.push(new ArrayList<>());
    }
    
    @Override
    public void visit(WhileConditionStart whileConditionStart)
    {
    	whileCondAddr.push(Code.pc);
    	continueTargetAddr.push(Code.pc);
    }
    
    @Override
    public void visit(WhileBodyStart whileBodyStart)
    {
    	fixupTrueJumps();
    }
    
    @Override
    public void visit(Statement_while statementWhile)
    {
    	Code.putJump(whileCondAddr.peek());
    	
    	fixupFalseJumps();
    	
    	List<Integer> breaks = breakJumps.pop();
        for (int addr : breaks)
            Code.fixup(addr);
        
        whileCondAddr.pop();
        continueTargetAddr.pop();
        condFalseJumps.pop();
        condTrueJumps.pop();
    }
    
    /* Do-While loop */

    private Stack<Integer> doWhileBodyAddr = new Stack<>();

    @Override
    public void visit(DoWhileInit doWhileInit)
    {
        breakJumps.push(new ArrayList<>());
        continueTargetAddr.push(-1); // deferred — body is emitted before condition
        continueJumps.push(new ArrayList<>());
        doWhileBodyAddr.push(Code.pc);
    }

    @Override
    public void visit(DoWhileCondStart doWhileCondStart)
    {
        condFalseJumps.push(new ArrayList<>());
        condTrueJumps.push(new ArrayList<>());

        // Fix up deferred continue jumps to here (condition start)
        List<Integer> continues = continueJumps.peek();
        for (int addr : continues)
            Code.fixup(addr);
        continues.clear();
    }

    @Override
    public void visit(Statement_do_while statementDoWhile)
    {
        // Fix up OR short-circuit true jumps
        fixupTrueJumps();

        // Condition was true — jump back to body
        Code.putJump(doWhileBodyAddr.peek());

        // Condition was false — exit loop
        fixupFalseJumps();

        // Fix up break jumps to here (END)
        List<Integer> breaks = breakJumps.pop();
        for (int addr : breaks)
            Code.fixup(addr);

        doWhileBodyAddr.pop();
        continueTargetAddr.pop();
        continueJumps.pop();
        condFalseJumps.pop();
        condTrueJumps.pop();
    }

    /* ForEach */

    private Stack<int[]> foreachSlots = new Stack<>(); // [arrSlot, idxSlot]
    private Stack<Integer> foreachCondAddr = new Stack<>();
    private Stack<Integer> foreachEndPatchAddr = new Stack<>();

    // ForInit is shared between for and foreach.
    // For foreach, the setup is done in ForeachBodyStart instead.

    @Override
    public void visit(ForeachBodyStart foreachBodyStart)
    {
        // At this point both Designators have been visited (iter var and array)
        // Get the array and iterator from the parent Statement_foreach
        Statement_foreach parent = (Statement_foreach) foreachBodyStart.getParent();
        Obj arrObj = parent.getDesignator1().obj;
        Obj iterVar = parent.getDesignator().obj;

        int arrSlot = tempSlotBase + nextTempSlot++;
        int idxSlot = tempSlotBase + nextTempSlot++;
        foreachSlots.push(new int[]{arrSlot, idxSlot});

        // Load array address and save it
        Code.load(arrObj);
        Code.put(Code.putstatic);
        Code.put2(arrSlot);

        // Init index = 0
        Code.loadConst(0);
        Code.put(Code.putstatic);
        Code.put2(idxSlot);

        // CONDITION: if index >= arr.length goto END
        int condAddr = Code.pc;
        foreachCondAddr.push(condAddr);

        Code.put(Code.getstatic);
        Code.put2(arrSlot);
        Code.put(Code.arraylength);  // [len]
        Code.put(Code.getstatic);
        Code.put2(idxSlot);          // [len, idx]
        Code.putFalseJump(Code.gt, 0); // if !(len > idx) goto END
        foreachEndPatchAddr.push(Code.pc - 2);

        // iter = arr[index]
        Code.put(Code.getstatic);
        Code.put2(arrSlot);
        Code.put(Code.getstatic);
        Code.put2(idxSlot);
        if (arrObj.getType().getElemType() == Tab.charType)
            Code.put(Code.baload);
        else
            Code.put(Code.aload);
        Code.store(iterVar);
    }

    @Override
    public void visit(Statement_foreach statementForeach)
    {
        int[] slots = foreachSlots.pop();
        int idxSlot = slots[1];

        // Fix up continue jumps to here (index++ section)
        List<Integer> continues = continueJumps.pop();
        for (int addr : continues)
            Code.fixup(addr);
        continueTargetAddr.pop();

        // index++
        Code.put(Code.getstatic);
        Code.put2(idxSlot);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.put(Code.putstatic);
        Code.put2(idxSlot);

        // Jump back to condition
        Code.putJump(foreachCondAddr.pop());

        // END: fix up condition false jump
        Code.fixup(foreachEndPatchAddr.pop());

        // Fix up break jumps
        List<Integer> breaks = breakJumps.pop();
        for (int addr : breaks)
            Code.fixup(addr);

        nextTempSlot -= 2;
    }

    /* Break/Continue */

    @Override
    public void visit(Statement_break statementBreak)
    {
        Code.putJump(0);
        breakJumps.peek().add(Code.pc - 2);
    }

    private Stack<Integer> continueTargetAddr = new Stack<>(); // -1 means deferred (foreach)
    private Stack<List<Integer>> continueJumps = new Stack<>(); // for foreach deferred continues

    @Override
    public void visit(Statement_continue statementContinue)
    {
        int target = continueTargetAddr.peek();
        if (target == -1)
        {
            // Deferred continue (foreach) - record for later fixup
            Code.putJump(0);
            continueJumps.peek().add(Code.pc - 2);
        }
        else
        {
            Code.putJump(target);
        }
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

    /* Class */

    @Override
    public void visit(ClassName className)
    {
        this.currentClassStruct = className.obj.getType();
    }

    @Override
    public void visit(AbsClassName className)
    {
        this.currentClassStruct = className.obj.getType();
    }

    @Override
    public void visit(ClassDecl classDecl)
    {
        LinkedHashMap<String, Obj> vtable = buildVTableMap(currentClassStruct);
        classVTables.put(currentClassStruct, vtable);

        int vtableStartAddr = currentDataAddr;
        classVTableAddrs.put(currentClassStruct, vtableStartAddr);

        int vtableSize = 0;

        for (String methodName : vtable.keySet())
        {
            vtableSize += methodName.length() + 1; // name chars + -1 terminator
            vtableSize += 1; // method address
        }

        vtableSize += 1; // -2 VFT terminator

        currentDataAddr += vtableSize;

        this.currentClassStruct = null;
    }

    @Override
    public void visit(AbstractClassDecl classDecl)
    {
        this.currentClassStruct = null;
    }

    private void generateVTableInit()
    {
        for (Map.Entry<Struct, LinkedHashMap<String, Obj>> entry : classVTables.entrySet())
        {
            Struct classStruct = entry.getKey();
            LinkedHashMap<String, Obj> vtable = entry.getValue();
            int addr = classVTableAddrs.get(classStruct);

            for (Map.Entry<String, Obj> methodEntry : vtable.entrySet())
            {
                String methodName = methodEntry.getKey();
                Obj methodObj = methodEntry.getValue();

                // Write method name characters
                for (char c : methodName.toCharArray())
                {
                    Code.loadConst((int) c);
                    Code.put(Code.putstatic);
                    Code.put2(addr++);
                }

                // Write name terminator (-1)
                Code.loadConst(-1);
                Code.put(Code.putstatic);
                Code.put2(addr++);

                // Write method address
                Code.loadConst(methodObj.getAdr());
                Code.put(Code.putstatic);
                Code.put2(addr++);
            }

            // Write VFT terminator (-2)
            Code.loadConst(-2);
            Code.put(Code.putstatic);
            Code.put2(addr++);
        }
    }

    private boolean isVirtualMethod(Obj obj)
    {
        if (obj.getKind() != Obj.Meth) return false;
        // A method is virtual if its locals contain "this" (the first local)
        for (Obj local : obj.getLocalSymbols())
        {
            if ("this".equals(local.getName()))
            {
                return true;
            }
        }
        return false;
    }

    private LinkedHashMap<String, Obj> buildVTableMap(Struct struct)
    {
        LinkedHashMap<String, Obj> vtable = new LinkedHashMap<>();
        Struct parentStruct = struct.getElemType();

        if (parentStruct != null)
        {
            vtable.putAll(buildVTableMap(parentStruct));
        }
        
        LinkedHashMap<String, Obj> ownMethods = classOwnMethods.get(struct);
        if (ownMethods != null)
        {
            for (Map.Entry<String, Obj> entry : ownMethods.entrySet())
            {
                vtable.put(entry.getKey(), entry.getValue());
            }
        }
        
        return vtable;
    }

}

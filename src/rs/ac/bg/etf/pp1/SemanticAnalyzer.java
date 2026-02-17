package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor {
	
	private Obj mainMethod = null;
	
	private boolean errorDetected = false;	
	Logger log = Logger.getLogger(getClass());
	private Obj myProgram;
	int nVars;

	private Obj currentMethod;
	private boolean methodHasReturnStmt = false;

	private Struct currentType;

	private Struct currentEnumStructNode;
	private int previousEnumValue;
	private HashSet<Integer> currentEnumValues;

	private Obj currentClass;
	private Struct currentClassStructNode;
	private int currentFieldAddress;

	private HashSet<Struct> abstractClassStructNodes = new HashSet<>();
	private HashMap<String, Obj> currentClassMembersMap = null;

	private int c_val;
	private Struct c_type;
	private Struct boolType = Tab.find("bool").getType();

	private int loopDepth = 0;
	private int switchDepth = 0;
	private Stack<HashSet<Integer>> caseLabelsStack = new Stack<>();

	private ArrayList<Struct> methodCallActualParams = null;
	private Stack<ArrayList<Struct>> methodCallActualParamsLists = new Stack<>();

	/* log */

	public void report_error(String message, SyntaxNode info)
	{
		this.errorDetected = true;
		StringBuilder sb = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			sb.append(" On line number ").append(line);
		this.log.error(sb.toString());
	}
	
	public void report_info(String message, SyntaxNode info)
	{
		StringBuilder sb = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			sb.append(" On line number ").append(line);
		this.log.info(sb.toString());
	}
	
	public boolean success()
	{
		return !this.errorDetected;
	}
	
	/* symbol table */
	
	/* ProgramName */ 
	
	@Override
	public void visit(ProgramName programName)
	{
		myProgram = Tab.insert(Obj.Prog, programName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	/* Program */
	
	@Override
	public void visit(Program program)
	{
		this.nVars = Tab.currentScope().getnVars();
		Tab.chainLocalSymbols(myProgram);
		Tab.closeScope();
		
		this.myProgram = null;
		
		if (this.mainMethod == null)
			report_error("Program is missing entry point (method main).", program);
		else if (this.mainMethod.getLevel() > 0)
			report_error("Main method can't have formal parameters.", program);
	}
	
	/* ConstDecl */
	
	@Override
	public void visit(ConstDecl constDecl)
	{
		Obj constant = Tab.find(constDecl.getI1());
		if (constant != Tab.noObj)
		{
			report_error("Multiple definitions of constant: " + constDecl.getI1(), constDecl);
		}
		else
		{
			if (this.c_type.assignableTo(this.currentType))
			{
				constant = Tab.insert(Obj.Con, constDecl.getI1(), this.currentType);
				constant.setAdr(this.c_val);
			}
			else
			{
				report_error("Non assignable constant types: " + constDecl.getI1(), constDecl);
			}
		}
	}
	
	/* Type */
	
	@Override
	public void visit(Type type)
	{
		Obj typeObj = Tab.find(type.getI1());
		if (typeObj == Tab.noObj)
		{
			report_error("Type: " + type.getI1() + " doesn't exist at this point of parsing.", type);
			this.currentType = Tab.noType;
		}
		else if (typeObj.getKind() != Obj.Type) 
		{
			report_error("Type: " + type.getI1() + " isn't a type.", type);
			this.currentType = Tab.noType;
		}
		else
		{
			this.currentType = typeObj.getType();
		}
	}
	
	/* Constant */
	
	@Override
	public void visit(Constant_num constant_num)
	{
		this.c_val = constant_num.getN1();
		this.c_type = Tab.intType;
	}
	
	@Override
	public void visit(Constant_chr constant_chr)
	{
		this.c_val = constant_chr.getC1();
		this.c_type = Tab.charType;
	}
	
	@Override
	public void visit(Constant_boo constant_boo)
	{
		this.c_val = constant_boo.getB1();
		this.c_type = this.boolType;
	}
	
	/* VarDecl */
	
	public void visit(VarDecl_single varDecl)
	{
		Obj variable = null;

		if (this.currentMethod == null && this.currentClass == null)
			variable = Tab.find(varDecl.getI1());
		else
			variable = Tab.currentScope().findSymbol(varDecl.getI1());

		if (variable != null && variable != Tab.noObj)
		{
			report_error("Multiple definitions of variable: " + varDecl.getI1(), varDecl);
		}
		else
		{
			if (this.currentClass != null && this.currentMethod == null)
			{
				variable = Tab.insert(Obj.Fld, varDecl.getI1(), this.currentType);
				variable.setAdr(this.currentFieldAddress++);
				variable.setLevel(1);
				if (this.currentClassMembersMap != null)
					this.currentClassMembersMap.put(varDecl.getI1(), variable);
			}
			else
			{
				variable = Tab.insert(Obj.Var, varDecl.getI1(), this.currentType);
				if (this.currentClass != null && this.currentMethod != null)
					variable.setLevel(2);
				else if (this.currentMethod != null)
					variable.setLevel(1);
				else
					variable.setLevel(0);
			}
		}
	}

	public void visit(VarDecl_array varDecl)
	{
		Obj variable = null;

		if (this.currentMethod == null && this.currentClass == null)
			variable = Tab.find(varDecl.getI1()); // either finds symbol or noObj
		else
			variable = Tab.currentScope().findSymbol(varDecl.getI1()); // either finds symbol or null

		if (variable != null && variable != Tab.noObj)
		{
			report_error("Multiple definitions of variable: " + varDecl.getI1(), varDecl);
		}
		else
		{
			if (this.currentClass != null && this.currentMethod == null)
			{
				variable = Tab.insert(Obj.Fld, varDecl.getI1(), new Struct(Struct.Array, this.currentType));
				variable.setAdr(this.currentFieldAddress++);
				variable.setLevel(1);
				if (this.currentClassMembersMap != null)
					this.currentClassMembersMap.put(varDecl.getI1(), variable);
			}
			else
			{
				variable = Tab.insert(Obj.Var, varDecl.getI1(), new Struct(Struct.Array, this.currentType));
				if (this.currentClass != null && this.currentMethod != null)
					variable.setLevel(2);
				else if (this.currentMethod != null)
					variable.setLevel(1);
				else
					variable.setLevel(0);
			}
		}
	}

	/* MethodDecl */

	private boolean isInheritedMethod(String methodName)
	{
		Struct parentStruct = this.currentClassStructNode.getElemType();
		if (parentStruct != null && parentStruct != Tab.noType)
		{
			for (Obj member : parentStruct.getMembers())
			{
				if (member.getKind() == Obj.Meth && member.getName().equals(methodName))
					return true;
			}
		}
		return false;
	}

	@Override
	public void visit(MethodTypeAndName_void methodTypeAndName)
	{
		String methodName = methodTypeAndName.getI1();

		if (this.currentClass != null)
		{
			Obj existing = Tab.currentScope().findSymbol(methodName);
			if (existing != null && existing.getKind() == Obj.Meth && !isInheritedMethod(methodName))
			{
				report_error("Multiple definitions of method: " + methodName, methodTypeAndName);
			}
		}
		else
		{
			Obj existing = Tab.find(methodName);
			if (existing != Tab.noObj)
			{
				report_error("Multiple definitions of method: " + methodName, methodTypeAndName);
			}
		}

		this.currentMethod = Tab.insert(Obj.Meth, methodName, Tab.noType);
		methodTypeAndName.obj = this.currentMethod;

		if (methodName.equalsIgnoreCase("main"))
			this.mainMethod = this.currentMethod;

		Tab.openScope();

		if (this.currentClass != null)
		{
			Obj thisParam = Tab.insert(Obj.Var, "this", this.currentClassStructNode);
			thisParam.setLevel(2);
			thisParam.setFpPos(1);
			this.currentMethod.setLevel(1);
			if (this.currentClassMembersMap != null)
				this.currentClassMembersMap.put(methodName, this.currentMethod);
		}
	}

	@Override
	public void visit(MethodTypeAndName_type methodTypeAndName)
	{
		String methodName = methodTypeAndName.getI2();

		if (this.currentClass != null)
		{
			Obj existing = Tab.currentScope().findSymbol(methodName);
			if (existing != null && existing.getKind() == Obj.Meth && !isInheritedMethod(methodName))
			{
				report_error("Multiple definitions of method: " + methodName, methodTypeAndName);
			}
		}
		else
		{
			Obj existing = Tab.find(methodName);
			if (existing != Tab.noObj)
			{
				report_error("Multiple definitions of method: " + methodName, methodTypeAndName);
			}
		}

		this.currentMethod = Tab.insert(Obj.Meth, methodName, this.currentType);
		methodTypeAndName.obj = this.currentMethod;
		
		Tab.openScope();

		if (this.currentClass != null)
		{
			Obj thisParam = Tab.insert(Obj.Var, "this", this.currentClassStructNode);
			thisParam.setLevel(2);
			thisParam.setFpPos(1);
			this.currentMethod.setLevel(1);
			if (this.currentClassMembersMap != null)
				this.currentClassMembersMap.put(methodName, this.currentMethod);
		}
	}
	
	@Override
	public void visit(FormPar_single formPar)
	{
		Obj parameter = Tab.currentScope().findSymbol(formPar.getI2());
		
		if (parameter != null && parameter != Tab.noObj)
		{
			report_error("Multiple definitions of formal parameter: " + formPar.getI2(), formPar);
		}
		else
		{
			parameter = Tab.insert(Obj.Var, formPar.getI2(), this.currentType);
			parameter.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);

			if (this.currentClass != null)
				parameter.setLevel(2);
			else
				parameter.setLevel(1);
		}
	}
	
	@Override
	public void visit(FormPar_array formPar)
	{
		Obj parameter = Tab.currentScope().findSymbol(formPar.getI2());
		
		if (parameter != null && parameter != Tab.noObj)
		{
			report_error("Multiple definitions of formal parameter: " + formPar.getI2(), formPar);
		}
		else
		{
			parameter = Tab.insert(Obj.Var, formPar.getI2(), new Struct(Struct.Array, this.currentType));
			parameter.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);

			if (this.currentClass != null)
				parameter.setLevel(2);
			else
				parameter.setLevel(1);
		}
	}
	
	@Override
	public void visit(MethodDecl methodDecl)
	{
		this.currentMethod.setFpPos(0);
		Tab.chainLocalSymbols(this.currentMethod);
		Tab.closeScope();

		if (this.currentMethod.getType() != Tab.noType && !this.methodHasReturnStmt)
		{
			report_error("Non-void method must have a return statement.", methodDecl);
		}
		this.methodHasReturnStmt = false;

		this.currentMethod = null;
	}

	@Override
	public void visit(AbstractMethodDecl abstractMethodDecl)
	{
		this.currentMethod.setFpPos(1);
		Tab.chainLocalSymbols(this.currentMethod);
		Tab.closeScope();

		this.currentMethod = null;
	}

	/* EnumDecl */
	
	@Override
	public void visit(EnumName enumName)
	{
		Obj enumType = Tab.find(enumName.getI1());
		this.currentEnumStructNode = new Struct(Struct.Enum);
		if (enumType != Tab.noObj)
		{
			report_error("Multiple definitions of enum type: " + enumName.getI1(), enumName);
		}
		else
		{
			Tab.insert(Obj.Type, enumName.getI1(), this.currentEnumStructNode);
		}
		Tab.openScope();
		this.currentEnumValues = new HashSet<>();
		this.previousEnumValue = -1;
	}

	@Override
	public void visit(EnumDeclList enumDeclList)
	{
		Tab.chainLocalSymbols(this.currentEnumStructNode);
		Tab.closeScope();
		
		this.currentEnumValues = null;
		this.currentEnumStructNode = null;
	}

	private boolean isEnumMemberValueUnique(int value)
	{
		for (int i = 0; i < this.currentEnumValues.size(); i++)
		{
			if (this.currentEnumValues.contains(value))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void visit(EnumIDT_asg enumMember)
	{
		int enumMemberValue = enumMember.getN2();
		String enumMemberName = enumMember.getI1();

		boolean valueUnique = isEnumMemberValueUnique(enumMemberValue);

		if (Tab.currentScope().findSymbol(enumMemberName) != null)
		{
			report_error("Multiple definitions of enum member: " + enumMemberName, enumMember);
			return;
		}
		else if (!valueUnique)
		{
			report_error("Enum members must be unique.", enumMember);
			return;
		}
		else
		{
			Obj newEnumMember = Tab.insert(Obj.Con, enumMemberName, this.currentEnumStructNode);
			newEnumMember.setAdr(enumMemberValue);
			this.previousEnumValue = enumMemberValue;
			this.currentEnumValues.add(enumMemberValue);
		}
	}

	@Override
	public void visit(EnumIDT_no_asg enumMember)
	{
		String enumMemberName = enumMember.getI1();
		int enumMemberValue = this.previousEnumValue + 1;

		if (Tab.currentScope().findSymbol(enumMemberName) != null)
		{
			report_error("Multiple definitions of enum member: " + enumMemberName, enumMember);
			return;
		}
		else
		{
			boolean valueUnique = isEnumMemberValueUnique(enumMemberValue);
			if (!valueUnique)
			{
				report_error("Enum members must be unique.", enumMember);
				return;
			}

			Obj newEnumMember = Tab.insert(Obj.Con, enumMemberName, this.currentEnumStructNode);
			newEnumMember.setAdr(enumMemberValue);
			this.previousEnumValue = enumMemberValue;
			this.currentEnumValues.add(enumMemberValue);
		}
	}

	/* AbstractClassDecl */

	@Override
	public void visit(AbsClassName absClassName)
	{
		Obj absClassType = Tab.find(absClassName.getI1());
		this.currentClassStructNode = new Struct(Struct.Class);
		if (absClassType != Tab.noObj)
		{
			report_error("Multiple definitions of abstract class type: " + absClassName.getI1(), absClassName);
			this.currentClass = new Obj(Obj.Type, absClassName.getI1(), this.currentClassStructNode);
		}
		else
		{
			this.currentClass = Tab.insert(Obj.Type, absClassName.getI1(), this.currentClassStructNode);
			this.abstractClassStructNodes.add(this.currentClassStructNode);
		}
		Tab.openScope();
		this.currentFieldAddress = 1;
		this.currentClassMembersMap = new HashMap<>();

		absClassName.obj = this.currentClass;
	}

	@Override
	public void visit(AbstractClassDecl abstractClassDecl)
	{
		Tab.chainLocalSymbols(this.currentClassStructNode);
		Tab.closeScope();

		this.currentClass = null;
		this.currentClassStructNode = null;
		this.currentClassMembersMap = null;
	}

	/* ClassDecl */

	@Override
	public void visit(ClassName className)
	{
		Obj classType = Tab.find(className.getI1());
		this.currentClassStructNode = new Struct(Struct.Class);
		if (classType != Tab.noObj) {
			report_error("Multiple definitions of class type: " + className.getI1(), className);
			this.currentClass = new Obj(Obj.Type, className.getI1(), this.currentClassStructNode);
		} else {
			this.currentClass = Tab.insert(Obj.Type, className.getI1(), this.currentClassStructNode);
		}
		Tab.openScope();
		this.currentFieldAddress = 1;
		this.currentClassMembersMap = new HashMap<>();

		className.obj = this.currentClass;
	}

	@Override
	public void visit(ClassDecl classDecl)
	{
		for (Obj member : Tab.currentScope().values())
		{
			if (member.getKind() == Obj.Meth && member.getFpPos() == 1)
			{
				report_error("Non-abstract class must implement abstract method: " + member.getName(), classDecl);
			}
		}
		Tab.chainLocalSymbols(this.currentClassStructNode);
		Tab.closeScope();

		this.currentClass = null;
		this.currentClassStructNode = null;
		this.currentClassMembersMap = null;
	}

	@Override
	public void visit(ExtendsOpt_exists extedsOpt)
	{
		String parentClassName = extedsOpt.getType().getI1();
		Obj parentClassObj = Tab.find(parentClassName);
		if (parentClassObj == Tab.noObj)
		{
			report_error("Parent class: " + parentClassName + " doesn't exist.", extedsOpt);
			this.currentClassStructNode.setElementType(Tab.noType);
		}
		else if (parentClassObj.getKind() != Obj.Type || parentClassObj.getType().getKind() != Struct.Class)
		{
			report_error("Parent class: " + parentClassName + " isn't a class.", extedsOpt);
			this.currentClassStructNode.setElementType(Tab.noType);
		}
		else
		{
			this.currentClassStructNode.setElementType(parentClassObj.getType());

			// copy parent class members into the beginning of the current class scope
			Collection<Obj> parentMembers = parentClassObj.getType().getMembers();
			if (parentMembers != null)
			{
				for (Obj member : parentMembers)
				{
					Obj copy = Tab.insert(member.getKind(), member.getName(), member.getType());
					copy.setAdr(member.getAdr());
					copy.setLevel(member.getLevel());
					copy.setFpPos(member.getFpPos());
					if (member.getKind() == Obj.Fld && member.getAdr() >= this.currentFieldAddress)
					{
						this.currentFieldAddress = member.getAdr() + 1;
					}
					if (member.getKind() == Obj.Meth)
					{
						Tab.openScope();
						for (Obj local : member.getLocalSymbols())
						{
							Obj localCopy = Tab.insert(local.getKind(), local.getName(), local.getType());
							localCopy.setAdr(local.getAdr());
							localCopy.setLevel(local.getLevel());
							localCopy.setFpPos(local.getFpPos());
						}
						Tab.chainLocalSymbols(copy);
						Tab.closeScope();
					}
					if (this.currentClassMembersMap != null)
						this.currentClassMembersMap.put(member.getName(), copy);
				}
			}
		}
	}

	/* context conditions */

	private boolean isAssignable_deep(Struct src, Struct dest)
	{
		if (src.assignableTo(dest)) return true;
		// check class inheritance: walk up parent chain
		if (src.getKind() == Struct.Class && dest.getKind() == Struct.Class)
		{
			Struct parent = src.getElemType();
			while (parent != null && parent != Tab.noType)
			{
				if (parent == dest) return true;
				parent = parent.getElemType();
			}
		}
		return false;
	}

	/* Factor */

	@Override
	public void visit(Factor_designator_base factor)
	{
		factor.struct = factor.getDesignator().obj.getType();
	}

	@Override
	public void visit(Factor_designator_method factor)
	{
		Obj methodObj = factor.getDesignator().obj;
		if (methodObj.getKind() != Obj.Meth)
		{
			report_error("'" + methodObj.getName() + "' is not a method.", factor);
			factor.struct = Tab.noType;
		}
		else
		{
			ArrayList<Struct> formalParams = new ArrayList<>();
			int numFormalParams = methodObj.getLevel();
			int count = 0;
			for (Obj localSym : methodObj.getLocalSymbols()) {
				if (count >= numFormalParams) break;
				if (localSym.getKind() == Obj.Var) {
					if (!localSym.getName().equals("this")) {
						formalParams.add(localSym.getType());
					}
					count++;
				}
			}

			try
			{
				if (formalParams.size() != this.methodCallActualParams.size())
				{
					throw new Exception("Number of actual parameters doesn't match the number of formal parameters.");
				}

				for (int i = 0; i < formalParams.size(); i++)
				{
					if (!isAssignable_deep(this.methodCallActualParams.get(i), formalParams.get(i)))
					{
						throw new Exception("Actual parameter " + (i + 1) + " is not compatible with formal parameter.");
					}
				}
			}
			catch (Exception e)
			{
				report_error("Factor_designator_method exception: " + e.getMessage(), factor);
				factor.struct = Tab.noType;
			}

			factor.struct = methodObj.getType();
		}
	}

	@Override
	public void visit(Factor_constant_chr factor)
	{
		factor.struct = Tab.charType;
	}

	@Override
	public void visit(Factor_constant_num factor)
	{
		factor.struct = Tab.intType;
	}

	@Override
	public void visit(Factor_constant_boo factor)
	{
		factor.struct = this.boolType;
	}

	@Override
	public void visit(Factor_array factor)
	{
		if (factor.getExpression().struct != Tab.intType && factor.getExpression().struct.getKind() != Struct.Enum)
		{
			report_error("Array size must be of int type.", factor);
		}
		factor.struct = new Struct(Struct.Array, this.currentType);
	}

	@Override
	public void visit(Factor_object factor)
	{
		if (this.currentType.getKind() != Struct.Class)
		{
			report_error("Can only instantiate class types with 'new'.", factor);
			factor.struct = Tab.noType;
		}
		else if (this.abstractClassStructNodes.contains(this.currentType))
		{
			report_error("Cannot instantiate abstract class.", factor);
			factor.struct = Tab.noType;
		}
		else
		{
			factor.struct = this.currentType;
		}
	}

	@Override
	public void visit(Factor_expression factor)
	{
		factor.struct = factor.getExpression().struct;
	}

	@Override
	public void visit(ExtensionFactor extFactor)
	{
		if (extFactor.getMinusOpt() instanceof MinusOpt_minus)
		{
			if (extFactor.getFactor().struct == Tab.intType || extFactor.getFactor().struct.getKind() == Struct.Enum)
			{
				extFactor.struct = Tab.intType;
			}
			else
			{
				report_error("Unary minus operator can only be applied to int type.", extFactor);
				extFactor.struct = Tab.noType;
			}
		}
		else
		{
			extFactor.struct = extFactor.getFactor().struct;
		}
	}

	/* FactorList */

	@Override
	public void visit(FactorList_single factorList)
	{
		factorList.struct = factorList.getExtensionFactor().struct;
	}

	@Override
	public void visit(FactorList_recursion factorList)
	{
		Struct left = factorList.getFactorList().struct;
		Struct right = factorList.getExtensionFactor().struct;

		if (left == Tab.intType && right == Tab.intType
			|| left.getKind() == Struct.Enum && right.getKind() == Struct.Enum
			|| left == Tab.intType && right.getKind() == Struct.Enum
			|| left.getKind() == Struct.Enum && right == Tab.intType)
		{
			factorList.struct = Tab.intType;
		}
		else
		{
			report_error("Mulop operands must be of int type.", factorList);
			factorList.struct = Tab.noType;
		}
	}

	/* Term */

	@Override
	public void visit(Term term)
	{
		term.struct = term.getFactorList().struct;
	}

	/* TermList */

	@Override
	public void visit(TermList_single termList)
	{
		termList.struct = termList.getTerm().struct;
	}

	@Override
	public void visit(TermList_recursion termList)
	{
		Struct left = termList.getTermList().struct;
		Struct right = termList.getTerm().struct;

		if (left == Tab.intType && right == Tab.intType
				|| left.getKind() == Struct.Enum && right.getKind() == Struct.Enum
				|| left == Tab.intType && right.getKind() == Struct.Enum
				|| left.getKind() == Struct.Enum && right == Tab.intType)
		{
			termList.struct = Tab.intType;
		}
		else
		{
			report_error("Addop operands must be of int type.", termList);
			termList.struct = Tab.noType;
		}
	}

	/* SimpleExpression */

	@Override
	public void visit(SimpleExpression simpleExpression)
	{
		simpleExpression.struct = simpleExpression.getTermList().struct;
	}

	/* Expression */

	@Override
	public void visit(Expression_termlist expression)
	{
		expression.struct = expression.getSimpleExpression().struct;
	}

	@Override
	public void visit(Expression_condition expression)
	{
		Struct trueExpr = expression.getExpression().struct;
		Struct falseExpr = expression.getExpression1().struct;

		if (trueExpr != null && trueExpr.compatibleWith(falseExpr))
		{
			expression.struct = trueExpr;
		}
		else
		{
			report_error("Ternary expression branches must have compatible types.", expression);
			expression.struct = Tab.noType;
		}
	}

	/* Designator */

	private Stack<Obj> designatorObjStack = new Stack<>();
	private Stack<Struct> designatorTypeStack = new Stack<>();
	private Stack<Boolean> designatorErrorStack = new Stack<>();

	@Override
	public void visit(DesignatorName designatorName)
	{
		// Push new designator context (supports nesting like niz[nula])
		Obj baseObj = Tab.find(designatorName.getI1());
		if (baseObj == Tab.noObj)
		{
			report_error("Designator: " + designatorName.getI1() + " doesn't exist.", designatorName);
			designatorObjStack.push(Tab.noObj);
			designatorTypeStack.push(Tab.noType);
			designatorErrorStack.push(true);
			designatorName.obj = Tab.noObj;
			return;
		}

		designatorObjStack.push(baseObj);
		designatorTypeStack.push(baseObj.getType());
		designatorErrorStack.push(false);
		designatorName.obj = baseObj;
	}

	@Override
	public void visit(DesignatorSuffix_member suffix)
	{
		if (designatorErrorStack.peek()) return;

		String memberName = suffix.getI2();
		Struct curType = designatorTypeStack.peek();

		if (curType.getKind() != Struct.Class && curType.getKind() != Struct.Enum)
		{
			report_error("Cannot access member '" + memberName + "' on non-class type.", suffix);
			designatorObjStack.set(designatorObjStack.size() - 1, Tab.noObj);
			designatorTypeStack.set(designatorTypeStack.size() - 1, Tab.noType);
			designatorErrorStack.set(designatorErrorStack.size() - 1, true);
			return;
		}

		Obj memberObj = null;
		if (this.currentClassMembersMap != null && curType == this.currentClassStructNode)
		{
			memberObj = this.currentClassMembersMap.get(memberName);
		}
		else
		{
			for (Obj member : curType.getMembers())
			{
				if (member.getName().equals(memberName))
				{
					memberObj = member;
					break;
				}
			}
		}

		if (memberObj == null)
		{
			report_error("Member '" + memberName + "' not found in class.", suffix);
			designatorObjStack.set(designatorObjStack.size() - 1, Tab.noObj);
			designatorTypeStack.set(designatorTypeStack.size() - 1, Tab.noType);
			designatorErrorStack.set(designatorErrorStack.size() - 1, true);
			return;
		}

		designatorObjStack.set(designatorObjStack.size() - 1, memberObj);
		designatorTypeStack.set(designatorTypeStack.size() - 1, memberObj.getType());
	}

	@Override
	public void visit(DesignatorSuffix_array suffix)
	{
		if (designatorErrorStack.peek()) return;

		Struct curType = designatorTypeStack.peek();

		if (curType.getKind() != Struct.Array)
		{
			report_error("Cannot index into non-array type.", suffix);
			designatorObjStack.set(designatorObjStack.size() - 1, Tab.noObj);
			designatorTypeStack.set(designatorTypeStack.size() - 1, Tab.noType);
			designatorErrorStack.set(designatorErrorStack.size() - 1, true);
			return;
		}

		if (suffix.getExpression().struct != Tab.intType && suffix.getExpression().struct.getKind() != Struct.Enum)
		{
			report_error("Array index must be of int type.", suffix);
			designatorObjStack.set(designatorObjStack.size() - 1, Tab.noObj);
			designatorTypeStack.set(designatorTypeStack.size() - 1, Tab.noType);
			designatorErrorStack.set(designatorErrorStack.size() - 1, true);
			return;
		}

		Struct elemType = curType.getElemType();
		Obj curObj = designatorObjStack.peek();
		designatorObjStack.set(designatorObjStack.size() - 1, new Obj(Obj.Elem, curObj.getName(), elemType));
		designatorTypeStack.set(designatorTypeStack.size() - 1, elemType);
	}

	@Override
	public void visit(DesignatorSuffix_length suffix)
	{
		if (designatorErrorStack.peek()) return;

		Struct curType = designatorTypeStack.peek();

		if (curType.getKind() != Struct.Array)
		{
			report_error("Cannot access .length on non-array type.", suffix);
			designatorObjStack.set(designatorObjStack.size() - 1, Tab.noObj);
			designatorTypeStack.set(designatorTypeStack.size() - 1, Tab.noType);
			designatorErrorStack.set(designatorErrorStack.size() - 1, true);
			return;
		}

		designatorObjStack.set(designatorObjStack.size() - 1, new Obj(Obj.Fld, "length", Tab.intType));
		designatorTypeStack.set(designatorTypeStack.size() - 1, Tab.intType);
	}

	@Override
	public void visit(Designator designator)
	{
		boolean hasError = designatorErrorStack.pop();
		Obj curObj = designatorObjStack.pop();
		designatorTypeStack.pop();

		if (hasError)
		{
			designator.obj = Tab.noObj;
			return;
		}

		// simple designator — validate that the symbol is usable
		if (designator.getDesignatorSuffix() instanceof DesignatorSuffix_epsilon)
		{
			Obj baseObj = curObj;
			if (baseObj.getKind() != Obj.Var && baseObj.getKind() != Obj.Con
					&& baseObj.getKind() != Obj.Fld && baseObj.getKind() != Obj.Meth)
			{
				report_error("Designator: " + designator.getDesignatorName().getI1() + " isn't valid.", designator);
				designator.obj = Tab.noObj;
				return;
			}
		}

		designator.obj = curObj;
	}

	/* Statement */

	/* DesignatorStatement */

	@Override
	public void visit(DesignatorStatement_assign designatorStatement)
	{
		int designatorKind = designatorStatement.getDesignator().obj.getKind();
		if (designatorKind != Obj.Var && designatorKind != Obj.Elem && designatorKind != Obj.Fld)
		{
			report_error("Left-hand side of assignment must be a variable, array element, or field.", designatorStatement);
		}
		else if (!isAssignable_deep(designatorStatement.getExpression().struct, designatorStatement.getDesignator().obj.getType()))
		{
			report_error("Incompatible types in assignment.", designatorStatement);
		}
	}

	@Override 
	public void visit(DesignatorStatement_method designatorStatement)
	{
		if (designatorStatement.getDesignator().obj.getKind() != Obj.Meth)
		{
			report_error("Bad call of method: " + designatorStatement.getDesignator().obj.getName(), designatorStatement);
		}
		else
		{
			Obj methObj = designatorStatement.getDesignator().obj;
			ArrayList<Struct> formalParams = new ArrayList<>();
			int numFormalParams = methObj.getLevel();
			int count = 0;
			for (Obj localSym : methObj.getLocalSymbols()) {
				if (count >= numFormalParams) break;
				if (localSym.getKind() == Obj.Var) {
					if (!localSym.getName().equals("this")) {
						formalParams.add(localSym.getType());
					}
					count++;
				}
			}

			try
			{
				if (formalParams.size() != this.methodCallActualParams.size())
				{
					throw new Exception("Number of actual parameters doesn't match the number of formal parameters.");
				}

				for (int i = 0; i < formalParams.size(); i++)
				{
					if (!isAssignable_deep(this.methodCallActualParams.get(i), formalParams.get(i)))
					{
						throw new Exception("Actual parameter " + (i + 1) + " is not compatible with formal parameter.");
					}
				}
			}
			catch (Exception e)
			{
				report_error("Designator_method exception: " + e.getMessage(), designatorStatement);
			}
		}
	}

	@Override
	public void visit(DesignatorStatement_inc designatorStatement)
	{
		int designatorKind = designatorStatement.getDesignator().obj.getKind();
		if (designatorKind != Obj.Var && designatorKind != Obj.Elem && designatorKind != Obj.Fld)
		{
			report_error("Operand of '++' must be a variable, array element, or field.", designatorStatement);
		}
		else if (designatorStatement.getDesignator().obj.getType() != Tab.intType)
		{
			report_error("Operand of '++' must be of int type.", designatorStatement);
		}
	}

	@Override
	public void visit(DesignatorStatement_dec designatorStatement)
	{
		int designatorKind = designatorStatement.getDesignator().obj.getKind();
		if (designatorKind != Obj.Var && designatorKind != Obj.Elem && designatorKind != Obj.Fld)
		{
			report_error("Operand of '--' must be a variable, array element, or field.", designatorStatement);
		}
		else if (designatorStatement.getDesignator().obj.getType() != Tab.intType)
		{
			report_error("Operand of '--' must be of int type.", designatorStatement);
		}
	}

	/* Statement */

	@Override
	public void visit(Statement_read statement)
	{
		int designatorKind = statement.getDesignator().obj.getKind();
		if (designatorKind != Obj.Var && designatorKind != Obj.Elem && designatorKind != Obj.Fld)
		{
			report_error("Operand of 'read' must be a variable, array element, or field.", statement);
		}
		else if (statement.getDesignator().obj.getType() != Tab.intType &&
				 statement.getDesignator().obj.getType() != Tab.charType &&
				 statement.getDesignator().obj.getType() != this.boolType)
		{
			report_error("Operand of 'read' must be of int, char, or bool type.", statement);
		}
	}

	@Override
	public void visit(Statement_print statement)
	{
		if (statement.getExpression().struct != Tab.intType &&
			statement.getExpression().struct != Tab.charType &&
			statement.getExpression().struct != this.boolType)
		{
			report_error("Operand of 'print' must be of int, char, or bool type.", statement);
		}
	}

	@Override
	public void visit(Statement_return statement)
	{
		this.methodHasReturnStmt = true;
		if (this.currentMethod.getType() == Tab.noType && statement.getExpressionOpt() instanceof ExpressionOpt_exists)
		{
			report_error("Void method cannot return a value.", statement);
		}
		else if (this.currentMethod.getType() != Tab.noType && statement.getExpressionOpt() instanceof ExpressionOpt_epsilon)
		{
			report_error("Non-void method must return a value.", statement);
		}
		else if (this.currentMethod.getType() != Tab.noType && statement.getExpressionOpt() instanceof ExpressionOpt_exists)
		{
			ExpressionOpt_exists returnExpr = (ExpressionOpt_exists) statement.getExpressionOpt();
			if (!returnExpr.getExpression().struct.equals(this.currentMethod.getType()))
			{
				report_error("Incompatible return type.", statement);
			}
		}
	}

	@Override
	public void visit(SwitchInit switchInit)
	{
		this.switchDepth++;
		this.caseLabelsStack.push(new HashSet<>());
	}

	@Override
	public void visit(Statement_switch statement)
	{
		if (statement.getExpression().struct != Tab.intType
				&& statement.getExpression().struct.getKind() != Struct.Enum) {
			report_error("Switch expression must be of int type.", statement);
		}
		this.switchDepth--;
		this.caseLabelsStack.pop();
	}
	
	@Override
	public void visit(CaseList_recursion caseList)
	{
		int caseValue = caseList.getNumConst().getN1();
		HashSet<Integer> currentCaseLabels = this.caseLabelsStack.peek();
		if (currentCaseLabels.contains(caseValue))
		{
			report_error("Duplicate case label: " + caseValue, caseList);
		}
		else
		{
			currentCaseLabels.add(caseValue);
		}
	}

	@Override
	public void visit(CaseList_single caseList)
	{
		int caseValue = caseList.getNumConst().getN1();
		HashSet<Integer> currentCaseLabels = this.caseLabelsStack.peek();
		if (currentCaseLabels.contains(caseValue))
		{
			report_error("Duplicate case label: " + caseValue, caseList);
		}
		else
		{
			currentCaseLabels.add(caseValue);
		}
	}

	@Override
	public void visit(ForInit forInit)
	{
		loopDepth++;
	}

	@Override
	public void visit(Statement_for statement)
	{
		loopDepth--;
	}

	@Override
	public void visit(Statement_break statement)
	{
		if (loopDepth == 0 && switchDepth == 0)
		{
			report_error("'break' statement not within a loop or switch.", statement);
		}
	}

	@Override
	public void visit(Statement_continue statement) 
	{
		if (loopDepth == 0)
		{
			report_error("'continue' statement not within a loop.", statement);
		}
	}

	/* Condition */

	@Override
	public void visit(CondFact_single condFact)
	{
		if (condFact.getSimpleExpression().struct != this.boolType)
		{
			report_error("Condition must be of type 'bool'.", condFact);
			condFact.struct = Tab.noType; 
		}
		else
		{
			condFact.struct = this.boolType;
		}
	}

	@Override
	public void visit(CondFact_relop condFact)
	{
		Struct left = condFact.getSimpleExpression().struct;
		Struct right = condFact.getSimpleExpression1().struct;

		if (!left.compatibleWith(right))
		{
			report_error("Incompatible types in condition.", condFact);
			condFact.struct = Tab.noType;
		}
		else if (left.getKind() == Struct.Array && right.getKind() == Struct.Array
				|| left.getKind() == Struct.Class && right.getKind() == Struct.Class)
		{
			if (condFact.getRelop() instanceof Relop_eql || condFact.getRelop() instanceof Relop_neq)
			{
				condFact.struct = this.boolType;
			}
			else
			{
				report_error("Only '==' and '!=' operators can be applied to class or array types in condition.", condFact);
				condFact.struct = Tab.noType;
			}
		}
		else
		{
			condFact.struct = this.boolType;
		}
	}

	@Override
	public void visit(CondTerm_recursion condTerm)
	{
		Struct left = condTerm.getCondTerm().struct;
		Struct right = condTerm.getCondFact().struct;
		
		if (left != this.boolType || right != this.boolType)
		{
			report_error("Operands of '&&' must be of type 'bool'.", condTerm);
			condTerm.struct = Tab.noType;
		}
		else 
		{
			condTerm.struct = this.boolType;
		}
	}

	@Override
	public void visit(CondTerm_single condTerm)
	{
		if (condTerm.getCondFact().struct != this.boolType)
		{
			condTerm.struct = Tab.noType;
		}
		else
		{
			condTerm.struct = this.boolType;
		}
	}

	@Override
	public void visit(Condition_recursion condition)
	{
		Struct left = condition.getCondition().struct;
		Struct right = condition.getCondTerm().struct;
		
		if (left != this.boolType || right != this.boolType)
		{
			report_error("Operands of '||' must be of type 'bool'.", condition);
			condition.struct = Tab.noType;
		}
		else 
		{
			condition.struct = this.boolType;
		}
	}

	@Override
	public void visit(Condition_single condition)
	{
		if (condition.getCondTerm().struct != this.boolType)
		{
			condition.struct = Tab.noType;
		}
		else
		{
			condition.struct = this.boolType;
		}
	}

	/* ActPars */

	@Override
	public void visit(ActPar actPar)
	{
		this.methodCallActualParamsLists.peek().add(actPar.getExpression().struct);
	}

	@Override
	public void visit(ActParsListOpener actParsListOpener)
	{
		this.methodCallActualParamsLists.push(new ArrayList<>());
	}

	@Override
	public void visit(ActParsOpt_exist actParsOpt)
	{
		this.methodCallActualParams = this.methodCallActualParamsLists.pop();
	}

	@Override
	public void visit(ActParsOpt_epsilon actParsOpt)
	{
		this.methodCallActualParams = this.methodCallActualParamsLists.pop();
	}
}

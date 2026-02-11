package rs.ac.bg.etf.pp1;

import java.util.ArrayList;

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
	private Obj currentMethod;
	private Struct currentType;

	private Obj currentEnum;
	private Struct currentEnumStructNode;
	private int previousEnumValue;
	private ArrayList<Integer> currentEnumValues;

	private Obj currentClass;
	private Struct currentClassStructNode;
	private int currentFieldAddress;

	private int c_val;
	private Struct c_type;
	private Struct boolType = Tab.find("bool").getType();


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
	
	/* analysis */
	
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

		if (methodName.equalsIgnoreCase("main"))
			this.mainMethod = this.currentMethod;

		Tab.openScope();

		if (this.currentClass != null)
		{
			Obj thisParam = Tab.insert(Obj.Var, "this", this.currentClassStructNode);
			thisParam.setLevel(2);
			thisParam.setFpPos(1);
			this.currentMethod.setLevel(1);
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
		Tab.openScope();

		if (this.currentClass != null)
		{
			Obj thisParam = Tab.insert(Obj.Var, "this", this.currentClassStructNode);
			thisParam.setLevel(2);
			thisParam.setFpPos(1);
			this.currentMethod.setLevel(1);
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
          	this.currentEnum = new Obj(Obj.Type, enumName.getI1(), this.currentEnumStructNode);
		}
		else
		{
			this.currentEnum = Tab.insert(Obj.Type, enumName.getI1(), this.currentEnumStructNode);
		}
		Tab.openScope();
		this.currentEnumValues = new ArrayList<>();
		this.previousEnumValue = -1;
	}

	@Override
	public void visit(EnumDeclList enumDeclList)
	{
		Tab.chainLocalSymbols(this.currentEnumStructNode);
		Tab.closeScope();
		
		this.currentEnumValues = null;
		this.currentEnumStructNode = null;
		this.currentEnum = null;
	}

	private boolean isEnumMemberValueUnique(int value)
	{
		for (int i = 0; i < this.currentEnumValues.size(); i++)
		{
			if (this.currentEnumValues.get(i) == value)
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
			Obj newEnumMember = Tab.insert(Obj.Con, enumMemberName, Tab.intType);
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

			Obj newEnumMember = Tab.insert(Obj.Con, enumMemberName, Tab.intType);
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
		}
		Tab.openScope();
		this.currentFieldAddress = 1;
	}

	@Override
	public void visit(AbstractClassDecl abstractClassDecl)
	{
		Tab.chainLocalSymbols(this.currentClassStructNode);
		Tab.closeScope();

		this.currentClass = null;
		this.currentClassStructNode = null;
	}

	/* ClassDecl */

	@Override
	public void visit(ClassName className)
	{
		Obj classType = Tab.find(className.getI1());
		this.currentClassStructNode = new Struct(Struct.Class);
		if (classType != Tab.noObj)
		{
			report_error("Multiple definitions of class type: " + className.getI1(), className);
		  	this.currentClass = new Obj(Obj.Type, className.getI1(), this.currentClassStructNode);
		}
		else
		{
			this.currentClass = Tab.insert(Obj.Type, className.getI1(), this.currentClassStructNode);
		}
		Tab.openScope();
		this.currentFieldAddress = 1;
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
			java.util.Collection<Obj> parentMembers = parentClassObj.getType().getMembers();
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
				}
			}
		}
	}
}

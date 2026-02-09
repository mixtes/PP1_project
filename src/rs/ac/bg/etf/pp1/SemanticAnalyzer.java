package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor {
	
	private boolean mainExists = false;
	
	private boolean errorDetected = false;	
	Logger log = Logger.getLogger(getClass());
	private Obj myProgram;
	private Obj currentMethod;
	private Struct currentType;
	
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
		
		if (!this.mainExists)
			report_error("Program is missing entry point (method main).", program);
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
		
		if (this.currentMethod == null)
			variable = Tab.find(varDecl.getI1());
		else
			variable = Tab.currentScope().findSymbol(varDecl.getI1());
		
		if (variable != null && variable != Tab.noObj)
		{
			report_error("Multiple definitions of variable: " + varDecl.getI1(), varDecl);
		}
		else
		{
			variable = Tab.insert(Obj.Var, varDecl.getI1(), this.currentType);
		}
	}

	public void visit(VarDecl_array varDecl)
	{
		Obj variable = null;
		
		if (this.currentMethod == null)
			variable = Tab.find(varDecl.getI1()); // either finds symbol or noObj
		else
			variable = Tab.currentScope().findSymbol(varDecl.getI1()); // either finds symbol or null
		
		if (variable != null && variable != Tab.noObj)
		{
			report_error("Multiple definitions of variable: " + varDecl.getI1(), varDecl);
		}
		else
		{
			variable = Tab.insert(Obj.Var, varDecl.getI1(), new Struct(Struct.Array, this.currentType));
		}
	}
	
	/* MethodDecl */
	
	@Override
	public void visit(MethodTypeAndName_void methodTypeAndName)
	{
		if (methodTypeAndName.getI1().equalsIgnoreCase("main")) 
			this.mainExists = true;
		
		this.currentMethod = Tab.insert(Obj.Meth, methodTypeAndName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit(MethodTypeAndName_type methodTypeAndName)
	{	
		this.currentMethod = Tab.insert(Obj.Meth, methodTypeAndName.getI2(), this.currentType);
		Tab.openScope();
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
		}
	}
	
	@Override
	public void visit(MethodDecl methodDecl)
	{
		Tab.chainLocalSymbols(this.currentMethod);
		Tab.closeScope();
		
		this.currentMethod = null; 
	}
	
}

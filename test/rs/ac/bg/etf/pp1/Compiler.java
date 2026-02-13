package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler 
{

	static 
	{
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception 
	{
		
		Logger log = Logger.getLogger(Compiler.class);
		
		Reader br = null;
		
		try
		{
			File sourceCode = new File("test/program.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			
			Yylex lexer = new Yylex(br);
			
			MJParser parser = new MJParser(lexer);
			
			Symbol s = parser.parse();
			
			Program program = (Program)(s.value); // the post-order ensures that the last visited node was the root
			
			log.info(program.toString(""));
			log.info("====================================================================");

			// symbol table initialization
			Tab.init();
			Struct boolType = new Struct(Struct.Bool);
			Obj boolTypeObj = Tab.insert(Obj.Type, "bool", boolType);
			boolTypeObj.setAdr(-1);
			boolTypeObj.setLevel(-1);
			
			SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
			program.traverseBottomUp(semanticAnalyzer);
			
			log.info("====================================================================");
			Tab.dump();
			
			if(!parser.errorDetected && semanticAnalyzer.success())
			{
				log.info("Parsing successful!");
			}
			else
			{
				log.error("Parsing failed!");
			}
		}
		finally
		{
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}
	}
	
}

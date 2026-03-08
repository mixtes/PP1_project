package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;

import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import rs.etf.pp1.mj.runtime.Code;

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

		String inputPath  = (args.length > 0) ? args[0] : "test/program.mj";
		String outputPath = (args.length > 1) ? args[1] : "test/program.obj";

		BufferedReader br = null;

		try
		{
			File sourceCode = new File(inputPath);
			if (!sourceCode.exists()) {
				log.error("Input file not found: " + sourceCode.getAbsolutePath());
				return;
			}
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

			// semantic analysis
			SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
			program.traverseBottomUp(semanticAnalyzer);

			log.info("====================================================================");
			Tab.dump();

			if(!parser.errorDetected && semanticAnalyzer.success())
			{
				log.info("Parsing successful!");

				// code generation
				File objFile = new File(outputPath);
				if (objFile.exists())
					objFile.delete();

				CodeGenerator codeGenerator = new CodeGenerator(semanticAnalyzer.nVars());
				program.traverseBottomUp(codeGenerator);

				Code.dataSize = codeGenerator.getDataSize();

				Code.mainPc = codeGenerator.getMainPC();
				Code.write(new FileOutputStream(objFile));
			}
			else
			{
				log.error("Parsing failed!");
			}
		}
		finally
		{
			if (br != null) try { br.close(); } catch (IOException ioe) { log.error(ioe.getMessage(), ioe); }
		}
	}
	
}

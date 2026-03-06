package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	private Symbol new_symbol(int type) 
	{
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	private Symbol new_symbol(int type, Object value)
	{
		return new Symbol(type, yyline+1, yycolumn, value);
	}
	
%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" "		{ }
"\b" 	{ }
"\t"	{ }
"\r\n"	{ }
"\f"	{ }

"program"						{ return new_symbol(sym.PRG, yytext()); }
"break"							{ return new_symbol(sym.BRK, yytext()); }
"enum"							{ return new_symbol(sym.ENM, yytext()); }
"class"							{ return new_symbol(sym.CLS, yytext()); }
"abstract"						{ return new_symbol(sym.ABS, yytext()); }
"else"							{ return new_symbol(sym.ELS, yytext()); }
"const"							{ return new_symbol(sym.CNS, yytext()); }
"if"							{ return new_symbol(sym.SIF, yytext()); }
"new"							{ return new_symbol(sym.NEW, yytext()); }
"print"							{ return new_symbol(sym.PRT, yytext()); }
"read"							{ return new_symbol(sym.RED, yytext()); }
"return"						{ return new_symbol(sym.RET, yytext()); }
"void"							{ return new_symbol(sym.VOI, yytext()); }
"extends"						{ return new_symbol(sym.EXT, yytext()); }
"continue"						{ return new_symbol(sym.CON, yytext()); }
"for"							{ return new_symbol(sym.FOR, yytext()); }
"foreach"						{ return new_symbol(sym.FOR, yytext()); }
"length"						{ return new_symbol(sym.LEN, yytext()); }
"switch"						{ return new_symbol(sym.SWC, yytext()); }
"case"							{ return new_symbol(sym.CSE, yytext()); }

"do"							{ return new_symbol(sym.SDO, yytext()); }	
"while"							{ return new_symbol(sym.WHL, yytext()); }

"+"								{ return new_symbol(sym.PLS, yytext()); }
"-"								{ return new_symbol(sym.MNS, yytext()); }
"*"								{ return new_symbol(sym.MUL, yytext()); }
"/"								{ return new_symbol(sym.DIV, yytext()); }
"%"								{ return new_symbol(sym.MOD, yytext()); }
"=="							{ return new_symbol(sym.EQL, yytext()); }
"!="							{ return new_symbol(sym.NEQ, yytext()); }
">"								{ return new_symbol(sym.GRT, yytext()); }
">="							{ return new_symbol(sym.GRE, yytext()); }
"<"								{ return new_symbol(sym.LET, yytext()); }
"<="							{ return new_symbol(sym.LTE, yytext()); }
"&&"							{ return new_symbol(sym.AND, yytext()); }
"||"							{ return new_symbol(sym.LOR, yytext()); }
"="								{ return new_symbol(sym.ASG, yytext()); }
"++"							{ return new_symbol(sym.INC, yytext()); }
"--"							{ return new_symbol(sym.DEC, yytext()); }
"?"								{ return new_symbol(sym.QSM, yytext()); }
";"								{ return new_symbol(sym.SCL, yytext()); }
":"								{ return new_symbol(sym.COL, yytext()); }
","								{ return new_symbol(sym.COM, yytext()); }
"."								{ return new_symbol(sym.DOT, yytext()); }
"("								{ return new_symbol(sym.SOB, yytext()); }
")"								{ return new_symbol(sym.SCB, yytext()); }
"["								{ return new_symbol(sym.MOB, yytext()); }
"]"								{ return new_symbol(sym.MCB, yytext()); }
"{"								{ return new_symbol(sym.LOB, yytext()); }
"}"								{ return new_symbol(sym.LCB, yytext()); }

"//"							{ yybegin(COMMENT); }
<COMMENT> . 					{ yybegin(COMMENT); }
<COMMENT> "\r\n"				{ yybegin(YYINITIAL); }

(0|[1-9][0-9]*)					{ return new_symbol(sym.NUM, new Integer(yytext())); }
"'"."'"							{ return new_symbol(sym.CHR, new Character(yytext().charAt(1))); }
("true"|"false")				{ return new_symbol(sym.BOO, yytext().equals("true") ? 1 : 0); }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{ return new_symbol(sym.IDT, yytext()); }

.								{ System.err.println("Leksicka greska ("+yytext()+") na liniji "+(yyline+1) + " u koloni " + (yycolumn + 1) + "\n"); }

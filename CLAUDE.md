# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MJCompiler is a compiler for the MicroJava (MJ) language, a Java-like subset used in the PP1 (Programski Prevodioci 1) course at ETF Belgrade. The compiler follows a classic pipeline: Lexical Analysis → Parsing → AST → Semantic Analysis. Code generation is not yet implemented.

## Build Commands

The project uses **Apache Ant** with targets that chain: `delete → lexerGen → parserGen → repackage → compile`.

```bash
# Full build (regenerates lexer, parser, AST classes, then compiles)
ant compile

# Run the compiler on the test program (from project root)
java -cp "bin;lib/log4j-1.2.17.jar;lib/cup_v10k.jar;lib/JFlex.jar;lib/symboltable-1-1.jar" rs.ac.bg.etf.pp1.Compiler
```

The compiler driver hardcodes the input file as `test/program.mj`. Logs go to `logs/mj-test.log`.

## Architecture

### Compilation Pipeline

1. **Lexer** (`spec/mjlexer.lex`) — JFlex specification. Generates `Yylex.java`. Tokenizes MJ source into terminals defined in `dict.txt`.
2. **Parser** (`spec/mjparser.cup`) — CUP grammar. Generates `MJParser.java`, `sym.java`, and 120+ AST node classes in `src/rs/ac/bg/etf/pp1/ast/`. A second file `mjparser_astbuild.cup` exists with explicit AST construction actions.
3. **Semantic Analyzer** (`src/rs/ac/bg/etf/pp1/SemanticAnalyzer.java`) — Hand-written visitor (`VisitorAdaptor`) that traverses the AST bottom-up. Manages symbol table via `rs.etf.pp1.symboltable.Tab`, performs type checking, scope management, and validates the `main` method exists.
4. **Compiler Driver** (`test/rs/ac/bg/etf/pp1/Compiler.java`) — Orchestrates the pipeline: lexer → parser → AST print → symbol table init → semantic analysis → symbol table dump.

### Key Conventions

- The AST is **auto-generated** by CUP with `-ast -buildtree` flags. Do not edit files in `src/rs/ac/bg/etf/pp1/ast/` manually.
- The `ant compile` `delete` target wipes everything in `src/rs/ac/bg/etf/pp1/` **except** `SemanticAnalyzer.java` and `util/Log4JUtils.java`. Only these two files are hand-written source.
- The `repackage` target fixes package names from `src.rs.ac.bg.etf.pp1.ast` to `rs.ac.bg.etf.pp1.ast` in generated source.
- The symbol table library (`symboltable-1-1.jar`) provides `Tab`, `Obj`, and `Struct` classes. A `bool` type is manually registered in the compiler driver since MJ doesn't have it natively.
- Terminal symbol abbreviations (e.g., `PRG` for `program`, `PLS` for `+`) are documented in `dict.txt`.

### Source Layout

- `spec/` — Lexer (`.lex`) and parser (`.cup`) specifications. **Edit these to change the grammar.**
- `src/rs/ac/bg/etf/pp1/SemanticAnalyzer.java` — **Primary hand-written file.** Most development work happens here.
- `test/rs/ac/bg/etf/pp1/Compiler.java` — Compiler driver / main entry point.
- `test/program.mj` — Test MJ source program.
- `lib/` — JFlex, CUP v10k, Log4j 1.2.17, symbol table library.
- `config/log4j.xml` — Logging config (DEBUG level, console + rolling file).

### MJ Language Features

The grammar supports: programs, constants, variables, arrays, enums, classes with inheritance, abstract classes, methods, `if`/`else`, `for` loops, `switch`/`case`, `break`/`continue`, `read`/`print`, ternary expressions (`? :`), and standard arithmetic/relational/logical operators.

## Environment

- **Java**: 17 (source/target compatibility)
- **IDE**: Eclipse (`.project`, `.classpath` configured)
- **Classpath separator**: Use `;` on Windows

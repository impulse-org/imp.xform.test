/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.xform.test;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.java.matching.PolyglotASTAdapter;
import org.eclipse.imp.java.parser.JavaLexer;
import org.eclipse.imp.java.parser.JavaParser;
import org.eclipse.imp.xform.pattern.matching.IASTMatcher;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory_c;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.ParserlessJLExtensionInfo;
import polyglot.frontend.goals.Goal;
import polyglot.main.Options;
import polyglot.main.Main.TerminationException;
import polyglot.types.TypeSystem_c;
import polyglot.util.ErrorQueue;
import polyglot.util.SimpleCodeWriter;
import polyglot.util.StdErrorQueue;
import polyglot.visit.PrettyPrinter;

public class PolyglotJavaTester extends MatchTester {
    private static final TypeSystem_c fTypeSystem= new TypeSystem_c();
    private static final NodeFactory_c fNodeFactory= new NodeFactory_c();

    protected void dumpSource(Object srcAST) {
	new PrettyPrinter().printAst((Node) srcAST, new SimpleCodeWriter(System.out, 120));
    }

    // RMF 6/8/2006 - Old version that directly calls parser, and so doesn't
    //                run disambiguation pass, and so can't constrain nodes
    //                on their "targetType" attribute.
    //
//    protected Object parseSourceFile(String srcFilePath) throws Exception {
//        StdErrorQueue eq= new StdErrorQueue(System.err, 100, "__ERRORS__");
//        File srcFile= new File(srcFilePath);
//        FileSource fileSource= new FileSource(srcFile);
//        JavaLexer lexer= new JavaLexer(srcFilePath);
//        JavaParser parser= new JavaParser(lexer.getLexStream(), fTypeSystem, fNodeFactory, fileSource, eq);
//
//        parser.getParseStream().resetTokenStream();
//        lexer.lexer(parser.getParseStream()); // Lex the stream to produce the token stream
//        return parser.parse();
//    }

    protected Object parseSourceFile(String srcFilePath) throws Exception {
	ExtensionInfo ext= new ParserlessJLExtensionInfo() {
	    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
	        try {
	            JavaLexer lexer= new JavaLexer(source.path());
	            JavaParser parser= new JavaParser(lexer, ts, nf, source, eq);

	            lexer.lexer(parser);
	            return parser; // Parse the token stream to produce an AST
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        throw new IllegalStateException("Could not parse " + source.path());
	    }
	    public Goal getCompileGoal(Job job) {
	        return scheduler.TypeChecked(job);
	    }
	};

	Options options= ext.getOptions();

	// Allow all objects to get access to the Options object. This hack should
	// be fixed somehow. XXX###@@@
	Options.global= options;

	ErrorQueue eq= new StdErrorQueue(System.err, options.error_count, ext.compilerName());
	Compiler compiler= new Compiler(ext, eq);
	Collection sources= new ArrayList();
//        File srcFile= new File(srcFilePath);
//        FileSource fileSource= new FileSource(srcFile);

	sources.add(srcFilePath);
	if (!compiler.compile(sources)) {
	    throw new TerminationException(1);
	}
	Job j= (Job)ext.scheduler().jobs().iterator().next();

	return j.ast();
    }

    protected IASTMatcher getASTAdapter() {
	return new PolyglotASTAdapter(fTypeSystem, fNodeFactory);
    }

    public void test1() {
        testHelper("[MethodDecl m]", "Simple.jl");
    }

    public void test2() {
        testHelper("[Expr e]", "Simple.jl");
    }

    public void test2a() {
        testHelper("[Expr e { name = 'x' }]", "Simple.jl");
    }

    public void testTargetType1() {
        testHelper("[Expr e:int]", "Simple.jl");
    }

    // [Assign lhs rhs]
    public void testChild1() {
        testHelper("[Assign a [Expr lhs] [Expr rhs]]", "Simple.jl");
    }

    public void testChild1a() {
        testHelper("[Assign a { left = [lhs { kind = Expr }], right = [rhs { kind = Expr } ] } ]", "Simple.jl");
    }

    public void testChild2() {
        testHelper("[Assign a [Variable lhs:int] [Expr rhs:int]]", "Simple.jl");
    }

    public void testRewrite1() {
	testRewriteHelper("[MethodDecl m { name = 'foo' }] => [m]", "Simple.jl");
    }

    public void testRewrite2() {
	testRewriteHelper("[MethodDecl m { name = 'foo' }] => [m { name = 'bar' }]", "Simple.jl");
    }

    public void testRewrite3() {
	testRewriteHelper("[MethodDecl m { name = 'foo' } [List args] [Block b]] => [MethodDecl _ args [Block] { name = m.name } ]", "Simple.jl");
    }

    public void testRewrite3a() {
	testRewriteHelper("[MethodDecl m { name = 'foo', args = [ [Formal] [Formal] [Formal] ] } [Block b]] => [MethodDecl _ args [Block] { name = m.name } ]", "Simple.jl");
    }

    public void testRewrite4() {
	testRewriteHelper("[MethodDecl m { name = 'foo' }] => [m { args = m.args, body = [Block] } ]", "Simple.jl");
    }
    // [For { init= [Expr], cond = [Expr], update = [Expr] } ]
}

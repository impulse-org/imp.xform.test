/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.xform.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.imp.lpg.parser.LPGLexer;
import org.eclipse.imp.lpg.parser.LPGParser;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.refactoring.LPGASTAdapter;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

public class JikesPGTester extends MatchTester {
    protected Object parseSourceFile(String srcFilePath) throws Exception {
        LPGLexer lexer= new LPGLexer(); // Create the lexer
        LPGParser parser= new LPGParser(lexer.getLexStream()); // Create the parser
        File file= new File(srcFilePath);
        InputStream is= new FileInputStream(file);

        lexer.initialize(StreamUtils.readStreamContents(is, "US-ASCII").toCharArray(), srcFilePath);
	parser.getParseStream().resetTokenStream();
//	parser.setMessageHandler(new SystemOutMessageHandler());
	lexer.lexer(null, parser.getParseStream()); // Lex the char stream to produce the token stream

        ASTNode ast= (ASTNode) parser.parser();

	return ast;
    }

    protected IASTAdapter getASTAdapter() {
	return new LPGASTAdapter();
    }

    protected void dumpSource(Object astRoot) {
	ASTNode node= (ASTNode) astRoot;
	System.out.println(node.toString());
    }

    public void test1() {
	testHelper("[nonTerm n]", "leg.g");
    }

    public void test2() {
	testHelper("[nonTerm n { name == 'statement' }]", "leg.g");
    }

    public void testChildren1() {
	testHelper("[nonTerm n { name == 'statement' } [rhsList r]]", "leg.g");
    }

    public void testAll1() {
	testAllHelper("[nonTerm n]", "leg.g");
    }
}

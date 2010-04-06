/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.imp.xform.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.imp.lpg.parser.LPGLexer;
import org.eclipse.imp.lpg.parser.LPGParser;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.refactoring.LPGASTAdapter;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.imp.xform.pattern.matching.IASTMatcher;

public class JikesPGTester extends MatchTester {
    protected Object parseSourceFile(String srcFilePath) throws Exception {
        LPGLexer lexer= new LPGLexer(); // Create the lexer
        LPGParser parser= new LPGParser(lexer.getILexStream()); // Create the parser
        File file= new File(srcFilePath);
        InputStream is= new FileInputStream(file);
        lexer.reset(StreamUtils.readStreamContents(is, "US-ASCII").toCharArray(), srcFilePath);
        parser.getIPrsStream().resetTokenStream();
        // parser.setMessageHandler(new SystemOutMessageHandler());
        lexer.lexer(null, parser.getIPrsStream()); // Lex the char stream to produce the token stream
        ASTNode ast= (ASTNode) parser.parser();
        return ast;
    }

    protected IASTMatcher getASTAdapter() {
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

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

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.imp.xform.pattern.matching.IASTMatcher;
import org.eclipse.imp.xform.pattern.matching.MatchResult;
import org.eclipse.imp.xform.pattern.matching.Matcher;
import org.eclipse.imp.xform.pattern.parser.ASTPatternLexer;
import org.eclipse.imp.xform.pattern.parser.ASTPatternParser;
import org.eclipse.imp.xform.pattern.parser.Ast.Pattern;
import org.eclipse.imp.xform.pattern.parser.Ast.PatternNode;
import org.eclipse.imp.xform.pattern.parser.Ast.RewriteRule;
import org.eclipse.imp.xform.pattern.rewriting.Rewriter;

public abstract class MatchTester extends TestCase {
    private IASTMatcher fAdapter;

    protected abstract IASTMatcher getASTAdapter();

    protected abstract Object parseSourceFile(String srcFilePath) throws Exception;

    protected abstract void dumpSource(Object astRoot);

    protected PatternNode parsePattern(String patternStr) {
        ASTPatternLexer lexer= new ASTPatternLexer(patternStr.toCharArray(), "__PATTERN__");
        ASTPatternParser parser= new ASTPatternParser(lexer.getILexStream());

        lexer.lexer(parser.getIPrsStream()); // Why wasn't this done by the parser ctor?
        ASTPatternParser.setASTAdapter(fAdapter);

        return parser.parser();
    }

    protected void testHelper(String patternStr, String srcFile) {
        try {
            System.out.println("\n**** " + getName() + " ****\n");
            fAdapter= getASTAdapter();

            Pattern pattern= (Pattern) parsePattern(patternStr);

            assertNotNull("No AST produced for AST pattern!", pattern);

            Object srcAST= parseSourceFile("resources/" + srcFile);
    
            assertNotNull("No AST produced for target source file!", srcAST);

            Matcher matcher= new Matcher(pattern);
            MatchResult m= fAdapter.findNextMatch(matcher, srcAST, 0);

            System.out.println("Pattern = " + pattern);
            System.out.println("Source  = ");
            dumpSource(srcAST);
            System.out.println("Result  = " + m);
            assertNotNull("No match for pattern!", m);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void testRewriteHelper(String rewriteStr, String srcFile) {
        try {
            System.out.println("\n**** " + getName() + " ****\n");
            fAdapter= getASTAdapter();

            RewriteRule rule= (RewriteRule) parsePattern(rewriteStr);

            assertNotNull("No AST produced for AST pattern!", rule);

            Object srcAST= parseSourceFile("resources/" + srcFile);
    
            assertNotNull("No AST produced for target source file!", srcAST);

            Matcher matcher= new Matcher((Pattern) rule.getlhs());
            MatchResult m= fAdapter.findNextMatch(matcher, srcAST, 0);

            System.out.println("Pattern = " + rule);
            System.out.println("Source  = ");
            dumpSource(srcAST);
            System.out.println("Result  = " + m);
            assertNotNull("No match for pattern!", m);

            Rewriter rewriter= new Rewriter(matcher, (Pattern) rule.getrhs());
            Object newAST= rewriter.rewrite(m);

            dumpSource(newAST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void testAllHelper(String patternStr, String srcFile) {
        try {
            System.out.println("\n**** " + getName() + " ****\n");
            fAdapter= getASTAdapter();

            Pattern pattern= (Pattern) parsePattern(patternStr);

            assertNotNull("No AST produced for AST pattern!", pattern);

            Object srcAST= parseSourceFile("resources/" + srcFile);
    
            assertNotNull("No AST produced for target source file!", srcAST);

            Matcher matcher= new Matcher(pattern);
            Set/*<MatchContext>*/ matches= fAdapter.findAllMatches(matcher, srcAST);

            System.out.println("Pattern = " + pattern);
            System.out.println("Source  = ");
            dumpSource(srcAST);
            System.out.println("Result  = " + matches);
            assertNotNull("Pattern match returned null", matches);
            assertTrue("No matches for pattern!", matches.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

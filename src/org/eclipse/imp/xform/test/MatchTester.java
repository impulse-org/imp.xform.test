package com.ibm.watson.safari.xform.test;


import java.util.Set;
import junit.framework.TestCase;
import com.ibm.watson.safari.xform.pattern.matching.IASTAdapter;
import com.ibm.watson.safari.xform.pattern.matching.MatchResult;
import com.ibm.watson.safari.xform.pattern.matching.Matcher;
import com.ibm.watson.safari.xform.pattern.parser.ASTPatternLexer;
import com.ibm.watson.safari.xform.pattern.parser.ASTPatternParser;
import com.ibm.watson.safari.xform.pattern.parser.Ast.Pattern;
import com.ibm.watson.safari.xform.pattern.parser.Ast.PatternNode;
import com.ibm.watson.safari.xform.pattern.parser.Ast.RewriteRule;
import com.ibm.watson.safari.xform.pattern.rewriting.Rewriter;

public abstract class MatchTester extends TestCase {
    private IASTAdapter fAdapter;

    protected abstract IASTAdapter getASTAdapter();

    protected abstract Object parseSourceFile(String srcFilePath) throws Exception;

    protected abstract void dumpSource(Object astRoot);

    protected PatternNode parsePattern(String patternStr) {
        ASTPatternLexer lexer= new ASTPatternLexer(patternStr.toCharArray(), "__PATTERN__");
        ASTPatternParser parser= new ASTPatternParser(lexer.getLexStream());

        lexer.lexer(parser); // Why wasn't this done by the parser ctor?
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

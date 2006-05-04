package com.ibm.watson.safari.xform.test;


import java.util.Set;
import junit.framework.TestCase;
import com.ibm.watson.safari.xform.pattern.matching.IASTAdapter;
import com.ibm.watson.safari.xform.pattern.matching.MatchResult;
import com.ibm.watson.safari.xform.pattern.matching.Matcher;
import com.ibm.watson.safari.xform.pattern.parser.ASTPatternLexer;
import com.ibm.watson.safari.xform.pattern.parser.ASTPatternParser;
import com.ibm.watson.safari.xform.pattern.parser.Ast.Pattern;

public abstract class MatchTester extends TestCase {
    private IASTAdapter fAdapter;

    protected abstract IASTAdapter getASTAdapter();

    protected abstract Object parseSourceFile(String srcFilePath) throws Exception;

    protected abstract void dumpSource(Object astRoot);

    protected Pattern parsePattern(String patternStr) {
        ASTPatternLexer lexer= new ASTPatternLexer(patternStr.toCharArray(), "__PATTERN__");
        ASTPatternParser parser= new ASTPatternParser(lexer.getLexStream());
    
        lexer.lexer(parser); // Why wasn't this done by the parser ctor?
        ASTPatternParser.setASTAdapter(fAdapter);
    
        Pattern pattern= parser.parser();
    
        return pattern;
    }

    protected void testHelper(String patternStr, String srcFile) {
        try {
            System.out.println("\n**** " + getName() + " ****\n");
            fAdapter= getASTAdapter();

            Pattern pattern= parsePattern(patternStr);

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
            e.printStackTrace();
        }
    }

    protected void testAllHelper(String patternStr, String srcFile) {
        try {
            System.out.println("\n**** " + getName() + " ****\n");
            fAdapter= getASTAdapter();

            Pattern pattern= parsePattern(patternStr);

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

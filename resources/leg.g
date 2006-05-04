%options package=leg.safari.parser
%options template=UIDE/dtParserTemplate.gi
%options import_terminals=LegLexer.gi
-- %options automatic_ast=toplevel,visitor=preorder,ast_directory=./Ast,ast_type=ASTNode

$Globals
    /.import org.eclipse.uide.parser.IParser;
    ./
$End

$Define
    $ast_class /.Object./
    $additional_interfaces /., IParser./
    $splat /.bar./
    $barf /.booboo./
$End

$Terminals
         int
         short
         IDENTIFIER 
         NUMBER
         SEMICOLON ::= ';'
         PLUS ::= '+'
         MINUS ::= '-'
         ASSIGN ::= '='
         LEFTBRACE ::= '{'
         RIGHTBRACE ::= '}'
$End

$Rules
        stmtList$$statement ::= $empty
                | stmtList statement
        statement ::= declaration
                    | assignment
        declaration ::= int IDENTIFIER ;
                      | short IDENTIFIER ;
        assignment ::= IDENTIFIER '=' expression ';'
        expression ::= expression '+' term
                     | expression '-' term
                     | term
        term ::= IDENTIFIER
               | NUMBER
$End

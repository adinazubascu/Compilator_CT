import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int index = 0;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        if (!tokens.isEmpty()) {
            currentToken = tokens.get(0);
        } else {
            currentToken = new Token(-1);
            currentToken.type = Token.IDs.END;
        }
    }

    private void advance() {
        index++;
        if (index < tokens.size()) {
            currentToken = tokens.get(index);
        } else {
            currentToken = new Token(-1);
            currentToken.type = Token.IDs.END;
        }
    }

    private void eat(Token.IDs expected) throws ParseException {
        if (currentToken.type == expected) {
            advance();
        } else {
            error("Expected " + expected + " but found " + currentToken.type);
        }
    }

    private void error(String message) throws ParseException {
        throw new ParseException("Parse error at line " + currentToken.myLine + ": " + message);
    }

    public void parse() {
        try {
            unit();
            System.out.println("Parsing finished successfully.");
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }
    // Start Symbol
    private void unit()  throws ParseException{
        while (isDeclStruct() || isDeclFunc() || isDeclVar()) {
            if (isDeclStruct()) declStruct();
            else if (isDeclFunc()) declFunc();
            else declVar();
        }
        eat(Token.IDs.END);
    }



    private boolean isTypeBase()  throws ParseException{
        return currentToken.type == Token.IDs.INT ||
                currentToken.type == Token.IDs.DOUBLE ||
                currentToken.type == Token.IDs.CHAR ||
                currentToken.type == Token.IDs.STRUCT;
    }

    private boolean isDeclStruct() {
        if (currentToken.type == Token.IDs.STRUCT) {
            int backupIndex = index;
            Token tempToken = currentToken;

            advance(); // eat 'struct'
            if (currentToken.type == Token.IDs.ID) {
                advance(); // eat struct name
                boolean isStructDefinition = currentToken.type == Token.IDs.LACC;

                // restore
                index = backupIndex;
                currentToken = tempToken;

                return isStructDefinition;
            }

            // restore
            index = backupIndex;
            currentToken = tempToken;
        }
        return false;
    }



    private boolean isDeclFunc()  throws ParseException{
        if (isTypeBase()) {
            int backupIndex = index;
            advance(); // typeBase

            if (currentToken.type == Token.IDs.ID) {
                advance();
                boolean isFunc = currentToken.type == Token.IDs.LPAR;
                index = backupIndex;
                currentToken = tokens.get(index);
                return isFunc;
            }
            index = backupIndex;
            currentToken = tokens.get(index);
        } else if (currentToken.type == Token.IDs.VOID) {
            return true;
        }
        return false;
    }

    private boolean isDeclVar() throws ParseException {
        return isTypeBase(); // distinction made in declFunc
    }

    private void declStruct()  throws ParseException{
        eat(Token.IDs.STRUCT);
        eat(Token.IDs.ID);
        eat(Token.IDs.LACC);
        while (isDeclVar()) {
            declVar();
        }
        eat(Token.IDs.RACC);
        eat(Token.IDs.SEMICOLON);
    }


    private void declVar()  throws ParseException{
        typeBase();
        eat(Token.IDs.ID);
        if (currentToken.type == Token.IDs.LBRACE) arrayDecl(); // [ expr? ]
        while (currentToken.type == Token.IDs.COMMA) {
            eat(Token.IDs.COMMA);
            eat(Token.IDs.ID);
            if (currentToken.type == Token.IDs.LBRACE) arrayDecl();
        }
        eat(Token.IDs.SEMICOLON);
    }

    private void typeBase()  throws ParseException{
        if (currentToken.type == Token.IDs.INT ||
                currentToken.type == Token.IDs.DOUBLE ||
                currentToken.type == Token.IDs.CHAR) {
            advance();
        } else if (currentToken.type == Token.IDs.STRUCT) {
            advance();
            eat(Token.IDs.ID);
        } else {
            error("Expected type base but found " + currentToken.type);
        }
    }

    private void arrayDecl() throws ParseException {
        eat(Token.IDs.LBRACE);
        if (isStartOfExpr()) {
            expr();
        }
        eat(Token.IDs.RBRACE);
    }

    private boolean isStartOfExpr() throws ParseException {
        return currentToken.type == Token.IDs.ID ||
                currentToken.type == Token.IDs.CONST_INT ||
                currentToken.type == Token.IDs.CONST_REAL ||
                currentToken.type == Token.IDs.CONST_CHAR ||
                currentToken.type == Token.IDs.CONST_STRING ||
                currentToken.type == Token.IDs.LPAR ||
                currentToken.type == Token.IDs.SUB ||
                currentToken.type == Token.IDs.NOT;
    }

    private void funcArg() throws ParseException {
        typeBase();
        eat(Token.IDs.ID);
        if (currentToken.type == Token.IDs.LBRACE) {
            arrayDecl();
        }
    }


    private void declFunc()  throws ParseException{
        if (isTypeBase()) {
            typeBase();
            if (currentToken.type == Token.IDs.MUL) {
                eat(Token.IDs.MUL);
            }
        } else if (currentToken.type == Token.IDs.VOID) {
            eat(Token.IDs.VOID);
        } else {
            error("Expected function return type");
        }

        eat(Token.IDs.ID);
        eat(Token.IDs.LPAR);

        if (isTypeBase()) {
            funcArg();
            while (currentToken.type == Token.IDs.COMMA) {
                eat(Token.IDs.COMMA);
                funcArg();
            }
        }

        eat(Token.IDs.RPAR);
        stmCompound();
    }
    private void stm()  throws ParseException{
        switch (currentToken.type) {
            case LACC:
                stmCompound();
                break;
            case IF:
                eat(Token.IDs.IF);
                eat(Token.IDs.LPAR);
                expr();
                eat(Token.IDs.RPAR);
                stm();
                if (currentToken.type == Token.IDs.ELSE) {
                    eat(Token.IDs.ELSE);
                    stm();
                }
                break;
            case WHILE:
                eat(Token.IDs.WHILE);
                eat(Token.IDs.LPAR);
                expr();
                eat(Token.IDs.RPAR);
                stm();
                break;
            case FOR:
                eat(Token.IDs.FOR);
                eat(Token.IDs.LPAR);
                if (isStartOfExpr()) expr();
                eat(Token.IDs.SEMICOLON);
                if (isStartOfExpr()) expr();
                eat(Token.IDs.SEMICOLON);
                if (isStartOfExpr()) expr();
                eat(Token.IDs.RPAR);
                stm();
                break;
            case BREAK:
                eat(Token.IDs.BREAK);
                eat(Token.IDs.SEMICOLON);
                break;
            case RETURN:
                eat(Token.IDs.RETURN);
                if (isStartOfExpr()) expr();
                eat(Token.IDs.SEMICOLON);
                break;
            default:
                if (isStartOfExpr()) {
                    expr();
                }
                eat(Token.IDs.SEMICOLON);
        }
    }
    private void stmCompound() throws ParseException {
        eat(Token.IDs.LACC);
        while (isDeclVar() || isStartOfStm()) {
            if (isDeclVar()) declVar();
            else stm();
        }
        eat(Token.IDs.RACC);
    }
    private boolean isStartOfStm()  throws ParseException{
        switch (currentToken.type) {
            case LACC:
            case IF:
            case WHILE:
            case FOR:
            case BREAK:
            case RETURN:
            case ID:
            case CONST_INT:
            case CONST_REAL:
            case CONST_CHAR:
            case CONST_STRING:
            case LPAR:
            case SUB:
            case NOT:
            case SEMICOLON:
                return true;
            default:
                return false;
        }
    }



    //Identifiers
    //
    //Function calls
    //
    //Constants
    //
    //Parenthesized expressions
    private void exprPrimary()  throws ParseException{
        if (currentToken.type == Token.IDs.ID) {
            eat(Token.IDs.ID);
            if (currentToken.type == Token.IDs.LPAR) {
                eat(Token.IDs.LPAR);
                if (isStartOfExpr()) {
                    expr();
                    while (currentToken.type == Token.IDs.COMMA) {
                        eat(Token.IDs.COMMA);
                        expr();
                    }
                }
                eat(Token.IDs.RPAR);
            }
        } else if (currentToken.type == Token.IDs.CONST_INT ||
                currentToken.type == Token.IDs.CONST_REAL ||
                currentToken.type == Token.IDs.CONST_CHAR ||
                currentToken.type == Token.IDs.CONST_STRING) {
            advance();
        } else if (currentToken.type == Token.IDs.LPAR) {
            eat(Token.IDs.LPAR);
            expr();
            eat(Token.IDs.RPAR);
        } else {
            error("Unexpected token in primary expression: " + currentToken.type);
        }
    }


    //access fields: ac.b, access array: a[1]
    private void exprPostfix() throws ParseException {
        exprPrimary();
        while (true) {
            if (currentToken.type == Token.IDs.LBRACE) {
                eat(Token.IDs.LBRACE);
                expr();
                eat(Token.IDs.RBRACE);
            } else if (currentToken.type == Token.IDs.DOT) {
                eat(Token.IDs.DOT);
                eat(Token.IDs.ID);
            } else {
                break;
            }
        }
    }

    //-a or !a
    private void exprUnary()  throws ParseException{
        if (currentToken.type == Token.IDs.SUB || currentToken.type == Token.IDs.NOT) {
            advance();
            exprUnary();
        } else {
            exprPostfix();
        }
    }


//(int) a
    private void exprCast()  throws ParseException{
        if (currentToken.type == Token.IDs.LPAR && isTypeAhead()) {
            eat(Token.IDs.LPAR);
            typeName();
            eat(Token.IDs.RPAR);
            exprCast();
        } else {
            exprUnary();
        }
    }

    private void typeName()  throws ParseException{
        typeBase();
        if (currentToken.type == Token.IDs.LBRACE) {
            arrayDecl();
        }
    }

    private boolean isTypeAhead()  throws ParseException{
        // Lookahead to see if we're casting
        int backup = index;
        Token temp = currentToken;

        if (isTypeBase()) {
            typeBase();
            if (currentToken.type == Token.IDs.LBRACE) arrayDecl();
            boolean result = true;
            index = backup;
            currentToken = temp;
            return result;
        }

        index = backup;
        currentToken = temp;
        return false;
    }

    private void exprMul()  throws ParseException{
        exprCast();
        while (currentToken.type == Token.IDs.MUL || currentToken.type == Token.IDs.DIV) {
            advance();
            exprCast();
        }
    }

    private void exprAdd() throws ParseException {
        exprMul();
        while (currentToken.type == Token.IDs.ADD || currentToken.type == Token.IDs.SUB) {
            advance();
            exprMul();
        }
    }


    private void exprRel()  throws ParseException{
        exprAdd();
        while (currentToken.type == Token.IDs.LESS ||
                currentToken.type == Token.IDs.LESSEQ ||
                currentToken.type == Token.IDs.GREATER ||
                currentToken.type == Token.IDs.GREATEREQ) {
            advance();
            exprAdd();
        }
    }

    private void exprEq()  throws ParseException{
        exprRel();
        while (currentToken.type == Token.IDs.EQUALS || currentToken.type == Token.IDs.NOTEQ) {
            advance();
            exprRel();
        }
    }

    private void exprAnd()  throws ParseException{
        exprEq();
        while (currentToken.type == Token.IDs.AND) {
            eat(Token.IDs.AND);
            exprEq();
        }
    }

    private void exprOr()  throws ParseException{
        exprAnd();
        while (currentToken.type == Token.IDs.OR) {
            eat(Token.IDs.OR);
            exprAnd();
        }
    }


    private void exprAssign() throws ParseException {
        exprOr();
        if (currentToken.type == Token.IDs.ASSIGN) {
            eat(Token.IDs.ASSIGN);
            exprAssign();
        }
    }

    private void expr()  throws ParseException{
        exprAssign();
    }





}

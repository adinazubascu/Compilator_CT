public class Token {

    public enum IDs {
        // Keywords
        IF, ELSE, FOR, WHILE, BREAK, RETURN,
        INT, DOUBLE, CHAR, STRUCT, VOID,

        // Identifiers and Constants
        ID, CONST_INT, CONST_REAL, CONST_CHAR, CONST_STRING,

        // Operators
        ADD, SUB, MUL, DIV,
        ASSIGN, EQUALS,NOTEQ, NOT,
        LESS, GREATER, LESSEQ, GREATEREQ,
        ANDBIT, ORBIT, AND, OR,

        // Delimiters / Separators
        DOT, SEMICOLON, COMMA,
        LPAR, RPAR,     // ( )
        LBRACE, RBRACE, // [ ]
        LACC, RACC,     // { }

        //Comments
        COMMENT,LINECOMMENT,

        // Special
        END
    }
    ;
    public enum IntType{DECIMAL,HEXA,OCTAL,REAL}
    public IDs type;
    public IntType intType=null;
    public String text;
    public Integer int_nr;
    public Double float_nr;
    public int myLine;
    public boolean expectingEscape = false;
    public boolean error = false;
    public String errorMessage = "";
    public boolean exponentPresent =false;
    public Token(int line) {
        type =IDs.END;
        text="";
        myLine = line;
    }
    public Token(IDs id,int line) {
        type =id;
        myLine=line;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token[type=").append(type);

        sb.append(", text=\"").append(text).append("\"");

        if (type == IDs.CONST_INT && intType != null) {


                sb.append(" (").append(intType).append(")");

        } else if (type == IDs.CONST_REAL && intType != null) {


                sb.append(" (").append(intType).append(")");

        }

        if (error) {
            sb.append(", ERROR: ").append(errorMessage);
        }

        sb.append(", line=").append(myLine).append("]");
        return sb.toString();
    }

    public boolean isWrong() {
        return error;
    }
    public void errorMessage() {
        System.out.println("Error at line "+myLine+" : "+errorMessage);
    }
    public void setError(String string) {
        error = true;
        errorMessage = string;
    }


}

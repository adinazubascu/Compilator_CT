import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lexical {



    public List<Token> tokens = new ArrayList<Token>();
    private File sourceFile;
    private List<String> formatParams = Arrays
            .asList(new String[] { "\\a", "\\b", "\\f", "\\n", "\\r", "\\t", "\\v", "\\\\", "\\'", "\\\"", "\\?" });
    public Lexical(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void compile() throws FileNotFoundException {
        FileReader fr = new FileReader(sourceFile);
        System.out.println("Lexical compile of " + sourceFile);

        try (BufferedReader br = new BufferedReader(fr)) {
            String lineContent;
            int currentLine = 1;

            while ((lineContent = br.readLine()) != null) {
                // Add a newline back if needed by your tokenizer logic
                Token tmp = new Token(currentLine); // Start a new token for this line

                for (int i = 0; i < lineContent.length(); i++) {
                    char character = lineContent.charAt(i);
                    tmp = CreateTokens(tmp, character);
                }

                if (!tmp.text.isEmpty() && tmp.type != Token.IDs.END) {
                    tokens.add(tmp);
                }
                currentLine++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Token CreateTokens(Token token, char character) {

         switch(token.type){

             case END:
                 if( Character.isDigit(character)){

                     token.type= Token.IDs.CONST_INT;
                     token.text=token.text+character;
                     break;
                 }
                 if( Character.isLetter(character) || character=='_'){
                     token.type= Token.IDs.ID;
                     token.text=token.text+character;
                     break;
                 }
                 //const char and string
                 if(character=='\''){
                     token.type= Token.IDs.CONST_CHAR;
                 }
                 if(character=='"'){
                     token.type= Token.IDs.CONST_STRING;
                 }
                 // Operators
                 if (character == '+') {
                     token.text += '+';
                     token.type = Token.IDs.ADD;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }
                 if (character == '/') {
                     token.text += '/';
                     token.type = Token.IDs.DIV;
                     break;

                 }
                 if (character == '-') {
                     token.text += '-';
                     token.type = Token.IDs.SUB;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == '*') {
                     token.text += '*';
                     token.type = Token.IDs.MUL;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == '!') {
                     token.text += '!';
                     token.type = Token.IDs.NOT;
                     break;
                 }

                 if (character == '=') {
                     token.text += '=';
                     token.type = Token.IDs.ASSIGN;
                     break;
                 }

                 if (character == '<') {
                     token.text += '<';
                     token.type = Token.IDs.LESS;
                     break;
                 }

                 if (character == '>') {
                     token.text += '>';
                     token.type = Token.IDs.GREATER;
                     break;
                 }

                 if (character == '&') {
                     token.text += '&';
                     token.type = Token.IDs.ANDBIT;
                     break;
                 }

                 if (character == '|') {
                     token.text += '|';
                     token.type = Token.IDs.ORBIT;
                     break;
                 }

                 if (character == '.') {
                     token.text += '.';
                     token.type = Token.IDs.DOT;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 // Delimiters
                 if (character == ';') {
                     token.text += ';';
                     token.type = Token.IDs.SEMICOLON;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == ',') {
                     token.text += ',';
                     token.type = Token.IDs.COMMA;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == '(') {
                     token.text += '(';
                     token.type = Token.IDs.LPAR;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == ')') {
                     token.text += ')';
                     token.type = Token.IDs.RPAR;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == '[') {
                     token.text += '[';
                     token.type = Token.IDs.LBRACE;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == ']') {
                     token.text += ']';
                     token.type = Token.IDs.RBRACE;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == '{') {
                     token.text += '{';
                     token.type = Token.IDs.LACC;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (character == '}') {
                     token.text += '}';
                     token.type = Token.IDs.RACC;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 break;

             case ASSIGN:
                 if (character == '=') {
                     token.text += '=';
                     token.type = Token.IDs.EQUALS;
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }
                 else{
                     tokens.add(token);
                     token=CreateTokens(new Token(token.myLine),character);
                     break;
                 }
             case NOT:
                 if (character == '=') {
                     token.text = "!=";
                     token.type = Token.IDs.NOTEQ;
                     tokens.add(token);
                     token = new Token(token.myLine);
                 } else {
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                 }
                 break;

             case ANDBIT:
                 if (character == '&') {
                     token.text = "&&";
                     token.type = Token.IDs.AND;
                     tokens.add(token);
                     token = new Token(token.myLine);
                 } else {
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                 }
                 break;

             case ORBIT:
                 if (character == '|') {
                     token.text = "||";
                     token.type = Token.IDs.OR;
                     tokens.add(token);
                     token = new Token(token.myLine);
                 } else {
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                 }
                 break;

             case LESS:
                 if (character == '=') {
                     token.text = "<=";
                     token.type = Token.IDs.LESSEQ;
                     tokens.add(token);
                     token = new Token(token.myLine);
                 } else {
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                 }
                 break;

             case GREATER:
                 if (character == '=') {
                     token.text = ">=";
                     token.type = Token.IDs.GREATEREQ;
                     tokens.add(token);
                     token = new Token(token.myLine);
                 } else {
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                 }
                 break;

             case DIV:
                 if (character == '*') {
                     token.text = "/*";
                     token.type = Token.IDs.COMMENT;
                 } else if (character == '/') {
                     token.text = "//";
                     token.type = Token.IDs.LINECOMMENT;
                 } else {

                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                 }
                 break;

             case COMMENT:
                 token.text += character;
                 if (token.text.endsWith("*/")) {
                     tokens.add(token);
                     token = new Token(token.myLine);
                 }
                 break;

             case LINECOMMENT:
                 if (character == '\n' || character == '\r') {
                     tokens.add(token);
                     token = new Token(token.myLine);
                 } else {
                     token.text += character;
                 }
                 break;
             case ID:
                 if (Character.isAlphabetic(character) || character == '_' || Character.isDigit(character)) {
                     token.text += character;
                     if (isKeyWord(token)) {
                         tokens.add(token);
                         token = new Token(token.myLine);
                     }
                     break;
                 } else {
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                     break;
                 }
             case CONST_INT:
                 if (token.intType == null) {
                     // Possible start of hexadecimal
                     if (character == 'x' && token.text.equals("0")) {
                         token.intType = Token.IntType.HEXA;
                         token.text += character;
                         break;
                     }

                     // Real number
                     if (character == '.') {
                         token.type = Token.IDs.CONST_REAL;
                         token.intType = Token.IntType.REAL;
                         token.text += character;
                         break;
                     }

                     if (character == 'e' || character == 'E') {
                         token.type = Token.IDs.CONST_REAL;
                         token.intType = Token.IntType.REAL;
                         token.exponentPresent = true;
                         token.text += character;
                         break;
                     }

                     // Digit: determine type
                     if (Character.isDigit(character)) {
                         if (token.text.startsWith("0") ) {
                             token.intType = Token.IntType.OCTAL;
                         } else {
                             token.intType = Token.IntType.DECIMAL;
                         }
                         token.text += character;
                         break;
                     }

                     // Invalid start for INT
                     if (Character.isLetter(character)) {
                         token.setError("Invalid number format");
                         token.text += character;
                         tokens.add(token);
                         token = new Token(token.myLine);
                         break;
                     }

                     // Number ended normally (separator, operator, etc.)
                     tokens.add(token);
                     token = CreateTokens(new Token(token.myLine), character);
                     break;
                 }

                 // HEXADECIMAL parsing
                 if (token.intType == Token.IntType.HEXA) {
                     if (Character.digit(character, 16) != -1) {
                         token.text += character;
                         break;
                     } else if (Character.isLetter(character)) {
                         token.setError("Invalid hexadecimal format");
                         token.text += character;
                         tokens.add(token);
                         token = new Token(token.myLine);
                         break;
                     } else {
                         // Valid end of HEX number
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                 }

                 // OCTAL parsing
                 if (token.intType == Token.IntType.OCTAL) {
                     if (character >= '0' && character <= '7') {
                         token.text += character;
                         break;
                     } else if (Character.isDigit(character)) {
                         token.setError("Invalid octal digit: " + character);
                         token.text += character;
                         tokens.add(token);
                         token = new Token(token.myLine);
                         break;
                     } else if (Character.isLetter(character) ){
                         token.setError("Invalid number, assumed Octal");
                         token.text += character;
                         tokens.add(token);
                         token = new Token(token.myLine);
                         break;
                     } else {
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                 }

                 // DECIMAL parsing
                 if (token.intType == Token.IntType.DECIMAL) {


                     // Real number
                     if (character == '.') {
                         token.type = Token.IDs.CONST_REAL;
                         token.intType = Token.IntType.REAL;
                         token.text += character;
                         break;
                     }

                     if (character == 'e' || character == 'E') {
                         token.type = Token.IDs.CONST_REAL;
                         token.intType = Token.IntType.REAL;
                         token.exponentPresent = true;
                         token.text += character;
                         break;
                     }
                     if (Character.isDigit(character)) {
                         token.text += character;
                         break;
                     } else if (Character.isLetter(character)) {
                         token.setError("Invalid number format");
                         token.text += character;
                         tokens.add(token);
                         token = new Token(token.myLine);
                         break;
                     } else {
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                 }

                      break;

             case CONST_REAL:

                 if (character == '.') {
                     if (token.exponentPresent) {
                         token.setError("Invalid real number: dot after exponent");
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                     if (token.text.contains(".")) {
                         token.setError("Invalid real number: multiple dots");
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                     token.intType = Token.IntType.REAL;
                     token.text += character;
                     break;
                 }

                 if (character == 'e' || character == 'E') {
                     if (token.exponentPresent) {
                         token.setError("Invalid real number: multiple exponents");
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                     token.exponentPresent = true;
                     token.intType = Token.IntType.REAL;
                     token.text += character;
                     break;
                 }

                 if (character == '+' || character == '-') {
                     if (token.text.endsWith("e") || token.text.endsWith("E")) {
                         token.text += character;
                         break;
                     } else {
                         tokens.add(token);
                         token = CreateTokens(new Token(token.myLine), character);
                         break;
                     }
                 }

                 if (Character.isDigit(character)) {
                     token.intType = Token.IntType.REAL;
                     token.text += character;
                     break;
                 }

                 // Finalize token on invalid character
                 tokens.add(token);
                 token = CreateTokens(new Token(token.myLine), character);
                 break;


             case CONST_CHAR:
                 if (token.expectingEscape) {
                     String escapeSeq = "\\" + character;
                     if (!formatParams.contains(escapeSeq)) {
                         token.setError("Invalid char formatter");
                     }
                     token.text += formatString(escapeSeq);
                     token.expectingEscape = false;
                     break;
                 }

                 if (character == '\\') {
                     token.expectingEscape = true;
                     break;
                 }

                 if (character == '\'') {
                     if (token.text.isEmpty()) {
                         token.setError("Empty char literal");
                     } else if (token.text.length() > 1) {
                         token.setError("Too many characters in char literal");
                     }
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 if (!token.text.isEmpty()) {
                     token.setError("Invalid char literal length");
                 }

                 token.text += character;
                 break;

             case CONST_STRING:
                 if (token.expectingEscape) {
                     String escapeSeq = "\\" + character;

                     if (formatParams.contains(escapeSeq)) {
                         token.text += formatString(escapeSeq);
                     } else {
                         token.setError("Invalid string escape sequence");
                         token.text += escapeSeq; // still include it for debugging
                     }

                     token.expectingEscape = false;
                     break;
                 }

                 if (character == '\\') {
                     token.expectingEscape = true;
                     break;
                 }

                 if (character == '\"') {
                     tokens.add(token);
                     token = new Token(token.myLine);
                     break;
                 }

                 token.text += character;
                 break;

             default:break;

         }
        return token;
    }

    public static boolean isKeyWord(Token token) {
        switch (token.text) {
            case "if":
                token.type = Token.IDs.IF;
                break;
            case "else":
                token.type = Token.IDs.ELSE;
                break;
            case "for":
                token.type = Token.IDs.FOR;
                break;
            case "while":
                token.type = Token.IDs.WHILE;
                break;
            case "break":
                token.type = Token.IDs.BREAK;
                break;
            case "return":
                token.type = Token.IDs.RETURN;
                break;
            case "int":
                token.type = Token.IDs.INT;
                break;
            case "double":
                token.type = Token.IDs.DOUBLE;
                break;
            case "char":
                token.type = Token.IDs.CHAR;
                break;
            case "void":
                token.type = Token.IDs.VOID;
                break;
            case "struct":
                token.type = Token.IDs.STRUCT;
                break;
            default:
                return false;
        }
        return true;
    }
    private static String formatString(String end) {

        int c;
        switch (end) {

            case "\\'":
                c = 0x27;
                break;
            case "\\\"":
                c = 0x22;
                break;

            case "\\?":
                c = 0x3f;
                break;

            case "\\\\":
                c = 0x5c;
                break;
            case "\\a":
                c = 0x07;
                break;

            case "\\b":
                c = 0x08;
            case "\\f":
                c = 0x0c;
                break;

            case "\\n":
                c = 0x0a;
                break;
            case "\\r":
                c = 0x0d;
                break;
            case "\\t":
                c = 0x09;
                break;

            case "\\v":
                c = 0x0b;
                break;
            default:
                // System.out.print("blsfaf");
                return end;
        }

        return "" + (char) c;

    }

    public static void main(String[] args) {
        try {
            File file = new File("C:\\Users\\Sandy\\IdeaProjects\\Compilator_CT\\src\\input.txt");
            Lexical lexical = new Lexical(file);
            lexical.compile();

            for (Token t : lexical.getTokens()) {
                System.out.println(t);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Input file not found.");
        }
     /*   for (int i = 0; i <= 9; i++) {

            File file = new File("C:\\Users\\Sandy\\IdeaProjects\\Compilator_CT\\src\\input.txt" + i + ".c");
            Lexical lexical = new Lexical(file);
            try {
                lexical.compile();
                for (Token t : lexical.getTokens()) {
                    System.out.println(t);
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
        }*/

    }

}
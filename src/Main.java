import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws ParseException, FileNotFoundException {
        for (int i = 0; i <= 9; i++) {
            String filePath = "C:\\Users\\Sandy\\IdeaProjects\\Compilator_CT\\src\\CCodeTest\\" + i + ".c";
            System.out.println("Parsing file: " + filePath);
            try {
                File file = new File(filePath);
                Lexical lexical = new Lexical(file);
                lexical.compile();  // Tokenizes input, removing comments
                Parser parser = new Parser(lexical.getTokens());
                parser.parse();     // Parses token stream
                System.out.println("Parsing completed successfully.\n");
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath + "\n");
            } catch (RuntimeException e) {
                System.err.println("Syntax error in file " + filePath + ":");
                System.err.println(e.getMessage() + "\n");
            } catch (Exception e) {
                System.err.println("Unexpected error while parsing " + filePath + ":");
                e.printStackTrace();
            }
        }
    }
}

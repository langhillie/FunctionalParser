import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.*;

public class scanner {
	
	
	public static void main(String[] args) {
	    String[] operators = { "!", "%", "&", "|", "*", "-", "/", "+", "=", "#", "<", ">", "(", ")", "[", "]", "{", "}", ",", "@"};
		String[] delimiters = {};
		String[] keywords = {"true", "false", "nil", "let", "letrec", "def", "set", "lambda", "if", "elseif", "else", "guard", "catch", "raise"};
		
		String integerLiterals = "[0-9][0-9]*";
		String stringLiteral = "[^\"]*";
		String Symbol = "[a-zA-Z_][0-9a-zA-Z_]*";
		
		Scanner input = new Scanner(System.in);
		int lineNum = 0;
		int colNum = 0;
		String curLine;
		String curSymbol = "";
		
		//input.hasNextLine()
		while (input.hasNextLine()) {
			lineNum++;
			colNum = 0;
			// get next line
			curLine = input.nextLine();
			
			// Loop through line character by character
			while (colNum < curLine.length()) {
				if (Arrays.asList(operators).contains(String.valueOf(curLine.charAt(colNum)))) {
					System.out.println("line " + lineNum + " col " + (colNum + 1) + " : " + curLine.charAt(colNum));
					colNum++;
				}
				else if (Pattern.matches("[a-zA-Z_]", String.valueOf(curLine.charAt(colNum)))) {
					int currCol = colNum;
					String symbol = "";
					while (currCol < curLine.length() && Pattern.matches("[0-9a-zA-Z_]", String.valueOf(curLine.charAt(currCol)))) {
						symbol += curLine.charAt(currCol);
						currCol++;
					}
					// Here we would check if symbol is a keyword
					
					System.out.println("line " + lineNum + " col " + (colNum + 1) + " : " + symbol);
					colNum = currCol;
				}
				else if (Pattern.matches("[0-9]", String.valueOf(curLine.charAt(colNum)))) {
					int currCol = colNum;
					String integerLiteral = "";
					while (currCol < curLine.length() && Pattern.matches("[0-9]", String.valueOf(curLine.charAt(currCol)))) {
						integerLiteral += curLine.charAt(currCol);
						currCol++;
					}
					
					System.out.println("line " + lineNum + " col " + (colNum + 1) + " : " + integerLiteral);
					colNum = currCol;
					
				}
				else if (curLine.charAt(colNum) == '"') {
					String strLiteral = "";
					int currCol = colNum;
					currCol++;
					while (currCol < curLine.length() && curLine.charAt(currCol) != '"') {			
						strLiteral += curLine.charAt(currCol);	
						currCol++;
					}
					System.out.println("line " + lineNum + " col " + (colNum + 1) + " : \"" + strLiteral + "\"");
					// maybe bug here (+1?)
					colNum = currCol + 1;
				}
				else if (curLine.charAt(colNum) == ' ') {
					colNum++;
				}
			}
			//System.out.println();
			//System.out.println("line " + lineNum + " col " + colNum + " : " + curSymbol);
			
		}

	}

}

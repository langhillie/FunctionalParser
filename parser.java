
/*
  Parser for CSCI 3136 Assignment 5
  Update: added semantic analysis for Assignment 7

  Author: Justin Langille
  Date: June 26, 2019
  Updated: July 11, 2019

  Update2: July 17th, 2019
  Added DEF and LET
*/

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import java.util.ArrayList;

public class parser {
	public static ArrayList<String> tokens;
	// Used to keep track of location in token array
	public static int i;

	public static String string = "\".*\"";
	public static String integer = "[0-9][0-9]*";
	public static String symbols = "[a-zA-Z][a-zA-Z0-9]*";
	public static String literals = "true|false|nil";

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String curLine;
		tokens = new ArrayList<String>();
		i = 0;
		// Getting input from Scanner
		while (input.hasNextLine()) {
			curLine = input.nextLine();
			tokens.add(getToken(curLine));
		}
		// EOF
		tokens.add("$");
		
		// Printing scanner output
		//for (int j = 0; j < tokens.size() - 1; j++) {
		//	System.out.println(j + ": " + tokens.get(j));
		//}

		Node head = new Node();
		// Start Parsing
		S(head);

		for (int i = 0; i < head.list.size(); i++) {
			System.out.println(head.list.get(i));
		}
	}

	// Gets the symbol from the scanner output
	public static String getToken(String line) {
		String[] split = line.split(":", 2);
		return split[1].substring(1);
	}

	public static void throwError(String function) {
		System.out.print("Syntax Error");
		// For Debugging
		System.out.print(" " + function + " " + i + " " + tokens.get(i));
		System.out.println();
		System.exit(0);
	}

	public static void productionWrite(String production, Node currentNode) {
		// Disabled for assignment 7
		//System.out.println(production);
		//currentNode.frame.forEach((key, value) -> System.out.println("	"+key + ":" + value));
	}

	public static void S(Node currentNode) {
		if (tokens.get(i).matches("\\$")) {
			productionWrite("S -> EPSILON", currentNode);
			currentNode.list.clear();
		} else if (tokens.get(i)
				.matches("\\(|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols + "|\\[")) {
			productionWrite("S -> E_LIST", currentNode);

			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			childNode.frame = currentNode.frame;
			E_LIST(childNode);
			currentNode.frame = childNode.frame;
			currentNode.list = childNode.list;
		} else {
			throwError("S");
		}
	}

	public static void E_LIST(Node currentNode) {
		if (tokens.get(i).matches("\\(|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols + "|\\[")) {
			productionWrite("E_LIST -> EXPR E_TAIL", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			
			EXPR(childNode1);
			childNode2.frame = childNode1.frame;
			
			E_TAIL(childNode2);
			currentNode.frame = childNode2.frame;
			currentNode.list = childNode2.list;
			currentNode.list.add(0, childNode1.val);
			
		} else {
			throwError("E_LIST");
		}
	}

	public static void E_TAIL(Node currentNode) {
		if (tokens.get(i).matches("\\(|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols + "|\\[")) {
			productionWrite("E_TAIL -> E_LIST", currentNode);

			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			childNode.frame = currentNode.frame;
			
			E_LIST(childNode);
			currentNode.list = childNode.list;
			currentNode.frame = childNode.frame;
		} else if (tokens.get(i).matches("\\$|\\}")) {
			productionWrite("E_TAIL -> epsilon", currentNode);
		} else {
			throwError("E_TAIL");
			currentNode.list.clear();
		}
	}

	public static void EXPR(Node currentNode) {
		
		if (tokens.get(i).matches("head")) {
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			head(childNode1);
			
			currentNode.val = childNode1.val;
		} else if (tokens.get(i).matches("tail")) {
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			tail(childNode1);
			
			currentNode.val = childNode1.val;
		} else if (tokens.get(i).matches("prepend")) {
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			prepend(childNode1);
			
			currentNode.val = childNode1.val;
		} else if (tokens.get(i).matches("if")) {
			productionWrite("EXPR -> IF", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			IF(childNode1);
			
			currentNode.val = childNode1.val;
		} else if (tokens.get(i).matches("set")) {
			productionWrite("EXPR -> SET", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			SET(childNode1);
			
			currentNode.val = childNode1.val;
			currentNode.frame = childNode1.frame;
			
		} else if (tokens.get(i).matches("lambda")) {
			productionWrite("EXPR -> LAMBDA", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			LAMBDA(childNode1);
			
			currentNode.val = childNode1.val;
			
		} else if (tokens.get(i).matches("def")) {
			productionWrite("EXPR -> DEF", currentNode);

			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			childNode.frame = currentNode.frame;

			DEF(childNode);

			currentNode.val = childNode.val;
			currentNode.frame = childNode.frame;
		} else if (tokens.get(i).matches("let")) {
			productionWrite("EXPR -> LET", currentNode);

			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			//System.out.println("LET before: " + currentNode.frame);
			// I don't know why, but I think doing "childNode.frame = currentNode.frame;" actually makes a reference?
			// 		(Side note: If it actually is a reference perhaps I need to update the entire program to do this for each semantic rule)
			// Updating the child node frame updates the parent node frame, even though it shouldn't
			// So I'm creating a copy here so I don't have to deal with that
			childNode.frame = new HashMap<String, Object>(currentNode.frame);
			
			LET(childNode);
			//System.out.println("LET after: " + currentNode.frame);
			currentNode.val = childNode.val;
			
		} else if (tokens.get(i).matches("\\(|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols + "|\\[")) {
			productionWrite("EXPR -> S_EXPR", currentNode);

			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			childNode.frame = currentNode.frame;

			S_EXPR(childNode);
			currentNode.val = childNode.val;
		} else {
			throwError("S_EXPR");
		}
	}

	public static void SET(Node currentNode) {
		if (tokens.get(i).equals("set")) {
			productionWrite("SET -> 'set' SYMBOL '=' EXPR", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;
			
			i++; // 'set'

			currentNode.val = currentNode.frame.get(tokens.get(i)); // SET function needs to return old value
			// This /should/ be done in SYMBOL, if I had more time I would clean it up
			childNode1.val = tokens.get(i);
			productionWrite("SYMBOL -> symbol(" + tokens.get(i) + ")", childNode1);
			i++; // SYMBOL
			
			i++; // =
			EXPR(childNode2);
			
			currentNode.frame.put((String) childNode1.val, childNode2.val);
			
		} else {
			throwError("SET");
		}
	}
	
	public static void LAMBDA(Node currentNode) {
		if (tokens.get(i).equals("lambda")) {
			productionWrite("LAMBDA -> 'lambda' '(' PARAMS ')' BODY", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			
				
			i++;
			i++;
			Closure function = new Closure();
			PARAMS(childNode1);
			function.params = childNode1.list;
			i++;
			
			// creating pointer to where the start of the function is
			function.start = i;
			
			childNode2.frame = currentNode.frame;
			//
			//BODY(childNode2);
			// Skipping through the body
			int stack = 0;

			do {
				if (tokens.get(i).equals("{")) {
					stack++;
				} else if (tokens.get(i).equals("}")) {
					stack--;
				}
				i++;
			} while (stack != 0);
			
			// Closure object
			currentNode.val = function;
		} else {
			throwError("LAMBDA");
		}
	}
	
	public static void PARAMS(Node currentNode) {
		if (tokens.get(i).matches(symbols)) {
			productionWrite("PARAMS -> SYMBOL P_LIST", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;
			
			SYMBOL(childNode1);
			P_LIST(childNode2);
			
			currentNode.list.add(childNode1.val);
			currentNode.list.addAll(childNode2.list);
			
		} else if(tokens.get(i).equals(")")) {
			productionWrite("PARAMS -> epsilon", currentNode);
		} else {
			throwError("PARAMS");
		}
	}
	
	public static void P_LIST(Node currentNode) {
		if (tokens.get(i).equals(",")) {
			productionWrite("P_LIST -> ',' SYMBOL P_LIST", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			i++;
			//SYMBOL(childNode1);
			productionWrite("SYMBOL -> symbol(" + tokens.get(i) + ")", childNode1);
			childNode1.val = tokens.get(i);
			i++;
			
			// Adding symbol to list of params
			currentNode.list.add(childNode1.val);
			
			// Going through the rest of the list
			P_LIST(childNode2);
			
			// Appending the rest of the params recursively
			currentNode.list.addAll(childNode2.list);
			
		} else if (tokens.get(i).equals(")")) {
			productionWrite("P_LIST -> epsilon", currentNode);
		} else {
			throwError("P_LIST");
		}
	}
	
	// BONUS
	public static void IF(Node currentNode) {
		if (tokens.get(i).equals("if")) {
			productionWrite("IF -> 'if' EXPR BODY ELSEIF ELSE", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			Node childNode3 = new Node();
			childNode3.parent = currentNode;
			currentNode.children.add(childNode3);
			
			Node childNode4 = new Node();
			childNode4.parent = currentNode;
			currentNode.children.add(childNode4);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;
			childNode3.frame = currentNode.frame;
			childNode4.frame = currentNode.frame;
			
			i++; // 'if'
			
			EXPR(childNode1);
			
			if ( (Boolean) childNode1.val == true) {
				BODY(childNode2);
				currentNode.val = childNode2.val;	
			} else {
				
				int stack = 0;
				do {
					
					if (tokens.get(i).equals("{")) {
						stack++;
					} else if (tokens.get(i).equals("}")) {
						stack--;
					}
					i++;
				} while (stack != 0);
				
				
				
				
				ELSEIF(childNode3);
				
				if (childNode3.val != null) {
					currentNode.val = childNode3.val;
				} else {
					ELSE(childNode4);
					currentNode.val = childNode4.val;
				}
			}
			// Skipping through the rest of the else statements
			while (tokens.get(i).matches("else|elseif")) {
				int stack = 0;
				do {
					i++;
					if (tokens.get(i).equals("{")) {
						stack++;
					} else if (tokens.get(i).equals("}")) {
						stack--;
					}
				} while (stack != 0);
			}
		} else {
			throwError("IF");
		}
	}
	
	public static void ELSEIF(Node currentNode) {
		if (tokens.get(i).equals("elseif")) {
			productionWrite("ELSEIF -> 'elseif' EXPR BODY ELSEIF", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			Node childNode3 = new Node();
			childNode3.parent = currentNode;
			currentNode.children.add(childNode3);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;
			childNode3.frame = currentNode.frame;
			
			i++; // 'elseif'

			EXPR(childNode1);
			
			if ( (boolean) childNode1.val == true) {
				BODY(childNode2);
				currentNode.val = childNode2.val;
			} else {
				int stack = 0;
				do {
					
					if (tokens.get(i).equals("{")) {
						stack++;
					} else if (tokens.get(i).equals("}")) {
						stack--;
					}
					i++;
				} while (stack != 0);
				
				
				ELSEIF(childNode3);
				currentNode.val = childNode3.val;
			}

		} else {
			productionWrite("ELSEIF -> epsilon", currentNode);
		}
	}
	
	public static void ELSE(Node currentNode) {
		if (tokens.get(i).equals("else")) {
			productionWrite("ELSE -> 'else' BODY", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			
			i++; // 'else'
			
			BODY(childNode1);
			
			currentNode.val = childNode1.val;
		} else {
			productionWrite("ELSE -> epsilon", currentNode);
		}
	}
	
	public static void DEF(Node currentNode) {
		if (tokens.get(i).matches("def")) {
			productionWrite("DEF -> 'def' SYMBOL '=' EXPR", currentNode);

			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);

			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			i++;
			childNode1.val = tokens.get(i);
			
			// We need this instead of calling SYMBOL directly because
			// We might need to re-define symbols
			// EDIT for assignment 9: Actually, the SET keyword is supposed to do this apparently.
			productionWrite("SYMBOL -> symbol(" + tokens.get(i) + ")", childNode1);
			childNode1.val = tokens.get(i);
			i++;
			
			i++;
			EXPR(childNode2);


			currentNode.val = childNode2.val;
			
			// New binding being created
			currentNode.frame.put((String) childNode1.val, childNode2.val);
		} else {
			throwError("DEF");
		}
	}

	public static void LET(Node currentNode) {
		if (tokens.get(i).matches("let")) {
			productionWrite("LET -> 'let' V_LIST BODY", currentNode);

			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);

			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);

			i++;
	
			childNode1.frame = currentNode.frame;
			V_LIST(childNode1);
			childNode2.frame = childNode1.frame;
			BODY(childNode2);
			currentNode.val = childNode2.val;

		} else {
			throwError("LET");
		}
	}

	public static void V_LIST(Node currentNode) {
		if (tokens.get(i).matches(symbols)) {
			productionWrite("V_LIST -> SYMBOL '=' EXPR V_TAIL", currentNode);

			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);

			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);

			Node childNode3 = new Node();
			childNode3.parent = currentNode;
			currentNode.children.add(childNode3);

			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;
			
			// This is bad but it works
			// I don't want to go into the symbol method because it will mess up how DEF works
			productionWrite("SYMBOL -> symbol(" + tokens.get(i) + ")", childNode1);
			childNode1.val = tokens.get(i);
			i++;
			
			
			i++;
			EXPR(childNode2);

			childNode3.frame = currentNode.frame;
			V_TAIL(childNode3);
			currentNode.frame = childNode3.frame;
			currentNode.frame.put((String) childNode1.val, childNode2.val);
		} else {
			throwError("V_LIST");
		}
	}

	public static void V_TAIL(Node currentNode) {
		if (tokens.get(i).matches(",")) {
			productionWrite("V_TAIL -> ',' V_LIST", currentNode);
			i++;
			
			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			childNode.frame = currentNode.frame;
			
			V_LIST(childNode);
			
			currentNode.frame = childNode.frame;
		} else if (tokens.get(i).matches("\\{")) {
			productionWrite("V_TAIL -> epsilon", currentNode);
		} else {
			throwError("V_TAIL");
		}
	}

	public static void BODY(Node currentNode) {
		if (tokens.get(i).matches("\\{")) {
			productionWrite("BODY -> '{' E_LIST '}'", currentNode);
			
			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			childNode.frame = currentNode.frame;
			
			i++; // {
			E_LIST(childNode);
			// Getting the value of the last expression in the LET statement
			currentNode.val = childNode.list.get(childNode.list.size() - 1);
			//System.out.println(currentNode.val);
			
			if (tokens.get(i).matches("\\}")) {
				i++;
			} else {
				throwError("BODY");
			}
		} else {
			throwError("BODY");
		}
	}

	public static void S_EXPR(Node currentNode) {
		if (tokens.get(i).matches("\\(|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols + "|\\[")) {
			productionWrite("S_EXPR -> ANDOP S_TAIL", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			ANDOP(childNode1);
			if (currentNode.tmp != null && childNode1.val != null) {
				childNode2.tmp = (Boolean) currentNode.tmp || (Boolean) childNode1.val;
			} else if (currentNode.tmp != null)
				childNode2.tmp = currentNode.tmp;
			else if (childNode1.val != null)
				childNode2.tmp = childNode1.val;
			S_TAIL(childNode2);
			currentNode.val = childNode2.val;
		} else {
			throwError("S_EXPR");
		}

	}

	public static void S_TAIL(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);

		childNode.frame = currentNode.frame;
		
		if (tokens.get(i).matches("\\|")) {
			productionWrite("S_TAIL -> '|' S_EXPR", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			S_EXPR(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches(",|\\)|\\(|\\[|\\]|\\{|\\}|" + integer + "|" + string + "|" + literals + "|" + symbols + "|-|!|\\$")) {
			productionWrite("S_TAIL -> epsilon", currentNode);
			currentNode.val = currentNode.tmp;
		} else {
			throwError("S_TAIL");
		}
	}

	public static void ANDOP(Node currentNode) {

		if (tokens.get(i).matches("\\(|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols + "|\\[")) {
			productionWrite("ANDOP -> RELOP A_TAIL", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			RELOP(childNode1);
			if (currentNode.tmp != null && childNode1.val != null) {
				childNode2.tmp = (Boolean) currentNode.tmp && (Boolean) childNode1.val;
			} else if (currentNode.tmp != null)
				childNode2.tmp = currentNode.tmp;
			else if (childNode1.val != null)
				childNode2.tmp = childNode1.val;
			A_TAIL(childNode2);
			currentNode.val = childNode2.val;
		}
	}

	public static void A_TAIL(Node currentNode) {
		if (tokens.get(i).equals("&")) {
			productionWrite("A_TAIL -> '&' ANDOP", currentNode);
			i++;

			Node childNode = new Node();
			childNode.parent = currentNode;
			currentNode.children.add(childNode);
			
			childNode.frame = currentNode.frame;

			childNode.tmp = currentNode.tmp;
			ANDOP(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i)
				.matches("\\||,|\\)|\\(|\\{|\\}|" + integer + "|-|!|\".*\"|true|false|nil|" + symbols + "|\\[|\\]|\\$|&")) {
			productionWrite("A_TAIL -> epsilon", currentNode);
			currentNode.val = currentNode.tmp;
		} else {
			throwError("A_TAIL");
		}
	}

	public static void RELOP(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);

		if (tokens.get(i).matches("\\(|[0-9][0-9]*|\".*\"|true|false|nil|-|!|[a-zA-Z][a-zA-Z0-9]*|\\[")) {
			productionWrite("RELOP -> TERM R_TAIL", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			TERM(childNode1);

			childNode2.tmp = calcOperator(currentNode.tmp, currentNode.op, childNode1.val, currentNode.frame);

			R_TAIL(childNode2);
			currentNode.val = childNode2.val;

		}
	}

	// Applies given operator to the given values and returns the result
	@SuppressWarnings("unchecked")
	private static Object calcOperator(Object arg1, String operator, Object arg2, HashMap<String, Object> curFrame) {
		if (arg1 == null || operator == null || arg2 == null) {
			if (arg1 != null)
				return arg1;
			else if (arg2 != null)
				return arg2;
			else
				return null;
		}
		//System.out.println(" CALC: " + arg1 + " " + operator + " " + arg2);

		try {
			if (operator.equals("<")) {
				return ((int) arg1 < (int) arg2);
			} else if (operator.equals(">")) {
				return ((int) arg1 > (int) arg2);
			} else if (operator.equals("=")) {
				return ((int) arg1 == (int) arg2);
			} else if (operator.equals("#")) {
				return ((int) arg1 != (int) arg2);
			} else if (operator.equals("+")) {
				if (arg1 instanceof String) {
					return (String) arg1.toString().substring(0, arg1.toString().length() - 1)
							+ (String) arg2.toString().substring(1);
				} else if (arg1 instanceof ArrayList) {
					if (arg2 instanceof ArrayList) {
						((ArrayList<Object>) arg1).addAll((ArrayList<Object>) arg2);
					} else {
						((ArrayList<Object>) arg1).add(arg2);
					}
					return arg1;
				} else {
					return ((int) arg1 + (int) arg2);
				}

			} else if (operator.equals("-")) {
				return ((int) arg1 - (int) arg2);
			} else if (operator.equals("*")) {
				return ((int) arg1 * (int) arg2);
			} else if (operator.equals("/")) {
				return ((int) arg1 / (int) arg2);
			} else
				return null;
		}
		catch (Exception e) {
			return null;
		}
		
	}

	public static void R_TAIL(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);
		
		childNode.frame = currentNode.frame;

		if (tokens.get(i).equals("<")) {
			productionWrite("R_TAIL -> '<' RELOP", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			childNode.op = "<";
			RELOP(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).equals(">")) {
			productionWrite("R_TAIL -> '>' RELOP", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			childNode.op = ">";
			RELOP(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).equals("=")) {
			productionWrite("R_TAIL -> '=' RELOP", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			childNode.op = "=";
			RELOP(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).equals("#")) {
			productionWrite("R_TAIL -> '#' RELOP", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			childNode.op = "#";
			RELOP(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches(
				"&|\\||,|\\)|\\(|\\[|\\{|\\}|" + integer + "|" + string + "|" + literals + "|-|!|\\]|\\$|" + symbols)) {
			productionWrite("R_TAIL -> epsilon", currentNode);
			currentNode.val = currentNode.tmp;
		} else {
			throwError("R_TAIL");
		}
	}

	public static void TERM(Node currentNode) {
		if (tokens.get(i).matches("\\(|\\[|\\+|" + integer + "|" + string + "|" + literals + "|-|!|" + symbols)) {
			productionWrite("TERM -> FACT T_TAIL", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			FACT(childNode1);
			childNode2.tmp = calcOperator(currentNode.tmp, currentNode.op, childNode1.val, currentNode.frame);
			T_TAIL(childNode2);
			currentNode.val = childNode2.val;
		} else {
			throwError("TERM");
		}

	}

	public static void T_TAIL(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);
		
		childNode.frame = currentNode.frame;

		if (tokens.get(i).matches("\\+")) {
			i++;
			productionWrite("T_TAIL -> '+' TERM", currentNode);
			childNode.tmp = currentNode.tmp;
			childNode.op = "+";
			TERM(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches("-")) {
			i++;
			productionWrite("T_TAIL -> '-' TERM", currentNode);
			childNode.tmp = currentNode.tmp;
			childNode.op = "-";
			TERM(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches(
				"<|>|=|#|&|\\||,|\\)|\\(|\\[|\\]|-|!|\\$|\\{|\\}|" + integer + "|" + string + "|" + symbols + "|" + literals)) {
			productionWrite("T_TAIL -> epsilon", currentNode);
			currentNode.val = currentNode.tmp;
		} else {
			throwError("T_TAIL");
		}
	}

	public static void FACT(Node currentNode) {
		if (tokens.get(i).matches("\\(|\\[|-|!|\\+|" + integer + "|" + string + "|" + literals + "|" + symbols)) {
			productionWrite("FACT -> VALUE F_TAIL", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			VALUE(childNode1);
			// System.out.println(currentNode.tmp + " " + currentNode.op + " "
			// +childNode1.val);
			childNode2.tmp = calcOperator(currentNode.tmp, currentNode.op, childNode1.val, currentNode.frame);
			F_TAIL(childNode2);
			currentNode.val = childNode2.val;
		} else {
			throwError("FACT");
		}
	}

	public static void F_TAIL(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);
		
		childNode.frame = currentNode.frame;

		if (tokens.get(i).matches("\\*")) {
			productionWrite("F_TAIL -> '*' FACT", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			childNode.op = "*";
			FACT(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches("/")) {
			productionWrite("F_TAIL -> '/' FACT", currentNode);
			i++;
			childNode.tmp = currentNode.tmp;
			childNode.op = "/";
			FACT(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches(
				"\\+|-|<|>|=|#|&|\\||\\)|\\(|\\]|\\[|!|\\$|,|\\{|\\}|" + integer + "|" + literals + "|" + symbols + "|" + string)) {
			productionWrite("F_TAIL -> epsilon", currentNode);
			currentNode.val = currentNode.tmp;
		} else {
			throwError("F_TAIL");
		}
	}

	public static void VALUE(Node currentNode) {
		
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);
		
		childNode.frame = currentNode.frame;

		if (tokens.get(i).matches("[0-9][0-9]*|\".*\"|true|false|nil")) {
			productionWrite("VALUE -> LITERAL", currentNode);
			LITERAL(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches("\\[")) {
			productionWrite("VALUE -> LIST", currentNode);
			LIST(childNode);
			currentNode.val = childNode.list;
		} else if (tokens.get(i).matches("-|!")) {
			productionWrite("VALUE -> UNARY", currentNode);
			UNARY(childNode);
			currentNode.val = childNode.val;
		} else if (tokens.get(i).matches("\\(")) {
			productionWrite("VALUE -> '(' EXPR ')'", currentNode);
			i++;
			EXPR(childNode);
			currentNode.val = childNode.val;
			// Make sure closing parenthesis is there
			if (tokens.get(i).matches("\\)|,")) {
				i++;
			} else {
				throwError("VALUE");
			}
		} else if (tokens.get(i).matches("[a-zA-Z][a-zA-Z0-9]*")) {
			productionWrite("VALUE -> SYMBOL CALL", currentNode);
			SYMBOL(childNode);
			currentNode.val = childNode.val;
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			childNode2.frame = currentNode.frame;
			
			//System.out.println(childNode.val);
			if (childNode.val instanceof Closure) {
				// Passing closure function down to the call
				childNode2.val = childNode.val;
				CALL(childNode2);
				
				currentNode.val = childNode2.val;
			} else {
				productionWrite("CALL -> epsilon", childNode2);
			}	
			
		} else {
			throwError("VALUE");
		}
	}

	public static void CALL(Node currentNode) {
		if (tokens.get(i).equals("(")) {
			productionWrite("CALL -> '(' ARGS ')'", currentNode);
			
			Node childNode1 = new Node();
			childNode1.parent = currentNode;
			currentNode.children.add(childNode1);
			
			childNode1.frame = currentNode.frame;
			//System.out.println(tokens.get(i));
			i++;
			ARGS(childNode1);
			//System.out.println(childNode1.list);
			i++;
			
			//System.out.println(tokens.get(i));
			
			//System.out.println(childNode1.list);
			
			// saving current spot to call function
			int tempi = i;
			i = ((Closure) currentNode.val).start;
			
			Node childNode2 = new Node();
			childNode2.parent = currentNode;
			currentNode.children.add(childNode2);
			childNode2.frame = currentNode.frame;
			
			// Loops through each paramater in the closure, and gets the corresponding value from the ARGS, then adds it to the frame of the node we will be using to execute the function call
			int j = 0;
			for (Object param : ((Closure) currentNode.val).params) {
				childNode2.frame.put((String) param, childNode1.list.get(j));
				j++;
			}
			
			BODY(childNode2);
			currentNode.val = childNode2.val;
			
			i = tempi;
		} else {
			productionWrite("CALL -> epsilon", currentNode);
		} 
		//else {
		//	throwError("CALL");
		//}
	}
	
	public static void LIST(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);
		
		childNode.frame = currentNode.frame;

		productionWrite("LIST -> '[' ARGS ']'", currentNode);
		i++;
		ARGS(childNode);
		currentNode.list = childNode.list;
		i++;
	}

	public static void UNARY(Node currentNode) {
		Node childNode = new Node();
		childNode.parent = currentNode;
		currentNode.children.add(childNode);
		
		childNode.frame = currentNode.frame;

		if (tokens.get(i).equals("!")) {
			productionWrite("UNARY -> '!' VALUE", currentNode);
			i++;
			VALUE(childNode);
			currentNode.val = !(boolean) childNode.val;
		} else if (tokens.get(i).equals("-")) {
			productionWrite("UNARY -> '-' VALUE", currentNode);
			i++;
			VALUE(childNode);
			currentNode.val = -(int) childNode.val;
		}

	}

	public static void ARGS(Node currentNode) {
		if (tokens.get(i).matches("\\(|\\[|[0-9][0-9]*|\".*\"|true|false|nil|-|!|[a-zA-Z][a-zA-Z0-9]*")) {
			productionWrite("ARGS -> EXPR A_LIST", currentNode);

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			EXPR(childNode1);
			A_LIST(childNode2);

			currentNode.list = childNode2.list;
			currentNode.list.add(0, childNode1.val);
		} else if (tokens.get(i).matches("\\]|\\)")) {
			productionWrite("ARGS -> epsilon", currentNode);
			currentNode.list.clear();
		} else {
			throwError("ARGS");
		}
	}

	public static void A_LIST(Node currentNode) {
		if (tokens.get(i).matches(",")) {
			productionWrite("A_LIST -> ',' EXPR A_LIST", currentNode);
			i++;

			Node childNode1 = new Node();
			Node childNode2 = new Node();
			childNode1.parent = currentNode;
			childNode2.parent = currentNode;
			currentNode.children.add(childNode1);
			currentNode.children.add(childNode2);
			
			childNode1.frame = currentNode.frame;
			childNode2.frame = currentNode.frame;

			EXPR(childNode1);
			A_LIST(childNode2);

			currentNode.list = childNode2.list;
			currentNode.list.add(0, childNode1.val);
		} else if (tokens.get(i).matches("\\]|\\)")) {
			productionWrite("A_LIST -> epsilon", currentNode);
			currentNode.list.clear();
		} else {
			throwError("A_LIST");
		}
	}

	public static void SYMBOL(Node currentNode) {
		if (tokens.get(i).matches(symbols)) {
			productionWrite("SYMBOL -> symbol(" + tokens.get(i) + ")", currentNode);
			if (currentNode.frame.containsKey(tokens.get(i))) {
				currentNode.val = currentNode.frame.get(tokens.get(i));
			} else {
				currentNode.val = tokens.get(i);
			}
			i++;
		} else {
			throwError("SYMBOL");
		}

	}

	public static void LITERAL(Node currentNode) {
		if (tokens.get(i).matches("[0-9][0-9]*")) {
			productionWrite("LITERAL -> int(" + tokens.get(i) + ")", currentNode);
			currentNode.val = Integer.parseInt(tokens.get(i));
		} else if (tokens.get(i).equals("true")) {
			productionWrite("LITERAL -> 'true'", currentNode);
			currentNode.val = Boolean.parseBoolean(tokens.get(i));
		} else if (tokens.get(i).equals("false")) {
			productionWrite("LITERAL -> 'false'", currentNode);
			currentNode.val = Boolean.parseBoolean(tokens.get(i));
		} else if (tokens.get(i).equals("nil")) {
			productionWrite("LITERAL -> 'nil'", currentNode);
			currentNode.val = "nil";
		} else if (tokens.get(i).matches("\".*\"")) {
			productionWrite("LITERAL -> string(" + tokens.get(i) + ")", currentNode);
			currentNode.val = tokens.get(i);
		}
		i++;
	}
	
	// BUILT IN FUNCTIONS
	@SuppressWarnings("unchecked")
	public static void head(Node currentNode) {
		i++; // head
		i++; // (
		
		ArrayList<Object> list = new ArrayList<Object>();
		if (tokens.get(i).equals("[")) {
			i++; // [
			boolean br = false;
			while (br == false) {
				list.add(tokens.get(i));
				i++;
				if (tokens.get(i).equals(",")) {
					// Continue
					i++;
				} else if (tokens.get(i).equals("]")) {
					br = true;
					i++; // ]
				}
			}
		}
		i++; // )
		//System.out.println(list);
		currentNode.val = list.get(0);
		
		
		//System.out.println(tokens.get(i));
	}
	
	@SuppressWarnings("unchecked")
	public static void tail(Node currentNode) {
		i++; // tail
		i++; // (
		
		ArrayList<Object> list = new ArrayList<Object>();
		if (tokens.get(i).equals("[")) {
			i++; // [
			boolean br = false;
			while (br == false) {
				list.add(tokens.get(i));
				i++;
				if (tokens.get(i).equals(",")) {
					// Continue
					i++;
				} else if (tokens.get(i).equals("]")) {
					br = true;
					i++; // ]
				}
			}
		}
		i++; // )
		
		list.remove(0);
		currentNode.val = list;
	}
	
	@SuppressWarnings("unchecked")
	public static void prepend(Node currentNode) {
		i++; // prepend
		i++; // (
		
		Object newNum = tokens.get(i);
		i++; // num

		i++; // ,
		
		ArrayList<Object> list = new ArrayList<Object>();
		if (tokens.get(i).equals("[")) {
			i++; // [
			boolean br = false;
			while (br == false) {
				list.add(tokens.get(i));
				i++;
				if (tokens.get(i).equals(",")) {
					// Continue
					i++;
				} else if (tokens.get(i).equals("]")) {
					br = true;
					i++; // ]
				}
			}
		}
		i++; // )
		
		
		list.add(0, newNum);
		currentNode.val = list;
	}
	
}

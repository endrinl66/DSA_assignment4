// Stack Examples discussed in class.

import java.util.Scanner;

public class StackDriver
{
    private static String currentExpr = null;     // last fully parenthesized infix entered
    private static String lastPostfix = null;     // last post-fix converted in option 3

   
    private static boolean evalUsedOnCurrent = false;     
    private static boolean convertUsedOnCurrent = false; 

    public static void main(String []args)
    {
        Scanner in = new Scanner(System.in);

        while (true) {  // infinite loop; only breaks on Quit (5)
            System.out.println();
            System.out.println("1. Enter a fully parenthesized expression.");
            System.out.println("2. Evaluate a fully parenthesized expression.");
            System.out.println("3. Convert a fully parenthesized expression to post-fix.");
            System.out.println("4. Evaluate post-fix");
            System.out.println("5. Quit");
            System.out.print("Enter your choice: ");

            String choiceLine = in.nextLine().trim();
            if (choiceLine.isEmpty()) continue;
            char choice = choiceLine.charAt(0);

            switch (choice) {
                case '1': // Enter expression + check legality
                    System.out.print("Enter any fully parenthesized expression: ");
                    String s = in.nextLine();

                    if (isLegal(s)) {
                        System.out.println(s + " is a legal parenthesization");
                        currentExpr = s;
                        evalUsedOnCurrent = false;
                        convertUsedOnCurrent = false;
                    } else {
                        System.out.println(s + " is a not legal parenthesization");
                    }
                    break;

                case '2': // Evaluate fully parenthesized expression
                    if (currentExpr == null) {
                        System.out.println("No expression has been entered.");
                    } else if (evalUsedOnCurrent) {
                        System.out.println("You have already evaluated the current expression.");
                        System.out.println("Please enter a NEW expression (option 1) before evaluating again.");
                    } else {
                        evaluateExpr(currentExpr);  // prints result or error safely
                        evalUsedOnCurrent = true;
                    }
                    break;

                case '3': // Convert fully parenthesized to post-fix
                    if (currentExpr == null) {
                        System.out.println("No expression has been entered.");
                    } else if (convertUsedOnCurrent) {
                        System.out.println("You have already converted the current expression to postfix.");
                        System.out.println("Please enter a NEW expression (option 1) before converting again.");
                    } else {
                        lastPostfix = toPostfix(currentExpr);
                        if (lastPostfix == null) {
                            System.out.println("Could not convert to postfix (illegal expression).");
                        } else {
                            System.out.println("Post-fix: " + lastPostfix);
                            convertUsedOnCurrent = true;
                        }
                    }
                    break;

                case '4': // Evaluate post-fix
                    if (lastPostfix == null) {
                        System.out.print("No converted postfix stored. Enter a new post-fix expression: ");
                        String pf = in.nextLine();
                        try {
                            int ans = evalPostfix(pf);
                            System.out.println("The result of the post-fix expression is: " + ans);
                        } catch (Exception ex) {
                            System.out.println("Error while evaluating post-fix: " + ex.getMessage());
                        }
                    } else {
                        System.out.print("Use the post-fix from option 3? (y/n): ");
                        String yn = in.nextLine().trim().toLowerCase();
                        String pf = ( !yn.isEmpty() && yn.charAt(0) == 'y') ? lastPostfix : null;
                        if (pf == null) {
                            System.out.print("Enter a new post-fix expression: ");
                            pf = in.nextLine();
                        }
                        try {
                            int ans = evalPostfix(pf);
                            System.out.println("The result of the post-fix expression is: " + ans);
                        } catch (Exception ex) {
                            System.out.println("Error while evaluating post-fix: " + ex.getMessage());
                        }
                    }
                    break;

                case '5': // Quit
                    System.out.println("Goodbye!");
                    in.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try 1-5.");
                    break;
            }
        }
    }

    // ====== convert fully parenthesized infix (single-digit operands) to post-fix ======
    // Returns null if it detects something clearly inconsistent.
    private static String toPostfix(String str) {
        StringBuilder out = new StringBuilder();
        ListStack<Character> ops = new ListStack<>();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            } else if (Character.isDigit(c)) {
                out.append(c).append(' ');
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                ops.push(c);
            } else if (c == '(') {
                // ignore; structure is guaranteed by full parenthesization and isLegal()
            } else if (c == ')') {
                if (ops.isEmpty()) return null; // mismatched structure
                out.append(ops.topAndPop()).append(' ');
            } else {
                // unexpected char
                return null;
            }
        }
        while (!ops.isEmpty()) out.append(ops.topAndPop()).append(' ');
        return out.toString().trim();
    }

    // ====== evaluate post-fix with single-digit operands and + - * / % ======
    // Throws with a clear message if malformed; callers catch and print nicely.
    private static int evalPostfix(String pf) {
        ListStack<Integer> st = new ListStack<>();
        for (int i = 0; i < pf.length(); i++) {
            char c = pf.charAt(i);
            if (Character.isWhitespace(c)) continue;

            if (Character.isDigit(c)) {
                st.push(c - '0');
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                if (st.isEmpty()) throw new IllegalArgumentException("Bad postfix: missing operand");
                int op2 = st.topAndPop();
                if (st.isEmpty()) throw new IllegalArgumentException("Bad postfix: missing operand");
                int op1 = st.topAndPop();

                switch (c) {
                    case '+': st.push(op1 + op2); break;
                    case '-': st.push(op1 - op2); break;
                    case '*': st.push(op1 * op2); break;
                    case '/':
                        if (op2 == 0) throw new ArithmeticException("Division by zero");
                        st.push(op1 / op2);
                        break;
                    case '%':
                        if (op2 == 0) throw new ArithmeticException("Modulo by zero");
                        st.push(op1 % op2);
                        break;
                }
            } else {
                throw new IllegalArgumentException("Unsupported token: " + c);
            }
        }
        if (st.isEmpty()) throw new IllegalArgumentException("Bad postfix: no result");
        int res = st.topAndPop();
        if (!st.isEmpty()) throw new IllegalArgumentException("Bad postfix: extra operands");
        return res;
    }

    //  (prevents crashes on extra ')' or '(') 
    public static boolean isLegal(String str) {
        ListStack<Character> s = new ListStack<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') s.push('(');
            else if (c == ')') {
                if (s.isEmpty()) return false; // extra ')'
                s.pop();
            }
        }
        return s.isEmpty(); // false if extra '('
    }

    // Evaluate the expression (must be fully parenthesized, single-digit)
    // Prints result or a friendly error; never throws.
    public static void evaluateExpr(String str)
    {
        ListStack<Integer> s1 = new ListStack<>();
        ListStack<Character> s2 = new ListStack<>();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if (Character.isDigit(ch)) {
                s1.push(ch - '0');
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                s2.push(ch);
            } else if (ch == ')') {
                if (s2.isEmpty() || s1.isEmpty()) { System.out.println("Illegal expression."); return; }

                char oper = s2.topAndPop();

                int opnd2;
                try { opnd2 = s1.topAndPop(); }
                catch (Exception e) { System.out.println("Illegal expression."); return; }

                int opnd1;
                try { opnd1 = s1.topAndPop(); }
                catch (Exception e) { System.out.println("Illegal expression."); return; }

                switch (oper) {
                    case '+': s1.push(opnd1 + opnd2); break;
                    case '-': s1.push(opnd1 - opnd2); break;
                    case '*': s1.push(opnd1 * opnd2); break;
                    case '/':
                        if (opnd2 == 0) { System.out.println("Division by zero."); return; }
                        s1.push(opnd1 / opnd2); 
                        break;
                    case '%':
                        if (opnd2 == 0) { System.out.println("Modulo by zero."); return; }
                        s1.push(opnd1 % opnd2); 
                        break;
                    default:
                        System.out.println("Unsupported operator: " + oper);
                        return;
                }
            } else if (Character.isWhitespace(ch) || ch == '(') {
                // ignore
            	
            } else {
                System.out.println("Unsupported token in expression: " + ch);
                return;
            }
        }

        if (s1.isEmpty()) { System.out.println("Illegal expression."); return; }
        int result = s1.topAndPop();
        if (!s1.isEmpty()) { System.out.println("Illegal expression."); return; }

        System.out.println("The result of the expression is: " + result);
    }
}

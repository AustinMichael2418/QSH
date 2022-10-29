/*
 * This class helps generate quantum gates given ArrayLists of program input
 */

import java.util.ArrayList;
public class GateGen {
    /**
     * Generate permutation gate
     * 
     * @param   data   ArrayList of data
     * @return         resulting gate
     **/
    public static Complex[][] permutation(ArrayList<String> data) {
        // Only first row of ArrayList used
        String row = data.get(0);

        // Split by comma
        String[] items = row.split(",");

        // Generate list of ints from list of strings
        int[] intItems = new int[items.length];
        for(int i = 0; i < items.length; i++) {
            intItems[i] = Integer.parseInt(items[i].trim());
        }

        // Apply permutation to matrix
        Complex[][] res = new Complex[items.length][items.length];
        for(int i = 0; i < items.length; i++) {
            for(int j = 0; j < items.length; j++) {
                res[i][j] = new Complex(0, 0);
            }
            res[i][intItems[i]] = new Complex(1, 0);
        }

        return res;
    }

    /**
     * Generate simple gate
     * 
     * @param   data   ArrayList of data
     * @return         resulting gate
     **/
    public static Complex[][] simple(ArrayList<String> data) {
        // Declare variable for resulting matrix
        Complex[][] fin = new Complex[data.size()][data.size()];

        int rowNum = 0; 
        for(String item : data) {
            // Each row is delimited by commas
            String[] row = item.split(",");
            for(int i = 0; i < row.length; i++) {
                row[i] = row[i].trim();
            }

            for(int i = 0; i < row.length; i++) {
                fin[rowNum][i] = parseMath(row[i]);
            }
            rowNum++;
        }

        return fin;
    }

    // Constant arrays for function names and constants
    private static final String[] FN_NAMES = {
            "sin",
            "cos",
            "cis",
            "sqrt",
            "exp"
        };

    private static final String[] CONSTANTS = {
            "pi",
            "i"
        };

    /**
     * Process mathematical expression
     * 
     * @param   _mathStuff   expression
     * @return               result of expression
     **/
    public static Complex parseMath(String _mathStuff) {
        /*
         * The parsing algorithms are extremely complex and I do not wish to
         * document them completely at this time.
         */

        String mathStuff = _mathStuff.trim();

        // Start with functions
        boolean containsFn = false;
        for(String name : FN_NAMES) {
            if(mathStuff.indexOf(name) != -1) containsFn = true;
        }
        if(containsFn) {
            int depth = 0;
            boolean inFn = false;
            String res = mathStuff;
            String current = "";

            for(int i = 0; i < mathStuff.length(); i++) {
                if(!inFn) {
                    if(startsWithFn(mathStuff.substring(i))) {
                        inFn = true;
                        current = res.substring(i, mathStuff.indexOf(
                                "(", i) + 1);
                        i = mathStuff.indexOf("(", i);
                        depth = 1;
                    }
                } else {
                    current += mathStuff.substring(i, i + 1);
                    if(mathStuff.charAt(i) == '(') {
                        depth++;
                    } else if (mathStuff.charAt(i) == ')') {
                        depth--;
                    }

                    if(depth == 0) {
                        Complex inside = parseMath(current.substring(
                                    current.indexOf("(") + 1, current.lastIndexOf(")")));

                        Complex rtrn = new Complex(0, 0);
                        if(current.startsWith("sin")) {
                            rtrn = new Complex(Math.sin(inside.getRealPart()),
                                0);
                        }
                        if(current.startsWith("cos")) {
                            rtrn = new Complex(Math.cos(inside.getRealPart()),
                                0);
                        }
                        if(current.startsWith("sqrt")) {
                            rtrn = new Complex(Math.sqrt(inside.getRealPart()),
                                0);
                        }
                        if(current.startsWith("sin")) {
                            rtrn = new Complex(Math.sin(inside.getRealPart()),
                                0);
                        }
                        if(current.startsWith("exp")) {
                            rtrn = new Complex(Math.exp(inside.getRealPart()),
                                0);
                        }
                        if(current.startsWith("cis")) {
                            rtrn = new Complex(Math.cos(inside.getRealPart()),
                                Math.sin(inside.getRealPart()));
                        }

                        res = res.replace(current, "<" + rtrn + ">");

                        inFn = false;
                        current = "";
                    }

                }
            }
            return parseMath(res);
        }

        // Then constants
        if(mathStuff.indexOf("pi") != -1) return parseMath(mathStuff.replace(
                    "pi", "" + Math.PI));

        if(mathStuff.indexOf("-i") != -1) return parseMath(mathStuff.replace(
                    "-i", "<" + new Complex(0, -1) + ">"));

        // Find iota and process
        boolean plainI = false;
        int gtltDepth = 0;
        for(int i = 0; i < mathStuff.length(); i++) {
            if(mathStuff.charAt(i) == '<') gtltDepth++;
            if(mathStuff.charAt(i) == '>') gtltDepth--;
            if(mathStuff.charAt(i) == 'i' && gtltDepth == 0) plainI = true;
        }

        if(plainI) {
            gtltDepth = 0;
            String res = new String(mathStuff);
            for(int i = 0; i < res.length(); i++) {
                if(res.charAt(i) == '<') gtltDepth++;
                if(res.charAt(i) == '>') gtltDepth--;
                if(res.charAt(i) == 'i' && gtltDepth == 0) {
                    String toReplace = "";
                    if(i > 0) {
                        int toNum = res.charAt(i - 1) - 48;
                        if(toNum >= 0 && toNum < 10) {
                            toReplace = " * <" + new Complex(0, 1) + ">";
                        } else {
                            toReplace = "<" + new Complex(0, 1) + ">";
                        }
                    } else {
                        toReplace = "<" + new Complex(0, 1) + ">";
                    }
                    String before = res.substring(0, i);
                    String after = res.substring(i + 1);
                    i += toReplace.length() - 1;
                    res = before + toReplace + after;
                }
            }
            return parseMath(res);
        }

        // Multiplication and division
        if(mathStuff.indexOf("*") != -1 || mathStuff.indexOf("/") != -1) {
            Complex first = new Complex(0, 0);
            Complex second = new Complex(0, 0);
            int start = 0;
            int end = 0;

            gtltDepth = 0;
            String res = new String(mathStuff);
            for(int i = 0; i < res.length(); i++) {
                if(mathStuff.charAt(i) == '<') gtltDepth++;
                if(mathStuff.charAt(i) == '>') gtltDepth--;
                if(mathStuff.charAt(i) == '*' && gtltDepth == 0) {
                    String toReplace = "";
                    int j = i - 1;
                    for(; mathStuff.charAt(j) == ' '; j--) {}
                    end = j + 1;
                    for(; j >= 0 && ((mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j--) {
                        if(mathStuff.charAt(j) == '>') gtltDepth++;
                        if(mathStuff.charAt(j) == '<') gtltDepth--;
                    }
                    start = j + 1;
                    int overallStart = start;
                    first = parseMath(mathStuff.substring(start, end));

                    j = i + 1;
                    for(; mathStuff.charAt(j) == ' '; j++) {}
                    start = j;
                    for(; j < mathStuff.length() && ((
                            mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j++) {
                        if(mathStuff.charAt(j) == '<') gtltDepth++;
                        if(mathStuff.charAt(j) == '>') gtltDepth--;
                    }
                    end = j;

                    second = parseMath(mathStuff.substring(start, end));
                    int overallEnd = end;

                    toReplace = "<" + first.multiply(second) + ">";

                    String before = res.substring(0, overallStart);
                    String after = res.substring(overallEnd);
                    res = before + toReplace + after;
                    break;
                }

                if(mathStuff.charAt(i) == '/' && gtltDepth == 0) {
                    String toReplace = "";
                    int j = i - 1;
                    for(; mathStuff.charAt(j) == ' '; j--) {}
                    end = j + 1;
                    for(; j >= 0 && ((mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j--) {
                        if(mathStuff.charAt(j) == '>') gtltDepth++;
                        if(mathStuff.charAt(j) == '<') gtltDepth--;
                    }
                    start = j + 1;
                    int overallStart = start;
                    first = parseMath(mathStuff.substring(start, end));

                    j = i + 1;
                    for(; mathStuff.charAt(j) == ' '; j++) {}
                    start = j;
                    for(; j < mathStuff.length() && ((
                            mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j++) {
                        if(mathStuff.charAt(j) == '<') gtltDepth++;
                        if(mathStuff.charAt(j) == '>') gtltDepth--;
                    }
                    end = j;

                    second = parseMath(mathStuff.substring(start, end));
                    int overallEnd = end;

                    double quotient = first.getRealPart() / second.getRealPart();

                    toReplace = "<" + new Complex(quotient, 0) + ">";

                    String before = res.substring(0, overallStart);
                    String after = res.substring(overallEnd);
                    res = before + toReplace + after;
                    break;
                }
            }
            return parseMath(res);
        }

        // Addition and subtraction
        boolean plainPlusMinus = false;
        gtltDepth = 0;
        for(int i = 0; i < mathStuff.length(); i++) {
            if(mathStuff.charAt(i) == '<') gtltDepth++;
            if(mathStuff.charAt(i) == '>') gtltDepth--;
            if(mathStuff.charAt(i) == '+' && gtltDepth == 0) plainPlusMinus = true;
            if(mathStuff.charAt(i) == '-' && gtltDepth == 0) plainPlusMinus = true;
        }

        if(plainPlusMinus) {
            Complex first = new Complex(0, 0);
            Complex second = new Complex(0, 0);
            int start = 0;
            int end = 0;

            gtltDepth = 0;
            String res = new String(mathStuff);
            for(int i = 0; i < res.length(); i++) {
                if(mathStuff.charAt(i) == '<') gtltDepth++;
                if(mathStuff.charAt(i) == '>') gtltDepth--;
                if(mathStuff.charAt(i) == '+' && gtltDepth == 0) {
                    String toReplace = "";
                    int j = i - 1;

                    for(; mathStuff.charAt(j) == ' '; j--) {}
                    end = j + 1;
                    for(; j >= 0 && ((mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j--) {
                        if(mathStuff.charAt(j) == '>') gtltDepth++;
                        if(mathStuff.charAt(j) == '<') gtltDepth--;
                    }
                    start = j + 1;
                    int overallStart = start;
                    first = parseMath(mathStuff.substring(start, end));

                    j = i + 1;
                    for(; mathStuff.charAt(j) == ' '; j++) {}
                    start = j;
                    for(; j < mathStuff.length() && ((
                            mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j++) {
                        if(mathStuff.charAt(j) == '<') gtltDepth++;
                        if(mathStuff.charAt(j) == '>') gtltDepth--;
                    }
                    end = j;

                    second = parseMath(mathStuff.substring(start, end));
                    int overallEnd = end;

                    toReplace = "<" + first.add(second) + ">";

                    String before = res.substring(0, overallStart);
                    String after = res.substring(overallEnd);
                    res = before + toReplace + after;
                    break;
                }

                if(mathStuff.charAt(i) == '-' && gtltDepth == 0) {
                    String toReplace = "";
                    int j = 0;
                    int overallStart = 0;
                    if(i > 0) {
                        j = i - 1;
                        for(; mathStuff.charAt(j) == ' '; j--) {}
                        end = j + 1;
                        for(; j >= 0 && ((mathStuff.charAt(j) != ' ' && 
                                mathStuff.charAt(j) != '+' &&
                                mathStuff.charAt(j) != '-' &&
                                mathStuff.charAt(j) != '*' &&
                                mathStuff.charAt(j) != '/') || gtltDepth != 0); j--) {
                            if(mathStuff.charAt(j) == '>') gtltDepth++;
                            if(mathStuff.charAt(j) == '<') gtltDepth--;
                        }
                        start = j - 1;
                        overallStart = start;
                        first = parseMath(mathStuff.substring(start, end));
                    } else {
                        overallStart = 0;
                        first = new Complex(0, 0);
                    }
                    j = i + 1;
                    for(; mathStuff.charAt(j) == ' '; j++) {}
                    start = j;
                    for(; j < mathStuff.length() && ((
                            mathStuff.charAt(j) != ' ' && 
                            mathStuff.charAt(j) != '+' &&
                            mathStuff.charAt(j) != '-' &&
                            mathStuff.charAt(j) != '*' &&
                            mathStuff.charAt(j) != '/') || gtltDepth != 0); j++) {
                        if(mathStuff.charAt(j) == '<') gtltDepth++;
                        if(mathStuff.charAt(j) == '>') gtltDepth--;
                    }
                    end = j;

                    second = parseMath(mathStuff.substring(start, end));
                    int overallEnd = end;

                    toReplace = "<" + first.subtract(second) + ">";
                    String before = res.substring(0, overallStart);
                    String after = res.substring(overallEnd);
                    res = before + toReplace + after;
                    break;
                }
            }
            return parseMath(res);
        }

        try {
            // Real number
            double num = Double.parseDouble(mathStuff);
            return new Complex(num, 0);
        } catch (Exception e) {
            // Complex number
            return new Complex(mathStuff);
        }
    }

    /**
     * Get if a string starts with a function name
     * 
     * @param   data   the string
     * @return         whether or not the string starts with a function name
     **/
    private static boolean startsWithFn(String data) {
        for(String name : FN_NAMES) {
            if(data.startsWith(name)) return true;
        }
        return false;
    }
}
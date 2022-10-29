/*
 * This functions as an interpreter for Quil
 */

import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
public class QuilInterpreter {
    private HashMap<String, Complex[][]> gates;
    private QubitMatrix matrix;
    public String retVal;
    private int[] bits;
    private ArrayList<String> indentedLines;
    private String gateName;
    private HashMap<String, ArrayList<String>> circuits;

    /*
     * Values:
     * 0 - none
     * 1 - simple gate
     * 2 - permutation gate
     * 3 - parametric gate
     * 4 - simple circuit
     * 5 - parametric circuit
     */
    private int processing;

    /*
     * Constructor - takes in QubitMatrix and list of gates
     */
    public QuilInterpreter(QubitMatrix matrix, 
    HashMap<String, Complex[][]> gates) {
        this.gates = gates;
        this.matrix = matrix;
        bits = new int[matrix.getSize()];
        circuits = new HashMap<String, ArrayList<String>>();
    }

    /**
     * Main parsing method
     * 
     * @param   line   line of code to parse
     * @return         return code (number 0 - 3)
     **/
    public int parse(String line) {
        // If indented, parse indent
        if(line.startsWith("\t")) return parseIndent(line);
        else if(processing != 0) parseIndentedLines(); // If done indenting,
                                                       // process indentation
        processing = 0;
        // Split by semicolons
        String[] lines = line.trim().split(";");

        // Parse each line
        for(int i = 0; i < lines.length - 1; i++) {
            parseLine(lines[i]);
        }

        return parseLine(lines[lines.length - 1]);
    }

    /**
     * Runs a quantum circuit
     * 
     * @param   circuitName   the name of the circuit
     * @return                return code
     **/
    private int runCircuit(String circuitName) {
        ArrayList<String> circuit = circuits.get(circuitName);

        for(int i = 0; i < circuit.size() - 1; i++) {
            parse(circuit.get(i));
        }

        return parse(circuit.get(circuit.size() - 1));
    }

    /**
     * Parses after indentation is complete
     **/
    private void parseIndentedLines() {
        if(processing == 1) {
            gates.put(gateName, GateGen.simple(indentedLines));
            // Simple gate
        } else if (processing == 2) {
            gates.put(gateName, GateGen.permutation(indentedLines));
            // Permutation gate
        } else if (processing == 3) {
            // Parametric gate - not implemented
        } else if (processing == 4) {
            circuits.put(gateName, new ArrayList<String>(indentedLines));
            // Simple circuit
        } else if (processing == 5) {
            // Parametric circuit -  not implemented
        }
    }

    /**
     * Parse indentation
     * 
     * @param   line   the line of code
     * @return         return code
     **/
    private int parseIndent(String line) {
        if(processing == 0) {
            System.out.println("Error: Invalid indentation");
            return 2;
        } else {
            indentedLines.add(line.substring(1));
            return 1;
        }
    }

    /**
     * Parse a line of code
     * 
     * @param   line   the line
     * @return         return code
     **/
    private int parseLine(String line) {
        return parseLine(line.trim().split(" "));
    }

    /*
     * Return types:
     * 0 - Statevectors
     * 1 - Indent
     * 2 - Error
     * 3 - Return value
     */

    /**
     * Parse a line of code
     * 
     * @param   params   array of space-delimited parameters
     * @return           return code
     **/
    private int parseLine(String[] params) {
        retVal = "";
        if(inGates(params[0])) {
            // If a quantum gate
            try {
                int[] bits = new int[params.length - 1];
                for(int i = 1; i < params.length; i++) {
                    bits[i - 1] = Integer.parseInt(params[i]);
                }
                try {
                    matrix.addGate(gates.get(params[0]), bits);
                } catch(Exception e) {
                    System.out.println("Error: Failed to add gate");
                    return 2;
                }
            } catch(Exception e) {
                System.out.println("Error: Invalid qubit number");
                return 2;
            }
        } else if (inCircuits(params[0])) {
            // If a quantum circuit
            runCircuit(params[0]);
        } else if (params[0].equals("MEASURE")) {
            // If measuring
            try {
                int qubitNum = Integer.parseInt(params[1]);
                try {
                    int bitNum = Integer.parseInt(params[2].substring(1, 
                                params[2].length() - 1));
                    this.bits[bitNum] = Quantum.measureBit(matrix, qubitNum);
                    retVal = "" + this.bits[bitNum];
                    return 3;
                } catch (Exception e) {
                    System.out.println("Error: Invalid bit");
                    return 2;
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid qubit number");
                return 2;
            }
        } else if (params[0].equals("DEFGATE")) {
            // DEFGATE
            String rest = params[1];
            for(int i = 2; i < params.length; i++) {
                rest += " " + params[i];
            }

            if(rest.indexOf("(")  != -1) {
                System.out.println("Error: Parametric gates not yet supported");
                return 2;
            } else {
                if(rest.substring(rest.length() - 1).equals(":")) {
                    rest = rest.substring(0, rest.length() - 1).trim();
                    gateName = rest.split(" ")[0];
                    if(rest.endsWith(" AS PERMUTATION")) {
                        processing = 2;
                        indentedLines = new ArrayList<String>();
                        return 1;
                    } else {
                        processing = 1;
                        indentedLines = new ArrayList<String>();
                        return 1;
                    }
                } else {
                    System.out.println("Error: Missing colon in DEFGATE");
                }
            }

        } else if (params[0].equals("DEFCIRCUIT")) {
            // DEFCIRCUIT
            String rest = params[1];
            for(int i = 2; i < params.length; i++) {
                rest += " " + params[i];
            }

            if(rest.indexOf("(")  != -1) {
                System.out.println("Error: Parametric circuits not yet supported");
                return 2;
            } else {
                if(rest.substring(rest.length() - 1).equals(":")) {
                    rest = rest.substring(0, rest.length() - 1).trim();
                    gateName = rest.split(" ")[0];
                    processing = 4;
                    indentedLines = new ArrayList<String>();
                    return 1;
                } else {
                    System.out.println("Error: Missing colon in DEFCIRCUIT");
                }
            }

        }

        // Default is statevector return
        return 0;
    }

    /**
     * Get if a name is a gate
     * 
     * @param   gateName   the gate name
     * @return             if a gate
     **/
    private boolean inGates(String gateName) {
        for(String gate : gates.keySet()) {
            if(gate.equals(gateName)) return true;
        }
        return false;
    }

    /**
     * Get if a name is a circuit
     * 
     * @param   gateName   the circuit name
     * @return             if a circuit
     **/
    private boolean inCircuits(String gateName) {
        for(String gate : circuits.keySet()) {
            if(gate.equals(gateName)) return true;
        }
        return false;
    }

    /**
     * Get statevector list
     * 
     * @return   statevector list
     **/
    public String statevector() {
        HashMap<String, Complex> matrix = this.matrix.applyAndContinue();
        String res = "";
        for(String key : matrix.keySet()) {
            res += String.format("(%s)|%s> ", matrix.get(key), 
                key);
        }
        return res;
    }
}
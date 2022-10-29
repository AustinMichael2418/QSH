/*
 * Handles QSH commands
 */

import java.util.HashMap;
import java.text.NumberFormat;
import java.io.File;
import java.util.Scanner;
public class QuantumShell {
    private boolean indented;
    private boolean active;
    private HashMap<String, Complex[][]> gates;
    private QubitMatrix matrix;
    private QuilInterpreter interpreter;

    /*
     * Constructor
     */
    public QuantumShell() {
        indented = false;
        active = true;
        gates = new HashMap<String, Complex[][]>();
        gates.put("H", DefaultGates.H);
        gates.put("I", DefaultGates.I);
        gates.put("X", DefaultGates.X);
        gates.put("CNOT", DefaultGates.CNOT);
        gates.put("CCNOT", DefaultGates.CCNOT);
        gates.put("SWAP", DefaultGates.SWAP);
        matrix = null;
        interpreter = null;
    }

    /**
     * Gets if the next line needs to be indented
     * 
     * @return   if it needs to be indented
     **/
    public boolean isIndented() { return indented; }

    /**
     * Gets if this shell instance is active
     * 
     * @return   if this shell instance is active
     **/
    public boolean isActive() { return active; }

    /**
     * Parses a line of input
     * 
     * @param   _input   the input
     * @return           the output
     **/
    public String parse(String _input) throws Exception {
        String input = _input.trim();
        // If empty line, unindent
        if(input.equals("")) { indented = false; interpreter.parse(""); 
            return ""; }

        // Split params
        String[] params = input.split(" ");
        if(params[0].equals("help")) {
            // Read from help file on help command
            File file = new File("help.txt");
            Scanner fileScanner = new Scanner(file);
            String res = "";
            while(fileScanner.hasNextLine()) {
                res += fileScanner.nextLine() + "\n";
            }

            fileScanner.close();
            return res;
        } else if (params[0].equals("set")) {
            // Set command - generates new QubitMatrix and QuilInterpreter
            try {
                int qubits = Integer.parseInt(params[1]);
                matrix = new QubitMatrix(qubits);
                interpreter = new QuilInterpreter(matrix, gates);
                return "Qubit matrix configured with " + qubits + " qubits\n";
            } catch (Exception e) {
                return "Error: Invalid qubit count \"" + params[1] + "\"\n";
            }
        } else if (params[0].equals("exit")) {
            // Exits the program
            active = false;
            return "";
        } else if (params[0].equals("listgates")) {
            // Lists all gates
            String res = "";
            for(String key : gates.keySet()) {
                res += key + "\n";
            }
            return res;
        } else if (params[0].equals("prob")) {
            // Probabilities

            // NumberFormat object
            NumberFormat fmt = NumberFormat.getInstance();
            fmt.setMinimumFractionDigits(0);
            fmt.setMaximumFractionDigits(6);

            // Get matrix
            HashMap<String, Complex> result = matrix.applyAndContinue();
            // Print all probabilities in proper format
            String res = "";
            for(String key : result.keySet()) {
                res += key + ": " + fmt.format(Quantum.qProb(result.get(key)) * 100) + "%\n";
            }
            return res;
        } else {
            // Quil commands

            // Add intentation if needed
            if(indented) input = "\t" + input;

            // Parse input
            int result = interpreter.parse(input);
            if(result == 0) {
                // Statevector output
                return interpreter.statevector() + "\n";
            } else if (result == 1) {
                // Indentation needed
                indented = true;
                return "";
            } else if (result == 2) {
                // Error
                return "";
            } else if (result == 3) {
                // Return value
                return interpreter.retVal + "\n";
            }
        }

        return "\n";
    }
}
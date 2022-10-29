/*
 * Represents matrices of qubits
 */

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
public class QubitMatrix {
    private int size;
    HashMap<String, Complex> matrix;
    GateMatrix gate;

    /*
     * Constructor - takes in size (in qubits) as parameter
     */
    public QubitMatrix(int size) {
        this.size = size;

        // Generate state matrix
        matrix = new HashMap<String, Complex>();
        for(int i = 0; i < Math.pow(2, size); i++) {
            String bin = Utils.decToBin(i);

            if(size != bin.length()) {
                bin = Utils.repeat("0", size - bin.length()) + bin;
            }

            matrix.put(bin, new Complex(0, 0));
        }
        matrix.put(Utils.repeat("0", size), new Complex(1, 0));

        // Generate initial gate as identity gate for all qubits
        int len = (int)Math.pow(2, size);
        Complex[][] _gate = new Complex[len][len];

        for(int i = 0; i < _gate.length; i++) {
            for(int j = 0; j < _gate[0].length; j++) {
                _gate[i][j] = new Complex(0, 0);
            }
        }

        for(int i = 0; i < len; i++) {
            _gate[i][i] = new Complex(1, 0);
        }

        gate = new GateMatrix(_gate);
    }

    /**
     * Adds a gate to the circuit
     * 
     * @param   gate   the gate
     * @param   bits   array of qubits applied
     **/
    public void addGate(Complex[][] gate, int[] bits) {
        GateMatrix thisGate = readGate(gate, bits);
        //System.out.println(this.gate.multiply(thisGate));
        this.gate = this.gate.multiply(thisGate);
    }

    /**
     * Reads a gate given the gate matrix and the qubits it is applied to.
     * This is essentially a modified tensor product method.
     * 
     * @param   gate    the gate matrix
     * @param   _bits   applied qubits
     * @return          GateMatrix object
     **/
    public GateMatrix readGate(Complex[][] gate, int[] _bits) {
        /*
         * Essentially, this just maps the gate matrix to the given bits
         */

        GateMatrix matrix = new GateMatrix(gate);

        int len = (int)Math.pow(2, size);
        Complex[][] fullGate = new Complex[len][len];

        for(int i = 0; i < fullGate.length; i++) {
            for(int j = 0; j < fullGate[0].length; j++) {
                fullGate[i][j] = new Complex(0, 0);
            }
        }

        GateMatrix m2 = new GateMatrix(fullGate);

        int[] bits = new int[_bits.length];
        for(int i = 0; i < _bits.length; i++) {
            bits[i] = size - _bits[i] - 1;
        }

        HashMap<String, String[]> rows = new HashMap<String, String[]>();

        for(String key : matrix.getData().keySet()) {
            Set<String> set = new HashSet<String>(m2.getData().keySet());
            String[] keys = new String[set.size()];
            keys = set.toArray(keys);
            for(int j = 0; j < key.length(); j++) {
                int count = 0;
                for(String k : keys) {
                    if(k.charAt(bits[j]) == key.charAt(j)) {
                        count++;
                    }
                }

                String[] filtered = new String[count];
                int counter = 0;
                for(String k : keys) {
                    if(k.charAt(bits[j]) == key.charAt(j)) {
                        filtered[counter] = k;
                        counter++;
                    }
                }

                keys = filtered;
            }
            Arrays.sort(keys);
            rows.put(key, keys);
        }

        for(String a : rows.keySet()) {
            for(int b = 0; b < rows.get(a).length; b++) {
                for(String c : rows.keySet()) {
                    m2.getData().get(rows.get(a)[b]).put(rows.get(c)[b],
                        matrix.getData().get(a).get(c));
                }
            }
        }

        m2.updateRaw();

        return m2;
    }

    /**
     * Applies a gate to the current state matrix
     * 
     * @param   gate   the gate
     **/
    public void applyGate(GateMatrix gate) {
        GateMatrix m2 = gate;
        HashMap<String, Complex> tempMatrix = new HashMap<String, Complex>(
                matrix);

        for(String i : m2.getData().keySet()) {
            Complex sum = new Complex(0, 0);
            for(String j : m2.getData().get(i).keySet()) {
                Complex temp = m2.getData().get(j).get(i).multiply(tempMatrix.
                        get(j));

                sum = sum.add(temp);
            }
            matrix.put(i, sum);
        }
    }

    /**
     * Apply current gate
     **/
    public void apply() {
        // Apply gate
        applyGate(gate);

        // Revert gate to identity gate
        int len = (int)Math.pow(2, size);
        Complex[][] _gate = new Complex[len][len];

        for(int i = 0; i < _gate.length; i++) {
            for(int j = 0; j < _gate[0].length; j++) {
                _gate[i][j] = new Complex(0, 0);
            }
        }

        for(int i = 0; i < len; i++) {
            _gate[i][i] = new Complex(1, 0);
        }

        gate = new GateMatrix(_gate);
    }

    /**
     * Apply and revert matrix to how it was before
     * 
     * @return   the matrix when applied
     **/
    public HashMap<String, Complex> applyAndContinue() {
        // Save old matrix
        HashMap<String, Complex> oldMatrix = new HashMap<String, Complex>(
                matrix);

        // Apply gate
        applyGate(gate);
        // Get result matrix
        HashMap<String, Complex> res = new HashMap<String, Complex>(matrix);

        // Revert matrix
        matrix = oldMatrix;

        // Return result matrix
        return res;
    }

    /**
     * Accessor for size
     * 
     * @return   the size
     **/
    public int getSize() {
        return size;
    }

    /**
     * Accessor for matrix
     * 
     * @return   the matrix
     **/
    public HashMap<String, Complex> getMatrix() {
        return matrix;
    }
}
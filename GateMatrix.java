/*
 * Represents matrices of quantum gates
 */

import java.util.HashMap;
import java.util.Arrays;
public class GateMatrix {
    // Instance variables
    private int size;
    private Complex[][] rawData;
    private HashMap<String, HashMap<String, Complex>> data;

    /*
     * Constructor
     */
    public GateMatrix(Complex[][] array) throws QuantumSimException {
        // If not same rows and columns, there is an error
        if(array.length != array[0].length) {
            throw new QuantumSimException("Gate must have equal rows and " + 
                "columns");
        }

        // Calculate size in qubits
        // - Had to use change of base formula since here is no log base 2
        //   method natively in the Math class
        int size = (int)Math.round(Math.log(array.length) / Math.log(2));

        // If length of array is not a power of 2, it is invalid
        if((int)Math.pow(2, size) != array.length) {
            throw new QuantumSimException("Invalid qubit count in gate");
        }
        this.size = size;

        Complex[][] data = array;
        // HashMap of Complex arrays with String keys
        HashMap<String, Complex[]> data2 = new HashMap<String, Complex[]>();

        rawData = array;

        // Convert from array to HashMap with keys as binary strings
        for(int i = 0; i < (int)Math.pow(2, size); i++) {
            String bin = Utils.decToBin(i);

            if(size != bin.length()) {
                bin = Utils.repeat("0", size - bin.length()) + bin;
            }

            data2.put(bin, array[i]);
        }

        // Now, convert each row into a HashMap as well
        HashMap<String, HashMap<String, Complex>> tempData =
            new HashMap<String, HashMap<String, Complex>>();

        // Loop through HashMap
        for(String key : data2.keySet()) {
            Complex[] tempArr = data2.get(key);
            // New HashMap for this row
            HashMap<String, Complex> temp = new HashMap<String, Complex>();

            // Convert from array to HashMap with keys as binary strings
            for(int i = 0; i < (int)Math.pow(2, size); i++) {
                String bin = Utils.decToBin(i);

                if(size != bin.length()) {
                    bin = Utils.repeat("0", size - bin.length()) + bin;
                }

                temp.put(bin, tempArr[i]);
            }

            tempData.put(key, temp);
        }

        this.data = tempData;
    }

    /**
     * Accessor for rawData
     * 
     * @return   raw data
     **/
    public Complex[][] getRawData() {
        return rawData;
    }

    /**
     * Accessor for data
     * 
     * @return   data
     **/
    public HashMap<String, HashMap<String, Complex>> getData() {
        return data;
    }

    /**
     * Multiply matrices
     * 
     * @param   other   other matrix
     * @return          product
     **/
    public GateMatrix multiply(GateMatrix other) {
        // Get raw data of each matrix
        Complex[][] thisData = rawData;
        Complex[][] otherData = other.getRawData();

        // Gate for result of multiplication
        Complex[][] fullGate = new Complex[thisData.length]
            [otherData[0].length];
        // Initialize gate to include all 0s
        for(int i = 0; i < fullGate.length; i++) {
            for(int j = 0; j < fullGate[i].length; j++) {
                fullGate[i][j] = new Complex(0, 0);
            }
        }

        // Loop through rows of this matrix
        for(int row = 0; row < thisData.length; row++) {
            // Loop through columns of other matrix
            for(int col = 0; col < otherData[0].length; col++) {
                // Declare sum counter
                Complex sum = new Complex(0, 0);

                // Calculate dot product
                for(int i = 0; i < otherData.length; i++) {
                    sum = sum.add(otherData[i][col].
                        multiply(thisData[row][i]));
                }

                fullGate[row][col] = sum;
            }
        }

        return new GateMatrix(fullGate);
    }

    /**
     * Updates rawData
     **/
    public void updateRaw() {
        for(String row : data.keySet()) {
            for(String col : data.get(row).keySet()) {
                int rowAsInt = Utils.binToDec(row);
                int colAsInt = Utils.binToDec(col);
                rawData[rowAsInt][colAsInt] = data.get(row).get(col);
            }
        }
    }

    /**
     * toString() method - primarily for debugging purposes
     * 
     * @return   string representation of quantum gate matrix
     **/
    public String toString() {
        String res = "[\n";
        for(int i = 0; i < rawData.length; i++) {
            res += Arrays.toString(rawData[i]) + ",\n";
        }
        return res;
    }
}
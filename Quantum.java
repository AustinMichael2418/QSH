/*
 * Contains utilities for quantum computation
 */

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
public class Quantum {
    /**
     * Calculates quantum probability given the amplitude
     * 
     * @param   amplitude   the amplitude
     * @return              the probability
     **/
    public static double qProb(Complex amplitude) {
        Complex amp = amplitude;
        double prob = amp.getRealPart() * amp.getRealPart() + 
            amp.getImaginaryPart() * amp.getImaginaryPart();
        return prob;
    }

    /**
     * Measures a qubit in a QubitMatrix and returns the result
     * 
     * @param   qubitMatrix   the QubitMatrix
     * @param   bit           qubit number
     * @return                measured value of given qubit
     **/
    public static int measureBit(QubitMatrix qubitMatrix, int bit) {
        // Apply all gates
        qubitMatrix.apply();

        // Get size and matrix
        int size = qubitMatrix.getSize();
        HashMap<String, Complex> matrix = qubitMatrix.getMatrix();

        // Qubit 0 is last in string
        int b = size - bit - 1;

        // Generate list of states for the qubits
        HashMap<String, HashMap<String, Complex>> obj = new 
            HashMap<String, HashMap<String, Complex>>();
        obj.put("0", new HashMap<String, Complex>());
        obj.put("1", new HashMap<String, Complex>());

        for(String i : matrix.keySet()) {
            HashMap<String, Complex> temp = obj.get(i.substring(b, b + 1));
            temp.put(i, matrix.get(i));

            obj.put(i.substring(b, b + 1), temp);
        }

        // List of probabilities from the amplitudes
        HashMap<String, HashMap<String, Double>> obj2 = new 
            HashMap<String, HashMap<String, Double>>();
        obj2.put("0", new HashMap<String, Double>());
        obj2.put("1", new HashMap<String, Double>());

        for(String i : obj.get("0").keySet()) {
            obj2.get("0").put(i, qProb(obj.get("0").get(i)));
        }

        for(String i : obj.get("1").keySet()) {
            obj2.get("1").put(i, qProb(obj.get("1").get(i)));
        }

        // List for total probabilities of 0 and 1 as the qubit
        HashMap<String, Double> sums = new HashMap<String, Double>();
        sums.put("0", 0.0);
        sums.put("1", 0.0);

        for(String i : obj2.keySet()) {
            double sum = 0;
            for(String j : obj2.get(i).keySet()) {
                sum += obj2.get(i).get(j);
            }
            sums.put(i, sum);
        }

        // There is an error if the two qubits don't add to 1
        if((int)Math.round(sums.get("0") + sums.get("1")) != 1) {
            throw new QuantumSimException("Probabilities don't add to 1");
        }

        // Random number
        double rand = Math.random();
        String choice = "0";
        // Generate choice from number
        if(rand > sums.get("0")) choice = "1";

        // Probability of generated choice
        double winSum = sums.get(choice);

        // Update matrix based on measured value
        for(String i : qubitMatrix.getMatrix().keySet()) {
            Set<String> set = new HashSet<String>(obj.get(choice).keySet());
            String[] keys = new String[set.size()];
            keys = set.toArray(keys);
            boolean found = false;
            for(String key : keys) {
                if(key.equals(i)) found = true;
            }

            if(found) {
                qubitMatrix.getMatrix().put(i, qubitMatrix.getMatrix().get(i).
                    multiply(new Complex(Math.sqrt(1 / winSum), 0)));
            } else {
                qubitMatrix.getMatrix().put(i, new Complex(0, 0));
            }
        }

        // Return choice as int
        return Integer.parseInt(choice);
    }
}
/*
 * This class is used to test the quantum simulation library
 */

public class QuantumTest {
    public static void main(String[] args) {
        // These variables will store the instances of 1 and 0 on the
        // specified qubit.
        int aa = 0;
        int bb = 0;

        for(int iii = 0; iii < 1000; iii++) {
            // This was used to show the percent complete, but was removed
            // since, unlike on Linux, Windows makes \r into a new line
            // instead of just returning to the beginning of the line.
            //System.out.print("\r" + String.format("%.1f", iii/10.0) + "%");

            // Create 4-qubit matrix
            QubitMatrix data = new QubitMatrix(4);

            // Hadamard on qubit 0
            data.addGate(DefaultGates.H, new int[]{0});
            // Hadamard on qubit 1
            data.addGate(DefaultGates.H, new int[]{1});
            // Toffoli on 0, 1, and 2
            data.addGate(DefaultGates.CCNOT, new int[]{0, 1, 2});
            // Swap 0 and 2
            data.addGate(DefaultGates.SWAP, new int[]{0, 2});

            if(Quantum.measureBit(data, 1) == 1) {
                // If qubit 1 is 1, use X gate to make it 0.
                data.addGate(DefaultGates.X, new int[]{1});
            }
            if(Quantum.measureBit(data, 2) == 1) {
                // If qubit 2 is 1, use X gate to make it 0.
                data.addGate(DefaultGates.X, new int[]{2});
            }

            // Run program
            data.apply();

            // If result is 100% chance of 0001, add to aa, or otherwise add
            // to bb.
            if((int)Math.round(data.getMatrix().get("0001").getRealPart())
            == 1) aa++;
            else bb++;
        }

        // New line
        System.out.println();

        // Calculate total
        double total = aa + bb;
        // Print probabilities
        System.out.println("Probability of 1: " + (aa / total * 100));
        System.out.println("Probability of 0: " + (bb / total * 100));
        // New line
        System.out.println();

        // New 2-qubit matrix
        QubitMatrix data = new QubitMatrix(2);
        // For testing Hadamard gate with proper tensor product calculations
        data.addGate(DefaultGates.H, new int[]{1});
        data.apply();
        System.out.println(data.getMatrix());
    }
}
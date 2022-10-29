/*
 * Gates represented by integers
 */

public class IntGates {
    public static int[][] CNOT = {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0}
        };
    public static int[][] CCNOT = {
            {1, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 1, 0}
        };
    public static int[][] X = {
            {0, 1},
            {1, 0}
        };
    public static int[][] I = {
            {1, 0},
            {0, 1}
        };
    public static int[][] SWAP = {
            {1, 0, 0, 0},
            {0, 0, 1, 0},
            {0, 1, 0, 0},
            {0, 0, 0, 1}
        };

    /**
     * Converts int gates to Complex gates
     * 
     * @param   gate   int gate
     * @return         Complex gate
     **/
    public static Complex[][] toComplexGate(int[][] gate) {
        Complex[][] res = new Complex[gate.length][gate[0].length];
        for(int i = 0; i < gate.length; i++) {
            for(int j = 0; j < gate[0].length; j++) {
                res[i][j] = new Complex(gate[i][j], 0);
            }
        }
        return res;
    }
}
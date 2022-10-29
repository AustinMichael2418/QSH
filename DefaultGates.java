/*
 * List of all default quantum gates
 */

public class DefaultGates {
    public static Complex[][] H = {
            {new Complex(1 / Math.sqrt(2), 0), new Complex(1 / Math.sqrt(2), 0)},
            {new Complex(1 / Math.sqrt(2), 0), new Complex(-1 / Math.sqrt(2), 0)}
        };
    public static Complex[][] CNOT = IntGates.toComplexGate(IntGates.CNOT);
    public static Complex[][] CCNOT = IntGates.toComplexGate(IntGates.CCNOT);
    public static Complex[][] X = IntGates.toComplexGate(IntGates.X);
    public static Complex[][] I = IntGates.toComplexGate(IntGates.I);
    public static Complex[][] SWAP = IntGates.toComplexGate(IntGates.SWAP);
}
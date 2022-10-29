/*
 * This class represents complex numbers
 */

public class Complex {
    // Real part and imaginary part of complex number 
    private double realPart;
    private double imaginaryPart;

    /*
     * Constructor
     */
    public Complex(double real, double imaginary) {
        realPart = real;
        imaginaryPart = imaginary;
    }

    /*
     * Constructor for reading from String - needed for numerical processor
     */
    public Complex(String _data) {
        // Input is as <a + bi>, so remove the "<" and ">"
        String data = _data.substring(1, _data.length() - 2);
        // Split by space
        String[] params = data.split(" ");
        // Get real and imaginary parts
        realPart = Double.parseDouble(params[0]);
        imaginaryPart = Double.parseDouble(params[2]);
        if(params[1].equals("-")) {
            // If subtracting, opposite the imaginary part
            imaginaryPart = -imaginaryPart;
        }
    }

    /**
     * Accessor for realPart
     * 
     * @return   real part of the number
     **/
    public double getRealPart() {
        return realPart;
    }

    /**
     * Accessor for imaginaryPart
     * 
     * @return   imaginary part of the number
     **/
    public double getImaginaryPart() {
        return imaginaryPart;
    }

    /**
     * Adds two complex numbers
     * 
     * @param   val2   other number
     * @return         sum
     **/
    public Complex add(Complex val2) {
        return new Complex(realPart + val2.getRealPart(), imaginaryPart + 
            val2.getImaginaryPart());
    }

    /**
     * Subtracts two complex numbers
     * 
     * @param   val2   other number
     * @return         difference
     **/
    public Complex subtract(Complex val2) {
        return new Complex(realPart - val2.getRealPart(), imaginaryPart - 
            val2.getImaginaryPart());
    }

    /**
     * Multiplies two complex numbers
     * 
     * @param   val2   other number
     * @return         product
     **/
    public Complex multiply(Complex val2) {
        // Get parts
        double numA = realPart;
        double numB = imaginaryPart;
        double numC = val2.getRealPart();
        double numD = val2.getImaginaryPart();
        // Multiply using FOIL method and the fact that i^2 = -1
        return new Complex(numA * numC - numB * numD, numA * numD + numB *
            numC);
    }

    /**
     * Squares a complex number
     * 
     * @return   this number squared  
     **/
    public Complex square() {
        return multiply(this);
    }

    /**
     * toString() method
     * 
     * @return   complex number as a string
     **/
    public String toString() {
        return realPart + (imaginaryPart < 0 ? " - " : " + ") + 
        Math.abs(imaginaryPart) + "i";
    }
}
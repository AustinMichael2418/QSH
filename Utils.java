/*
 * Contains utility methods
 */

public class Utils {
    /**
     * Converts decimal number (int) to binary string
     * 
     * @param   dec   decimal number
     * @return        number as binary string
     **/
    public static String decToBin(int dec) {
        String res = "";

        int val = dec;
        while(val != 0) {
            res = (val % 2) + res;
            val /= 2;
        }

        return res;
    }

    /**
     * Converts binary string into decimal number
     * 
     * @param   bin   binary string
     * @return        decimal number
     **/
    public static int binToDec(String bin) {
        int accumulator = 0;
        for(int i = bin.length() - 1, j = 0; i >= 0; i--, j++) {
            int thisValue = bin.charAt(i) - '0';
            accumulator += ((int)Math.pow(2, j)) * thisValue;
        }
        return accumulator;
    }

    /**
     * Repeats a string a given number of times
     * 
     * @param   str     the string
     * @param   times   times to repeat
     * @return          `str` repeated `times` times
     **/
    public static String repeat(String str, int times) {
        String res = "";

        for(int i = 0; i < times; i++) {
            res += str;
        }

        return res;
    }
}
/*
 * This class is just for creating the thumbnail sketch for the practice activity.
 */

import java.util.Scanner;
public class Sketch {
    public static void main(String[] args) {
        Scanner _ = new Scanner(System.in);
        System.out.println("[ QSH 0.0.1 - a quantum shell ]");
        System.out.println("");
        System.out.println("Type \"help\" to get a list of commands");
        System.out.print("qsh> ");
        _.nextLine(); // help
        System.out.println("set <qubits> - set with specified number of qubits");
        System.out.println("listgates      - list all defined gates");
        System.out.println("prob           - list probabilities of states");
        System.out.println("exit           - exit QSH");
        System.out.println("Any working Quil commands will work as well");
        System.out.print("qsh> ");
        _.nextLine(); // set 2
        System.out.println("Configured for 2 qubits");
        System.out.print("qsh> ");/*
        _.nextLine(); // listgates
        System.out.println("H");
        System.out.println("X");
        System.out.println("CNOT");
        System.out.println("CCNOT");
        System.out.println("SWAP");
        System.out.print("qsh> ");*/
        _.nextLine(); // H 0
        System.out.println("(0.70711+0j)|00> + (0.70711+0j)|01>");
        System.out.print("qsh> ");
        _.nextLine(); // prob
        System.out.println("00:  50%");
        System.out.println("01:  50%");
        System.out.println("10:   0%");
        System.out.println("11:   0%");
        System.out.print("qsh> ");
        _.nextLine(); // exit

    }
}
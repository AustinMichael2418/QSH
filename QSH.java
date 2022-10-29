/*
 * Author: Austin Stitz 
 * Date: 5/2/2021 
 * Program: QSH
 *
 * Description: This program enables a quantum programmer to test new quantum
 * programs/circuits through a command line interface.
 * What I learned: I learned nothing while making this program.
 * Difficulties: There were a few bugs along the way, but they were fairly
 * simple to fix, the simplest simply being that Java doesn't by default sort
 * key sets in HashMaps.
 */


/*
 * Future support:
 * - Parametric gates and circuits
 * - Decision making in circuits
 * - Starter guide
 * - Checkpoints
 */

import java.util.Scanner;
public class QSH {
    public static void main(String[] args) throws Exception {
        // Create shell
        QuantumShell shell = new QuantumShell();
        // Create scanner
        Scanner scanner = new Scanner(System.in);

        // Header
        System.out.println("[ QSH 0.0.1 - a quantum shell ]");
        System.out.println("");
        System.out.println("Type \"help\" to get a list of commands");
        
        // While shell is active
        while(shell.isActive()) {
            // Prompt
            System.out.print("qsh> ");
            // Get and parse input
            String input = scanner.nextLine();
            System.out.print(shell.parse(input));
            
            // Indentation prompt if needed
            while(shell.isIndented()) {
                System.out.print("qsh> ... ");
                input = scanner.nextLine();
                System.out.print(shell.parse(input));  
            }
        }        
    }
}

/*
 * Sample Output:
 * [ QSH 0.0.1 - a quantum shell ]
 * 
 * Type "help" to get a list of commands
 * qsh> help
 * set <qubits> - set with specified number of qubits
 * listgates    - list all defined gates
 * prob         - list probabilities of states
 * exit         - exit QSH
 * Any working quil Quil commands will work as well.
 * 
 * QSH has full Quil support other than the following features:
 *  - declaration commands
 *  - gate/circuit modifiers
 *  - dereferencing
 *  - pragmas
 *  - control modifiers
 *  - parametric gates/circuits
 * 
 * With these limitations in mind, you can find the Quil specs here:
 * https://github.com/rigetti/quil/blob/master/spec/Quil.md
 * https://arxiv.org/pdf/1608.03355.pdf
 * 
 * I hope you enjoy using QSH to learn/practice quantum computing!
 * qsh> set 2
 * Qubit matrix configured with 2 qubits
 * qsh> listgates
 * SWAP
 * H
 * X
 * I
 * CCNOT
 * CNOT
 * qsh> DEFCIRCUIT BELL_STATE:
 * qsh> ... H 0
 * qsh> ... CNOT 0 1
 * qsh> ... 
 * qsh> BELL_STATE
 * (0.7071067811865475 + 0.0i)|00> (0.7071067811865475 + 0.0i)|11> (0.0 + 0.0i)|01> (0.0 + 0.0i)|10> 
 * qsh> prob
 * 00: 50%
 * 11: 50%
 * 01: 0%
 * 10: 0%
 * qsh> H 0
 * (0.4999999999999999 + 0.0i)|00> (-0.4999999999999999 + 0.0i)|11> (0.4999999999999999 + 0.0i)|01> (0.4999999999999999 + 0.0i)|10> 
 * qsh> prob
 * 00: 25%
 * 11: 25%
 * 01: 25%
 * 10: 25%
 * qsh> H 1
 * (0.7071067811865474 + 0.0i)|00> (0.7071067811865474 + 0.0i)|11> (0.0 + 0.0i)|01> (0.0 + 0.0i)|10> 
 * qsh> prob
 * 00: 50%
 * 11: 50%
 * 01: 0%
 * 10: 0%
 * qsh> exit
 */

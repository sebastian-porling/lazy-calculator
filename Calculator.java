/*
 * To compile (Mac, but should work for Windows and Linux):
 * javac Calculator.java
 *
 * To execute (Mac, but should work for Windows and Linux):
 * java Calculator
 * or
 * java Calculator [file]
 * or
 * java Calculator < [file]
 */


import java.io.*;
import java.util.*;

/**
 *  The main class for a Calculator that can add, subtract and multiply.
 *  It supports variables and lazy evaluation.
 *
 *  Commands:
 *      [register] [operator] [register],
 *      print [register],
 *      quit
 *
 *  [register] : Can be anything that is alphanumerical, or just an integer.
 *  [operator] : can be add, subtract or multiply. In text form. ex. "a add 2".
 */
public class Calculator {

    /**
     *  Class for making an Operation object.
     */
    static class Operation{
        public String term1;
        public String operator;
        public String term2;

        /**
         *  Making an Operation Object
         * @param line String[], This should be a normal operation from the command line, like "a add b".
         */
        public Operation(String[] line) throws IllegalOperationException{
            if (line.length != 3){
                throw new IllegalOperationException();
            }
            this.term1 = line[0];
            this.operator = line[1];
            this.term2 = line[2];
        }
    }

    /**
     *  Exception for cycles.
     */
    static class CycleException extends Exception{

        /**
         * Create cycle exception.
         */
        public CycleException(){
            super("Illegal operation, There is a cycle in the evaluation");
        }
    }

    /**
     *  Exception for illegal operations
     */
    static class IllegalOperationException extends Exception{

        /**
         *  Create illegal operation exception.
         */
        public IllegalOperationException(){
            super("Illegal operation");
        }
    }

    /**
     * Checks if the input string is an integer.
     * Returns True when it is integer and False otherwise.
     * @param input String
     * @return Boolean
     */
    private static boolean checkIfInteger(String input){
        try {
            Integer.parseInt(input);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks of the input string is alphanumeric (Only characters that are in the alphabet and integers).
     * Returns True if it is and false otherwise
     * @param input String
     * @return Boolean
     */
    private static boolean checkIfAlphanumeric(String input){
        return input.matches("[a-zA-Z0-9]+");
    }

    /**
     * Checks if the input string is a valid register.
     * Returns True if it is and false otherwise
     * @param input String
     * @return Boolean
     */
    private static boolean checkIfValidRegister(String input){
        if (!checkIfInteger(input) && !checkIfAlphanumeric(input)) return false;
        return true;
    }

    /**
     * Checks if the given terms can make a valid operation.
     * First term is not allowed to be an integer. First and second most be a valid register.
     * @param term1 String First term in operation.
     * @param term2 String Second term in operation
     * @return Boolean
     */
    private static boolean checkValidOperation(String term1, String term2){
        if(!(checkIfValidRegister(term1) && checkIfInteger(term1)) && checkIfValidRegister(term2)) return false;
        return true;
    }

    /**
     * Evaluates the chosen register. It will create a Hashset and Hashmap that will be used for dependency and values.
     * Returns the value of the register.
     * @param register String, The chosen register to evaluate.
     * @param operations ArrayList, All the recorded operations.
     * @return int, The result of the evaluation.
     * @throws CycleException When there is a cycle.
     */
    private static int evaluate(String register, ArrayList<Operation> operations) throws CycleException{
        HashSet<String> dependent_operations = new HashSet<>();
        HashMap<String, Integer> mapped_values = new HashMap<>();
        return evaluate(register, operations, mapped_values, dependent_operations);
    }

    /**
     *  Evaluates the chosen register, it does this recursively by recording dependencies and the evaluated values.
     * @param register String, The chosen register to evaluate.
     * @param operations ArrayList, All the recorded operations.
     * @param mapped_values HashMap, Here we will add evaluated values for registers.
     * @param dependent_registers HashSet, Here we will record dependency.
     * @return int, The result of the evaluation.
     * @throws CycleException When there is a cycle.
     */
    private static int evaluate(String register, ArrayList<Operation> operations, HashMap<String, Integer> mapped_values, HashSet<String> dependent_registers) throws CycleException {
        int result = 0;

        // If the "register" is an integer, just return it.
        if(checkIfInteger(register)){
            return Integer.parseInt(register);
        }

        // Check if the register has already been evaluated.
        if(mapped_values.containsKey(register)) return mapped_values.get(register);

        // If we have already evaluated the register we have a cycle. Abort the evaluation.
        if (dependent_registers.contains(register)){
            throw new CycleException();
        }
        dependent_registers.add(register);

        // Evaluate all the operations.
        for(Operation operation : operations){
            // If the first term is not the same as the register it's not supposed to be evaluated
            if (!operation.term1.equals(register)){
                continue;
            }
            // Evaluate the second term in the operation.
            if (operation.operator.equals("add")){
                result += evaluate(operation.term2, operations, mapped_values, dependent_registers);
            } else if (operation.operator.equals("subtract")){
                result -= evaluate(operation.term2, operations, mapped_values, dependent_registers);
            } else if (operation.operator.equals("multiply")){
                result *= evaluate(operation.term2, operations, mapped_values, dependent_registers);
            } else {
                System.err.println("Operation: " + operation.operator + " is not supported, will be ignored.");
            }
        }
        // Add the register and its value. And then return the evaluated value.
        mapped_values.replace(register, result);
        return result;
    }


    /**
     *  The main function.
     * @param args String[] Takes in a string array. But only one argument is allowed. Which should a file name with extension.
     */
    public static void main(String[] args) {

        // Init
        boolean running = true;
        boolean reading_from_file = false;
        Scanner scanner = new Scanner(System.in);
        ArrayList<Operation> operations = new ArrayList<>();

        // Check if using file
	    if(args.length != 0) {
	        if (args.length > 1) {
	            running = false;
                System.err.println("Error, to many arguments.");
            }
            try {
                reading_from_file = true;
                File file = new File(args[0]);
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                System.err.println("Error, File not found: " + e.getMessage());
                running = false;
            }
        }

	    // The running program
	    while(running){
	        String line = null;

	        // Get the next line
	        if(scanner.hasNext()){
	            line = scanner.nextLine();
            }

	        if(line != null) {
                line.replaceAll("\n", "");
                String lineComponents[] = line.split(" ");

                if (lineComponents.length == 1){
                    /*
                    If the length is one it should be a quit command.
                    If it is not quit then continue.
                    */

                    if (lineComponents[0].toLowerCase().equals("quit")){
                        running = false;
                        break;
                    } else {
                        System.err.println("Illegal command: " + line);
                        continue;
                    }

                } else if (lineComponents.length == 3){
                    /*
                    If the length is 3 then it should be an operation.
                    Check if the operation is legal, then record the operation.
                     */
                    if (checkValidOperation(lineComponents[0], lineComponents[2])){
                        System.err.println("Illegal command: " + line);
                        continue;
                    }

                    try {
                        operations.add(new Operation(lineComponents));
                    }catch (IllegalOperationException exception){
                        System.err.println(exception.getMessage());
                    }

                } else if (lineComponents.length == 2){
                    /*
                    If the length is 2 then it should be a print operation.
                    We can then evaluate the given register.
                    */
                    if (lineComponents[0].toLowerCase().equals("print")){
                        try {
                            int result = evaluate(lineComponents[1], operations);
                            System.out.println(result);
                        } catch (CycleException exception){
                            System.err.println(exception.getMessage());
                            continue;
                        }
                    } else {
                        System.err.println("Illegal command: " + line);
                        continue;
                    }

                } else {
                    System.err.println("Illegal command: " + line);
                    continue;
                }
            }
        }

	    /*
	    Close the scanner and end the program.
	    */
	    if (reading_from_file){
	        scanner.close();
        }
    }
}


# Lazy Calculator
This is a calculator that can use the operators add, subtract and multiply.
It supports the use of registers/variables and [lazy evaluation](https://en.wikipedia.org/wiki/Lazy_evaluation).

## Commands
> "<register> <operator> <register>"
> "print <register> : Will evaluate the chosen register/variable."
> "quit : Quits/Aborts the program."

### Definitions
> "<register> : Can be anything that is alphanumerical. In order to not complicate things it's not possible to assign values to integers. ex 123 adds 5."
> "<operator> : Can be add, subtract or multiply. Everything else will be ignored."

## Compile and execute

To compile using Mac(Using Windows or Linux should also work) use the following:
> javac Calculator.java

To execute the program use the following in Mac(Should work for Windows and Linux):
> java Calculator
> or
> java Calculator file.ext
> or
> java Calculator < file.ext

The calculator does only take one argument which is a file. Otherwise it will take input from standard input stream.

### More about the calculator
It will log which commands/operations that is not possible. But should more or less always run.
The only exceptions are when the input file doesn't exist or there were to many arguments.
Chosen to evaluate a register to 0 if nothing has been assigned. Like "a add b, print a" will result in a 0.


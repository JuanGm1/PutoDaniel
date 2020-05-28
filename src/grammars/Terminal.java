/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammars;

/**
 * This class let us create a terminal for the grammar and productions
 *
 * @author danielescobar
 */
public class Terminal {

    private char symbol;

    /**
     * Constructor to create a terminal for the grammar.
     *
     * @param symbol Is the value of the terminal.
     */
    public Terminal(char symbol) {
        setSymbol(symbol);
    }

    /**
     * Method to set the value of the terminal
     *
     * @param symbol Is going to be the value of the terminal
     */
    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Method to return the value of the terminal
     *
     * @return Return value of the symbol
     */
    public char getSymbol() {
        return this.symbol;
    }

    @Override
    public String toString() {
        return String.valueOf(getSymbol());
    }
}

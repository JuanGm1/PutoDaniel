/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammars;

/**
 * This class let us create a non terminal to the grammar and productions
 *
 * @author danielescobar
 */
public class NonTerminal {

    private String id;

    /**
     * Constructor to create a non terminal for the grammar
     *
     * @param name Is the ID of the non terminal
     */
    public NonTerminal(String name) {
        setID(name);
    }

    /**
     * Method to set the value of the ID
     *
     * @param name Is the ID of the non terminal
     */
    public void setID(String name) {
        this.id = name.toUpperCase();
    }

    /**
     * This method let us know the Id of a non Terminal
     *
     * @return The Id of the non terminal.
     */
    public String getID() {
        return this.id;
    }

    @Override
    public String toString(){
        return "<"+getID()+">";
    }
    
}

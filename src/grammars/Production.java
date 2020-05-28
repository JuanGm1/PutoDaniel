/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammars;

import java.util.ArrayList;
import java.util.List;

/**
 * This class let us create a production
 *
 * @author danielescobar
 */
public class Production {

    private NonTerminal leftSide;
    List<Object> rightSide = new ArrayList<>();
    List<NonTerminal> nonTerminalsInRight = new ArrayList<>();
    List<Terminal> terminalsInRight = new ArrayList<>();

    /**
     * Constructor to create a empty production.
     */
    public Production() {
            
    }

    /**
     * Constructor to create a production for the grammar
     *
     * @param leftSide Is the left side of the production
     */
    public Production(NonTerminal leftSide) {
        setLeftSide(leftSide);
    }

    /**
     * This method let us set the left side value of the production
     *
     * @param leftSide Is the left side of the production
     */
    public void setLeftSide(NonTerminal leftSide) {
        this.leftSide = leftSide;
    }

    /**
     * This method let us know the non terminal of the left side
     *
     * @return The non terminal of the left side
     */
    public NonTerminal getLeftSide() {
        return this.leftSide;
    }

    /**
     * This method let us add a non terminal to the right side of the production
     *
     * @param rightSide Element to be added to the right side of the production
     */
    public void addElementRightSideN(NonTerminal rightSide) {
        this.rightSide.add(rightSide);
        this.nonTerminalsInRight.add(rightSide);
    }

    /**
     * This method let us add a terminal to the right side of the production
     *
     * @param rightSide Element to be added to the right side of the production
     */
    public void addElementRightSide(Terminal rightSide) {
        this.rightSide.add(rightSide);
        if (rightSide.getSymbol() != 'λ') {
            this.terminalsInRight.add(rightSide);
        }
    }

    /**
     * This method let us know the first item of the right side
     *
     * @return the terminal or non terminal in the first part of the right side
     */
    public Object firstItem() {
        return rightSide.get(0);
    }

    /**
     * This method let us know if the first item is a terminal
     *
     * @return True if the first item is terminal, otherwise return False
     */
    public boolean firstItemIsTerminal() {
        return firstItem().getClass().toString().equals("class grammars.Terminal");
    }

    /**
     * This method will return the first item when it is non terminal
     *
     * @return The non terminal in the right side
     */
    public NonTerminal firstItemNonTerminal() {
        return (NonTerminal) rightSide.get(0);
    }

    /**
     * This method will return the first item when it is terminal
     *
     * @return The terminal in the right side
     */
    public Terminal firstItemTerminal() {
        return (Terminal) rightSide.get(0);
    }

    /**
     * This method let us know if the first item is a terminal different than
     * lambda
     *
     * @return True if the first item is terminal, otherwise return False
     */
    public boolean firstItemIsTerminalNotLambda() {
        if (firstItemIsLambda()) {
            return false;
        } else {
            return firstItemIsTerminal();
        }
    }

    /**
     * This method let us know if the first item of the right side is lambda
     *
     * @return True if the first element is lambda, otherwise returns False
     */
    public boolean firstItemIsLambda() {
        return firstItem().toString().equalsIgnoreCase("λ");
    }

    /**
     * This method let us know the terminals defined in the right side
     *
     * @return
     */
    public List<NonTerminal> nonTerminalsInRight() {
        return this.nonTerminalsInRight;
    }

    /**
     * This method let us know if the left symbol is alive because there are not
     * NonTerminals
     *
     * @return True if the left symbol is alive, False if the found one
     * NonTerminal
     */
    public boolean isRightSideAllTerminals() {
        return nonTerminalsInRight().isEmpty();
    }
    
    public boolean isRightSideAllNotTerminals() {
        return terminalsInRight.isEmpty();
    }

    /**
     * This method let us know if the production is T-alpha
     *
     * @return True if the production is T-alpha, False if not
     */
    public boolean isTAlpha() {
        boolean isAlpha = false;
        if (firstItemIsTerminal()) {
            isAlpha = true;
            if (firstItemIsLambda()) {
                isAlpha = false;
            }
        }
        return isAlpha;
    }

    /**
     * This method will return the terminals of the alpha part
     *
     * @return The list with all terminals of the alpha part
     */
    public List<Terminal> getTerminalsInRightAlpha() {
        return this.terminalsInRight.subList(1, terminalsInRight.size());
    }

}

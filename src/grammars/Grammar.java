/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammars;

import java.util.ArrayList;
import java.util.List;

/**
 * This class let us create a grammar
 *
 * @author danielescobar
 */
public class Grammar {

    private List<Production> productions = new ArrayList<>();
    private List<NonTerminal> leftSiders = new ArrayList<>();
    private List<NonTerminal> nonTerminalsAlives = new ArrayList<>();
    private List<Terminal> terminals = new ArrayList<>();

    /**
     * Constructor to create a grammar without any productions
     */
    public Grammar() {

    }

    /**
     * Constructor to create a grammar with an initial production
     *
     * @param initialProduction Is the initial production of the grammar
     */
    public Grammar(Production initialProduction) {
        this.productions.add(initialProduction);
    }

    /**
     * This method let us add a terminal into a list for later use in stack
     *
     * @param terminal The terminal to be added
     */
    public void addTerminal(Terminal terminal) {
        if (terminals.isEmpty()) {
            terminals.add(terminal);
        } else {
            boolean exist = false;
            for (int i = 0; i < terminals.size(); i++) {
                if (terminal.getSymbol() == terminals.get(i).getSymbol()) {
                    exist = true;
                }
            }
            if (exist == false) {
                terminals.add(terminal);
            }
        }
    }

    /**
     * This method let us get the list of terminals
     *
     * @return The list with all terminals
     */
    public List<Terminal> getTerminals() {
        Terminal terminal = new Terminal('┐');
        terminals.add(terminal);
        return this.terminals;
    }

    /**
     * This method let us know if the non terminal is already added into the
     * list
     *
     * @param nonTerminal The non terminal to be checked
     * @return True if the non terminal is in the list, False if the non
     * terminal is not in the list
     */
    public boolean alreadyInLeft(NonTerminal nonTerminal) {
        if (leftSiders.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < leftSiders.size(); i++) {
                if (leftSiders.get(i).getID().equalsIgnoreCase(nonTerminal.getID())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method let us add a new production to the grammar
     *
     * @param production
     */
    public void addProduction(Production production) {
        if (!alreadyInLeft(production.getLeftSide())) {
            leftSiders.add(production.getLeftSide());
        }
        this.productions.add(production);
    }

    /**
     * This Method let us reinitialize our grammar, to create a new one.
     */
    public void reinitialize() {
        productions.clear();
    }

    /**
     * This method let us know if all the non terminals in the right side have a
     * production for themselves
     *
     * @return
     */
    public boolean isNonTerminalWithoutProduction() {
        List<String> namesLeft = new ArrayList<>();
        List<String> namesRight = new ArrayList<>();
        for (int i = 0; i < getProductions().size(); i++) {
            namesLeft.add(getProductions().get(i).getLeftSide().getID());
        }
        for (int i = 0; i < getProductions().size(); i++) {
            for (int j = 0; j < getProductions().get(i).nonTerminalsInRight().size(); j++) {
                namesRight.add(getProductions().get(i).nonTerminalsInRight().get(j).getID());
            }
        }
        if (namesLeft.size() < namesRight.size()) {
            return false;
        } else {
            for (int i = 0; i < namesRight.size(); i++) {
                int flag = 0; // 0 means it didnt find the terminal in left
                for (int j = 0; j < namesLeft.size(); j++) {
                    if (namesRight.get(i).equalsIgnoreCase(namesLeft.get(j))) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * This method let us get the productions of the grammar
     *
     * @return List with all productions
     */
    public List<Production> getProductions() {
        return this.productions;
    }

    /**
     * This method let us get all non terminals of the left side
     *
     * @return List with non terminals of the left side
     */
    public List<NonTerminal> getLeftSiders() {
        return this.leftSiders;
    }

    /**
     * This method let us set the first non productions with non terminals alive
     */
    public void setFirstAlive() {
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).isRightSideAllTerminals()) {
                if (nonTerminalsAlives.isEmpty()) {
                    addNonTerminalAlive(productions.get(i).getLeftSide());
                } else {
                    if (!isInsideAlives(productions.get(i).getLeftSide())) {
                        addNonTerminalAlive(productions.get(i).getLeftSide());
                    }
                }
            }
        }
    }

    /**
     * This method let us know if the non terminal is inside the list of alive
     *
     * @param nonTerminal The non terminal to be checked
     * @return True if the non terminal is inside, otherwise return False
     */
    public boolean isInsideAlives(NonTerminal nonTerminal) {
        for (int i = 0; i < nonTerminalsAlives.size(); i++) {
            if (nonTerminalsAlives.get(i).getID().equalsIgnoreCase(nonTerminal.getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method let us add a non terminal into a list where we can find non
     * terminals alives
     *
     * @param nonTerminal The non terminal alive
     */
    public void addNonTerminalAlive(NonTerminal nonTerminal) {
        nonTerminalsAlives.add(nonTerminal);
    }

    /**
     * This method will let us know when all the non terminals of the right side
     * of a production are alive
     *
     * @param production The production to be evaluated
     * @return True if all non terminals are alive, False if not
     */
    public boolean isProductionAllAlive(Production production) {
        if (!production.nonTerminalsInRight().isEmpty()) {
            for (int i = 0; i < production.nonTerminalsInRight().size(); i++) {
                if (!isInsideAlives(production.nonTerminalsInRight().get(i))) {
                    return false;
                }
            }
        }
        if (!isInsideAlives(production.getLeftSide())) {
            addNonTerminalAlive(production.getLeftSide());
        }
        return true;

    }

    /**
     * This method let us get the whole dead non terminals
     */
    public void checkDeadNonTerminals() {
        for (int i = 0; i < leftSiders.size(); i++) {
            for (int j = 0; j < productions.size(); j++) {
                if (isProductionAllAlive(productions.get(j))) {
                    if (!isInsideAlives(productions.get(j).getLeftSide())) {
                        addNonTerminalAlive(productions.get(j).getLeftSide());
                    }
                }
            }
        }
    }

    /**
     * This method let us know if the grammar is S
     *
     * @return True if the grammar is S, False if not.
     */
    public boolean isS() {
        for (int i = 0; i < getProductions().size(); i++) {
            // S grammar, the first item of the right must be a terminal.
            if (getProductions().get(i).firstItemIsLambda() || !getProductions().get(i).firstItemIsTerminal()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method let us know if the grammar is Q or not
     *
     * @return True if the grammar is Q, False if not
     */
    public boolean isQ() {
        boolean lambda = false;
        //Q grammar, the first item of the right must be a terminal or lambda
        for (int i = 0; i < getProductions().size(); i++) {
            if (getProductions().get(i).firstItemIsLambda()) {
                lambda = true;
            }
            if (!getProductions().get(i).firstItemIsTerminal()) {
                return false;
            }
        }
        // The Q grammar must have al least one production with lambda
        return lambda == true;
    }

    /**
     * This method let us know if the grammar is LL1 or not
     *
     * @return True if the grammar is LL1, False if not
     */
    public boolean isLL1() {
        boolean lambda = false;
        boolean nonTerminal = false;
        boolean terminal = false;
        for (int i = 0; i < getProductions().size(); i++) {
            if (getProductions().get(i).firstItemIsLambda()) {
                lambda = true;
            }
            if (getProductions().get(i).firstItemIsTerminal()) {
                terminal = true;
            } else {
                nonTerminal = true;
            }
        }
        // LL1 gramar must have at least one lambda production,one non terminal production and one terminal production 
        return lambda == true && nonTerminal == true && terminal == true;
    }

    /**
     * This method will return the terminals in T-Alpha productions
     *
     * @return The list with terminals
     */
    public List<Terminal> terminalsInAlpha() {
        List<Terminal> terminalsInAlpha = new ArrayList<>();
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).isTAlpha()) {
                for (int j = 0; j < productions.get(i).getTerminalsInRightAlpha().size(); j++) {
                    if (terminalsInAlpha.isEmpty()) {
                        terminalsInAlpha.add(productions.get(i).getTerminalsInRightAlpha().get(j));
                    } else {
                        for (int k = 0; k < terminalsInAlpha.size(); k++) {
                            if (!terminalsInAlpha.get(k).toString().equalsIgnoreCase(productions.get(i).getTerminalsInRightAlpha().get(j).toString())) {
                                terminalsInAlpha.add(productions.get(i).getTerminalsInRightAlpha().get(j));
                            }
                        }
                    }

                }
            }
        }
        return terminalsInAlpha;
    }

    public void nn(NonTerminal nonTerminal, Character inputSymbol) {
        List<Object> alphaReverse = new ArrayList<>();
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).getLeftSide().getID().equals(nonTerminal.getID())) {
                if (productions.get(i).firstItemIsTerminalNotLambda()) {
                    if (productions.get(i).firstItemTerminal().getSymbol() == inputSymbol) {
                        for (int j = productions.get(i).rightSide.size()-1; j >= 1; j--) {
                            alphaReverse.add(productions.get(i).rightSide.get(j));
                        }
                    }
                }
            }
        }
    }

}

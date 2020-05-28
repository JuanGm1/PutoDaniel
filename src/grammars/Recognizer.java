/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Juan Pablo
 */
public final class Recognizer {

    private final Grammar grammar;
    private final List<NonTerminal> nonTerminalsVoidables = new ArrayList<>();
    private final List<Integer> ProductionVoidables = new ArrayList<>();
    private final HashMap<String, List<Terminal>> firstsNonTerminal = new HashMap<>();
    private final HashMap<Integer, List<Terminal>> firstsProduction = new HashMap<>();
    private final HashMap<String, List<Terminal>> aftersNonTerminal = new HashMap<>();
    private final HashMap<Integer, List<Terminal>> selectionProduction = new HashMap<>();

    public Recognizer(Grammar grammar) {
        this.grammar = grammar;
        foundNonTerminalVoidables();
        foundProductionVoidables();
        foundFirstsToNonTerminal();
        foundFirstsToProduction();
        foundAftersNonTerminal();
        foundSetSelect();
    }
    
    
    /* Metodo que recorre todos los no terminales preguntando si son anulables */
    private void foundNonTerminalVoidables() {
        for (NonTerminal nonTerminal : grammar.getLeftSiders()) {
            if (this.isNonTerminalVoidable(nonTerminal)) {
                boolean contains = false;
                for (NonTerminal nonTerminal2 : getNonTerminalsVoidables()) {
                    if (nonTerminal2.getID().equals(nonTerminal.getID())) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    getNonTerminalsVoidables().add(nonTerminal);
                }
            }
        }
    }

    /* Metodo que se encarga de determinar si un no terminal es anulable */
    private boolean isNonTerminalVoidable(NonTerminal nonTerminal) {
        String idNonTerminal = nonTerminal.getID();
        boolean isVoidable = false;
        /* Se recorren todas las producciones preguntando si el lado izquierdo es igual al no terminal que se recibio como parametro */
        for (Production production : grammar.getProductions()) {
            if (production.getLeftSide().getID().equals(idNonTerminal)) {
                if (production.firstItemIsLambda()) {
                    isVoidable = true;
                } else {
                    /* Si la produccion no es lambda, se pregunta si todo el lado derecho de la produccion son No terminales */
                    if (production.terminalsInRight.isEmpty()) {
                        boolean auxiliar = false;
                        /* Se recorren todos los terminales del lado derecho preguntando 
                        si alguno es igual al no terminal que se recibio como parametro para evitar ciclos infinitos*/
                        for (NonTerminal nonTerminalP : production.nonTerminalsInRight) {
                            if (nonTerminalP.getID().equals(nonTerminal.getID())) {
                                auxiliar = false;
                                break;
                            }
                            /* En caso de que los terminales no sean iguales al no terminal que se recibio como parametro
                            se procede a llamar recursivamente al metodo para determinar si son anulables */
                            auxiliar = this.isNonTerminalVoidable(nonTerminalP);
                        }
                        isVoidable = auxiliar;
                    }
                }
            }
        }
        return isVoidable;
    }

    /* Metodo que recorre todas las producciones determinando si son anulables*/
    private void foundProductionVoidables() {
        int j = 0;
        for (Production production : grammar.getProductions()) {
            if (production.firstItemIsLambda()) {
                getProductionVoidables().add(j);
            } else {
                if (production.isRightSideAllNotTerminals()) {
                    int i = 0;
                    boolean auxiliar = false;
                    while (i < production.nonTerminalsInRight.size()) {
                        if (!this.isNonTerminalVoidable(production.nonTerminalsInRight.get(i))) {
                            auxiliar = true;
                            break;

                        } else {
                            i++;
                        }
                    }
                    if (!auxiliar) {
                        getProductionVoidables().add(j);
                    }
                }
            }
            j++;
        }
    }

    /* Metodo que recorre todos los no terminales para obtener el conjunto de primeros de cada uno */
    private void foundFirstsToNonTerminal() {
        for (NonTerminal nonTerminal : grammar.getLeftSiders()) {
            List<Terminal> firsts = this.FirstsToNonTerminal(nonTerminal);
            /* Ciclo encargado de eliminar terminales duplicados */
            for (int j = 0; j < firsts.size(); j++) {
                int k = j + 1;
                while (k < firsts.size()) {
                    if (firsts.get(j).getSymbol() == firsts.get(k).getSymbol() && j!=k) {
                        firsts.remove(k);
                        continue;
                    }
                    k++;
                }
            }
            /* Despues de eliminar los duplicados se agrega el conjunto */
            getFirstsNonTerminal().put(nonTerminal.getID(), firsts);
        }
    }

    /* Metodo que busca el conjunto de primeros de un no terminal */
    private List<Terminal> FirstsToNonTerminal(NonTerminal nonTerminal) {
        String idNonTerminal = nonTerminal.getID();
        List<Terminal> firsts = new ArrayList<>();
        for (Production production : grammar.getProductions()) {
            if (production.getLeftSide().getID().equals(idNonTerminal)) {
                if (!production.firstItemIsLambda()) {
                    if (production.firstItemIsTerminal()) {
                        firsts.add(production.firstItemTerminal());
                    } else {
                        if (production.firstItemNonTerminal().getID().equals(nonTerminal.getID())) {
                            continue;
                        }
                        firsts.addAll(this.FirstsToNonTerminal(production.firstItemNonTerminal()));
                        boolean contains = false;
                        for (NonTerminal nonTerminalP : this.getNonTerminalsVoidables()) {
                            if (production.firstItemNonTerminal().getID().equals(nonTerminalP.getID())) {
                                contains = true;
                                break;
                            }
                        }
                        if (contains) {
                            Production productionAuxiliar = new Production();
                            for (int i = 0; i < production.rightSide.size(); i++) {
                                productionAuxiliar.rightSide.add(production.rightSide.get(i));
                            }
                            productionAuxiliar.rightSide.remove(0);
                            if (productionAuxiliar.rightSide.size() > 0) {
                                if (productionAuxiliar.firstItemIsTerminal()) {
                                    firsts.add(productionAuxiliar.firstItemTerminal());
                                } else {
                                    if (productionAuxiliar.firstItemNonTerminal().getID().equals(nonTerminal.getID())) {
                                        continue;
                                    }
                                    firsts.addAll(this.FirstsToNonTerminal(productionAuxiliar.firstItemNonTerminal()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return firsts;
    }

    /* Metodo que recorre todas las producciones para obtener el conjunto de primeros de cada una */
    private void foundFirstsToProduction() {
        int i = 0;
        for (Production production : grammar.getProductions()) {
            List<Terminal> firsts = this.FirstsToProduction(production);
            /* Ciclo encargado de eliminar terminales duplicados */
            for (int j = 0; j < firsts.size(); j++) {
                int k = j + 1;
                while (k < firsts.size()) {
                    if (firsts.get(j).getSymbol() == firsts.get(k).getSymbol() && j!=k) {
                        firsts.remove(k);
                        continue;
                    }
                    k++;
                }
            }
            /* Despues de eliminar los duplicados se agrega el conjunto */
            this.getFirstsProduction().put(i, firsts);
            i++;
        }
    }

    /* Metodo que busca el conjunto de primeros de una produccion */
    private List<Terminal> FirstsToProduction(Production production) {
        List<Terminal> firsts = new ArrayList<>();
        if (!production.firstItemIsLambda()) {
            if (production.firstItemIsTerminal()) {
                firsts.add(production.firstItemTerminal());
            } else {
                firsts.addAll(this.getFirstsNonTerminal().get(production.firstItemNonTerminal().getID()));
                boolean contains = false;
                for (NonTerminal nonTerminalP : this.getNonTerminalsVoidables()) {
                    if (production.firstItemNonTerminal().getID().equals(nonTerminalP.getID())) {
                        contains = true;
                        break;
                    }
                }
                if (contains) {
                    Production productionAuxiliar = new Production();
                    for (int j = 0; j < production.rightSide.size(); j++) {
                        productionAuxiliar.rightSide.add(production.rightSide.get(j));
                    }
                    productionAuxiliar.rightSide.remove(0);
                    if (productionAuxiliar.rightSide.size() > 0) {
                        if (productionAuxiliar.firstItemIsTerminal()) {
                            firsts.add(productionAuxiliar.firstItemTerminal());
                        } else {
                            firsts.addAll(this.FirstsToNonTerminal(productionAuxiliar.firstItemNonTerminal()));
                        }
                    }
                }
            }
        }
        return firsts;
    }
    
    /* Metodo que recorre todos los no terminales para obtener el conjunto de siguientes de cada uno */
    private void foundAftersNonTerminal() {
        for (NonTerminal nonTerminal : grammar.getLeftSiders()) {
            List<Terminal> afters = this.aftersToNonTerminal(nonTerminal);
            /* Ciclo encargado de eliminar terminales duplicados */
            for (int i = 0; i < afters.size(); i++) {
                int j = i+1;
                while (j < afters.size()) {
                    if (afters.get(i).getSymbol() == afters.get(j).getSymbol() && i!=j) {
                        afters.remove(j);
                        continue;
                    }
                    j++;
                }
            }
            /* Despues de eliminar los duplicados se agrega el conjunto */
            this.getAftersNonTerminal().put(nonTerminal.getID(), afters);
        }
    }

    /* Metodo que busca el conjunto de siguientes de un no terminal */
    private List<Terminal> aftersToNonTerminal(NonTerminal nonTerminal) {
        String idNonTerminal = nonTerminal.getID();
        List<Terminal> afters = new ArrayList<>();
        if (grammar.getProductions().get(0).getLeftSide().getID().equals(idNonTerminal)) {
            Terminal fin = new Terminal('┐');
            afters.add(fin);
        }
        for (Production production : grammar.getProductions()) {
            boolean contain = false;
            for (NonTerminal nonTerminalP : production.nonTerminalsInRight) {
                if (nonTerminalP.getID().equals(idNonTerminal)) {
                    contain = true;
                }
            }
            if (contain) {
                Production productionAuxiliar = new Production();
                for (int i = 0; i < production.rightSide.size(); i++) {
                    productionAuxiliar.rightSide.add(production.rightSide.get(i));
                }
                boolean ready = false;
                while (productionAuxiliar.rightSide.size() > 0) {
                    if (!ready) {
                        if (!productionAuxiliar.firstItemIsTerminal()) {
                            if (productionAuxiliar.firstItemNonTerminal().getID().equals(idNonTerminal)) {
                                ready = true;
                                productionAuxiliar.rightSide.remove(0);
                                break;
                            }
                        }
                        productionAuxiliar.rightSide.remove(0);
                    }
                }
                if (productionAuxiliar.rightSide.isEmpty()) {
                    if (production.getLeftSide().getID().equals(nonTerminal.getID())) {
                        continue;
                    }
                    afters.addAll(this.aftersToNonTerminal(production.getLeftSide()));
                }
                while (!productionAuxiliar.rightSide.isEmpty()) {
                    if (productionAuxiliar.firstItemIsTerminal()) {
                        afters.add(productionAuxiliar.firstItemTerminal());
                        break;
                    } else {
                        afters.addAll(this.getFirstsNonTerminal().get(productionAuxiliar.firstItemNonTerminal().getID()));
                        boolean contains = false;
                        for (NonTerminal noTerminalV : this.getNonTerminalsVoidables()) {
                            if (noTerminalV.getID().equals(productionAuxiliar.firstItemNonTerminal().getID())) {
                                contains = true;
                            }
                        }
                        if (contains) {
                            if (productionAuxiliar.rightSide.size() == 1) {
                                if (production.getLeftSide().getID().equals(nonTerminal.getID())) {
                                    continue;
                                }
                                afters.addAll(this.aftersToNonTerminal(production.getLeftSide()));
                            }
                            productionAuxiliar.rightSide.remove(0);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return afters;
    }

    /* Metodo que determina el conjunto de seleccion de cada produccion*/
    private void foundSetSelect() {
        int i = 0;
        for (Production production : grammar.getProductions()) {
            List<Terminal> select = new ArrayList<>();
            select.addAll(this.getFirstsProduction().get(i));
            if (this.getProductionVoidables().contains(i)) {
                select.addAll(this.getAftersNonTerminal().get(production.getLeftSide().getID()));
            }
            getSelectionProduction().put(i, select);
            i++;
        }
    }

    /**
     * @return the nonTerminalsVoidables
     */
    public List<NonTerminal> getNonTerminalsVoidables() {
        return nonTerminalsVoidables;
    }

    /**
     * @return the ProductionVoidables
     */
    public List<Integer> getProductionVoidables() {
        return ProductionVoidables;
    }

    /**
     * @return the firstsNonTerminal
     */
    public HashMap<String, List<Terminal>> getFirstsNonTerminal() {
        return firstsNonTerminal;
    }

    /**
     * @return the firstsProduction
     */
    public HashMap<Integer, List<Terminal>> getFirstsProduction() {
        return firstsProduction;
    }

    /**
     * @return the aftersNonTerminal
     */
    public HashMap<String, List<Terminal>> getAftersNonTerminal() {
        return aftersNonTerminal;
    }

    /**
     * @return the selectionProduction
     */
    public HashMap<Integer, List<Terminal>> getSelectionProduction() {
        return selectionProduction;
    }

}

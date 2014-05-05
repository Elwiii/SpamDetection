/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Nikolai
 */
public class BaseApprentissage implements Serializable{
    
    private final List<Couple> base;
    
    private int nombre_spam;

    private int nombre_ham;
    
    private final Dictionnaire dictionnaire;
    
    public BaseApprentissage(List<Couple> base, int nombre_spam, int nombre_ham, Dictionnaire dictionnaire){
        this.base = base;
        this.nombre_ham = nombre_ham;
        this.nombre_spam = nombre_spam;
        this.dictionnaire = dictionnaire;
    }
    
    public void add(Couple couple){
        base.add(couple);
    }
    
    //@todo mettre ici les ajouts à la base
    
    
    
    public Dictionnaire getDictionnaire() {
        return dictionnaire;
    }

    
    
    /**
     * @return the base
     */
    public List<Couple> getBase() {
        return base;
    }

    public int getNombre_spam() {
        return nombre_spam;
    }

    public int getNombre_ham() {
        return nombre_ham;
    }
    
}

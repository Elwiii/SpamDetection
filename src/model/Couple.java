/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Nikolai
 */
public class Couple implements Serializable{

    public int[] x;
    public TypeMail y;

    public Couple(int[] x, TypeMail y) {
        this.x = x;
        this.y = y;
    }
    
    public String toString(Dictionnaire dictionnaire) {
        String s = "[";
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 1) {
                s +=" " + dictionnaire.getContenu().get(i);
            }
        }
        return s+" ] : "+y;
    }
}

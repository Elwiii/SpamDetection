/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author Nikolai
 */
public enum TypeMail {
    SPAM,HAM;

    public TypeMail parseTypeMail(String s) throws Exception{
        switch(s){
            case "SPAM":
                return SPAM;
            case "HAM":
                return HAM;
            default:
                throw new Exception(s+" : type de mail incorrect");
        }
    }
}

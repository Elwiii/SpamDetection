/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton(s) dictionnaire
 * @author Nikolai
 */
public class Dictionnaire implements Serializable{

    private final List<String> contenu; // @todo utiliser une structure de donnée plus rapide dans la mesure ou les mots sont triés par ordre alphabétique
    private final File file;

    private static HashMap<String, Dictionnaire> instances;

    /**
     * Renvoi un nouveau dictionnaire. Si il existe deja un dictionnaire qui a lu ce fichier, renvoit ce dicitonnaire
     * @param file
     * @return 
     */
    public static Dictionnaire getInstance(String file) {
        if (instances == null) {
            instances = new HashMap();
        }
        Dictionnaire d = instances.get(file);
        if (d == null) {
            d = new Dictionnaire(file);
            d.charger_dictionnaire();
            instances.put(file, d);
        }
        return d;
    }

    
    public String show(int[] exemple) {
        String s = "[";
        for (int i = 0; i < exemple.length; i++) {
            if (exemple[i] == 1) {
                s += " " +contenu.get(i);
            }
        }
        return s + " ]";
    }
    
    private Dictionnaire(File file) {
        this.file = file;
        this.contenu = new ArrayList<>();
    }

    private Dictionnaire(String file) {
        this.file = new File(file);
        this.contenu = new ArrayList<>();
    }
    
    public int size(){
        return contenu.size();
    }

    /**
     * Charge le fichier dans ce dictionnaire On suppose que le fichier regroupe
     * un ensemble de mot séparé par des sauts de lignes
     */
    private void charger_dictionnaire() {
        InputStream ips = null;
        try {
            ips = new FileInputStream(file);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne ;
            while ((ligne = br.readLine()) != null) {
                if (!isUselessForSpamFilter(ligne)) {
                    contenu.add(ligne.toUpperCase());
                }
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Dictionnaire.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Dictionnaire.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ips.close();
            } catch (IOException ex) {
                Logger.getLogger(Dictionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * On fixe l'inutilité d'un mot par son nombre de lettre. inutile = mot de 3
     * lettres ou moins
     *
     * @param mot
     * @return vrai si le mot est considéré comme inutile pour le filtre de spam
     */
    private boolean isUselessForSpamFilter(String mot) {
        return mot.length() <= 3;
    }

    @Override
    public String toString() {
        return "Dictionnaire{" + ", file=" + file+", contenu=" + contenu +'}';
    }

    /**
     * @return the contenu
     */
    public List<String> getContenu() {
        return contenu;
    }

}

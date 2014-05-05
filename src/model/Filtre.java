/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import batch.Batch;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.TypeMail.HAM;
import static model.TypeMail.SPAM;

/**
 *
 * @author Nikolai
 */
public class Filtre {

    private final ClassifieurSPAM classifieur;

    /**
     * Manière naive de lire un fichier (ignore les mots type "mot," ou "mot!"
     * sauf si ces derniers se trouve dans le dictionnaire).
     */
    public static final int SIMPLE = 0;

    /**
     * Manière sophistiquée de lire les mots dans un fichier (ignore les
     * ponctuations et autres joyeusetés afin de retrouver les mots du
     * dictionnaire).
     */
    public static final int ADVANCED = 1;

    public static final int SLOW_ADD = 0;

    public static final int FAST_ADD = 1;

    private final Dictionnaire dictionnaire;

    private int nombre_hams;

    private int nombre_spams;

    private final BaseApprentissage base;

    private String path_baseapp_ham;

    private String path_baseapp_spam;

    private List<Couple> couples;

    private final int EPSILON_RAFFINEMENT = 1;

    private int extractWord = ADVANCED;

    public static void main(String[] args) {
        try {
            Filtre f = new Filtre("../../dictionnaire1000en.txt", "test/baseapp/spam", "test/baseapp/ham", 1, 1);
            System.out.println("" + f.computeErreurHamBaseTest("test/basetest/ham", 1));
        } catch (Exception ex) {
            Logger.getLogger(Filtre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Filtre(String path_dictionnaire, String path_baseapp_spam, String path_baseapp_ham, int nombre_spams, int nombre_hams) throws Exception {
        dictionnaire = Dictionnaire.getInstance(path_dictionnaire);
        this.nombre_spams = nombre_spams;
        couples = new ArrayList<>();
        this.nombre_hams = nombre_hams;
        this.path_baseapp_ham = path_baseapp_ham;
        this.path_baseapp_spam = path_baseapp_spam;
        remplirCouplesWithBaseapp(extractWord);
        base = new BaseApprentissage(couples, nombre_spams, nombre_hams, dictionnaire);
        classifieur = new ClassifieurSPAM(base);
        classifieur.apprentissage(EPSILON_RAFFINEMENT);
    }

    public Filtre(String path_dictionnaire, String path_baseapp_spam, String path_baseapp_ham, int nombre_spams, int nombre_hams, int typeExtractWord) throws Exception {
        dictionnaire = Dictionnaire.getInstance(path_dictionnaire);
        this.nombre_spams = nombre_spams;
        couples = new ArrayList<>();
        this.nombre_hams = nombre_hams;
        this.path_baseapp_ham = path_baseapp_ham;
        this.path_baseapp_spam = path_baseapp_spam;
        this.extractWord = typeExtractWord;
        remplirCouplesWithBaseapp(extractWord);
        base = new BaseApprentissage(couples, nombre_spams, nombre_hams, dictionnaire);
        classifieur = new ClassifieurSPAM(base);
        classifieur.apprentissage(EPSILON_RAFFINEMENT);

    }

    public Filtre(String path_classifieur) throws Exception {
        classifieur = ClassifieurSPAM.load(path_classifieur);
        base = classifieur.getBaseApprentissage();
        dictionnaire = base.getDictionnaire();
    }

    /**
     * prédit si un mail est un spam ou un ham
     *
     * @param pathMail
     * @return
     */
    public TypeMail prediction(String pathMail) throws Exception {
        return classifieur.prediction(lire_message(pathMail, SIMPLE));
    }

    /**
     * prédit si un mail est un spam ou un ham
     *
     * @param pathMail
     * @return
     */
    public TypeMail prediction(File pathMail) throws Exception {
        return classifieur.prediction(lire_message(pathMail, extractWord));
    }

    public void saveClassifieur(String path_save) {
        classifieur.save(path_save);
    }

    public void updateClassifieur(String path_save) {
        classifieur.update(path_save);
    }

    /**
     * Apprentissage en ligne du classifieur avec un fichier.
     *
     * @param path_file chemin du mail
     * @param type type du mail SPAM ou HAM
     * @param vitesse algorithme utilisé par le classifieur pour se mettre à
     * jour (dans le doute,pour minimiser les bugs et arrondis, utiliser
     * SLOW_ADD)
     */
    public void addFileToClassifieur(String path_file, TypeMail type, int vitesse) throws Exception {
        int[] x = lire_message(path_file, SIMPLE);
        Couple c = new Couple(x, type);
        switch (vitesse) {
            case SLOW_ADD:
                classifieur.apprentissageEnLigneSlow(c, EPSILON_RAFFINEMENT);
                break;
            case FAST_ADD:
                classifieur.apprentissageEnLigneFast(c, EPSILON_RAFFINEMENT);
                break;
        }
        if (Batch.OUT) {
            System.out.println("Rajout du mail " + path_file + " de type " + type + " à la base d'apprentissage");
        }
    }

    /**
     * test le filtre sur un ensemble de ham
     *
     * @param path_basetest_ham
     * @param nombre_ham
     * @return
     */
    public double computeErreurHamBaseTest(String path_basetest_ham, int nombre_ham) throws Exception {
        if (Batch.OUT) {
            System.out.println("Phase de test pour les hams de " + path_basetest_ham);
        }
        int erreurs = 0;
        File dossier_ham = new File(path_basetest_ham);
        File[] hams = dossier_ham.listFiles();
        for (int i = 0; i < nombre_ham /* hams.length */; i++) {
            if (Batch.OUT) {
                System.out.println("\n" + hams[i]);
            }
            if (prediction(hams[i]) != HAM) {
                erreurs++;
            }
        }
        if (Batch.OUT) {
            System.out.println("\n" + erreurs + " erreurs sur " + nombre_ham + " hams");
        }
        return erreurs / (double) nombre_ham;
    }

    /**
     * test le filtre sur un ensemble de spam
     *
     * @param path_basetest_spam
     * @param nombre_spam
     * @return
     */
    public double computeErreurSpamBaseTest(String path_basetest_spam, int nombre_spam) throws Exception {
        if (Batch.OUT) {
            System.out.println("Phase de test pour les spams de " + path_basetest_spam);
        }
        int erreurs = 0;
        File dossier_spam = new File(path_basetest_spam);
        File[] spams = dossier_spam.listFiles();
        for (int i = 0; i < nombre_spam /* hams.length */; i++) {
            if (Batch.OUT) {
                System.out.println("\n" + spams[i]);
            }
            if (prediction(spams[i]) != SPAM) {
                erreurs++;
            }
        }
        if (Batch.OUT) {
            System.out.println("\n" + erreurs + " erreurs sur " + nombre_spam + " spams");
        }
        return erreurs / (double) nombre_spam;
    }

    /**
     * Remplit une liste d'exemple et d'etiquette, "couples", grâce à la base
     * d'apprentissage.
     */
    private void remplirCouplesWithBaseapp(int typeExtractWord) throws Exception {

        File dossier_ham = new File(path_baseapp_ham);
        File[] hams = dossier_ham.listFiles();
        if (hams.length < nombre_hams) {
            throw new Exception("Il n'y a pas assez de ham sous le dossier " + path_baseapp_ham);
        }
        for (int i = 0; i < nombre_hams; i++) {
            couples.add(new Couple(lire_message(hams[i], typeExtractWord), HAM));
        }

        File dossier_spam = new File(path_baseapp_spam);
        File[] spams = dossier_spam.listFiles();
        if (spams.length < nombre_spams) {
            throw new Exception("Il n'y a pas assez de spams sous le dossier " + path_baseapp_spam);
        }
        for (int i = 0; i < nombre_spams; i++) {
            couples.add(new Couple(lire_message(spams[i], typeExtractWord), SPAM));
        }

    }

    /**
     * lit un fichier et retourne un tableau de présence de mot, mot présent
     * dans le dictionnaire
     *
     * @param fichier
     * @param typeExtractWord
     * @return
     */
    private int[] lire_message(String fichier, int typeExtractWord) throws Exception {
        File file = new File(fichier);
        return lire_message(file, typeExtractWord);
    }

    //@todo ya un caractère qui fait qu'on prend meme les bons mots (on fait pas de traitement mais ça use quand meme de la capacité de calcul imo
    private final String REGEX_TO_SPACE = "\\(|\\)|\"|,|!|:|;|$|£|%|\\.|@|€|$|%|£|=|/|&|~|>|<|#|\\*|\\?|\\[|\\]|\\{|\\}|\\+|\\-";

    /**
     * cette fonction doit pouvoir lire un message (dans un fichier texte) et le
     * traduire en une représentation sous forme de vecteur binaire x à partir
     * d’un dictionnaire
     *
     * @param fichier
     * @param typeExtractWord
     * @todo version ADVANCED
     * @return
     */
    private int[] lire_message(File file, int typeExtractWord) throws Exception {
        int[] representation = new int[dictionnaire.size()];
        InputStream ips = null;
        try {
            ips = new FileInputStream(file);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            while ((line = br.readLine()) != null) {
                String temp = line;
                if (typeExtractWord == ADVANCED) {
                    line = line.replaceAll(REGEX_TO_SPACE, " ");
//                    if (!temp.equals(line)) {
//                        System.out.println("avant : " + temp + " apres : " + line);
//                    }
                }
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                while (tokenizer.hasMoreTokens()) {
                    String word = tokenizer.nextToken();

                    word = word.toUpperCase();
                    int index = dictionnaire.getContenu().indexOf(word);
                    if (index != -1) {
                        representation[index] = 1;
                    }
                }
            }
            br.close();
            ips.close();
        } catch (FileNotFoundException ex) {
            throw new Exception("fichier inexistant : " + file.getAbsolutePath());
        } catch (IOException ex) {
            throw ex;
        }
//        System.out.println("" + dictionnaire.show(representation));
        return representation;
    }

    /**
     * @return the dictionnaire
     */
    public Dictionnaire getDictionnaire() {
        return dictionnaire;
    }

    /**
     * @return the couples
     */
    public List<Couple> getCouples() {
        return couples;
    }

}

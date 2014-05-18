/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package batch;

import model.Filtre;
import model.TypeMail;

/**
 *
 * @author Nikolai
 */
public class Batch {

    public static boolean OUT = false;

    private static final String USAGE = ""
            + "USAGE : java Batch\n\n"
            + "-d   | -dictionnaire        path du dictionnaire\n"
            + "-c   | -classifieur         path du classifieur à charger\n"
            + "-as  | -apprentissagespam   path du dossier contenant la base d'apprentissage des spams suivis du nombre de fichier à prendre en compte\n"
            + "-ah  | -apprentissageham    path du dossier contenant la base d'apprentissage des hams suivis du nombre de fichier à prendre en compte\n"
            + "-ts  | -testspam            path du dossier contenant la base de test des spams suivis du nombre de fichier à prendre en compte\n"
            + "-th  | -testham             path du dossier contenant la base de test des hams suivis du nombre de fichier à prendre en compte\n"
            + "-f   | -file                path d'un fichier à faire apprendre à une base déjà existante suivi de SPAM ou HAM. Cette option est obligatoirement combinée à -c. SLOW_ADD par défaut, FAST_ADD buggé, spécifiable avant le path.\n"
            + "-pr  | -prediction          path du fichier à prédire\n"
            + "-s   | -save                path du classifieur qu'on veut sauvergarder\n"
            + "-v   | -verbose             autorise certains system.out\n"
            + "-pa  | -parse               manière de parser les emails : SIMPLE ou ADVANCED (advanced ignore les metas caractères d'une phrase)\n"
            + "                            ADVANCED par defaut si l'option -pa n'est pas precisé"
            + "\n\n"
            + "Exemple : \n\n"
            + "prediction via une base d'apprentissage\n\n"
            + "\tjava Batch -d dico.txt -as baseapp/spam 200 -ah baseapp/ham 200 -pr email.txt\n\n"
            + "test via une base d'apprentissage\n\n"
            + "\tjava Batch -d dico.txt -as baseapp/spam 200 -ah baseapp/ham 200 -ts basetest/spam 150 -th basetest/ham 100\n\n"
            + "test avec un classifieur\n\n"
            + "\tjava Batch -c monclassifieur -ts basetest/spam 150 -th basetest/ham 100\n\n"
            + "prediction avec un classifieur\n\n"
            + "\tjava Batch -c monclassifieur -pr email.txt\n\n"
            + "apprentissage en ligne\n\n"
            + "\tjava Batch -c monclassifieur -f nouveaumailaapprendre.txt SPAM\n\n"
            + "\tjava Batch -c monclassifieur -f FAST nouveaumailaapprendre.txt SPAM\n\n"
            + "creation d'un classifieur\n\n"
            + "\tjava Batch -d dico.txt -s monnouveauclassifieur -as baseapp/spam 150 -ah baseapp/ham 100\n\n";

    public static void main(String[] args) {
        try {

            String path_classifieur = null;
            String path_as = null;
            String path_ah = null;
            String path_ts = null;
            String path_th = null;
            String path_f = null;
            String path_p = null;
            String path_s = null;
            String path_d = null;
            int nb_as = 0;
            int nb_ah = 0;
            int nb_ts = 0;
            int nb_th = 0;
            int typeExtractWord = Filtre.ADVANCED;
            int typeSlowOrFastAdd = Filtre.SLOW_ADD;
            TypeMail typeAdd = null;
            for (int i = 0; i < args.length; i++) {
//                System.out.println("arg[" + i + "] : " + args[i]);
                switch (args[i]) {
                    case "-pa":
                    case "-parse":
                        i++;
                        if (args[i].equals("ADVANCED")) {
                            typeExtractWord = Filtre.ADVANCED;
                        } else if (args[i].equals("SIMPLE")) {
                            typeExtractWord = Filtre.SIMPLE;
                        } else {
                            throw new Exception("Type de parsing incorrect");
                        }
                        break;
                    case "-d":
                    case "-dictionnaire":
                        i++;
                        path_d = args[i];
                        break;
                    case "-c":
                    case "-classifieur":
                        i++;
                        path_classifieur = args[i];
                        break;
                    case "-as":
                    case "-apprentissagespam":
                        i++;
                        path_as = args[i];
                        i++;
                        nb_as = Integer.parseInt(args[i]);
                        break;
                    case "-ah":
                    case "-apprentissageham":
                        i++;
                        path_ah = args[i];
                        i++;
                        nb_ah = Integer.parseInt(args[i]);
                        break;
                    case "-ts":
                    case "-testspam":
                        i++;
                        path_ts = args[i];
                        i++;
                        nb_ts = Integer.parseInt(args[i]);
                        break;
                    case "-th":
                    case "-testham":
                        i++;
                        path_th = args[i];
                        i++;
                        nb_th = Integer.parseInt(args[i]);
                        break;
                    case "-f":
                    case "-file":
                        i++;
                        if (args[i].equals("SLOW")) {
                            typeSlowOrFastAdd = Filtre.SLOW_ADD;
                        } else if (args[i].equals("FAST")) {
                            typeSlowOrFastAdd = Filtre.FAST_ADD;
                        } else {
                            path_f = args[i];
                            i++;
                            typeAdd = TypeMail.valueOf(args[i]);
                            break;
                        }
                        i++;
                        path_f = args[i];
                        i++;
                        typeAdd = TypeMail.valueOf(args[i]);
                        break;
                    case "-pr":
                    case "-prediction":
                        i++;
                        path_p = args[i];
                        break;
                    case "-s":
                    case "-save":
                        i++;
                        path_s = args[i];
                        break;
                    case "-v":
                    case "-verbose":
                        OUT = true;
                        break;
                }
            }

            Filtre f = null;

            if (path_as != null && path_ah != null) {
                if (path_d != null) {
                    f = new Filtre(path_d, path_as, path_ah, nb_as, nb_ah, typeExtractWord);
                } else {
                    throw new Exception("Veuillez préciser l'emplacement du dictionnaire");
                }
            } else if (path_classifieur != null) {
                f = new Filtre(path_classifieur);
            } else {
                throw new Exception("Vous devez spécifier le path d'un classifieur déjà existant ou fournir les chemins pour l'aprentissage (-ah et -as)");
            }

            if (path_f != null) {
                if (path_classifieur != null) {
                    f.addFileToClassifieur(path_f, typeAdd, typeSlowOrFastAdd);
                    f.updateClassifieur(path_classifieur);
                } else {
                    throw new Exception("Vous devez préciser le chemin du classifieur pour apprendre une nouvelle donnée");
                }
            }

            if (path_s != null) {
                f.saveClassifieur(path_s);
            }

            if (path_p != null) {
                System.out.println("" + path_p + " : " + f.prediction(path_p));
            }

            double erreur_test_ham = 0.0;
            double erreur_test_spam = 0.0;

            if (path_th != null) {
                erreur_test_ham = f.computeErreurHamBaseTest(path_th, nb_th);
                System.out.println(erreur_test_ham * 100.0 + "% d'erreur sur " + nb_th + " hams");
            }

            if (path_ts != null) {
                erreur_test_spam = f.computeErreurSpamBaseTest(path_ts, nb_ts);
                System.out.println(erreur_test_spam * 100.0 + "% d'erreur sur " + nb_ts + " spams");
            }

            if (path_th != null && path_ts != null) {
                System.out.println(((erreur_test_ham + erreur_test_spam) / 2.0) * 100.0 + "% d'erreur sur " + (nb_th + nb_ts) + " mails");
            }

        } catch (NumberFormatException e) {
            System.err.println("\nERROR : vous devez préciser un nombre derrière -as -ah -ts -th\n");
            System.err.println("" + USAGE);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.err.println("\nERROR : il manque un argument ou plusieurs arguements à une ou plusieurs options\n");
            System.err.println("" + USAGE);
        } catch (Exception e) {
            System.err.println("\nERROR : " + e + "\n");
//            e.printStackTrace();
            System.err.println("" + USAGE);
        }

    }
}

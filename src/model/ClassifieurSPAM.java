/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import batch.Batch;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.TypeMail.HAM;
import static model.TypeMail.SPAM;

/**
 * Classification de mails. Necessite une base d'apprentissage. Permet de
 * prédire une étiquette en fonction d'un exemple.
 *
 * @author Nikolai
 */
public class ClassifieurSPAM implements Serializable {

    private final int SPAM_INDEX = 0;

    private final int HAM_INDEX = 1;

    private final double[] probaApriori;

    private final double[] bspam;

    private final double[] bham;

    private final BaseApprentissage baseApprentissage;

    private double diviseur_bspam;

    private double diviseur_bham;

    /**
     * Creer un classifieur pour la classification de mail.
     *
     * @param baseApprentissage
     */
    public ClassifieurSPAM(BaseApprentissage baseApprentissage) {
        this.baseApprentissage = baseApprentissage;
        probaApriori = new double[2];
        bspam = new double[baseApprentissage.getDictionnaire().size()];
        bham = new double[baseApprentissage.getDictionnaire().size()];
    }

    /**
     * Sauvergarde le classifieur à l'emplacement précisé
     *
     * @param path_save
     */
    public void save(String path_save) {
        FileOutputStream fichier = null;
        try {
            if (new File(path_save).exists()) {
                System.err.print(path_save + " : Fichier déjà existant");
            }
            fichier = new FileOutputStream(path_save);
            ObjectOutputStream oos = new ObjectOutputStream(fichier);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            fichier.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClassifieurSPAM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassifieurSPAM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sauve le classifieur, ecrase le fichier si ce dernier est existant
     *
     * @param path_save
     */
    public void update(String path_save) {
        File f = new File(path_save);
        if (f.exists()) {
            f.delete();
        }
        save(path_save);
    }

    /**
     * Charge un classifieur à l'emplacement indiqué
     *
     * @param path_classifieur
     * @return
     */
    public static ClassifieurSPAM load(String path_classifieur) throws Exception {
        File fich = new File(path_classifieur);
        if (fich.exists()) {
            FileInputStream fichier = null;
            try {
                fichier = new FileInputStream(path_classifieur);
                ObjectInputStream ois = new ObjectInputStream(fichier);
                ClassifieurSPAM c = (ClassifieurSPAM) ois.readObject();
                ois.close();
                fichier.close();
                return c;
            } catch (FileNotFoundException ex) {
                throw new Exception("Fichier pour classifieur inexistant");
            } catch (IOException ex) {
                Logger.getLogger(ClassifieurSPAM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                throw new Exception("Ce fichier ne correspond pas à un classifieur");
            }
        } else {
            System.err.println(path_classifieur + " : Classifieur inexistant");
        }
        return null;
    }

    /**
     * Rajoute un couple etiquette exemple à la base d'apprentissage puis refais
     * l'apprentissage complet du classifieur
     *
     * @todo test margaux
     * @param c
     * @param epsilon_raffinement
     */
    public void apprentissageEnLigneSlow(Couple c, double epsilon_raffinement) {
        baseApprentissage.add(c);
        apprentissage(epsilon_raffinement);
    }

    /**
     * Apprentissage en ligne d'un exemple rapidement Contrairement à
     * l'apprentissage en ligne lent, celui ci modifie juste les bj déjà
     * existant, au lieu de tout recalculer
     *
     * @todo test margaux
     * @param c
     * @param epsilon_raffinement
     */
    public void apprentissageEnLigneFast(Couple c, double epsilon_raffinement) {
        baseApprentissage.add(c);
        probaApriori[SPAM_INDEX] = baseApprentissage.getNombre_spam() / (double) (baseApprentissage.getNombre_ham() + baseApprentissage.getNombre_spam());
        probaApriori[HAM_INDEX] = 1. - probaApriori[SPAM_INDEX];

        double ancien_diviseur_bham = diviseur_bham;
        double ancien_diviseur_bspam = diviseur_bspam;

        diviseur_bham = ((2. * epsilon_raffinement) + baseApprentissage.getNombre_ham()) * (double)ancien_diviseur_bham;
        diviseur_bspam = ((2. * epsilon_raffinement) + baseApprentissage.getNombre_spam()) * (double)ancien_diviseur_bspam;

        if (c.y == SPAM) {
            for (int i = 0; i < bspam.length; i++) {
                bspam[i] = (bspam[i] + c.x[i]) / (double)diviseur_bspam;
            }
        } else {
            for (int i = 0; i < bham.length; i++) {
                bham[i] = (bham[i] + c.x[i]) / (double)diviseur_bham;
            }
        }
    }

    /**
     * apprend selon la base d'apprentissage
     *
     * @param epsilon_raffinement
     */
    public void apprentissage(double epsilon_raffinement) {
        if (batch.Batch.OUT) {
            System.out.println("\nAprentissage ...\n");
        }

        computeProbaAprioriAndBs(epsilon_raffinement);

        if (batch.Batch.OUT) {
            System.out.println("\nApprentissage : done\n");
        }
    }

    /**
     * calcul les probabilités à priori des étiquettes ainsi que les bj pour
     * chaque mot à partir de la base d'apprentissage.
     *
     * @param epsilon_raffinement
     */
    private void computeProbaAprioriAndBs(double epsilon_raffinement) {
        for (Couple c : baseApprentissage.getBase()) {

            switch (c.y) {
                case SPAM:
                    for (int i = 0; i < c.x.length; i++) {
                        bspam[i] += c.x[i];
                    }
                    break;
                case HAM:
                    for (int i = 0; i < c.x.length; i++) {
                        bham[i] += c.x[i];
                    }
                    break;
            }
        }

        // calcul des probas à priori
        probaApriori[SPAM_INDEX] = baseApprentissage.getNombre_spam() / (double) (baseApprentissage.getNombre_ham() + baseApprentissage.getNombre_spam());
        probaApriori[HAM_INDEX] = 1. - probaApriori[SPAM_INDEX];

        if (Batch.OUT) {
            System.out.println("P(Y = HAM) = " + probaApriori[HAM_INDEX]);
            System.out.println("P(Y = SPAM) = " + probaApriori[SPAM_INDEX]);
        }

        // calcul des bham et bspam
        diviseur_bham = (2. * epsilon_raffinement) + baseApprentissage.getNombre_ham();
        diviseur_bspam = (2. * epsilon_raffinement) + baseApprentissage.getNombre_spam();

        for (int i = 0; i < bspam.length; i++) {
            bspam[i] = (bspam[i] + epsilon_raffinement) / diviseur_bspam;
            bham[i] = (bham[i] + epsilon_raffinement) / diviseur_bham;
        }

    }

    /**
     * creer un string d'un exemple représentant les mots du dictionnaire
     * présent dans cette exemple
     *
     * @param exemple
     * @return
     */
    public String show(int[] exemple) {
        String s = "[";
        for (int i = 0; i < exemple.length; i++) {
            if (exemple[i] == 1) {
                s += " " + baseApprentissage.getDictionnaire().getContenu().get(i);
            }
        }
        return s + " ]";
    }

    /**
     * prédit une étiquette selon un exemple
     *
     * @param exemple
     * @return
     */
    public TypeMail prediction(int[] exemple) {

        if (Batch.OUT) {
            System.out.println("\nprediction pour l'exemple " + show(exemple) + " ... \n");
        }

        // calcul des probabilité a postériori
        // ln(P(X = x | Y = SPAM))
        double lnpxspam = 0;
        // ln(P(X = x | Y = HAM))
        double lnpxham = 0;

        for (int i = 0; i < exemple.length; i++) {
            if (exemple[i] == 0) {
                lnpxspam += Math.log(1 - bspam[i]);
                lnpxham += Math.log(1 - bham[i]);
            } else {
                lnpxspam += Math.log(bspam[i]);
                lnpxham += Math.log(bham[i]);
            }
        }

        final double Zx = 1; // on s'en moque vu qu'on compare pspamx et phamx
        // P(Y = SPAM | X = x)
        double pspamx = probaApriori[SPAM_INDEX] * Math.exp(lnpxspam) * Zx;
        // P(Y = HAM | X = x)
        double phamx = probaApriori[HAM_INDEX] * Math.exp(lnpxham) * Zx;

        assert (pspamx >= 0 && pspamx <= 1) : "pspamx n'est pas une probabilité : " + pspamx;
        assert (phamx >= 0 && phamx <= 1) : "phamx n'est pas une probabilité : " + phamx;

        if (Batch.OUT) {
            System.out.println("P(Y = SPAM | X = x) = " + pspamx + " , " + "P(Y = HAM | X = x) = " + phamx);
        }

        // calcul de la réponse
        TypeMail reponse = pspamx > phamx ? SPAM : HAM;

        if (Batch.OUT) {
            System.out.println("=> " + reponse);
            System.out.println("\nprediction : done\n");
        }
        return reponse;
    }

    /**
     * @return the baseApprentissage
     */
    public BaseApprentissage getBaseApprentissage() {
        return baseApprentissage;
    }

}

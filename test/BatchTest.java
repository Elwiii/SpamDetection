/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import model.Filtre;
import model.TypeMail;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nikolai
 */
public class BatchTest {
    
    public BatchTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    final String MON_CLASSIFIEUR = "mon_classifieur";
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

//     @Test
     public void save() throws Exception {
         Filtre f = new Filtre("dictionnaire1000en.txt","baseapp/spam","baseapp/ham",200,200);
         f.updateClassifieur(MON_CLASSIFIEUR);
         assert((new File(MON_CLASSIFIEUR)).exists()):"Fichier non crée";
     }
     
//     @Test
     public void load() throws Exception{
         Filtre filtre = new Filtre(MON_CLASSIFIEUR);
         assertEquals(0.05,filtre.computeErreurHamBaseTest("basetest/ham",200),0);
         assertEquals(0.09,filtre.computeErreurSpamBaseTest("basetest/spam",100),0);
     }
     
     @Test
     public void apprentissageEnLigne() throws Exception{
         Filtre f = new Filtre("dictionnaire1000en.txt","baseapp/spam","baseapp/ham",1,1);
         f.updateClassifieur(MON_CLASSIFIEUR+2);
         Filtre f2 = new Filtre(MON_CLASSIFIEUR+2);
         long a = System.nanoTime();
         f2.addFileToClassifieur("test/Message/message1.txt", TypeMail.SPAM, Filtre.SLOW_ADD);
         long b = System.nanoTime();
         System.out.println("SLOW_ADD : "+(b-a));
         f2.addFileToClassifieur("test/basetest/ham/hamtest0", TypeMail.HAM, Filtre.FAST_ADD);
         long c = System.nanoTime();
         System.out.println("FAST_ADD : "+(c-b));
         f.updateClassifieur(MON_CLASSIFIEUR+2);
     }
}
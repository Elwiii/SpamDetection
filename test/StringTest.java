/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import model.Filtre;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nikolai
 */
public class StringTest {

    public StringTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {
        String s = "coucou!";
        String sbis = s.replaceAll("!", "");
        System.out.println("sbis : " + sbis);
        assertEquals(sbis, "coucou");
    }

    @Test
    public void hello1() {
        String s = "co,uco,u!";
        String sbis = s.replaceAll("!|,", "");
        System.out.println("sbis : " + sbis);
        assertEquals(sbis, "coucou");
    }

    @Test
    public void hello2() {
        try {
            Filtre f = new Filtre("dictionnaire1000en.txt", "baseapp/spam", "baseapp/ham", 1, 1, Filtre.ADVANCED);
            System.out.println("------------------------------------f2------------------------------------------------");
            Filtre f2 = new Filtre("dictionnaire1000en.txt", "baseapp/spam", "baseapp/ham", 1, 1, Filtre.SIMPLE);
//            for (int i = 0; i < 200; i++) {
//                System.out.println(f.getDictionnaire().show(f.getCouples().get(i).x));
//                System.out.println(f2.getDictionnaire().show(f.getCouples().get(i).x));
//            }
            

        } catch (Exception ex) {
            Logger.getLogger(StringTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import model.Filtre;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nikolai
 */
public class FiltreTest {

    public FiltreTest() {
    }
    
    private Filtre f;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
         f = new Filtre("dictionnaire1000en.txt","baseapp/spam","baseapp/ham",200,200);
    }

    @After
    public void tearDown() {
    }

//    @Test
//    public void testLireMessage() {
//        boolean[] representation = f.lire_message("test/Message/message1.txt", Filtre.SIMPLE);
//        assert(representation[6]);
//        assert(representation[1]);
//    }

    
    @Test
    public void testBasetest() throws Exception{
        System.out.println(""+f.computeErreurHamBaseTest("basetest/ham",200));
        System.out.println(""+f.computeErreurSpamBaseTest("basetest/spam",100));
//        System.out.println(f.prediction("basetest/ham/0.txt"));
//        System.out.println(f.prediction("basetest/spam/0.txt"));
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import model.Dictionnaire;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nikolai
 */
public class DictionnaireTest {

    private Dictionnaire d;

    public DictionnaireTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        d = Dictionnaire.getInstance("dictionnaire1000en.txt");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void contenu() {
        System.out.println("" + d);
    }
}

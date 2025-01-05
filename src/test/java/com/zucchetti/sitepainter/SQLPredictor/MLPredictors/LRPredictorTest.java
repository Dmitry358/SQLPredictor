package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class LRPredictorTest {
    private LRPredictor predictor;

    @BeforeEach
    public void setupFields() {
        double[] parameters = {0.5, 0.8, 0.7, 0.9};
        predictor = new LRPredictor("predictorName", 5, "2024-10-11", parameters );
    }

    @Test
    public void testGetPredictorName(){
        String expected = "predictorName";
        String result = predictor.getPredictorName();
        assertEquals(expected, result);
    }
    @Test
    public void testGetQueryWithEmptyFieldsList(){
        //PrintStream originalOut = System.out;
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Fields list must not be empty" + nl;
        String expectedQuery = null;

        String resultQuery = predictor.getQuery(new ArrayList<String>());

        // Ripristinare l'output originale
        //System.setOut(originalOut);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithNumberOfFieldsPassedLessThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Request must contain 3 fields" + nl;
        String expectedQuery = null;

        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=0; i < 2; ++i) fieldsList.add("t.field_"+(i+1));
        String resultQuery = predictor.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithNumberOfFieldsPassedGreaterThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Request must contain 3 fields" + nl;
        String expectedQuery = null;

        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=0; i < 4; ++i) fieldsList.add("t.field_"+(i+1));
        String resultQuery = predictor.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithCorrectInput(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=0; i < 3; ++i) fieldsList.add("t.field_"+(i+1));
        double[] parameters = {0.5, 0.8, 0.7, 0.9};

        String expectedQuery = "(";
        for (int i=0; i < parameters.length; ++i){
            if(i < parameters.length-1) expectedQuery += fieldsList.get(i) + "*" + parameters[i] + " + ";
            else expectedQuery += parameters[i] + ")";
        }

        assertEquals(expectedQuery, predictor.getQuery(fieldsList));
    }
}

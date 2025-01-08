package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class ABCPredictorTest {
    private ABCPredictor predictor;

    @BeforeEach
    public void setupFields(){
        predictor = new ABCPredictor("predictorName", 5, "2024-10-11", "tableName");
    }

    @Test
    public void testGetPredictorName(){
        String expected = "predictorName";
        String result = this.predictor.getPredictorName();
        assertEquals(expected, result);
    }
    @Test
    public void testGetQueryWithEmptyFieldsList(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Request contains incorrect number of fields" + nl;
        String expectedQuery = null;

        String resultQuery = this.predictor.getQuery(new ArrayList<String>());
        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithNumberOfFieldsPassedGreaterThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Request contains incorrect number of fields" + nl;
        String expectedQuery = null;

        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 3; ++i) { fieldsList.add("t.field_"+i); }

        String resultQuery = this.predictor.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
}

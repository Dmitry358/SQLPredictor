package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SVMPredictorTest {
    private SVMPredictor predictorLinear;
    private SVMPredictor predictorPolynomial;
    private SVMPredictor predictorRBF;
    private SVMPredictor predictorLinearRhoNegative;
    private SVMPredictor predictorPolynomialRhoNegative;
    private SVMPredictor predictorRBFRhoNegative;
    private SVMPredictor predictorLinearMinusOne;
    private SVMPredictor predictorPolynomialMinusOne;
    private SVMPredictor predictorRBFMinusOne;

    @BeforeEach
    public void setupFields(){
        ArrayList<ArrayList<Double>> supportVectors = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(0.007, 0.21751, 0.49433, 1.333, 1.4444)),
                new ArrayList<>(Arrays.asList(1.0, 0.23078, 0.51545, 2.333, 2.4444)),
                new ArrayList<>(Arrays.asList(0.5, 0.22071, 0.51669, 3.333, 3.4444)),
                new ArrayList<>(Arrays.asList(-0.333, 0.21233, 0.46617, 4.333, 4.4444)),
                new ArrayList<>(Arrays.asList(-1.0, 0.22696, 0.49682, 5.333, 5.4444)),
                new ArrayList<>(Arrays.asList(-0.02, 0.22651, 0.48274, 6.333, 6.4444))
        ));
        ArrayList<ArrayList<Double>> supportVectorsCoefficintOfFirstIsOne = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(-1.0, 0.21751, 0.49433, 1.333, 1.4444)),
                new ArrayList<>(Arrays.asList(1.0, 0.23078, 0.51545, 2.333, 2.4444)),
                new ArrayList<>(Arrays.asList(0.5, 0.22071, 0.51669, 3.333, 3.4444)),
                new ArrayList<>(Arrays.asList(-0.333, 0.21233, 0.46617, 4.333, 4.4444)),
                new ArrayList<>(Arrays.asList(-1.0, 0.22696, 0.49682, 5.333, 5.4444)),
                new ArrayList<>(Arrays.asList(-0.02, 0.22651, 0.48274, 6.333, 6.4444))
        ));
        Map<String,String> modelParamLinear = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","linear");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","0.07949817180633545");
        }};
        Map<String,String> modelParamPolinom = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","polynomial");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","0.07949817180633545");
        }};
        Map<String,String> modelParamRBF = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","rbf");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","0.07949817180633545");
        }};

        predictorLinear = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamLinear, supportVectors);
        predictorPolynomial = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamPolinom, supportVectors);
        predictorRBF = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamRBF, supportVectors);

        Map<String,String> modelParamLinearRhoNegative = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","linear");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","-0.07949817180633545");
        }};
        Map<String,String> modelParamPolinomRhoNegative = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","polynomial");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","-0.07949817180633545");
        }};
        Map<String,String> modelParamRBFRhoNegative = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","rbf");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","-0.07949817180633545");
        }};

        predictorLinearRhoNegative = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamLinearRhoNegative, supportVectors);
        predictorPolynomialRhoNegative = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamPolinomRhoNegative, supportVectors);
        predictorRBFRhoNegative = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamRBFRhoNegative, supportVectors);

        predictorLinearMinusOne = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamLinear, supportVectorsCoefficintOfFirstIsOne);
        predictorPolynomialMinusOne = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamPolinom, supportVectorsCoefficintOfFirstIsOne);
        predictorRBFMinusOne = new SVMPredictor("predictorName", 5, "2024-10-11", modelParamRBF, supportVectorsCoefficintOfFirstIsOne);
    }

    @Test
    public void testGetPredictorName(){
        String expected = "predictorName";
        String result = predictorLinear.getPredictorName();
        assertEquals(expected, result);
    }
    @Test
    public void testGetLinearKernelQuery(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + ((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) - 0.333*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) - ((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) - 0.02*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) - 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getLinearKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorLinear, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetLinearKernelQueryWithRhoNegative(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + ((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) - 0.333*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) - ((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) - 0.02*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) + 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getLinearKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorLinearRhoNegative, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetLinearKernelQueryWithFirstVectorWithCoefficientMinusOne(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(-((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + ((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) - 0.333*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) - ((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) - 0.02*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) - 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getLinearKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorLinearMinusOne, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetPolynomialKernelQuery(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*(POWER ((0.5*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + 0.6), 4)) + (POWER ((0.5*((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.6), 4)) + 0.5*(POWER ((0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) + 0.6), 4)) - 0.333*(POWER ((0.5*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) + 0.6), 4)) - (POWER ((0.5*((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) + 0.6), 4)) - 0.02*(POWER ((0.5*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) + 0.6), 4)) - 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getPolynomialKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorPolynomial, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetPolynomialKernelQueryWithRhoNegative(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*(POWER ((0.5*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + 0.6), 4)) + (POWER ((0.5*((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.6), 4)) + 0.5*(POWER ((0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) + 0.6), 4)) - 0.333*(POWER ((0.5*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) + 0.6), 4)) - (POWER ((0.5*((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) + 0.6), 4)) - 0.02*(POWER ((0.5*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) + 0.6), 4)) + 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getPolynomialKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorPolynomialRhoNegative, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetPolynomialKernelQueryWithFirstVectorWithCoefficientMinusOne(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(-(POWER ((0.5*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + 0.6), 4)) + (POWER ((0.5*((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.6), 4)) + 0.5*(POWER ((0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) + 0.6), 4)) - 0.333*(POWER ((0.5*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) + 0.6), 4)) - (POWER ((0.5*((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) + 0.6), 4)) - 0.02*(POWER ((0.5*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) + 0.6), 4)) - 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getPolynomialKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorPolynomialMinusOne, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetRBFKernelQuery(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21751), 2) + POWER (((t.field_2) - 0.49433), 2) + POWER (((t.field_3) - 1.333), 2) + POWER (((t.field_4) - 1.4444), 2)))) + (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.23078), 2) + POWER (((t.field_2) - 0.51545), 2) + POWER (((t.field_3) - 2.333), 2) + POWER (((t.field_4) - 2.4444), 2)))) + 0.5*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22071), 2) + POWER (((t.field_2) - 0.51669), 2) + POWER (((t.field_3) - 3.333), 2) + POWER (((t.field_4) - 3.4444), 2)))) - 0.333*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21233), 2) + POWER (((t.field_2) - 0.46617), 2) + POWER (((t.field_3) - 4.333), 2) + POWER (((t.field_4) - 4.4444), 2)))) - (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22696), 2) + POWER (((t.field_2) - 0.49682), 2) + POWER (((t.field_3) - 5.333), 2) + POWER (((t.field_4) - 5.4444), 2)))) - 0.02*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22651), 2) + POWER (((t.field_2) - 0.48274), 2) + POWER (((t.field_3) - 6.333), 2) + POWER (((t.field_4) - 6.4444), 2)))) - 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getRBFKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorRBF, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetRBFKernelQueryWithRhoNegative(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21751), 2) + POWER (((t.field_2) - 0.49433), 2) + POWER (((t.field_3) - 1.333), 2) + POWER (((t.field_4) - 1.4444), 2)))) + (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.23078), 2) + POWER (((t.field_2) - 0.51545), 2) + POWER (((t.field_3) - 2.333), 2) + POWER (((t.field_4) - 2.4444), 2)))) + 0.5*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22071), 2) + POWER (((t.field_2) - 0.51669), 2) + POWER (((t.field_3) - 3.333), 2) + POWER (((t.field_4) - 3.4444), 2)))) - 0.333*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21233), 2) + POWER (((t.field_2) - 0.46617), 2) + POWER (((t.field_3) - 4.333), 2) + POWER (((t.field_4) - 4.4444), 2)))) - (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22696), 2) + POWER (((t.field_2) - 0.49682), 2) + POWER (((t.field_3) - 5.333), 2) + POWER (((t.field_4) - 5.4444), 2)))) - 0.02*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22651), 2) + POWER (((t.field_2) - 0.48274), 2) + POWER (((t.field_3) - 6.333), 2) + POWER (((t.field_4) - 6.4444), 2)))) + 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getRBFKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorRBFRhoNegative, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetRBFKernelQueryWithFirstVectorWithCoefficientMinusOne(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(-(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21751), 2) + POWER (((t.field_2) - 0.49433), 2) + POWER (((t.field_3) - 1.333), 2) + POWER (((t.field_4) - 1.4444), 2)))) + (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.23078), 2) + POWER (((t.field_2) - 0.51545), 2) + POWER (((t.field_3) - 2.333), 2) + POWER (((t.field_4) - 2.4444), 2)))) + 0.5*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22071), 2) + POWER (((t.field_2) - 0.51669), 2) + POWER (((t.field_3) - 3.333), 2) + POWER (((t.field_4) - 3.4444), 2)))) - 0.333*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21233), 2) + POWER (((t.field_2) - 0.46617), 2) + POWER (((t.field_3) - 4.333), 2) + POWER (((t.field_4) - 4.4444), 2)))) - (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22696), 2) + POWER (((t.field_2) - 0.49682), 2) + POWER (((t.field_3) - 5.333), 2) + POWER (((t.field_4) - 5.4444), 2)))) - 0.02*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22651), 2) + POWER (((t.field_2) - 0.48274), 2) + POWER (((t.field_3) - 6.333), 2) + POWER (((t.field_4) - 6.4444), 2)))) - 0.07949817180633545)";

        try {
            Method method = SVMPredictor.class.getDeclaredMethod("getRBFKernelQuery", ArrayList.class);
            method.setAccessible(true);
            String resultQuery = (String) method.invoke(predictorRBFMinusOne, fieldsList);
            assertEquals(expectedQuery, resultQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testGetQueryWithLinearKernelPredictor(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + ((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) - 0.333*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) - ((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) - 0.02*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) - 0.07949817180633545)";
        String resultQuery = predictorLinear.getQuery(fieldsList);
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithPolynomialKernelPredictor(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*(POWER ((0.5*((t.field_1)*0.21751 + (t.field_2)*0.49433 + (t.field_3)*1.333 + (t.field_4)*1.4444) + 0.6), 4)) + (POWER ((0.5*((t.field_1)*0.23078 + (t.field_2)*0.51545 + (t.field_3)*2.333 + (t.field_4)*2.4444) + 0.6), 4)) + 0.5*(POWER ((0.5*((t.field_1)*0.22071 + (t.field_2)*0.51669 + (t.field_3)*3.333 + (t.field_4)*3.4444) + 0.6), 4)) - 0.333*(POWER ((0.5*((t.field_1)*0.21233 + (t.field_2)*0.46617 + (t.field_3)*4.333 + (t.field_4)*4.4444) + 0.6), 4)) - (POWER ((0.5*((t.field_1)*0.22696 + (t.field_2)*0.49682 + (t.field_3)*5.333 + (t.field_4)*5.4444) + 0.6), 4)) - 0.02*(POWER ((0.5*((t.field_1)*0.22651 + (t.field_2)*0.48274 + (t.field_3)*6.333 + (t.field_4)*6.4444) + 0.6), 4)) - 0.07949817180633545)";
        String resultQuery = predictorPolynomial.getQuery(fieldsList);
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithRBFKernelPredictor(){
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("(t.field_"+i+")"); }

        String expectedQuery = "(0.007*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21751), 2) + POWER (((t.field_2) - 0.49433), 2) + POWER (((t.field_3) - 1.333), 2) + POWER (((t.field_4) - 1.4444), 2)))) + (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.23078), 2) + POWER (((t.field_2) - 0.51545), 2) + POWER (((t.field_3) - 2.333), 2) + POWER (((t.field_4) - 2.4444), 2)))) + 0.5*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22071), 2) + POWER (((t.field_2) - 0.51669), 2) + POWER (((t.field_3) - 3.333), 2) + POWER (((t.field_4) - 3.4444), 2)))) - 0.333*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.21233), 2) + POWER (((t.field_2) - 0.46617), 2) + POWER (((t.field_3) - 4.333), 2) + POWER (((t.field_4) - 4.4444), 2)))) - (POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22696), 2) + POWER (((t.field_2) - 0.49682), 2) + POWER (((t.field_3) - 5.333), 2) + POWER (((t.field_4) - 5.4444), 2)))) - 0.02*(POWER (EXP(1), -0.5*(POWER (((t.field_1) - 0.22651), 2) + POWER (((t.field_2) - 0.48274), 2) + POWER (((t.field_3) - 6.333), 2) + POWER (((t.field_4) - 6.4444), 2)))) - 0.07949817180633545)";
        String resultQuery = predictorRBF.getQuery(fieldsList);
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWhenDescriptionFileNotContainAnyVector(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));

        Map<String,String> modelParameters = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","linear");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","0.07949817180633545");
        }};
        ArrayList<ArrayList<Double>> supportVectors = new ArrayList<>();
        SVMPredictor emptyPredictor = new SVMPredictor("predictorName", 5,
                                                        "2024-10-11", modelParameters, supportVectors);

        String expectedOutput = "Description file does not contain any vector" + nl;
        String expectedQuery = null;

        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 4; ++i) { fieldsList.add("t.field_"+i); }
        String resultQuery = emptyPredictor.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithEmptyFieldsList(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Fields list must not be empty" + nl;
        String expectedQuery = null;

        String resultQuery = predictorLinear.getQuery(new ArrayList<String>());

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithNumberOfFieldsPassedLessThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Request must contain 4 field names" + nl;
        String expectedQuery = null;

        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 4; ++i) { fieldsList.add("t.field_"+i); }
        String resultQuery = predictorLinear.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWithNumberOfFieldsPassedGreaterThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Request must contain 4 field names" + nl;
        String expectedQuery = null;

        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 6; ++i) { fieldsList.add("t.field_"+i); }
        String resultQuery = predictorLinear.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
    @Test
    public void testGetQueryWhenDescriptionFileContainUnmanageableKernelType(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));

        ArrayList<ArrayList<Double>> supportVectors = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(0.007, 0.21751, 0.49433, 1.333, 1.4444)),
                new ArrayList<>(Arrays.asList(1.0, 0.23078, 0.51545, 2.333, 2.4444)),
                new ArrayList<>(Arrays.asList(0.5, 0.22071, 0.51669, 3.333, 3.4444)),
                new ArrayList<>(Arrays.asList(-0.333, 0.21233, 0.46617, 4.333, 4.4444)),
                new ArrayList<>(Arrays.asList(-1.0, 0.22696, 0.49682, 5.333, 5.4444)),
                new ArrayList<>(Arrays.asList(-0.02, 0.22651, 0.48274, 6.333, 6.4444))
        ));
        Map<String,String> modelParameters = new HashMap<String, String>() {{
            put("svm_type","c_svc");
            put("kernel_type","pol");
            put("degree","4");
            put("gamma","0.5");
            put("coef0","0.6");
            put("rho","0.07949817180633545");
        }};
        ArrayList<String> fieldsList = new ArrayList<String>();
        for (int i=1; i < 5; ++i) { fieldsList.add("t.field_"+i); }

        String expectedOutput = "Description file contain unmanageable kernel type" + nl;
        String expectedQuery = null;

        SVMPredictor predictorKernelError = new SVMPredictor("predictorName", 5, "2024-10-11", modelParameters, supportVectors);
        String resultQuery = predictorKernelError.getQuery(fieldsList);

        assertEquals(expectedOutput, outputResult.toString());
        assertEquals(expectedQuery, resultQuery);
    }
}

package com.zucchetti.sitepainter.SQLPredictor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MLSQLExpanderTest {
    @Test
    public void testGetFieldsListWithCorrectInput(){
        MLSQLExpander expander = new MLSQLExpander();
        String result = "    t.insurance_samples.csv+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13), tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz   ";
        ArrayList<String> expectedFieldsList = new ArrayList<String>();
        expectedFieldsList.add("(t.insurance_samples.csv+77)");
        expectedFieldsList.add("(t.tttt)");
        expectedFieldsList.add("(t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))");
        expectedFieldsList.add("(tccc +44)");
        expectedFieldsList.add("(t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))");
        expectedFieldsList.add("(t.zzz)");

        try {
        Method getFieldsList = MLSQLExpander.class.getDeclaredMethod("getFieldsList", String.class);
        getFieldsList.setAccessible(true);

        ArrayList<String> resultFieldsList = (ArrayList<String>) getFieldsList.invoke(expander, result);
        assertEquals(expectedFieldsList, resultFieldsList);
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetFieldsListWithEmptyFieldInFieldsList(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Fields in the fields list cannot be empty"+nl;

        String result = "    t.insurance_samples.csv+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),   ,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz   ";

        try {
            Method getFieldsList = MLSQLExpander.class.getDeclaredMethod("getFieldsList", String.class);
            getFieldsList.setAccessible(true);

            ArrayList<String> resultFieldsList = (ArrayList<String>) getFieldsList.invoke(expander, result);
            assertNull(resultFieldsList, "Method does not return null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Disabled
    @Test
    public void testTranslateWithABCPredictor(){
        String request = "<       abc_good   >(      t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) +  left(t.nome1_3,13) )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(SELECT classe FROM good_client AS  gwcaNN25 WHERE gwcaNN25.codice_cliente = (t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) +  left(t.nome1_3, 13)))";

        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithLRPredictor(){
        String request = "<       z_lr_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        MLSQLExpander expander = new MLSQLExpander();

        //String exspectedQuery = "((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*5.07 + (tccc +44)*3.45 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*0.95 + (t.zzz)*1.06 + 6.85)";
        String exspectedQuery = "(-26576.52119636412 + (t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13))*261.6787636981832 +   (tccc +44)*322.8567932638625   +     (t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22))*446.0453802537144 + (t.zzz)*4771.159870691035)";
        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithSVMPredictorLinearKernel(){
        String request = "<       z_svm_linear_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(0.007*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.21751 + (tccc +44)*0.49433 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*1.333 + (t.zzz)*1.4444) + ((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.23078 + (tccc +44)*0.51545 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*2.333 + (t.zzz)*2.4444) + 0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.22071 + (tccc +44)*0.51669 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*3.333 + (t.zzz)*3.4444) - 0.333*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.21233 + (tccc +44)*0.46617 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*4.333 + (t.zzz)*4.4444) - ((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.22696 + (tccc +44)*0.49682 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*5.333 + (t.zzz)*5.4444) - 0.02*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.22651 + (tccc +44)*0.48274 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*6.333 + (t.zzz)*6.4444) - 0.07949817180633545)";

        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithSVMPredictorPolynomialKernel(){
        String request = "<       z_svm_poly_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(0.007*(POWER ((0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.21751 + (tccc +44)*0.49433 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*1.333 + (t.zzz)*1.4444) + 0.6), 4)) + (POWER ((0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.23078 + (tccc +44)*0.51545 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*2.333 + (t.zzz)*2.4444) + 0.6), 4)) + 0.5*(POWER ((0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.22071 + (tccc +44)*0.51669 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*3.333 + (t.zzz)*3.4444) + 0.6), 4)) - 0.333*(POWER ((0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.21233 + (tccc +44)*0.46617 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*4.333 + (t.zzz)*4.4444) + 0.6), 4)) - (POWER ((0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.22696 + (tccc +44)*0.49682 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*5.333 + (t.zzz)*5.4444) + 0.6), 4)) - 0.02*(POWER ((0.5*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*0.22651 + (tccc +44)*0.48274 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*6.333 + (t.zzz)*6.4444) + 0.6), 4)) - 0.07949817180633545)";

        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithSVMPredictorRBFKernel(){
        String request = "<       z_svm_rbf_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(0.007*(POWER (EXP(1), -0.5*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 0.21751), 2) + POWER (((tccc +44) - 0.49433), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 1.333), 2) + POWER (((t.zzz) - 1.4444), 2)))) + (POWER (EXP(1), -0.5*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 0.23078), 2) + POWER (((tccc +44) - 0.51545), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 2.333), 2) + POWER (((t.zzz) - 2.4444), 2)))) + 0.5*(POWER (EXP(1), -0.5*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 0.22071), 2) + POWER (((tccc +44) - 0.51669), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 3.333), 2) + POWER (((t.zzz) - 3.4444), 2)))) - 0.333*(POWER (EXP(1), -0.5*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 0.21233), 2) + POWER (((tccc +44) - 0.46617), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 4.333), 2) + POWER (((t.zzz) - 4.4444), 2)))) - (POWER (EXP(1), -0.5*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 0.22696), 2) + POWER (((tccc +44) - 0.49682), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 5.333), 2) + POWER (((t.zzz) - 5.4444), 2)))) - 0.02*(POWER (EXP(1), -0.5*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 0.22651), 2) + POWER (((tccc +44) - 0.48274), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 6.333), 2) + POWER (((t.zzz) - 6.4444), 2)))) - 0.07949817180633545)";

        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithEmptyPredictorField(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Predictor field cannot be empty"+nl;

        String request = "<         >(     t.insurance_samples.csv+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13), t.ccc  ,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz   )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithEmptyFieldsList(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request does not contain all required fields"+nl;

        String request = "<   svm_polynomial     >(       )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithEmptyPredictorFieldAndFieldsList(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request does not contain all required fields"+nl;

        String request = "<        >(       )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithEmptyFieldInFieldsList(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Fields in the fields list cannot be empty"+nl;

        String request = "<   svm_polynomial     >(    t.insurance_samples.csv+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),   ,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz   )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithABCPredictorWhenFieldsListIsEmpty(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request does not contain all required fields"+nl;

        String request = "<   abc_good     >(       )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithABCPredictorWhenNumberOfFieldsPassedGreaterThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request contains incorrect number of fields"+nl;

        String request = "<   z_abc_for_testing     >( t.aaaa, t.bbbb       )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithLRPredictorWhenNumberOfFieldsPassedLessThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request must contain 3 field names"+nl;

        String request = "<   z_lr_for_testing     >(  t.aaaa, t.bbbb    )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithLRPredictorWhenNumberOfFieldsPassedGreaterThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request must contain 3 field names"+nl;

        String request = "<   z_lr_for_testing     >( t.aaaa, t.bbbb, t.cccc, t.dddd, t.eeee       )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithSVMPredictorWhenNumberOfFieldsPassedLessThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request must contain 2 field names"+nl;

        String request = "<   z_svm_rbf_for_testing     >(  t.aaaa, t.bbbb, t.cccc    )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Test
    public void testTranslateWithSVMPredictorWhenNumberOfFieldsPassedGreaterThanRequired(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        MLSQLExpander expander = new MLSQLExpander();
        String expectedOutput = "Request must contain 2 field names"+nl;

        String request = "<   z_svm_rbf_for_testing     >( t.aaaa, t.bbbb, t.cccc, t.dddd, t.eeee       )";

        String resultQuery = expander.translate(request);
        assertNull(resultQuery, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
}

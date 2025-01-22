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
        String exspectedQuery = "(-26576.52119636412 + (t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*261.6787636981832 + (tccc +44)*322.8567932638625 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*446.0453802537144 + (t.zzz)*4771.159870691035)";
        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithSVMPredictorLinearKernel(){
        String request = "<       z_svm_linear_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),    t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22)   )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*19.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*27.9) + 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*62.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*26.29) + 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*27.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*42.13) + 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*30.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*35.3) + 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*34.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*31.92) - 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*18.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*33.77) - 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*28.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*33.0) - 52.4652751096998*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*33.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*22.705) - 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*56.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*40.3) - 16.976681382087484*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*60.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*36.005) - 30.558043508208204*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*18.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*34.1) - 100.0*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*37.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*28.025) - 1.0567428899807627)";

        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithSVMPredictorPolynomialKernel(){
        String request = "<       z_svm_poly_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),    t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22)   )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(100.0*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*19.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*27.9) + 0.7), 2)) + 100.0*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*62.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*26.29) + 0.7), 2)) + 52.701593549014895*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*27.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*42.13) + 0.7), 2)) + 100.0*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*30.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*35.3) + 0.7), 2)) + 100.0*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*34.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*31.92) + 0.7), 2)) - 100.0*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*28.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*33.0) + 0.7), 2)) - 16.10459752218912*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*33.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*22.705) + 0.7), 2)) - 98.57615993519565*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*60.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*25.84) + 0.7), 2)) - 100.0*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*23.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*34.4) + 0.7), 2)) - 27.21209307394176*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*56.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*40.3) + 0.7), 2)) - 68.77168507359944*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*30.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*32.4) + 0.7), 2)) - 42.03705794409011*(POWER ((0.005*((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13))*18.0 + (t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22))*34.1) + 0.7), 2)) - 0.7396017266970515)";

        String resultQuery = expander.translate(request);
        assertEquals(exspectedQuery, resultQuery);
    }
    @Test
    public void testTranslateWithSVMPredictorRBFKernel(){
        String request = "<       z_svm_rbf_for_testing   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),    t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22)   )";
        MLSQLExpander expander = new MLSQLExpander();

        String exspectedQuery = "(100.0*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 19.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 27.9), 2)))) + 46.31949011305558*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 62.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 26.29), 2)))) + 5.9704114435513755*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 27.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 42.13), 2)))) + 19.879619148717307*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 30.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 35.3), 2)))) + 100.0*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 34.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 31.92), 2)))) - 32.89866162374878*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 18.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 33.77), 2)))) - 41.625425738355425*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 37.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 29.83), 2)))) - 43.07474955421971*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 60.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 25.84), 2)))) - 60.51792273292361*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 19.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 24.6), 2)))) - 2.2167640720760313*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 60.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 36.005), 2)))) - 91.83599698400076*(POWER (EXP(1), -0.005*(POWER (((t.km1 + left(t.nome1_1, 11) + left(t.nome1_2, 12) + left(t.nome1_3, 13)) - 30.0), 2) + POWER (((t.km2 +  left(t.nome2_1, 21) +   left(t.nome2_2, 22)) - 32.4), 2)))) - 1.3901821002393515)";

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
        String expectedOutput = "Request must contain 4 field names"+nl;

        String request = "<   z_lr_for_testing     >(  t.aaaa, t.bbbb, t.cccc    )";

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
        String expectedOutput = "Request must contain 4 field names"+nl;

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

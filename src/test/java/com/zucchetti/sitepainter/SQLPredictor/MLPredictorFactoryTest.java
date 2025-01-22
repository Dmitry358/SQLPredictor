package com.zucchetti.sitepainter.SQLPredictor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.ABCPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.LRPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.SVMPredictor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MLPredictorFactoryTest {

    /*
    @Test
    public void testCreazionePersona() throws NoSuchFieldException, IllegalAccessException {
        // Creiamo un oggetto Persona
        Persona persona = new Persona("Mario", 30);

        // Usare la riflessione per ottenere il valore del campo "nome"
        Field nomeField = Persona.class.getDeclaredField("nome");
        nomeField.setAccessible(true);
        String nome = (String) nomeField.get(persona);
        assertEquals("Mario", nome, "Il nome non è corretto");

        // Usare la riflessione per ottenere il valore del campo "eta"
        Field etaField = Persona.class.getDeclaredField("eta");
        etaField.setAccessible(true);
        int eta = (int) etaField.get(persona);
        assertEquals(30, eta, "L'età non è corretta");
    }
    */
    @Test
    public void testGetLRPredictorReturnCorrectType(){
        JsonArray parameters = new JsonArray();
        parameters.add(1.46);
        parameters.add(2.23);
        parameters.add(3.0);
        parameters.add(6.0);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "z_lr_for_testing");
        jsonObject.addProperty("version", 5);
        jsonObject.addProperty("last_train", "2023-10-1");
        jsonObject.add("parametersLR", parameters);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getLRPredictor = MLPredictorFactory.class.getDeclaredMethod("getLRPredictor", JsonObject.class);
            getLRPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getLRPredictor.invoke(factory, jsonObject);

            assertNotNull(resultPredictor, "Method returns null");
            assertTrue(resultPredictor.getClass() == LRPredictor.class, "Dynamic type of returned object is different from LRPredictor");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetLRPredictorWithInsufficientNumberOfCharacteristicsInDescriptionFile(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Description file does not contain all information needed to create object" + nl;

        JsonArray parameters = new JsonArray();
        parameters.add(1.46);
        parameters.add(2.23);
        parameters.add(3.0);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "auto_lr");
        jsonObject.addProperty("version", 5);
        jsonObject.add("parameters", parameters);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getLRPredictor = MLPredictorFactory.class.getDeclaredMethod("getLRPredictor", JsonObject.class);
            getLRPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getLRPredictor.invoke(factory, jsonObject);

            assertNull(resultPredictor, "Method don't returns null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    /*
    @Test
    public void testGetLRPredictorWithRepeatedfCharacteristicsInDescriptionFile(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String expectedOutput = "Description file contains multiple fields with same name" + nl;

        JsonArray parameters = new JsonArray();
        parameters.add(1.46);
        parameters.add(2.23);
        parameters.add(3.0);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "auto_lr");
        jsonObject.addProperty("version", 5);
        jsonObject.addProperty("last_train", "2023-10-1");
        jsonObject.addProperty("version", 6);
        jsonObject.add("parameters", parameters);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getLRPredictor = MLPredictorFactory.class.getDeclaredMethod("getLRPredictor", JsonObject.class);
            getLRPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getLRPredictor.invoke(factory, jsonObject);

            assertNull(resultPredictor, "Method don't returns null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    */
    /*
    @Test
    public void testGetLRPredictorCorrectnessOfAttributeValues(){
        JsonArray parameters = new JsonArray();
        parameters.add(1.46);
        parameters.add(2.23);
        parameters.add(3.0);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "auto_lr");
        jsonObject.addProperty("version", 6);
        jsonObject.addProperty("last_train", "2023-10-1");
        jsonObject.add("parameters", parameters);

        double[] param = {1.46, 2.23, 3.0};
        MLPredictor expectedPredictor = new LRPredictor("auto_lr", 5, "2023-10-1", param);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getLRPredictor = MLPredictorFactory.class.getDeclaredMethod("getLRPredictor", JsonObject.class);
            getLRPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getLRPredictor.invoke(factory, jsonObject);

            boolean classEquality = true;
            if (!expectedPredictor.getClass().equals(resultPredictor.getClass())) {
                classEquality = false;
            }

            Field[] fields = expectedPredictor.getClass().getDeclaredFields();
            boolean fieldEsquals = true;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value1 = field.get(expectedPredictor);
                Object value2 = field.get(resultPredictor);

                if ((value1 == null && value2 != null) || (value1 != null && value2 == null)) fieldEsquals = false;

                if (!value1.equals(value2)) fieldEsquals = false;

            }
            assertTrue(classEquality, "Dynamic type of returned object is different from LRPredictor");
            assertTrue(fieldEsquals, "Attributes have incorrect values");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    */
    @Test
    public void testGetABCPredictorReturnCorrectType(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "abc_good");
        jsonObject.addProperty("version", 2);
        jsonObject.addProperty("last_train", "2023-10-11");
        jsonObject.addProperty("table_name", "good_client");


        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getABCPredictor = MLPredictorFactory.class.getDeclaredMethod("getABCPredictor", JsonObject.class);
            getABCPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getABCPredictor.invoke(factory, jsonObject);

            assertNotNull(resultPredictor, "Method returns null");
            assertTrue(resultPredictor.getClass() == ABCPredictor.class, "Dynamic type of returned object is different from LRPredictor");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetABCPredictorWithInsufficientNumberOfCharacteristicsInDescriptionFile(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Description file does not contain all information needed to create object" + nl;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "auto_lr");
        jsonObject.addProperty("version", 5);
        jsonObject.addProperty("table_name", "good_client");

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getABCPredictor = MLPredictorFactory.class.getDeclaredMethod("getABCPredictor", JsonObject.class);
            getABCPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getABCPredictor.invoke(factory, jsonObject);

            assertNull(resultPredictor, "Method don't returns null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetSVMPredictorLinearKernelReturnCorrectType(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "svm_linear");
        jsonObject.addProperty("version", 2);
        jsonObject.addProperty("last_train", "2023-10-21");
        JsonObject modelData = new JsonObject();
        modelData.addProperty("svm_type", "c_svc");
        modelData.addProperty("kernel_type", "linear");
        modelData.addProperty("degree", 3);
        modelData.addProperty("gamma", 0.3);
        modelData.addProperty("coef0", 0.8);
        modelData.addProperty("rho", 0.0789);
        JsonArray supportVectors = new JsonArray();
        JsonArray sv1 = new JsonArray(); for(int i=0; i<5;++i ) { sv1.add(1+i%10+i%100); }
        JsonArray sv2 = new JsonArray(); for(int i=0; i<5;++i ) { sv2.add(2+i/10+i/100); }
        JsonArray sv3 = new JsonArray(); for(int i=0; i<5;++i ) { sv3.add(3+i/10+i/100); }
        JsonArray sv4 = new JsonArray(); for(int i=0; i<5;++i ) { sv4.add(4+i/10+i/100); }
        JsonArray sv5 = new JsonArray(); for(int i=0; i<5;++i ) { sv5.add(5+i/10+i/100); }
        JsonArray sv6 = new JsonArray(); for(int i=0; i<5;++i ) { sv6.add(6+i/10+i/100); }
        supportVectors.add(sv1); supportVectors.add(sv2); supportVectors.add(sv3); supportVectors.add(sv4); supportVectors.add(sv5); supportVectors.add(sv6);
        modelData.add("support_vectors", supportVectors);
        jsonObject.add("model_data", modelData);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getSVMPredictor = MLPredictorFactory.class.getDeclaredMethod("getSVMPredictor", JsonObject.class);
            getSVMPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getSVMPredictor.invoke(factory, jsonObject);

            assertNotNull(resultPredictor, "Method returns null");
            assertTrue(resultPredictor.getClass() == SVMPredictor.class, "Dynamic type of returned object is different from LRPredictor");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetSVMPredictorLinearKernelWithInsufficientNumberOfCharacteristicsInDescriptionFile(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "svm_linear");
        jsonObject.addProperty("last_train", "2023-10-21");
        JsonObject modelData = new JsonObject();
        modelData.addProperty("svm_type", "c_svc");
        modelData.addProperty("kernel_type", "linear");
        modelData.addProperty("degree", 3);
        modelData.addProperty("gamma", 0.3);
        modelData.addProperty("coef0", 0.8);
        modelData.addProperty("rho", 0.0789);
        JsonArray supportVectors = new JsonArray();
        JsonArray sv1 = new JsonArray(); for(int i=0; i<5;++i ) { sv1.add(1+i%10+i%100); }
        JsonArray sv2 = new JsonArray(); for(int i=0; i<5;++i ) { sv2.add(2+i/10+i/100); }
        JsonArray sv3 = new JsonArray(); for(int i=0; i<5;++i ) { sv3.add(3+i/10+i/100); }
        JsonArray sv4 = new JsonArray(); for(int i=0; i<5;++i ) { sv4.add(4+i/10+i/100); }
        JsonArray sv5 = new JsonArray(); for(int i=0; i<5;++i ) { sv5.add(5+i/10+i/100); }
        JsonArray sv6 = new JsonArray(); for(int i=0; i<5;++i ) { sv6.add(6+i/10+i/100); }
        supportVectors.add(sv1); supportVectors.add(sv2); supportVectors.add(sv3); supportVectors.add(sv4); supportVectors.add(sv5); supportVectors.add(sv6);
        modelData.add("support_vectors", supportVectors);
        jsonObject.add("model_data", modelData);

        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Description file does not contain all information needed to create object" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getSVMPredictor = MLPredictorFactory.class.getDeclaredMethod("getSVMPredictor", JsonObject.class);
            getSVMPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getSVMPredictor.invoke(factory, jsonObject);

            assertNull(resultPredictor, "Method don't returns null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetSVMPredictorPolynomialKernelReturnCorrectType(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "svm_linear");
        jsonObject.addProperty("version", 2);
        jsonObject.addProperty("last_train", "2023-10-21");
        JsonObject modelData = new JsonObject();
        modelData.addProperty("svm_type", "c_svc");
        modelData.addProperty("kernel_type", "polynomial");
        modelData.addProperty("degree", 3);
        modelData.addProperty("gamma", 0.3);
        modelData.addProperty("coef0", 0.8);
        modelData.addProperty("rho", 0.0789);
        JsonArray supportVectors = new JsonArray();
        JsonArray sv1 = new JsonArray(); for(int i=0; i<5;++i ) { sv1.add(1+i%10+i%100); }
        JsonArray sv2 = new JsonArray(); for(int i=0; i<5;++i ) { sv2.add(2+i/10+i/100); }
        JsonArray sv3 = new JsonArray(); for(int i=0; i<5;++i ) { sv3.add(3+i/10+i/100); }
        JsonArray sv4 = new JsonArray(); for(int i=0; i<5;++i ) { sv4.add(4+i/10+i/100); }
        JsonArray sv5 = new JsonArray(); for(int i=0; i<5;++i ) { sv5.add(5+i/10+i/100); }
        JsonArray sv6 = new JsonArray(); for(int i=0; i<5;++i ) { sv6.add(6+i/10+i/100); }
        supportVectors.add(sv1); supportVectors.add(sv2); supportVectors.add(sv3); supportVectors.add(sv4); supportVectors.add(sv5); supportVectors.add(sv6);
        modelData.add("support_vectors", supportVectors);
        jsonObject.add("model_data", modelData);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getSVMPredictor = MLPredictorFactory.class.getDeclaredMethod("getSVMPredictor", JsonObject.class);
            getSVMPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getSVMPredictor.invoke(factory, jsonObject);

            assertNotNull(resultPredictor, "Method returns null");
            assertTrue(resultPredictor.getClass() == SVMPredictor.class, "Dynamic type of returned object is different from LRPredictor");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetSVMPredictorPolynomialKernelWithInsufficientNumberOfCharacteristicsInDescriptionFile(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "svm_linear");
        jsonObject.addProperty("last_train", "2023-10-21");
        JsonObject modelData = new JsonObject();
        modelData.addProperty("svm_type", "c_svc");
        modelData.addProperty("kernel_type", "polynomial");
        modelData.addProperty("degree", 3);
        modelData.addProperty("gamma", 0.3);
        modelData.addProperty("coef0", 0.8);
        modelData.addProperty("rho", 0.0789);
        JsonArray supportVectors = new JsonArray();
        JsonArray sv1 = new JsonArray(); for(int i=0; i<5;++i ) { sv1.add(1+i%10+i%100); }
        JsonArray sv2 = new JsonArray(); for(int i=0; i<5;++i ) { sv2.add(2+i/10+i/100); }
        JsonArray sv3 = new JsonArray(); for(int i=0; i<5;++i ) { sv3.add(3+i/10+i/100); }
        JsonArray sv4 = new JsonArray(); for(int i=0; i<5;++i ) { sv4.add(4+i/10+i/100); }
        JsonArray sv5 = new JsonArray(); for(int i=0; i<5;++i ) { sv5.add(5+i/10+i/100); }
        JsonArray sv6 = new JsonArray(); for(int i=0; i<5;++i ) { sv6.add(6+i/10+i/100); }
        supportVectors.add(sv1); supportVectors.add(sv2); supportVectors.add(sv3); supportVectors.add(sv4); supportVectors.add(sv5); supportVectors.add(sv6);
        modelData.add("support_vectors", supportVectors);
        jsonObject.add("model_data", modelData);

        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Description file does not contain all information needed to create object" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getSVMPredictor = MLPredictorFactory.class.getDeclaredMethod("getSVMPredictor", JsonObject.class);
            getSVMPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getSVMPredictor.invoke(factory, jsonObject);

            assertNull(resultPredictor, "Method don't returns null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetSVMPredictorRBFKernelReturnCorrectType(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "svm_linear");
        jsonObject.addProperty("version", 2);
        jsonObject.addProperty("last_train", "2023-10-21");
        JsonObject modelData = new JsonObject();
        modelData.addProperty("svm_type", "c_svc");
        modelData.addProperty("kernel_type", "rbf");
        modelData.addProperty("degree", 3);
        modelData.addProperty("gamma", 0.3);
        modelData.addProperty("coef0", 0.8);
        modelData.addProperty("rho", 0.0789);
        JsonArray supportVectors = new JsonArray();
        JsonArray sv1 = new JsonArray(); for(int i=0; i<5;++i ) { sv1.add(1+i%10+i%100); }
        JsonArray sv2 = new JsonArray(); for(int i=0; i<5;++i ) { sv2.add(2+i/10+i/100); }
        JsonArray sv3 = new JsonArray(); for(int i=0; i<5;++i ) { sv3.add(3+i/10+i/100); }
        JsonArray sv4 = new JsonArray(); for(int i=0; i<5;++i ) { sv4.add(4+i/10+i/100); }
        JsonArray sv5 = new JsonArray(); for(int i=0; i<5;++i ) { sv5.add(5+i/10+i/100); }
        JsonArray sv6 = new JsonArray(); for(int i=0; i<5;++i ) { sv6.add(6+i/10+i/100); }
        supportVectors.add(sv1); supportVectors.add(sv2); supportVectors.add(sv3); supportVectors.add(sv4); supportVectors.add(sv5); supportVectors.add(sv6);
        modelData.add("support_vectors", supportVectors);
        jsonObject.add("model_data", modelData);

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getSVMPredictor = MLPredictorFactory.class.getDeclaredMethod("getSVMPredictor", JsonObject.class);
            getSVMPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getSVMPredictor.invoke(factory, jsonObject);

            assertNotNull(resultPredictor, "Method returns null");
            assertTrue(resultPredictor.getClass() == SVMPredictor.class, "Dynamic type of returned object is different from LRPredictor");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetSVMPredictorRBFKernelWithInsufficientNumberOfCharacteristicsInDescriptionFile(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("predictor_name", "svm_linear");
        jsonObject.addProperty("last_train", "2023-10-21");
        JsonObject modelData = new JsonObject();
        modelData.addProperty("svm_type", "c_svc");
        modelData.addProperty("kernel_type", "rbf");
        modelData.addProperty("degree", 3);
        modelData.addProperty("gamma", 0.3);
        modelData.addProperty("coef0", 0.8);
        modelData.addProperty("rho", 0.0789);
        JsonArray supportVectors = new JsonArray();
        JsonArray sv1 = new JsonArray(); for(int i=0; i<5;++i ) { sv1.add(1+i%10+i%100); }
        JsonArray sv2 = new JsonArray(); for(int i=0; i<5;++i ) { sv2.add(2+i/10+i/100); }
        JsonArray sv3 = new JsonArray(); for(int i=0; i<5;++i ) { sv3.add(3+i/10+i/100); }
        JsonArray sv4 = new JsonArray(); for(int i=0; i<5;++i ) { sv4.add(4+i/10+i/100); }
        JsonArray sv5 = new JsonArray(); for(int i=0; i<5;++i ) { sv5.add(5+i/10+i/100); }
        JsonArray sv6 = new JsonArray(); for(int i=0; i<5;++i ) { sv6.add(6+i/10+i/100); }
        supportVectors.add(sv1); supportVectors.add(sv2); supportVectors.add(sv3); supportVectors.add(sv4); supportVectors.add(sv5); supportVectors.add(sv6);
        modelData.add("support_vectors", supportVectors);
        jsonObject.add("model_data", modelData);

        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String expectedOutput = "Description file does not contain all information needed to create object" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        try {
            Method getSVMPredictor = MLPredictorFactory.class.getDeclaredMethod("getSVMPredictor", JsonObject.class);
            getSVMPredictor.setAccessible(true);

            MLPredictor resultPredictor = (MLPredictor) getSVMPredictor.invoke(factory, jsonObject);

            assertNull(resultPredictor, "Method don't returns null");
            assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
        }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (SecurityException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
    @Test
    public void testGetPredictorWithCorrectInputWhatReturnsLRPredictor(){
        String predictorName = "z_lr_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNotNull(resultPredictor, "Method returns null");
        assertTrue(resultPredictor.getClass() == LRPredictor.class, "Dynamic type of returned object is different from LRPredictor");
    }
    @Test
    public void testGetPredictorWithCorrectInputWhatReturnsABCPredictor(){
        String predictorName = "z_abc_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNotNull(resultPredictor, "Method returns null");
        assertTrue(resultPredictor.getClass() == ABCPredictor.class, "Dynamic type of returned object is different from LRPredictor");
    }
    @Test
    public void testGetPredictorWithCorrectInputWhatReturnsSVMPredictorLinearKernel(){
        String predictorName = "z_svm_linear_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNotNull(resultPredictor, "Method returns null");
        assertTrue(resultPredictor.getClass() == SVMPredictor.class, "Dynamic type of returned object is different from LRPredictor");
    }
    @Test
    public void testGetPredictorWithCorrectInputWhatReturnsSVMPredictorPolynomialKernel(){
        String predictorNmae = "z_svm_poly_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorNmae);

        assertNotNull(resultPredictor, "Method returns null");
        assertTrue(resultPredictor.getClass() == SVMPredictor.class, "Dynamic type of returned object is different from LRPredictor");
    }
    @Test
    public void testGetPredictorWithCorrectInputWhatReturnsSVMPredictorRBFKernel(){
        String predictorNmae = "z_svm_rbf_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorNmae);

        assertNotNull(resultPredictor, "Method returns null");
        assertTrue(resultPredictor.getClass() == SVMPredictor.class, "Dynamic type of returned object is different from LRPredictor");
    }
    @Test
    public void testGetPredictorWithNonExistentPredictorName(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setErr(new PrintStream(outputResult));
        String predictorName = "lr_aut";
        String expectedOutput = "Description file of predictor \"" + predictorName + "\" is not found" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNull(resultPredictor, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Disabled
    @Test
    public void testGetPredictorWithNonExistentTypeOfModel(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String predictorName = "lr_auto";
        String expectedOutput = "Description file contain unmanageable model type" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNull(resultPredictor, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Disabled
    @Test
    public void testGetPredictorWhenTypeOfModelIsNotPresentInDescriptionFile(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String predictorName = "lr_auto";
        String expectedOutput = "Description file does not contain information on model type" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNull(resultPredictor, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Disabled
    @Test
    public void testGetPredictorWithIncorrectStructureOfDescriptionFile(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String predictorName = "lr_auto";
        String expectedOutput = "Description file has wrong structure" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNull(resultPredictor, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
    @Disabled
    @Test
    public void testGetPredictorWithIncorrectSyntaxOfDescriptionFile(){
        ByteArrayOutputStream outputResult = new ByteArrayOutputStream();
        String nl = System.lineSeparator();
        System.setOut(new PrintStream(outputResult));
        String predictorName = "lr_auto";
        String expectedOutput = "Syntax of description file is incorrect" + nl;

        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);

        assertNull(resultPredictor, "Method does not return null");
        assertEquals(expectedOutput, outputResult.toString(), "Method output is incorrect");
    }
}




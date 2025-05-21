package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;
/*
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
*/


public class LRTrainerTest {

  @Test
  public void testLRTrainerConstructorWithCorrectInput() {
    String predictorName = "LR_predictor_test";
    int version = 1;
    String lastTrain = "2024-07-15";
    int trainingExpiration = 15;
    String trainingDataTableName = "training_table";
    String[] trainingFieldNamesList = {"feature1", "feature2"};
    String classificationField = "target";

    double[][] xTx = null;
    double[][] xTy = null;
    double[] parametersLR = null;

    LRTrainer trainer = new LRTrainer(
      predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
      trainingDataTableName, trainingFieldNamesList, classificationField);

    try{
      assertEquals(predictorName, getField(trainer, "predictorName", MLTrainer.class));
      assertEquals("linear_regression", getField(trainer, "trainingModelType", MLTrainer.class));
      assertEquals(version, getField(trainer, "version", MLTrainer.class));
      assertEquals(lastTrain, getField(trainer, "lastTrain", MLTrainer.class));
      assertEquals(trainingExpiration, getField(trainer, "trainingExpiration", MLTrainer.class));
      assertEquals(trainingDataTableName, getField(trainer, "trainingDataTableName", MLTrainer.class));
      assertArrayEquals(trainingFieldNamesList, (String[]) getField(trainer, "trainingFieldNamesList", MLTrainer.class));
      assertEquals(classificationField, getField(trainer, "classificationField", MLTrainer.class));

      assertEquals(xTx, getField(trainer, "transposeOfXTimesX", LRTrainer.class));
      assertEquals(xTy, getField(trainer, "transposeOfXTimesY", LRTrainer.class));
      assertEquals(parametersLR, getField(trainer, "parametersLR", LRTrainer.class));
    }
    catch (NoSuchFieldException | IllegalAccessException e) {
      fail("Error while accessing fields using reflection: " + e.getMessage());
    }
  }
  private Object getField(Object obj, String fieldName, Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(obj);
  }

  @Test
  public void testGetDescriptionFileDataWithCorrectInput() {
    String predictorName = "LR_trainer_test_" + UUID.randomUUID().toString().substring(0, 8);
    String folderPath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        boolean fileExists = true;
        while (fileExists) {
          fileExists = false;
          for (File file : files) {
            if (file.isFile() && file.getName().equals(predictorName + ".json")) {
              fileExists = true;
            }
          }
          if (fileExists) {
            predictorName = "LR_trainer_" + UUID.randomUUID().toString().substring(0, 8);
          }
        }
      }
      else {
        System.err.println("Nessun file trovato nella cartella.");
      }
    }
    else {
      System.err.println("La cartella " + folderPath + " non esiste" );
    }

    File file = new File(folderPath + predictorName + ".json");
    try {
      if (file.createNewFile()) {
        JsonObject rootObject = new JsonObject();
        rootObject.add("trainer", new JsonPrimitive("LR_trainer_test"));
        rootObject.add("id", new JsonPrimitive(15342));
        JsonArray dataArray = new JsonArray();
        JsonObject dataItem1 = new JsonObject();
        dataItem1.add("id", new JsonPrimitive(1) );
        dataItem1.add("name", new JsonPrimitive("Feature A"));
        dataItem1.add("value", new JsonPrimitive(0.45));
        dataArray.add(dataItem1);
        rootObject.add("data", dataArray);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(folderPath + predictorName + ".json")) {
          gson.toJson(rootObject, writer);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        System.out.println("The file already exists");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    try{
      Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
      method.setAccessible(true);
      int version = 1;
      String lastTrain = "2024-07-15";
      int trainingExpiration = 15;
      String trainingDataTableName = "training_table";
      String[] trainingFieldNamesList = {"feature1", "feature2"};
      String classificationField = "target";

      double[][] xTx = null;
      double[][] xTy = null;
      double[] parametersLR = null;

      LRTrainer lrTrainerInstance = new LRTrainer(
        predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
        trainingDataTableName, trainingFieldNamesList, classificationField
      );

      JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, predictorName);

      assertNotNull(result, "The result should not be null");
      assertTrue(result.isJsonObject(), "The result should be a JsonObject");
      assertEquals("LR_trainer_test", result.get("trainer").getAsString(), "The value of 'name' is incorrect");
    }
    catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    finally {
      if(file.exists()){
        file.delete();
      }
    }
  }

  @Test
  public void testGetDescriptionFileDataWithNonExistentDescriptionFile() {
    String nonExistentPredictorName = "LR_trainer_test_" + UUID.randomUUID().toString().substring(0, 8);
    String folderPath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        boolean fileExists = true;
        while (fileExists) {
          fileExists = false;
          for (File file : files) {
            if (file.isFile() && file.getName().equals(nonExistentPredictorName + ".json")) {
              fileExists = true;
            }
          }
          if (fileExists) {
            nonExistentPredictorName = "LR_trainer_" + UUID.randomUUID().toString().substring(0, 8);
          }
        }
      }
    }
    else {
      System.err.println("La cartella " + folderPath + " non esiste" );
    }

    try{
      int version = 1;
      String lastTrain = "2024-07-15";
      int trainingExpiration = 15;
      String trainingDataTableName = "training_table";
      String[] trainingFieldNamesList = {"feature1", "feature2"};
      String classificationField = "target";
      double[][] xTx = null;
      double[][] xTy = null;
      double[] parametersLR = null;

      LRTrainer lrTrainerInstance = new LRTrainer(
        nonExistentPredictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
        trainingDataTableName, trainingFieldNamesList, classificationField
      );

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream originalErr = System.err;
      System.setErr(new PrintStream(outputStream));

      Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
      method.setAccessible(true);
      JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, nonExistentPredictorName);

      System.setErr(originalErr);

      assertNull(result, "The result should be null");
      assertEquals("Description file of predictor \""+ nonExistentPredictorName +"\" is not found", outputStream.toString().trim());
    }
    catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetDescriptionFileDataWithIncorrectSyntaxOfDescriptionFile() {
    String predictorName = "LR_trainer_test_" + UUID.randomUUID().toString().substring(0, 8);
    String folderPath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        boolean fileExists = true;
        while (fileExists) {
          fileExists = false;
          for (File file : files) {
            if (file.isFile() && file.getName().equals(predictorName + ".json")) {
              fileExists = true;
            }
          }
          if (fileExists) {
            predictorName = "LR_trainer_" + UUID.randomUUID().toString().substring(0, 8);
          }
        }
      }
      else {
        System.err.println("Nessun file trovato nella cartella.");
      }
    }
    else {
      System.err.println("La cartella " + folderPath + " non esiste" );
    }

    File file = new File(folderPath + predictorName + ".json");
    try {
      if (file.createNewFile()) {

        String jsonErrato = "{\n" +
          "  \"nome\": \"Mario,\n" +
          "  \"eta\": 30,\n" +
          "  \"città\": \"Roma\"\n" +
          "  indirizzo: \"Via Roma 1\"\n" +
          "}";

        try (FileWriter fileWriter  = new FileWriter(folderPath + predictorName + ".json")) {
          fileWriter.write(jsonErrato);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        System.out.println("The file already exists");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    try{
      int version = 1;
      String lastTrain = "2024-07-15";
      int trainingExpiration = 15;
      String trainingDataTableName = "training_table";
      String[] trainingFieldNamesList = {"feature1", "feature2"};
      String classificationField = "target";

      double[][] xTx = null;
      double[][] xTy = null;
      double[] parametersLR = null;

      LRTrainer lrTrainerInstance = new LRTrainer(
        predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
        trainingDataTableName, trainingFieldNamesList, classificationField
      );

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream originalErr = System.err;
      System.setErr(new PrintStream(outputStream));


      Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
      method.setAccessible(true);
      JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, predictorName);

      System.setErr(originalErr);

      assertNull(result, "The result should be null");
      assertEquals("Syntax of description file is incorrect", outputStream.toString().trim());
    }
    catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    finally {
      if(file.exists()){
        file.delete();
      }
    }
  }

  @Test
  public void testGetDescriptionFileDataWhenDescriptionFileContainsNonJsonObject() {
    String predictorName = "LR_trainer_test_" + UUID.randomUUID().toString().substring(0, 8);
    String folderPath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        boolean fileExists = true;
        while (fileExists) {
          fileExists = false;
          for (File file : files) {
            if (file.isFile() && file.getName().equals(predictorName + ".json")) {
              fileExists = true;
            }
          }
          if (fileExists) {
            predictorName = "LR_trainer_" + UUID.randomUUID().toString().substring(0, 8);
          }
        }
      }
      else {
        System.err.println("Nessun file trovato nella cartella.");
      }
    }
    else {
      System.err.println("La cartella " + folderPath + " non esiste" );
    }

    File file = new File(folderPath + predictorName + ".json");
    try {
      if (file.createNewFile()) {

        String jsonErrato = "[\n" +
          "  \"mele\",\n" +
          "  \"banane\",\n" +
          "  \"ciliegie\"\n" +
          "]";

        try (FileWriter fileWriter  = new FileWriter(folderPath + predictorName + ".json")) {
          fileWriter.write(jsonErrato);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        System.out.println("The file already exists");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    try{
      int version = 1;
      String lastTrain = "2024-07-15";
      int trainingExpiration = 15;
      String trainingDataTableName = "training_table";
      String[] trainingFieldNamesList = {"feature1", "feature2"};
      String classificationField = "target";

      double[][] xTx = null;
      double[][] xTy = null;
      double[] parametersLR = null;

      LRTrainer lrTrainerInstance = new LRTrainer(
        predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
        trainingDataTableName, trainingFieldNamesList, classificationField
      );

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream originalErr = System.err;
      System.setErr(new PrintStream(outputStream));


      Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
      method.setAccessible(true);
      JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, predictorName);

      System.setErr(originalErr);

      assertNull(result, "The result should be null");
      assertEquals("Description file has incorrect structure, reading failed", outputStream.toString().trim());
    }
    catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    finally {
      if(file.exists()){
        file.delete();
      }
    }
  }

  @Test
  public void testConvertToJsonArrayMatricesWithCorrectInput() {
    double[][] matrix = {
      {1.1, 2.2},
      {3.3, 4.4}
    };

    try {
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, matrix, matrix, new double[]{0.5, 1.5}, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("convertToJsonArrayMatrices", double[][].class);
      method.setAccessible(true);

      JsonArray result = (JsonArray) method.invoke(lrTrainer, (Object) matrix);

      assertNotNull(result, "The result should not be null");
      assertEquals(2, result.size(), "The JSON array should contain 2 rows");

      JsonArray row1 = result.get(0).getAsJsonArray();
      assertEquals(2, row1.size(), "The first row should contain 2 elements");
      assertEquals(1.1, row1.get(0).getAsDouble(), "The first value of the first row should be 1.1");
      assertEquals(2.2, row1.get(1).getAsDouble(), "The second value of the first row should be 2.2");

      JsonArray row2 = result.get(1).getAsJsonArray();
      assertEquals(2, row2.size(), "The second row should contain 2 elements");
      assertEquals(3.3, row2.get(0).getAsDouble(), "The first value of the second row should be 3.3");
      assertEquals(4.4, row2.get(1).getAsDouble(), "The second value of the second row should be 4.4");

    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testConvertToJsonArrayParametersWithCorrectInput() {
    double[] parameters = {0.5, 1.5, -2.0};

    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, dummyMatrix, dummyMatrix, parameters, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("convertToJsonArrayParameters", double[].class);
      method.setAccessible(true);
      JsonArray result = (JsonArray) method.invoke(lrTrainer, (Object) parameters);

      assertNotNull(result, "The result should not be null");
      assertEquals(3, result.size(), "The JSON array should contain 3 elements");
      assertEquals(0.5, result.get(0).getAsDouble(), "First parameter should be 0.5");
      assertEquals(1.5, result.get(1).getAsDouble(), "Second parameter should be 1.5");
      assertEquals(-2.0, result.get(2).getAsDouble(), "Third parameter should be -2.0");
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testConvertToJsonArrayFieldNamesListWithCorrectInput() {
    String[] fieldNames = {"feature1", "feature2", "feature3"};

    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer( "testPredictor", 1, "2025-05-21 10:00:00",
        30, dummyMatrix, dummyMatrix, new double[]{0.5, 1.5}, "trainingData",
        fieldNames, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("convertToJsonArrayFieldNamesList", String[].class);
      method.setAccessible(true);
      JsonArray result = (JsonArray) method.invoke(lrTrainer, (Object) fieldNames);

      assertNotNull(result, "The result should not be null");
      assertEquals(3, result.size(), "The JSON array should contain 3 elements");
      assertEquals("feature1", result.get(0).getAsString(), "First field should be 'feature1'");
      assertEquals("feature2", result.get(1).getAsString(), "Second field should be 'feature2'");
      assertEquals("feature3", result.get(2).getAsString(), "Third field should be 'feature3'");

    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testGetXtXMatrixFromJsonWithCorrectInput() {
    JsonArray matrixArray = new JsonArray();
    JsonArray row1 = new JsonArray();
    row1.add(1.0);
    row1.add(2.0);
    JsonArray row2 = new JsonArray();
    row2.add(3.0);
    row2.add(4.0);
    matrixArray.add(row1);
    matrixArray.add(row2);
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("xTx", matrixArray);

    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer("testPredictor",1, "2025-05-21 10:00:00",
        30, dummyMatrix, dummyMatrix, new double[]{0.5, 1.5}, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("getXtXMatrixFromJson", JsonObject.class);
      method.setAccessible(true);
      double[][] result = (double[][]) method.invoke(lrTrainer, jsonObject);

      assertNotNull(result, "The result should not be null");
      assertEquals(2, result.length, "The matrix should have 2 rows");
      assertEquals(2, result[0].length, "Each row should have 2 elements");
      assertEquals(1.0, result[0][0]);
      assertEquals(2.0, result[0][1]);
      assertEquals(3.0, result[1][0]);
      assertEquals(4.0, result[1][1]);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testGetXtXMatrixFromJsonWithJsonObjectThatNotContainXTxField() {
    JsonArray matrixArray = new JsonArray();
    JsonArray row1 = new JsonArray();
    row1.add(1.0);
    row1.add(2.0);
    JsonArray row2 = new JsonArray();
    row2.add(3.0);
    row2.add(4.0);
    matrixArray.add(row1);
    matrixArray.add(row2);
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("aaa", matrixArray);

    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer("testPredictor",1, "2025-05-21 10:00:00",
        30, dummyMatrix, dummyMatrix, new double[]{0.5, 1.5}, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream originalErr = System.err;
      System.setErr(new PrintStream(outputStream));

      Method method = LRTrainer.class.getDeclaredMethod("getXtXMatrixFromJson", JsonObject.class);
      method.setAccessible(true);
      double[][] result = (double[][]) method.invoke(lrTrainer, jsonObject);

      System.setErr(originalErr);

      assertNull(result, "The result should be null");
      assertEquals("Description file does not contain xTx field", outputStream.toString().trim());
    }
    catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
      ex.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testGetXtYMatrixFromJsonWithCorrectInput() {
    JsonArray xTyArray = new JsonArray();
    xTyArray.add(5.0);
    xTyArray.add(10.0);
    xTyArray.add(15.0);

    JsonObject jsonObject = new JsonObject();
    jsonObject.add("xTy", xTyArray);

    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, dummyMatrix, dummyMatrix, new double[]{0.5, 1.5}, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("getXtYMatrixFromJson", JsonObject.class);
      method.setAccessible(true);
      double[][] result = (double[][]) method.invoke(lrTrainer, jsonObject);

      assertNotNull(result, "The result should not be null");
      assertEquals(3, result.length, "The matrix should have 3 rows");
      assertEquals(1, result[0].length, "Each row should have 1 element");
      assertEquals(5.0, result[0][0]);
      assertEquals(10.0, result[1][0]);
      assertEquals(15.0, result[2][0]);

    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testGetXtYMatrixFromJsonWithJsonObjectThatNotContainXTxField() {
    JsonArray xTyArray = new JsonArray();
    xTyArray.add(5.0);
    xTyArray.add(10.0);
    xTyArray.add(15.0);

    JsonObject jsonObject = new JsonObject();
    jsonObject.add("aaa", xTyArray);

    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, dummyMatrix, dummyMatrix, new double[]{0.5, 1.5}, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream originalErr = System.err;
      System.setErr(new PrintStream(outputStream));

      Method method = LRTrainer.class.getDeclaredMethod("getXtYMatrixFromJson", JsonObject.class);
      method.setAccessible(true);
      double[][] result = (double[][]) method.invoke(lrTrainer, jsonObject);

      System.setErr(originalErr);

      assertNull(result, "The result should be null");
      assertEquals("Description file does not contain xTy field", outputStream.toString().trim());
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }
  //////////////////////////////////////////////////////////////////////////////
  @Test
  public void testAddObservationWithCorrectInput() {
    try {
      double[][] xTx = {
        {4.5, 2.7, 0.0},
        {2.6, 5.1, 1.0},
        {0.0, 1.0, 3.0}
      };

      double[][] xTy = {
        {2.0},
        {3.0},
        {1.0}
      };

      double[] sampleX = {1.0, 2.0, 3.0};
      double[] sampleY = {6.0};

      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00", 30, xTx, xTy,
        new double[]{0.0, 0.0, 0.0}, "trainingData", new String[]{"f1", "f2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("addObservation", double[].class, double[].class);
      method.setAccessible(true);


      double[][] expectedXtX = new double[3][3];
      double[][] expectedXtY = new double[3][1];

      // Compute expected updated XtX and XtY
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          expectedXtX[i][j] = xTx[i][j] + sampleX[i] * sampleX[j];
        }
        expectedXtY[i][0] = xTy[i][0] + sampleX[i] * sampleY[0];
      }

      method.invoke(lrTrainer, sampleX, sampleY);

      for (int i = 0; i < 3; i++) {
        assertArrayEquals(expectedXtX[i], xTx[i], 0.0001);
        assertArrayEquals(expectedXtY[i], xTy[i], 0.0001);
      }


      Field parametersField = LRTrainer.class.getDeclaredField("parametersLR");
      parametersField.setAccessible(true);
      double[] resultedParams = (double[]) parametersField.get(lrTrainer);
      double[] expectedParams = {0.3080082135523621, 0.6057494866529765, 1.1529774127310064};


        assertEquals(3, resultedParams.length);
        assertArrayEquals(expectedParams, resultedParams);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred in testAddObservationWithRandom3x3Matrix: " + e.getMessage());
    }
  }

  /*
  @Test
  public void testAddObservationWithInitialValues() {
    try {
      double[][] xTx = {
        {1, 0.5, 0.2},
        {0.5, 2, 0.3},
        {0.2, 0.3, 3}
      };

      double[][] xTy = {
        {1},
        {2},
        {3}
      };

      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, xTx, xTy, new double[]{0.0, 0.0, 0.0}, "trainingData",
        new String[]{"feature1", "feature2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("addObservation", double[].class, double[].class);
      method.setAccessible(true);

      double[] x = {1, 2, 3};
      double[] y = {4};

      method.invoke(lrTrainer, x, y);

      double[][] expectedXTx = {
        {1.0 + 1.0, 0.5 + 2.0, 0.2 + 3.0},
        {0.5 + 2.0, 2.0 + 4.0, 0.3 + 6.0},
        {0.2 + 3.0, 0.3 + 6.0, 3.0 + 9.0}
      };

      double[][] expectedXTy = {
        {5},
        {10},
        {15}
      };

      for (int i = 0; i < expectedXTx.length; i++) {
        assertArrayEquals(expectedXTx[i], xTx[i], 0.0001);
      }

      for (int i = 0; i < expectedXTy.length; i++) {
        assertArrayEquals(expectedXTy[i], xTy[i], 0.0001);
      }

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception during testAddObservationWithInitialValues: " + e.getMessage());
    }
  }
  */
  /////////////////////////////////////////////////////////////////////////////
  @Test
  public void testCalculateCoefficientsWithCorrectInput() {
    try {
      double[][] xTx = {
        {4.5, 2.7, 0.0},
        {2.6, 5.1, 1.0},
        {0.0, 1.0, 3.0}
      };

      double[][] xTy = {
        {2.0},
        {3.0},
        {1.0}
      };

      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00", 30, xTx, xTy, new double[]{0.0, 0.0, 0.0}, "trainingData", new String[]{"f1", "f2", "f3"}, "target");

      Method method = LRTrainer.class.getDeclaredMethod("calculateCoefficients");
      method.setAccessible(true);
      double[] result = (double[]) method.invoke(lrTrainer);

      double[] expected = {0.16170016170016147, 0.4712404712404713, 0.17625317625317616};

      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], result[i], 0.0001);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception during testCalculateCoefficientsWith3x3Matrix: " + e.getMessage());
    }
  }

  @Test
  public void testMultiplyWithCorrectInput() {
    try {
      double[][] xTx = new double[2][2];
      double[][] xTy = new double[2][1];

      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00", 30, xTx, xTy, new double[]{0.0, 0.0}, "trainingData", new String[]{"feature1"}, "target");

      Method method = LRTrainer.class.getDeclaredMethod("multiply", double[][].class, double[][].class);
      method.setAccessible(true);

      double[][] lhs = {
        {1.0, 2.0},
        {3.0, 4.0}
      };

      double[][] rhs = {
        {5.0},
        {6.0}
      };

      double[][] expected = {
        {17.0},
        {39.0}
      };

      double[][] result = (double[][]) method.invoke(lrTrainer, lhs, rhs);

      for (int i = 0; i < expected.length; i++) {
        assertArrayEquals(expected[i], result[i], 0.0001);
      }

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception during testMultiply: " + e.getMessage());
    }
  }

  @Test
  public void testInverseWithCorrectInput() {
    try {
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00", 30, new double[][]{{1.0, 0.0}, {0.0, 1.0}}, new double[][]{{1.0, 0.0}, {0.0, 1.0}}, new double[]{0.5, 1.5}, "trainingData", new String[]{"feature1", "feature2"}, "target");

      Method method = LRTrainer.class.getDeclaredMethod("inverse", double[][].class, double[][].class);
      method.setAccessible(true);

      double[][] matrix = {
        {4.0, 7.0},
        {2.0, 6.0}
      };

      double[][] identity = {
        {1.0, 0.0},
        {0.0, 1.0}
      };

      double[][] result = (double[][]) method.invoke(lrTrainer, matrix, identity);

      assertEquals(0.6, result[0][0], 0.0000001);
      assertEquals(-0.7, result[0][1], 0.0000001);
      assertEquals(-0.2, result[1][0], 0.0000001);
      assertEquals(0.4, result[1][1], 0.0000001);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testRrefWithCorrectInput() {
    try {
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, new double[][]{{1.0, 0.0}, {0.0, 1.0}}, new double[][]{{1.0, 0.0}, {0.0, 1.0}},
        new double[]{0.5, 1.5}, "trainingData", new String[]{"feature1", "feature2"},
        "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("rref", double[][].class);
      method.setAccessible(true);
      double[][] matrix = {
        {1, 2, 3},
        {2, 4, 11},
        {1, 2, 3}
      };

      double[][] result = (double[][]) method.invoke(lrTrainer, (Object) matrix);

      for (int i = 0; i < result.length; i++) {
        for (int j = 0; j < result[i].length; j++) {
          if (Double.compare(result[i][j], -0.0) == 0) {
            result[i][j] = 0.0;
          }
        }
      }

      assertArrayEquals(new double[]{1, 2, 0}, result[0], 1e-9);
      assertArrayEquals(new double[]{0, 0, 1}, result[1], 1e-9);
      assertArrayEquals(new double[]{0, 0, 0}, result[2], 1e-9);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testAddRowAndColumnWithCorrectInput() {
    try {
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, new double[][]{{1.0, 0.0}, {0.0, 1.0}}, new double[][]{{1.0, 0.0}, {0.0, 1.0}},
        new double[]{0.5, 1.5}, "trainingData", new String[]{"feature1", "feature2"},
        "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("addRowAndColumn", double[][].class, double[].class, double[].class);
      method.setAccessible(true);

      double[][] xTx = {{0.0, 0.0}, {0.0, 0.0}};
      double[] lhsColumn = {1.0, 2.0};
      double[] rhsRow = {3.0, 4.0};

      method.invoke(lrTrainer, xTx, lhsColumn, rhsRow);

      assertEquals(3.0, xTx[0][0]);
      assertEquals(4.0, xTx[0][1]);
      assertEquals(6.0, xTx[1][0]);
      assertEquals(8.0, xTx[1][1]);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testIdentityMatrixWithCorrectInput() {
    try {
      double[][] dummyMatrix = {{1.0, 0.0}, {0.0, 1.0}};
      LRTrainer lrTrainer = new LRTrainer(
        "testPredictor", 1, "2025-05-21 10:00:00", 30, dummyMatrix,
        dummyMatrix, new double[]{0.5, 1.5}, "trainingData", new String[]{"feature1", "feature2"},
        "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("identityMatrix", int.class);
      method.setAccessible(true);

      double[][] result = (double[][]) method.invoke(lrTrainer, 3);

      assertNotNull(result, "The result should not be null");
      assertEquals(3, result.length, "The matrix should have 3 rows");
      assertEquals(3, result[0].length, "Each row should have 3 elements");
      assertEquals(1.0, result[0][0]);
      assertEquals(0.0, result[0][1]);
      assertEquals(0.0, result[0][2]);
      assertEquals(0.0, result[1][0]);
      assertEquals(1.0, result[1][1]);
      assertEquals(0.0, result[1][2]);
      assertEquals(0.0, result[2][0]);
      assertEquals(0.0, result[2][1]);
      assertEquals(1.0, result[2][2]);

    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }

  @Test
  public void testRectMatrixWithCorrectInput() {
    try {
      LRTrainer lrTrainer = new LRTrainer("testPredictor", 1, "2025-05-21 10:00:00",
        30, new double[][]{{1.0, 0.0}, {0.0, 1.0}}, new double[][]{{1.0, 0.0}, {0.0, 1.0}},
        new double[]{0.5, 1.5}, "trainingData", new String[]{"feature1", "feature2"}, "target"
      );

      Method method = LRTrainer.class.getDeclaredMethod("rectMatrix", int.class, int.class);
      method.setAccessible(true);
      double[][] result = (double[][]) method.invoke(lrTrainer, 3, 4);

      assertNotNull(result, "The result should not be null");
      assertEquals(3, result.length, "The matrix should have 3 rows");
      assertEquals(4, result[0].length, "Each row should have 4 columns");

      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 4; j++) {
          assertEquals(0.0, result[i][j]);
        }
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Exception occurred during test: " + e.getMessage());
    }
  }



////////////////////////////////////////////////////////////////////////////////////////////
  /* FUNZIONA ESCLUSO WINDOWS, DA GESTIRE DENTRO TEST throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException
    @Test
  void testUnreadableFile_WindowsCompatible() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    String predictorName = "unreadableTestFile";
    String baseDir = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    String filePathStr = baseDir + predictorName + ".json";

    // 1. Crea la directory
    File dir = new File(baseDir);
    if (!dir.exists()) {
      assertTrue(dir.mkdirs(), "Creazione della directory fallita");
    }

    // 2. Crea il file
    File file = new File(filePathStr);
    Files.writeString(file.toPath(), "{\"some\": \"data\"}");

    // 3. Rende il file NON leggibile
    boolean result = file.setReadable(false);
    if (!result) {
      System.out.println("⚠️ Impossibile rimuovere il permesso di lettura (forse non supportato). Il test potrebbe non essere valido.");
    }

    // 4. Chiama il metodo
    int version = 1;
    String lastTrain = "2024-07-15";
    int trainingExpiration = 15;
    String trainingDataTableName = "training_table";
    String[] trainingFieldNamesList = {"feature1", "feature2"};
    String classificationField = "target";

    double[][] xTx = null;
    double[][] xTy = null;
    double[] parametersLR = null;

    LRTrainer lrTrainerInstance = new LRTrainer(
      predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
      trainingDataTableName, trainingFieldNamesList, classificationField
    );

    Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
    method.setAccessible(true);
    JsonObject methodResult = (JsonObject) method.invoke(lrTrainerInstance, predictorName);

    // 5. Controlla che il metodo ritorni null
    assertNull(methodResult, "Il file non dovrebbe essere leggibile, quindi il metodo dovrebbe restituire null");

    // 6. Pulizia (riabilita lettura per poter cancellare il file)
    file.setReadable(true);
    Files.deleteIfExists(file.toPath());
  }
  */
  /* !!!!!!!! USA LIBRERIE OBSOLETE CHE BLOCCANO ALTRE CHE MI SERVONO, NON FUNZIONA
  @Test
  public void testIOExceptionDuringFileReading() throws Exception {
    // Arrange
    String predictorName = "dummy_predictor";

    // Mock FileReader to throw IOException
    FileReader mockReader = mock(FileReader.class);
    when(mockReader.read()).thenThrow(new IOException("Simulated IO error"));

    // Prepare PowerMockito to intercept new FileReader(...)
    mockStatic(FileReader.class);
    whenNew(FileReader.class).withAnyArguments().thenThrow(new IOException("Simulated IO error"));

    int version = 1;
    String lastTrain = "2024-07-15";
    int trainingExpiration = 15;
    String trainingDataTableName = "training_table";
    String[] trainingFieldNamesList = {"feature1", "feature2"};
    String classificationField = "target";

    double[][] xTx = null;
    double[][] xTy = null;
    double[] parametersLR = null;

    LRTrainer lrTrainerInstance = new LRTrainer(
      predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
      trainingDataTableName, trainingFieldNamesList, classificationField
    );

    Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
    method.setAccessible(true);
    JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, predictorName);

    // Assert
    assertNull(result); // metodo dovrebbe restituire null in caso di IOException
  }
*/
  /*  !!! SI PUO ELIMINARE
  @Test
  public void testJsonIOExceptionHandledGracefully() throws Exception {
    String predictorName = "LR_trainer_test_" + UUID.randomUUID().toString().substring(0, 8);
    String folderPath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        boolean fileExists = true;
        while (fileExists) {
          fileExists = false;
          for (File file : files) {
            if (file.isFile() && file.getName().equals(predictorName + ".json")) {
              fileExists = true;
            }
          }
          if (fileExists) {
            predictorName = "LR_trainer_" + UUID.randomUUID().toString().substring(0, 8);
          }
        }
      }
    }
    else {
      System.err.println("La cartella " + folderPath + " non esiste" );
    }

    File file = new File(folderPath + predictorName + ".json");
    try {
      if (file.createNewFile()) {
        JsonObject rootObject = new JsonObject();
        rootObject.add("trainer", new JsonPrimitive("LR_trainer_test"));
        rootObject.add("id", new JsonPrimitive(15342));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(folderPath + predictorName + ".json")) {
          gson.toJson(rootObject, writer);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        System.out.println("The file already exists");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    try (MockedStatic<JsonParser> mockedParser = mockStatic(JsonParser.class);
         FileReader reader = new FileReader(folderPath + predictorName + ".json")
    ) {
      mockedParser.when(() -> JsonParser.parseReader(reader)).thenThrow(new JsonIOException("Simulated JsonIOException"));

      int version = 1;
      String lastTrain = "2024-07-15";
      int trainingExpiration = 15;
      String trainingDataTableName = "training_table";
      String[] trainingFieldNamesList = {"feature1", "feature2"};
      String classificationField = "target";

      double[][] xTx = null;
      double[][] xTy = null;
      double[] parametersLR = null;

      LRTrainer lrTrainerInstance = new LRTrainer(
        predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
        trainingDataTableName, trainingFieldNamesList, classificationField
      );

      Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
      method.setAccessible(true);
      JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, predictorName);
      assertNull(result);
    }
    if(file.exists()){ //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      file.delete();
    }
  }
  */
  /*
  @Test //!!!!!!!!!!!!!!!!!! NON FUNZIONA
  public void testeDescriptionFileDataWhenLaunchedJsonIOException() throws Exception {
    String predictorName = "LR_trainer_test_" + UUID.randomUUID().toString().substring(0, 8);
    String folderPath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/";
    File folder = new File(folderPath);

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        boolean fileExists = true;
        while (fileExists) {
          fileExists = false;
          for (File file : files) {
            if (file.isFile() && file.getName().equals(predictorName + ".json")) {
              fileExists = true;
            }
          }
          if (fileExists) {
            predictorName = "LR_trainer_" + UUID.randomUUID().toString().substring(0, 8);
          }
        }
      }
    } else {
      System.err.println("La cartella " + folderPath + " non esiste");
    }

    // Crea il file JSON se non esiste
    File file = new File(folderPath + predictorName + ".json");
    try {
      if (file.createNewFile()) {
        JsonObject rootObject = new JsonObject();
        rootObject.add("trainer", new JsonPrimitive("LR_trainer_test"));
        rootObject.add("id", new JsonPrimitive(15342));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(folderPath + predictorName + ".json")) {
          gson.toJson(rootObject, writer);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("The file already exists");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Configura il mock per l'eccezione
    try (MockedStatic<JsonParser> mockedParser = mockStatic(JsonParser.class);
         FileReader reader = new FileReader(folderPath + predictorName + ".json")
    ) {
      // Simula il lancio di un JsonIOException
      mockedParser.when(() -> JsonParser.parseReader(reader)).thenThrow(new JsonIOException("Simulated JsonIOException"));

      // Instanza del LRTrainer
      int version = 1;
      String lastTrain = "2024-07-15";
      int trainingExpiration = 15;
      String trainingDataTableName = "training_table";
      String[] trainingFieldNamesList = {"feature1", "feature2"};
      String classificationField = "target";

      double[][] xTx = null;
      double[][] xTy = null;
      double[] parametersLR = null;

      LRTrainer lrTrainerInstance = new LRTrainer(
        predictorName, version, lastTrain, trainingExpiration, xTx, xTy, parametersLR,
        trainingDataTableName, trainingFieldNamesList, classificationField
      );

      // Invoca il metodo con il mock
      Method method = LRTrainer.class.getDeclaredMethod("getDescriptionFileData", String.class);
      method.setAccessible(true);
      JsonObject result = (JsonObject) method.invoke(lrTrainerInstance, predictorName);

      // Verifica il comportamento
      assertNull(result);
    }

    // Elimina il file creato per il test
    if (file.exists()) {
      file.delete();
    }
  }
  */

}
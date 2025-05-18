package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.ABCTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ABCTrainerTest {

  @Test
  public void testTrainMethodWithNONExistentDataTableName(){
    String dataBaseURL = "jdbc:h2:mem:test_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    String username = "";
    String password = "";

    String dataTableName = "abc_trainer";
    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));
    try {
      Connection conn = DriverManager.getConnection(dataBaseURL, username, password);
      Statement stmt = conn.createStatement();

      String createTable = "DROP TABLE IF EXISTS " + dataTableName + ";"+
        "CREATE TABLE " + dataTableName + " (" +
            " id INT PRIMARY KEY," +
            "totale DOUBLE);";
      stmt.execute(createTable);
      String insertValues = "INSERT INTO " + dataTableName + " (id, totale) VALUES " +
        "(1, 7), " +
        "(2, 13) "/*, " +
        "(3, 2), " +
        "(4, 19), " +
        "(5, 5), " +
        "(6, 11), " +
        "(7, 8), " +
        "(8, 4), " +
        "(9, 16), " +
        "(10, 3), " +
        "(11, 1), " +
        "(12, 12), " +
        "(13, 17), " +
        "(14, 6), " +
        "(15, 10), " +
        "(16, 14), " +
        "(17, 18), " +
        "(18, 9), " +
        "(19, 15), " +
        "(20, 20);"*/;
      stmt.execute(insertValues);

      DataBaseConnecter dbConnecter = new DataBaseConnecter(dataBaseURL, username, password);

      String[] dataTableFieldNamesList = {"   id "};
      String classificationFieldName = "  totale";

      String predictorName = "ABC_predictor";
      int version = 5;
      String lastTrain = "2025-02-05";
      int trainingExpiration = 12;
      String predictionTableName = "abc_predictor";
      double boundA = 0.7;
      double boundB = 0.85;

      //stmt.execute("DROP TABLE IF EXISTS " + predictionTableName);
      MLTrainer abcTrainer = new ABCTrainer(predictorName, version, lastTrain, trainingExpiration, dataTableName, dataTableFieldNamesList, classificationFieldName, predictionTableName, boundA, boundB);
      boolean isTrained = abcTrainer.train("AAAAA", dataTableFieldNamesList, classificationFieldName, dbConnecter);
      System.setErr(System.err);

      String log = errContent.toString();
      System.out.println("uuuuuuuuuuuuuuuu"+log);
      assertFalse(isTrained);
      //rs.close();
      stmt.close();
      conn.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTrainMethodWithCorrectInput(){
    String dataBaseURL = "jdbc:h2:mem:test_db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
    String username = "";
    String password = "";

    try {
      Connection conn = DriverManager.getConnection(dataBaseURL, username, password);
      Statement stmt = conn.createStatement();

      String createTable = """
        DROP TABLE IF EXISTS abc_trainer;
        CREATE TABLE abc_trainer (
            id INT PRIMARY KEY,
            totale DOUBLE
        );
        """;
      stmt.execute(createTable);
      String insertValues = "INSERT INTO abc_trainer (id, totale) VALUES " +
        "(1, 7), " +
        "(2, 13), " +
        "(3, 2), " +
        "(4, 19), " +
        "(5, 5), " +
        "(6, 11), " +
        "(7, 8), " +
        "(8, 4), " +
        "(9, 16), " +
        "(10, 3), " +
        "(11, 1), " +
        "(12, 12), " +
        "(13, 17), " +
        "(14, 6), " +
        "(15, 10), " +
        "(16, 14), " +
        "(17, 18), " +
        "(18, 9), " +
        "(19, 15), " +
        "(20, 20);";
      stmt.execute(insertValues);

      DataBaseConnecter dbConnecter = new DataBaseConnecter(dataBaseURL, username, password);
      String dataTableName = "abc_trainer";
      String[] dataTableFieldNamesList = {"   id "};
      String classificationFieldName = "  totale";

      String predictorName = "ABC_predictor";
      int version = 5;
      String lastTrain = "2025-02-05";
      int trainingExpiration = 12;
      String predictionTableName = "abc_predictor";
      double boundA = 0.7;
      double boundB = 0.85;

      //stmt.execute("DROP TABLE IF EXISTS " + predictionTableName);
      MLTrainer abcTrainer = new ABCTrainer(predictorName, version, lastTrain, trainingExpiration, dataTableName, dataTableFieldNamesList, classificationFieldName, predictionTableName, boundA, boundB);
      boolean isTrained = abcTrainer.train(dataTableName, dataTableFieldNamesList, classificationFieldName, dbConnecter);

      Map<Integer, String> expectedValues = new HashMap<>();
      expectedValues.put(1, "B");
      expectedValues.put(2, "A");
      expectedValues.put(3, "C");
      expectedValues.put(4, "A");
      expectedValues.put(5, "C");
      expectedValues.put(6, "A");
      expectedValues.put(7, "B");
      expectedValues.put(8, "C");
      expectedValues.put(9, "A");
      expectedValues.put(10, "C");
      expectedValues.put(11, "C");
      expectedValues.put(12, "A");
      expectedValues.put(13, "A");
      expectedValues.put(14, "B");
      expectedValues.put(15, "A");
      expectedValues.put(16, "A");
      expectedValues.put(17, "A");
      expectedValues.put(18, "B");
      expectedValues.put(19, "A");
      expectedValues.put(20, "A");

      int resultRowCount = 0;
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + predictionTableName);
      while (rs.next()) {
        ++resultRowCount;
        int id = rs.getInt("id");
        String resultClass = rs.getString("class");
        String expectedClass = expectedValues.get(id);
        /*
        System.out.println(resultRowCount);
        System.out.println("ID: " + id + ", CLASS: " + resultClass);
        */
        assertEquals(expectedClass, resultClass, "Value of class with id " + id + " does not match expected");
      }
      assertTrue(isTrained);
      /*
      int expRecNum = expectedValues.size();
      assertEquals(expRecNum, resultRowCount, "Number of records expected is " + expRecNum + " and of the result is " + resultRowCount);
      */
      //rs.close();
      stmt.close();
      conn.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testABCTrainerConstructorWithCorrectInput() {
    String predictorName = "predictorX";
    int version = 1;
    String lastTrain = "2025-05-15";
    int trainingExpiration = 30;
    String trainingDataTableName = "training_table";
    String[] trainingFieldNamesList = {"feature1", "feature2"};
    String classificationField = "target";
    String predictionTableName = "prediction_table";
    double boundA = 0.1;
    double boundB = 0.9;

    ABCTrainer trainer = new ABCTrainer(
      predictorName, version, lastTrain, trainingExpiration,
      trainingDataTableName, trainingFieldNamesList, classificationField,
      predictionTableName, boundA, boundB
    );

    try {
      assertEquals(predictorName, getField(trainer, "predictorName", MLTrainer.class));
      assertEquals("abc", getField(trainer, "trainingModelType", MLTrainer.class));
      assertEquals(version, getField(trainer, "version", MLTrainer.class));
      assertEquals(lastTrain, getField(trainer, "lastTrain", MLTrainer.class));
      assertEquals(trainingExpiration, getField(trainer, "trainingExpiration", MLTrainer.class));
      assertEquals(trainingDataTableName, getField(trainer, "trainingDataTableName", MLTrainer.class));
      assertArrayEquals(trainingFieldNamesList, (String[]) getField(trainer, "trainingFieldNamesList", MLTrainer.class));
      assertEquals(classificationField, getField(trainer, "classificationField", MLTrainer.class));
      assertEquals(predictionTableName, getField(trainer, "predictionTableName", ABCTrainer.class));
      assertEquals(0.0, getField(trainer, "boundA", ABCTrainer.class));
      assertEquals(0.0, getField(trainer, "boundB", ABCTrainer.class));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      fail("Error while accessing fields using reflection: " + e.getMessage());
    }
  }
  private Object getField(Object obj, String fieldName, Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(obj);
  }
}

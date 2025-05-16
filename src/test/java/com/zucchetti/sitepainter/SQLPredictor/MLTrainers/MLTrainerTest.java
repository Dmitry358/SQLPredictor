package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class MLTrainerTest {

  private MLTrainer trainer;

  @BeforeEach
  public void setUp() {
    trainer = new MLTrainer(
      "TestPredictor",
      "ModelType",
      42,
      "2024-05-16 10:17:22",
      30,
      "training_table",
      new String[]{"field1", "field2"},
      "class_field"
    ) {
      @Override
      public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnetter) {
        return true;
      }
    };
  }

  @Test
  public void testIncrementVersion() {
    int initialVersion = trainer.getVersion(); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    trainer.incrementVersion();
    int incrementedVersion = trainer.getVersion(); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    assertEquals(initialVersion + 1, incrementedVersion);
  }
  @Test
  public void testSetLastTrain() {
    String expectedDate = "2025-05-16 10:00:00";
    trainer.setLastTrain(expectedDate);

    try {
      Field field = MLTrainer.class.getDeclaredField("lastTrain");
      field.setAccessible(true);
      String resultDate = (String) field.get(trainer);

      assertEquals(expectedDate, resultDate);
    }
    catch (NoSuchFieldException | IllegalAccessException e) {
      fail("Reflection error: " + e.getMessage());
    }
  }
  @Test
  public void testGetPredictorName() {
    String expectedPredictorName = "TestPredictor";
    String resultPredictorName = trainer.getPredictorName();
    assertEquals(expectedPredictorName, resultPredictorName);
  }
  @Test
  public void testGetVersion() {
    assertEquals(42, trainer.getVersion());
  }
  @Test
  public void testGetLastTrain() {
    String expectedLastTrain = "2024-05-16 10:17:22";
    String resultPredictorName = trainer.getLastTrain();
    assertEquals(expectedLastTrain, resultPredictorName);
  }
  @Test
  public void testGetTrainingExpiration() {
    int expectedTrainingExpiration = 30;
    int resultTrainingExpiration = trainer.getTrainingExpiration();
    assertEquals(expectedTrainingExpiration, resultTrainingExpiration);
  }
  @Test
  public void testGetTrainingDataTableName() {
    String expectedTrainingDataTableName = "training_table";
    String resultTrainingDataTableName = trainer.getTrainingDataTableName();
    assertEquals(expectedTrainingDataTableName, resultTrainingDataTableName);
  }
  @Test
  public void testGetTrainingFieldNamesList() {
    String[] expectedTrainingFieldNamesList = {"field1", "field2"};
    String[] resultTrainingFieldNamesList = trainer.getTrainingFieldNamesList();
    assertArrayEquals(expectedTrainingFieldNamesList, resultTrainingFieldNamesList);
  }
  @Test
  public void testGetClassificationField() {
    String expectedClassificationField = "class_field";
    String resultClassificationField = trainer.getClassificationField();
    assertEquals(expectedClassificationField, resultClassificationField);
  }
}


package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.ABCTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ABCTrainerTest {

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

package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MLTrainerTest {

  @Test
  public void testIncrementVersion() {
    MLTrainer trainer = new MLTrainer(
      "TestPredictor",
      "ModelType",
      4,
      null,
      30,
      "training_table",
      new String[]{"field1", "field2"},
      "target"
    ) {
      @Override
      public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnetter) {
        return true;
      }
    };

    int initialVersion = trainer.getVersion();
    trainer.incrementVersion();
    int incrementedVersion = trainer.getVersion();

    assertEquals(initialVersion + 1, incrementedVersion);
  }

  @Test
  public void testGetVersion() {
    // Arrange: creiamo un'istanza della classe astratta con classe anonima
    MLTrainer trainer = new MLTrainer(
      "TestPredictor",
      "ModelType",
      42,
      null,
      30,
      "training_table",
      new String[]{"field1", "field2"},
      "target"
    ) {
      @Override
      public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnetter) {
        return true;
      }
    };

    // Act
    //int version = ((MLTrainer) trainer).exposeGetVersion();
    int version = trainer.getVersion();

    // Assert
    assertEquals(42, version);
  }

}

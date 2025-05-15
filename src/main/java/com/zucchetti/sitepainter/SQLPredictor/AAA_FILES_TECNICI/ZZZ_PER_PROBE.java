package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI;


import com.zucchetti.sitepainter.SQLPredictor.MLSQLExpander;
import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainerBuilder;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ZZZ_PER_PROBE {
    public static void main(String[] args) {
// !!!!!!!!!!!!!!!!!!!!! SITUAZIONE QUANDO CREO OGETTO DI TRAINER MA NON FACCIO ALLENAMENTO, COSA FARE CON FILE JSON???
        String dataBaseURL = "jdbc:postgresql://localhost:5432/sqlpredictor_db";
        String username = "postgres";
        String password = "a";
        DataBaseConnecter dbConnecter = new DataBaseConnecter(dataBaseURL, username, password);
        String tabelName = "person";
        String[] fields = {"weight", "height"};
        String classField = "age";
        double[][] result = dbConnecter.getTrainingData(tabelName, fields, classField);

        System.out.println("Contenuto del database (codificato):");
        for (double[] row : result) {
            System.out.println(Arrays.toString(row));
        }

        //*///////////////////////////   ABC   ///////////////////////////////
        Map<String, String> trainerData = new HashMap<>();
        trainerData.put("predictorName", "  ABC_abc_trainer   ");
        trainerData.put("machineLearningModelType", "   abc  ");
        trainerData.put("trainingExpiration", "   18   ");

        trainerData.put("predictionTableName", "  abc_predictor ");
        trainerData.put("boundA", "  0.8 ");
        trainerData.put("boundB", "  0.95 ");

        String dataTableName = "   abc_trainer   ";
        String[] dataTableFieldNamesList = { "   id " };
        String classificationField = "  totale   ";

        MLTrainerBuilder trainerBuilder = new MLTrainerBuilder();
        MLTrainer ABCTrainerTest = trainerBuilder.build(trainerData, dataTableName, dataTableFieldNamesList, classificationField);

        ABCTrainerTest.train(dataTableName, dataTableFieldNamesList, classificationField, dbConnecter);

    }
}


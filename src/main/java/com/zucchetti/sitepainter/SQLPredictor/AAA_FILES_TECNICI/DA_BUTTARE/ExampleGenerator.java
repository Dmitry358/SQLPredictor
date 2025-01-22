package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.DA_BUTTARE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ExampleGenerator {
    /*
    public static void main(String[] args) {
        double coefX = 1;
        double coefY = 1;
        String tableName = "pazienti";
        String[] fieldNames = {"xxx", "yyy"};
        String fieldClassName = "ccc";
        ExampleGenerator updateDB = new ExampleGenerator();
        updateDB.updateDatabase(tableName, fieldNames, fieldClassName, coefX, coefY);
    }
    */

    public void updateDatabase(String trainingDataTableName, String[] fieldNames, String fieldClassName, double coefX, double coefY){
        String inputFileName = "input.csv";
        String outputFileName = "output.csv";
        //CSVProcessor.trasformCSVFromOrange(coefX, coefY, inputFileName, outputFileName);

        generateFullSQLInstruction(trainingDataTableName, fieldNames, fieldClassName);
    }

    public static void generateFullSQLInstruction(String trainingDataTableName, String[] fieldNames, String fieldClassName){
        int c=1;
        /*
        // lettura file output.csv riga per riga e aggiornamento database
        try {
            BufferedReader br = new BufferedReader(new FileReader(outputFilePath));
            int j=1;
            while ((lineOutput = br.readLine()) != null) {
                if(j > 3) {
                    String[] valori = lineOutput.split(",");
                    if(valori[0].equals("4")) updateQuery+= "UPDATE " + tableName + "\nSET " + fieldClassName + " = 4, ";
                    if(valori[0].equals("7")) updateQuery+= "UPDATE " + tableName + "\nSET " + fieldClassName + " = 7, ";

                    for (int i = 0; i < fieldsName.length; i++) {
                        updateQuery+= fieldsName[i] + " = " + valori[i+1];
                        if(i < fieldsName.length - 1) updateQuery+=", ";
                        else updateQuery+="\n";
                    }
                    updateQuery += ("WHERE id = " + (c) + ";\n");
                    c++;
                }
                j++;
            }
            br.close();
            //insertQuery = createInsertValuesSQLInstruction(tableName, fieldClassName, fieldsName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter writerUpdateSQLInstruction = new BufferedWriter(new FileWriter("C:/Users/NADIYA/Desktop/TESI/src/main/java/proba/utils/updateSQL.txt"));
            writerUpdateSQLInstruction.write(insertQuery);
            writerUpdateSQLInstruction.close();
        }
        catch (IOException e) {
            System.out.println("Errore scrittura del file: " + e.getMessage());
        }
        */

        String url = "jdbc:postgresql://localhost:5432/proba_db";
        String username = "postgres";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // CREAZIONE TABELLA DATI TRAINING
            String createTableSQLInstruction = generateCreateTableSQLInstruction(trainingDataTableName, fieldClassName, fieldNames);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableSQLInstruction);
            }
            // INSERIMENTO VALORI DATI TRAINING
            String insertSQL = generateInsertValuesSQLInstruction(trainingDataTableName, fieldNames, fieldClassName);
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                int rowsAffected = preparedStatement.executeUpdate();
            }
            // CREAZIONE TABELLA DI CLASSI
            String createClassTableSQLInstruction = generateCreateClassTableSQLInstruction(trainingDataTableName);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createClassTableSQLInstruction);
            }
            // INSERIMENTO VALORI IN TABELLA DI CLASSI
            String insertClassTableSQL = generateInsertValuesClassTableSQLInstruction(trainingDataTableName);
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertClassTableSQL)) {
                int rowsAffected = preparedStatement.executeUpdate();
            }
            // CREAZIONE TABELLA DI EXAMPLES
            String createExamplesTableSQLInstruction = generateCreateExamplesTableSQLInstruction(trainingDataTableName, fieldNames);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createExamplesTableSQLInstruction);
            }
            // INSERIMENTO VALORI IN TABELLA DI EXAMPLES
            String insertExamplesTableSQL = generateInsertValuesExamplesTableSQLInstruction(trainingDataTableName, fieldNames);
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertExamplesTableSQL)) {
                int rowsAffected = preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateCreateExamplesTableSQLInstruction(String trainingDataTableName, String[] fieldNames){
        String exampleTableName = trainingDataTableName + "_examples";
        String createExamplesTableQuery = "DROP TABLE IF EXISTS " + exampleTableName + " ;\n" +
                "CREATE TABLE " + exampleTableName + " (\n" +
                "    id BIGSERIAL PRIMARY KEY,\n";
        for(int i=0; i < fieldNames.length; ++i){
            createExamplesTableQuery += fieldNames[i] + " DECIMAL(10, 2) NOT NULL";
            if(i < fieldNames.length - 1) createExamplesTableQuery += ",\n";
            else createExamplesTableQuery += "\n);\n\n";
        }

        return createExamplesTableQuery;
    }

    public static String generateInsertValuesExamplesTableSQLInstruction(String trainingDataTableName, String[] fieldNames){
        String exampleTableName = trainingDataTableName + "_examples";

        String insertExanplesValuesQuery = "INSERT INTO " + exampleTableName +" (";
        for (int i=0; i < fieldNames.length; ++i){
            insertExanplesValuesQuery += fieldNames[i];
            if(i < fieldNames.length - 1) insertExanplesValuesQuery += ", ";
            else insertExanplesValuesQuery += ") VALUES\n";
        }

        insertExanplesValuesQuery += "(0.28, 0.49),"+
                                     "(0.32, 0.37),"+
                                     "(0.37, 0.33),"+
                                     "(0.45, 0.27);";

        return insertExanplesValuesQuery;
    }

    public static String generateCreateClassTableSQLInstruction(String trainingDataTableName){
        String classTableName = trainingDataTableName + "_class";
        String createClassTableQuery = "DROP TABLE IF EXISTS " + classTableName + " ;\n" +
                "CREATE TABLE " + classTableName + " (\n" +
                "    id BIGSERIAL PRIMARY KEY,\n" +
                "    class_value DECIMAL(14, 6) NOT NULL,\n" +
                "    class_label INTEGER NOT NULL);\n\n";
        return createClassTableQuery;
    }

    public static String generateInsertValuesClassTableSQLInstruction(String trainingDataTableName){
        String classTableName = trainingDataTableName + "_class";
        String insertClassTableQuery = "INSERT INTO " + classTableName +" (class_value, class_label) VALUES\n" +
                                       "(0 , 4),\n"+
                                       "( -1000000.99999, 7);\n";
        return insertClassTableQuery;
    }

    public static String generateCreateTableSQLInstruction(String trainingDataTableName, String fieldClassName, String[] fieldNames) {
        String createTableQuery = "DROP TABLE IF EXISTS " + trainingDataTableName + " ;\n" +
                "CREATE TABLE " + trainingDataTableName + " (\n" +
                "    id BIGSERIAL PRIMARY KEY,\n";
        for(int i=0; i < fieldNames.length; ++i){
            createTableQuery += fieldNames[i] + " DECIMAL(10, 5) NOT NULL,\n";
        }
        createTableQuery += fieldClassName + " DECIMAL(10, 5) NOT NULL\n"+");\n"+"\n";

        return createTableQuery;
    }

    public static String generateInsertValuesSQLInstruction(String trainingDataTableName, String[] fieldsName, String fieldClassName){
        /*
        public static String createInsertValuesSQLInstruction(String tableName, int examplesNum){
        List<Triple<String,Double,Double>> paramList = new ArrayList<>();
        paramList.add(new Triple<String, Double, Double>("glicemia", 1.0, 10.0));
        paramList.add(new Triple<String, Double, Double>("insulina", 10.0, 50.0));
        paramList.add(new Triple<String, Double, Double>("peso", 40.0, 150.0));
        paramList.add(new Triple<String, Double, Double>("altezza", 70.0, 220.0));
        paramList.add(new Triple<String, Double, Double>("eta", 0.0, 110.0));
        paramList.add(new Triple<String, Double, Double>("diabete", 1.0, 5.0));

        double data [][] = getExamples(paramList, examplesNum);
        //generateCreateTableSQLInstruction(tableName, fieldClassName, fieldsName); //??????????? SERVE
        return generateInsertSQLInstruction(data, paramList, tableName);
        */
        String outputFileName = "output.csv";
        String outputFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/AAA_FILES_TECNICI/ExampleGenerator/csv/" + outputFileName;
        String lineOutput ="";

        int c=1;
        String insertValuesQuery = "INSERT INTO " + trainingDataTableName +" (";
        for (int i=0; i < fieldsName.length; ++i){
            insertValuesQuery += fieldsName[i] + ", ";
        }
        insertValuesQuery += fieldClassName + ") VALUES\n";

        // lettura file output.csv riga per riga e aggiornamento database
        try {
            BufferedReader br = new BufferedReader(new FileReader(outputFilePath));
            int j=1;
            while ((lineOutput = br.readLine()) != null) {
                if(j > 3) {
                    String[] valori = lineOutput.split(",");
                    insertValuesQuery += "(";
                    for(int i=1; i<valori.length; ++i){
                        insertValuesQuery += valori[i] + ",";
                    }
                    insertValuesQuery += valori[0];
                    insertValuesQuery += "),\n";
                    c++;
                }
                j++;
            }
            insertValuesQuery = insertValuesQuery.substring(0, insertValuesQuery.length() - 2);
            insertValuesQuery += ";\n";
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return insertValuesQuery;
    }
}
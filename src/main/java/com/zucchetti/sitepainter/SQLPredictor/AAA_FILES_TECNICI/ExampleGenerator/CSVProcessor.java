package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.ExampleGenerator;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;






public class CSVProcessor {
    public static void main(String[] args) {
        DataBaseConnecter dbConnecter = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "password");

        String csvFileName = "a_insurance";
        String DBTableName = csvFileName;

        createDBTableFromCSV(csvFileName, DBTableName, dbConnecter);
    }

    public static void createDBTableFromCSV(String csvFileName, String DBTableName, DataBaseConnecter dbConnecter){
        String inputFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/AAA_FILES_TECNICI/ExampleGenerator/csv/" + csvFileName + ".csv";
        /* !! CREAZIONE SAMPLES
        String createTableQuery = "DROP TABLE IF EXISTS " + DBTableName + ";\n" +
                "CREATE TABLE " + DBTableName + " (\n" +
                " id_s BIGSERIAL PRIMARY KEY,\n" +
                " age_s DECIMAL(6, 2) NOT NULL,\n" +
                " sex_s VARCHAR(55) NOT NULL,\n" +
                " bmi_s DECIMAL(10, 5) NOT NULL,\n" +
                " children_s INTEGER NOT NULL,\n" +
                " smoker_s INTEGER NOT NULL,\n" +
                " region_s VARCHAR(55) NOT NULL,\n" +
                " charges_s DECIMAL(10, 5) NOT NULL);\n";
        */

        String createTableQuery = "DROP TABLE IF EXISTS " + DBTableName + ";\n" +
                "CREATE TABLE " + DBTableName + " (\n" +
                " id BIGSERIAL PRIMARY KEY,\n" +
                " age DECIMAL(6, 2) NOT NULL,\n" +
                " sex VARCHAR(55) NOT NULL,\n" +
                " bmi DECIMAL(10, 5) NOT NULL,\n" +
                " children INTEGER NOT NULL,\n" +
                " smoker INTEGER NOT NULL,\n" +
                " region VARCHAR(55) NOT NULL,\n" +
                " charges DECIMAL(10, 5) NOT NULL);\n";

        String lineInput;
        String insertDataQuery = "INSERT INTO " + DBTableName + " (";
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
            int r = 1;
            while ((lineInput = br.readLine()) != null) {
                String[] valori = lineInput.split(",");
                if(r > 1){
                    insertDataQuery += "(";
                    for(int f=0; f < valori.length; ++f){
                        if(f < valori.length-1){
                            if(f==1 || f==4 ||f==5) {
                                if(f==4 && valori[f].equals("no")) {insertDataQuery += "3,";}
                                else if(f==4 && valori[f].equals("yes")) {insertDataQuery += "8,";}
                                else {insertDataQuery += "'" + valori[f] + "',";}
                            }
                            else {insertDataQuery += valori[f] + ",";}
                        }
                        else {
                            insertDataQuery += valori[f] + "),\n" ;
                        }
                    }
                }
                else{
                    for(int f=0; f < valori.length; ++f){
                        if(f < valori.length-1){
                            insertDataQuery += valori[f] + ",";
                        }
                        else {
                            insertDataQuery += valori[f] + ") VALUES \n" ;
                        }
                    }
                }
                r++;
            }
            br.close();
            insertDataQuery = insertDataQuery.substring(0, insertDataQuery.length() - 2) + ";";
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(dbConnecter.getDataBaseURL(), dbConnecter.getUsername(), dbConnecter.getPassword());
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableQuery);
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataQuery)) {
                int rowsAffected = preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }



    /*/ NON SERVE PIU
    public static void trasformCSVFromOrange(double coefX, double coefY, String inputFileName, String outputFileName){
        String inputFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/AAA_FILES_TECNICI/ExampleGenerator/csv/" + inputFileName;
        String outputFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/AAA_FILES_TECNICI/ExampleGenerator/csv/" + outputFileName;
        String lineInput;
        //String lineOutput ="x,y\n";
        String lineOutput ="";

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
            int i=1;
            while ((lineInput = br.readLine()) != null) {
                if(i < 4) lineOutput += lineInput + "\n";
                else {
                    String[] valori = lineInput.split(",");
                    lineOutput += valori[0] + ",";
                    double x = (double) Double.parseDouble(valori[1]) * coefX;
                    lineOutput += x + ",";
                    double y = (double) Double.parseDouble(valori[2]) * coefY;
                    lineOutput += y  + "\n";
                }
                i++;
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));
            bw.write(lineOutput);
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
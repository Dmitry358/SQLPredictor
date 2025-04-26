package com.zucchetti.sitepainter.SQLPredictor;

import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.DA_BUTTARE.ExampleGenerator;
import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.QueryPredictor.QueryPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {

        // !!!!!!!!!!!!!!!!!!!!! SITUAZIONE QUANDO CREO OGETTO DI TRAINER MA NON FACCIO ALLENAMENTO, COSA FARE CON FILE JSON???
        //String dataBaseURL = "jdbc:postgresql://localhost:5432/proba_db";
        String dataBaseURL = "jdbc:postgresql://localhost:5432/sqlpredictor_db";
        String username = "postgres";
        String password = "a";
        DataBaseConnecter dbConnecter = new DataBaseConnecter(dataBaseURL, username, password);

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

        //*///////////////////////////   ABC END  ///////////////////////////////

        /*///////////////////////////   LR   ///////////////////////////////
        Map<String, String> trainerData = new HashMap<>();
        String predictorName = "LR_a_insurance";
        trainerData.put("predictorName", predictorName);
        trainerData.put("machineLearningModelType", "linear_regression");
        trainerData.put("trainingExpiration", "13");

        String dataTableName = "a_insurance";
        String[] dataTableFieldNamesList = { "  age  ", "  bmi  ", "  children  " };
        String classificationField = "  charges  ";

        // ---- LR BUILD + TRAIN ----
        MLTrainerBuilder trainerBuilder = new MLTrainerBuilder();
        MLTrainer LRTrainerTest = trainerBuilder.build(trainerData, dataTableName, dataTableFieldNamesList, classificationField);
        LRTrainerTest.train(dataTableName, dataTableFieldNamesList, classificationField, dbConnecter);

        // ---- LR-PREDICTOR ----
        String sampleTableName = "a_insurance_samples";
        String idFieldName = "id_s";
        String[] sampleFieldsList = { "  age_s  ", "  bmi_s  ", "  children_s  " };

        String request = "<   " + predictorName + "  >(    ";
        for (int f =0; f < sampleFieldsList.length; ++f){
            request += sampleFieldsList[f];
            if(f < sampleFieldsList.length -1) request += " ,";
            else request += " )";
        }

        MLSQLExpander expander = new MLSQLExpander();
        String query = "\033[32m" + expander.translate(request) + "\033[0m";
        if (query != null) { System.out.println(query); }
        //*////////////////////////////   LR END   ///////////////////////////////

        /*///////////////////////  SVM  /////////////////////////
        //--- IMPOSTAZIONE PARAMETRI MODELLO
        Map<String, String> trainerData = new HashMap<>();
        String predictorName = "SVM_INSURANCE25";
        trainerData.put("predictorName", predictorName);
        trainerData.put("machineLearningModelType", "   svm  ");
        trainerData.put("trainingExpiration", "   14   ");
        trainerData.put("SVMType", "  c_svc ");
        //trainerData.put("kernelType", "  linear ");
        trainerData.put("kernelType", "  polynomial ");
        //trainerData.put("kernelType", "  rbf ");
        trainerData.put("paramC", "  1  ");
        trainerData.put("gamma", "  0.5 ");
        trainerData.put("degree", " 2 ");
        trainerData.put("coef0", " 0.000000001  ");

        //--- DATI DI TABELLA PER FARE TRAINING
        String dataTableName = "   a_insurance25   ";
        String[] dataTableFieldNamesList = { " age " , " bmi ", "  children  "};
        String classificationField = "  smoker   ";

        //--- TRAINING MODELLO
        MLTrainerBuilder trainerBuilder = new MLTrainerBuilder();
        MLTrainer SVMTrainerTest = trainerBuilder.build(trainerData, dataTableName, dataTableFieldNamesList, classificationField);
        SVMTrainerTest.train(dataTableName, dataTableFieldNamesList, classificationField, dbConnecter);

        //--- SVM-PREDICTOR
        String sampleTableName = "a_insurance_samples";
        String idFieldName = "id_s";
        String[] sampleFieldsList = { "  age_s " , "  bmi_s  ", "  children_s  "};

        String request = "<   " + predictorName + "  >(    ";
        for (int f =0; f < sampleFieldsList.length; ++f){
            request += sampleFieldsList[f];
            if(f < sampleFieldsList.length -1) request += " ,";
            else request += " )";
        }

        MLSQLExpander expander = new MLSQLExpander();
        String predictionQuery = expander.translate(request);

        //--- PREDIZIONE
        QueryPredictor queryPredictor = new QueryPredictor(dbConnecter);

        int falseNum = 0, libError=0, queryError=0;
        for (int sampleId = 1; sampleId < 21; ++sampleId) {
            String classFieldName = "smoker_s";
            int classFieldValue = getClassFieldValue(classFieldName, sampleTableName, idFieldName, sampleId, dbConnecter);
            System.out.print("id = " + sampleId + ":  ");
            int libsvmPrediction = predictionLIBSVM(predictorName, sampleTableName, sampleFieldsList, idFieldName, sampleId, dbConnecter);
            if(libsvmPrediction != classFieldValue) {
                System.err.print("L = " + libsvmPrediction + " S = " + classFieldValue); ++libError;
            }
            else System.out.print("L = " + libsvmPrediction + " S = " + classFieldValue);

            int[][] queryPredictionResult = queryPredictor.predict(sampleTableName, idFieldName, sampleId, predictionQuery);
            if(queryPredictionResult[0][0] != classFieldValue) {
                System.err.print(" | Q = " + queryPredictionResult[0][0] + " S = " + classFieldValue); ++queryError;
            }
            else { System.out.print(" | Q = " + queryPredictionResult[0][0] + " S = " + classFieldValue); }

            if (queryPredictionResult[0][0] != libsvmPrediction) { System.out.println(" | "+"\u001B[31m"+"false"+"\u001B[0m"); ++falseNum;}
            else  { System.out.println(" | true"); }
        }
        System.out.println("False number = " + falseNum);
        System.out.println("Libsvm error = " + libError);
        System.out.println("Query error = " + queryError);
        //*//////////////////////////   SVM END   ///////////////////////////////
    }

    //  ------------------    METODI PER USO INTERNO     ------------------
    private static int predictionLIBSVM(String predictorName, String sampleTableName, String[] sampleFieldsList, String idFieldName, int sampleId, DataBaseConnecter dbConnecter){
        // !!!!! controllo se data contiene giusto numero elementi per modello
        predictorName = predictorName.trim();
        String extractSampleDataQuery = "SELECT ";
        for (int f=0; f < sampleFieldsList.length; ++f){
            if(f < sampleFieldsList.length - 1) { extractSampleDataQuery += sampleFieldsList[f] + ", "; }
            else { extractSampleDataQuery += sampleFieldsList[f] + "\n"; }
        }
        extractSampleDataQuery += "FROM " + sampleTableName +" \n";
        extractSampleDataQuery += "WHERE " + idFieldName + " = " + sampleId + ";";

        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(dbConnecter.getDataBaseURL(), dbConnecter.getUsername(), dbConnecter.getPassword());
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(extractSampleDataQuery);

            ResultSetMetaData queryResultMetaData = queryResult.getMetaData(); //SUPERFLUO

            List<String> fieldsNameList = new ArrayList<>(); //SUPERFLUO
            for (int i = 1; i <= queryResultMetaData.getColumnCount(); i++) {
                fieldsNameList.add(queryResultMetaData.getColumnName(i));
            }

            while (queryResult.next()) {
                ArrayList<Double> row = new ArrayList<>();
                for(int i=0; i < fieldsNameList.size(); i++) {
                    row.add(queryResult.getDouble(fieldsNameList.get(i)));
                }
                result.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        int[][] resultQuery = new int[result.size()][result.get(0).size()];
        for(int i=0; i < result.size(); ++i){
            for(int j=0; j < result.get(0).size(); ++j){
                resultQuery[i][j] = result.get(i).get(j).intValue();
            }
        }

        double[] sampleData = new double[resultQuery[0].length];
        for (int i=0; i < sampleData.length; ++i) {
            sampleData[i] = resultQuery[0][i];
        }

        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + predictorName + ".model";
        int numFeatures = sampleData.length;

        svm_node[] testData = new svm_node[numFeatures];
        int prediction = 0;
        try {
            svm_model model = svm.svm_load_model(modelFilePath);

            for(int i=0; i < numFeatures; i++) {
                testData[i] = new svm_node();
                testData[i].index = i+1;
                testData[i].value = sampleData[i];
            }

            prediction = (int) svm.svm_predict(model, testData);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return prediction;
    }
    private static int getClassFieldValue(String classFieldName, String sampleTableName, String idFieldName, int sampleId, DataBaseConnecter dbConnecter){
        String extractSampleDataQuery = "SELECT " + classFieldName + " FROM " + sampleTableName +" WHERE " + idFieldName + " = " + sampleId + ";";

        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(dbConnecter.getDataBaseURL(), dbConnecter.getUsername(), dbConnecter.getPassword());
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(extractSampleDataQuery);

            ResultSetMetaData queryResultMetaData = queryResult.getMetaData(); //SUPERFLUO

            List<String> fieldsNameList = new ArrayList<>(); //SUPERFLUO
            for (int i = 1; i <= queryResultMetaData.getColumnCount(); i++) {
                fieldsNameList.add(queryResultMetaData.getColumnName(i));
            }

            while (queryResult.next()) {
                ArrayList<Double> row = new ArrayList<>();
                for(int i=0; i < fieldsNameList.size(); i++) {
                    row.add(queryResult.getDouble(fieldsNameList.get(i)));
                }
                result.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        int[][] resultQuery = new int[result.size()][result.get(0).size()];
        for(int i=0; i < result.size(); ++i){
            for(int j=0; j < result.get(0).size(); ++j){
                resultQuery[i][j] = result.get(i).get(j).intValue();
            }
        }

        return resultQuery[0][0];
    }

    private static void updateDBWithNewExamplesFromOrange(String trainingDataTableName, String[] fieldNames, String fieldClassName, double coefX, double coefY){
        ExampleGenerator updateDB = new ExampleGenerator();
        updateDB.updateDatabase(trainingDataTableName, fieldNames, fieldClassName, coefX, coefY);
    }
    private static double[][] getTrainingData(String tableName, String[] fieldNames, String fieldClassName){
        DataBaseConnecter dbc = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "");
        return dbc.getTrainingData(tableName, fieldNames, fieldClassName);
    }
    private static double[][] getDataFromQueryResult(double[][] queryResult){
        int examplesNum = queryResult.length;
        int featuresNum = queryResult[0].length - 1;

        double[][] extractedData = new double[examplesNum][featuresNum];

        for (int i=0; i < examplesNum; ++i){
            System.arraycopy(queryResult[i], 0, extractedData[i], 0, featuresNum);
        }
        return extractedData;
    }
    private static double[] getValuesFromQueryResult(double[][] queryResult){
        int examplesNum = queryResult.length;
        int valueIndex = queryResult[0].length - 1;

        double[] extractedValues = new double[examplesNum];

        for (int i=0; i < examplesNum; ++i){
            extractedValues[i] = queryResult[i][valueIndex];
        }
        return extractedValues;
    }
    private static void createABCTrainer(){
        /*
        DataBaseConnecter dbConnecter = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "password");

        String csvFileName = "abc_trainer";
        String DBTableName = csvFileName;


        String inputFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/AAA_FILES_TECNICI/ExampleGenerator/csv/" + csvFileName + ".csv";
        String createTableQuery = "DROP TABLE IF EXISTS " + DBTableName + ";\n" +
                "CREATE TABLE " + DBTableName + " (\n" +
                " id BIGSERIAL PRIMARY KEY,\n" +
                " totale DECIMAL(6, 1) NOT NULL );" ;

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
                                if(f==4 && valori[f].equals("no")) insertDataQuery += "3,";
                                else if(f==4 && valori[f].equals("yes")) insertDataQuery += "8,";
                                else insertDataQuery += "'" + valori[f] + "',";
                            }
                            else insertDataQuery += valori[f] + ",";
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
        */
    }
}
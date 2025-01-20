package com.zucchetti.sitepainter.SQLPredictor;

import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.ExampleGenerator.ExampleGenerator;
import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.QueryPredictor.QueryPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.LRPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.ABCTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
//import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.SVMTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;



public class Main {
    public static void main(String[] args) {
        // !!!!!!!!!!!!!!!!!!!!!!! SITUAZIONE QUANDO CREO OGETTO DI TRAINER MA NON FACCIO ALLENAMENTO, COSA FARE CON FILE JSON???
        String dataBaseURL = "jdbc:postgresql://localhost:5432/proba_db";
        String username = "postgres";
        String password = "";
        DataBaseConnecter dbConnecter = new DataBaseConnecter(dataBaseURL, username, password);


        /*///////////////////////////   LR   ///////////////////////////////

        Map<String, String> trainerData = new HashMap<>();
        String predictorName = "A_LR_INSURANCE25";
        trainerData.put("predictorName", predictorName);
        trainerData.put("machineLearningModelType", "linear_regression");
        trainerData.put("trainingExpiration", "13");
        String dataTableName = "a_insurance25";
        String[] dataTableFieldNamesList = { "  age  ", "  bmi  ", "  children  " };
        String classificationField = "  charges  ";

        MLTrainerBuilder trainerBuilder = new MLTrainerBuilder();
        MLTrainer LRTrainerTest = trainerBuilder.build(trainerData, dataTableName, dataTableFieldNamesList, classificationField);

        LRTrainerTest.train(dataTableName, dataTableFieldNamesList, classificationField, dbConnecter);
        //-------------- LR-PREDICTOR -----------------

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
        /*///////////////////////////   LR END   ///////////////////////////////

        /*///////////////////////  SVM  /////////////////////////
        //--- IMPOSTAZIONE PARAMETRI MODELLO
        Map<String, String> trainerData = new HashMap<>();
        String predictorName = "A_SVM_INSURANCE25";
        trainerData.put("predictorName", predictorName);
        trainerData.put("machineLearningModelType", "   svm  ");
        trainerData.put("trainingExpiration", "   14   ");
        trainerData.put("SVMType", "  c_svc ");
        //trainerData.put("kernelType", "  polynomial ");
        trainerData.put("kernelType", "  rbf ");
        //trainerData.put("kernelType", "  linear ");
        trainerData.put("paramC", "  100  ");
        trainerData.put("gamma", "  0.005 ");
        trainerData.put("degree", " 2 ");
        trainerData.put("coef0", " 0.7  ");

        //--- DATI DI TABELLA PER FARE TRAINING
        String dataTableName = "   a_insurance25   ";
        String[] dataTableFieldNamesList = { "  age " , " bmi   "};
        String classificationField = "  smoker   ";

        //--- TRAINING MODELLO
        MLTrainerBuilder trainerBuilder = new MLTrainerBuilder();
        MLTrainer SVMTrainerTest = trainerBuilder.build(trainerData, dataTableName, dataTableFieldNamesList, classificationField);
        SVMTrainerTest.train(dataTableName, dataTableFieldNamesList, classificationField, dbConnecter);

        //--- SVM-PREDICTOR
        String sampleTableName = "a_insurance_samples";
        String idFieldName = "id_s";
        String[] sampleFieldsList = { "  age_s " , "  bmi_s   "};

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
            else System.out.print(" | Q = " + queryPredictionResult[0][0] + " S = " + classFieldValue);

            if (queryPredictionResult[0][0] != libsvmPrediction) { System.err.println(" | false"); ++falseNum;}
            else  System.out.println(" | true");
        }
        System.out.println("False number = " + falseNum);
        System.out.println("Libsvm error = " + libError);
        System.out.println("Query error = " + queryError);
        /*//////////////////////////   SVM END   ///////////////////////////////

        /*///////////////////////////   ABC   ///////////////////////////////
        DataBaseConnecter dbConnecter = new DataBaseConnecter(dataBaseURL, username, password);
        Map<String, String> trainerData = new HashMap<>();
        trainerData.put("predictorName", "  A-ABC-ABC   ");
        trainerData.put("machineLearningModelType", "   abc  ");
        trainerData.put("trainingExpiration", "   8   ");

        trainerData.put("predictionTableName", "  abc_predictor ");
        trainerData.put("boundA", "  0.8 ");
        trainerData.put("boundB", "  0.95 ");

        String dataTableName = "   abc_trainer   ";
        String[] dataTableFieldNamesList = { "   id " };
        String classificationField = "  yyy   ";

        MLTrainerBuilder trainerBuilder = new MLTrainerBuilder();
        MLTrainer ABCTrainerTest = trainerBuilder.build(trainerData, dataTableName, dataTableFieldNamesList, classificationField);

        ABCTrainerTest.train(dataTableName, dataTableFieldNamesList, classificationField, dbConnecter);
        int b = 9;
        /*///////////////////////////   ABC END  ///////////////////////////////

    }

    //  ------------------    METODI PER USO INTERNO     ------------------
    private static int predictionLIBSVM(String predictorName, String sampleTableName, String[] sampleFieldsList, String idFieldName, int sampleId, DataBaseConnecter dbConnecter){
        // !!!!! controllo se data contiene giusto numero elementi per modello
        predictorName = predictorName.trim();
        String extractSampleDataQuery = "SELECT ";
        for (int f=0; f < sampleFieldsList.length; ++f){
            if(f < sampleFieldsList.length - 1) extractSampleDataQuery += sampleFieldsList[f] + ", ";
            else extractSampleDataQuery += sampleFieldsList[f] + "\n";
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
            for (int j=0; j < featuresNum; ++j){
                extractedData[i][j] = queryResult[i][j];
            }
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




    /////////////////////////////////////// CODICE VECCHIO /////////////////////////////////////////////
        /*///--------- SVM-TRAINER (VECCHIO) -----------------
        private static int predictionLIBSVM(String modelName, double[] data){
            // !!!!! controllo se data contiene giusto numero elementi per modello

            String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + modelName + ".model";
            int numFeatures = data.length;

            svm_node[] testData = new svm_node[numFeatures];
            int prediction = 0;
            try {
                svm_model model = svm.svm_load_model(modelFilePath);

                for(int i=0; i < numFeatures; i++) {
                    testData[i] = new svm_node();
                    testData[i].index = i+1;
                    testData[i].value = data[i];
                }

                prediction = (int) svm.svm_predict(model, testData);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return prediction;
        }
        String tableName = "pazienti";
        String[] fieldNames = {"xxx", "yyy"};
        String fieldClassName = "ccc";
        String lastTrain = "sss";
        double rho = -0.26667; // FORSE NON SERVE
        int version = 2;

        double coefX = 1;
        double coefY = 1;
        String svmType = "c_svc";
        //String kernelType = "polynomial";
        String kernelType = "rbf";
        int polDegree = 2;
        double gamma = 0.5;
        double coef0 = 0.5;
        double paramC = 1;
        // AGGIORNAMENTO BD CON DATI DA ORANGE ( NON SERVE PER PROGETTO)
        updateDBWithNewExamplesFromOrange(tableName, fieldNames, fieldClassName, coefX, coefY);
        double[][] queryResult = getTrainingData(tableName, fieldNames, fieldClassName);

        // TRAINING MODELLO
        SVMTrainer svm_t = new SVMTrainer(tableName, version, lastTrain, svmType, kernelType, polDegree, gamma, coef0, rho, paramC);
        svm_t.train(getDataFromQueryResult(queryResult), getValuesFromQueryResult(queryResult));

        // GENERAZIONE KERNEL QUERY
        String request = "<   pazienti  >( xxx,  yyy  )";
        MLSQLExpander expander = new MLSQLExpander();
        String predictorQuery = expander.translate(request);

        // PREDIZIONE TRAMITE QUERY
        QueryPredictor qp = new QueryPredictor(dataBaseURL,username, password);
        String exampleTableName = tableName +"_examples";

        double[] d_1 = {  0.28  , 0.49  };
        double[] d_2 = {  0.32  , 0.37  };
        double[] d_3 = {  0.37  , 0.33  };
        double[] d_4 = {  0.45  , 0.27  };

        for (int i =1; i<5; ++i) {
            if (i==1) System.out.print("L = " + predictionLIBSVM(tableName, d_1) + " ");
            if (i==2) System.out.print("L = " + predictionLIBSVM(tableName, d_2) + " ");
            if (i==3) System.out.print("L = " + predictionLIBSVM(tableName, d_3) + " ");
            if (i==4) System.out.print("L = " + predictionLIBSVM(tableName, d_4) + " ");

            int exampleId = i;
            int[][] queryPredictionResult = qp.predict(exampleTableName, exampleId, predictorQuery);
            System.out.println("Q = " + queryPredictionResult[0][0]);
        }
        //*///------------- END SVM-TRAINER (VECCHIO) -----------------
        /*
        String request = null;
        //request = "<     diabete_svm  >(    t.insurance_samples.csv+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13), t.bbb, tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";

        MLSQLExpander expander = new MLSQLExpander();
        String query = expander.translate(request);
        if (query != null) { System.out.println(query); }
        //*///------------- END ELENCO REQUESTS -----------------------
        /*/// ---------- BUILDER ABC (VECCHIO) ------------------------------------
        String predictorModelType = "abc";
        String dataTableName = "abc_data-table";
        String[] dataTableFieldNamesList = {"abc-f1","abc-f2"};
        String classificationField = "abc-class-field";
        //*///------------- END BUILDER ABC (VECCHIO) -----------------
        /*///--------- ABC-TRAINER (VECCHIO) -----------------
        DataBaseConnecter dbc = new DataBaseConnecter(dataBaseURL, username, password);
        String predictorName = "abc_predictor";
        String dataTableName = "pazienti";
        String idField = "id";
        String classificationField = "yyy";
        ABCTrainer abc = new ABCTrainer(predictorName);
        abc.train(dataTableName, idField, classificationField, dbc);
        //*///------------- END ABC-TRAINER (VECCHIO) -----------------
        /*//------------- TRAINING LR-TRAINER (VECCHIO) -----------------
        DataBaseConnecter dbc = new DataBaseConnecter(dataBaseURL, username, password);
        double[][] queryResult = dbc.getTrainingData(dataTableName, dataTableFieldNamesList, classificationField);

        int samplesNum = queryResult.length;
        int fieldsNum = dataTableFieldNamesList.length + 1;
        int usedRowNum = 0;
        double[] moc = new double[1];

        int set1 = 3;
        double[][] ts1 = new double[set1][fieldsNum];
        for (int i=0; i < set1; ++i){
            for (int j=0; j < fieldsNum; ++j){
                ts1[i][j] = queryResult[i+usedRowNum][j];
            }
        }
        usedRowNum += set1;
        int set2 = 1;
        double[][] ts2= new double[set2][fieldsNum];;
        for (int i=0; i < set2; ++i){
            for (int j=0; j < fieldsNum; ++j){
                ts2[i][j] = queryResult[i+usedRowNum][j];
            }
        }
        usedRowNum += set2;
        int set3 = 2;
        double[][] ts3= new double[set3][fieldsNum];;
        for (int i=0; i < set3; ++i){
            for (int j=0; j < fieldsNum; ++j){
                ts3[i][j] = queryResult[i+usedRowNum][j];
            }
        }
        LRTrainerTest.train(ts1, moc);
        int a = 8;
        LRTrainerTest.train(ts2, moc);
        LRTrainerTest.train(ts3, moc);
        //*///------------- END TRAINING LR-TRAINER (VECCHIO) ---------
}
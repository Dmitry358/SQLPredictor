package com.zucchetti.sitepainter.SQLPredictor;

import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.ExampleGenerator.ExampleGenerator;
import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.QueryPredictor.QueryPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.ABCTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
//import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.SVMTrainer;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import java.io.IOException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        String dataBaseURL = "jdbc:postgresql://localhost:5432/proba_db";
        String username = "postgres";
        String password = "";
        /*
        String request = null;
        //request = "<  !!!!!   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //request = "<     diabete_svm  >(    t.aaa+77, t.tttt, t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13), t.bbb, tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //request = "<     diabete_svm  >(     t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13) , t.aaa+77,  t.bbb, tccc +44 )";
        //request = "<       diabete_svm   >(      aaaa, bbbb , cccc , dddd )";
        //request = "<       lr_moto   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //request = "<       abc_good   >(      t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) +  left(t.nome1_3,13) )";
        //request = "<       svm_linear   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //request = "<       svm_polynomial   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //request = "<       svm_rbf   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        //request = "<       svm_polynomial   >(       t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13),  tccc +44,  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22) , t.zzz  )";
        request = "<       svm_rbf   >(       )";
        MLSQLExpander expander = new MLSQLExpander();
        String query = expander.translate(request);
        if (query != null) { System.out.println(query); }
        */
        /*
        DataBaseConnecter dbc = new DataBaseConnecter(dataBaseURL, username, password);

        String tableName = "pazienti";
        String[] fields = { "peso", "insulina" };
        String classField = "diabete";
        double[][] queryResult = dbc.getTrainingData(tableName, fields, classField);
        //*/
        /*//------------- LR-TRAINER -----------------
        double[][] ts1 = new double[3][4];
        for (int i=0; i < 3; ++i){
            for (int j=0; j < 4; ++j){
                ts1[i][j] = queryResult[i][j];
            }
        }
        double[][] ts2= new double[1][4];;
        for (int i=0; i < 1; ++i){
            for (int j=0; j < 4; ++j){
                ts2[i][j] = queryResult[i+3][j];
            }
        }
        double[][] ts3= new double[2][4];;
        for (int i=0; i < 2; ++i){
            for (int j=0; j < 4; ++j){
                ts1[i][j] = queryResult[i+4][j];
            }
        }

        LRTrainer lrt = new LRTrainer(tableName,4,1);
        lrt.train(ts1);
        lrt.train(ts2);
        lrt.train(ts3);
        String request = "<       " + tableName + "   >( aaa, vvv, uuu      )";
        //*///--------- LR-TRAINER END -----------------

        /*///--------- SVM-TRAINER END -----------------
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
        // AGGIORNAMENTO BD CON DATI DA ORANGE
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
        //*///--------- SVM-TRAINER END -----------------

        //*///--------- ABC-TRAINER  -----------------
        DataBaseConnecter dbc = new DataBaseConnecter(dataBaseURL, username, password);
        String predictorName = "abc_predictor";
        String dataTableName = "pazienti";
        String idField = "id";
        String classificationField = "yyy";
        ABCTrainer abc = new ABCTrainer(predictorName);
        abc.train(dataTableName, idField, classificationField, dbc);
        //*///--------- ABC-TRAINER END -----------------

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //// METODI PER USO INTERNO
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

}
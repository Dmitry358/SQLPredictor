package com.zucchetti.sitepainter.SQLPredictor;

import com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.ExampleGenerator.ExampleGenerator;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.SVMTrainer;


public class Main {
    public static void main(String[] args) {
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
        DataBaseConnecter dbc = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "");

        String tableName = "pazienti";
        String[] fields = { "peso", "insulina" };
        String classField = "diabete";
        ArrayList<ArrayList<Double>> queryResult = dbc.getQueryResult(tableName, fields, classField);
        */
        /*------------- LR-TRAINER -----------------
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

        String tableName = "pazienti";
        String[] fieldNames = {"xxx", "yyy"};
        String fieldClassName = "ccc";
        double rho = -0.26667;
        int version = 2;
        String lastTrain = "sss";

        double coefX = 1;
        double coefY = 1;
        String svmType = "c_svc";
        //String kernelType = "polynomial";
        String kernelType = "rbf";
        int polDegree = 2;
        double gamma = 0.5;
        double coef0 = 0.5;
        double paramC = 30;

        ExampleGenerator updateDB = new ExampleGenerator();
        updateDB.updateDatabase(tableName, fieldNames, fieldClassName, coefX, coefY);
        DataBaseConnecter dbc = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "");
        double[][] queryResult = dbc.getQueryResult(tableName, fieldNames, fieldClassName);
        SVMTrainer svm_t = new SVMTrainer(tableName, version, lastTrain, svmType, kernelType, polDegree, gamma, coef0, rho, paramC);
        svm_t.train(getDataFromQueryResult(queryResult), getValuesFromQueryResult(queryResult));

        /*
        String request = "<       pazienti   >(    aaa, bbb   )";
        MLSQLExpander expander = new MLSQLExpander();
        String query = expander.translate(request);
        if (query != null) { System.out.println(query); }
        //*/


        int b=5;
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
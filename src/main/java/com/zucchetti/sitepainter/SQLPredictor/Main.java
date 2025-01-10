package com.zucchetti.sitepainter.SQLPredictor;

import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;


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
        /*
        DROP TABLE IF EXISTS lr_auto;
        CREATE TABLE lr_auto (
        id BIGSERIAL PRIMARY KEY,
        x1 DECIMAL(8, 4) NOT NULL,
        x2 DECIMAL(8, 4) NOT NULL,
        x3 DECIMAL(8, 4) NOT NULL,
        y DECIMAL(8, 4) NOT NULL);
        INSERT INTO lr_auto (x1, x2, x3, y) VALUES (7.53, 4.861, 398.29, 65);
        INSERT INTO lr_auto (x1, x2, x3, y) VALUES (8.77, 1.298, 129.38, 27);
        INSERT INTO lr_auto (x1, x2, x3, y) VALUES (2.84, 2.309, 268.18, 50);
        INSERT INTO lr_auto (x1, x2, x3, y) VALUES (3.45, 3.279, 203.15, 27);
        INSERT INTO lr_auto (x1, x2, x3, y) VALUES (1.57, 7.541, 200.31, 29);
        INSERT INTO lr_auto (x1, x2, x3, y) VALUES (1.88, 3.444, 112.39, 91);
        */

        DataBaseConnecter dbc = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "");

        String tableName = "lr_auto";
        String[] fields = { "x1", "x2", "x3"};
        String classField = "y";
        double[][] queryResult = dbc.getQueryResult(tableName, fields, classField);

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

        String request = "<       lr_auto   >( aaa, vvv, uuu      )";
        MLSQLExpander expander = new MLSQLExpander();
        String query = expander.translate(request);
        if (query != null) { System.out.println(query); }


        int b=5;



    }
}
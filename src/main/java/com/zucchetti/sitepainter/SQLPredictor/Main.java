package com.zucchetti.sitepainter.SQLPredictor;

import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.LRPredictor;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        UPDATE lr_auto22 SET sample =  -4.54                , s_value =    1.6133334350585935   WHERE id = 1;
        UPDATE lr_auto22 SET sample =  -2.3200000000000003  , s_value =    2.633333435058594    WHERE id = 2;
        UPDATE lr_auto22 SET sample =  -1.96                , s_value =    1.5733334350585935    WHERE id = 3;
        */

        //DataBaseConnecter dbc = new DataBaseConnecter("jdbc:postgresql://localhost:5432/proba_db", "postgres", "");

        String tableName = "lr_auto22";
        /*
        String[] fields = { "insulina" };
        String classField = "peso";
        double[][] queryResult = dbc.getQueryResult(tableName, fields, classField);
*/
        //LRTrainer lrt = new LRTrainer(tableName,3,1);

        double[][] ts1 = { {  -4.34  ,  2.1933334350585945 },
                           {  -2.76  ,  3.633333435058594 } };
        double[][] ts2 = { {  -2      ,  2.6533334350585935 }};

        double[][] ts3 = {  {   -1.7199999999999998 ,70  ,  4.673333358764648    },
                            {   -0.9400000000000004  ,68 ,  4.113333358764649    },
                            {   -0.8399999999999999  ,4 ,  4.893333358764648    } };

        //lrt.train(queryResult);

        //*
        //lrt.train(ts1);
        //lrt.train(ts3);
        //*/
        String request = "<       lr_auto22   >(  aaa,  hhh     )";
        MLSQLExpander expander = new MLSQLExpander();
        String query = expander.translate(request);
        if (query != null) { System.out.println(query); }

        int b=5;

    }
}
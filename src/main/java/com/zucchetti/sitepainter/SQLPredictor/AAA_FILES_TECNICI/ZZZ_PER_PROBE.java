package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI;


import com.zucchetti.sitepainter.SQLPredictor.MLSQLExpander;
import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class ZZZ_PER_PROBE {
    public static void main(String[] args) {
        String predictorName = "z_svm_rbf_for_testing";
        String[] sampleFieldsList = { "  t.km1 + left(t.nome1_1,11) + left(t.nome1_2,12) + left(t.nome1_3,13) " , "  t.km2 +  left(t.nome2_1,21) +   left(t.nome2_2,22)  "};

        String request = "<   " + predictorName + "  >(    ";
        for (int f =0; f < sampleFieldsList.length; ++f){
            request += sampleFieldsList[f];
            if(f < sampleFieldsList.length -1) {request += " ,";}
            else {request += " )";}
        }

        MLSQLExpander expander = new MLSQLExpander();
        String predictionQuery = expander.translate(request);
        System.out.println(predictionQuery);




        /*
        String predictorName = "z_abc_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);
        if(resultPredictor == null){System.out.println("NULL");}
        */

    }
}


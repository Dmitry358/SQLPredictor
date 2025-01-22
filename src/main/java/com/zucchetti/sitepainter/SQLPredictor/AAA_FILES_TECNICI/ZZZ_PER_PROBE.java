package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI;


import com.zucchetti.sitepainter.SQLPredictor.*;
import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class ZZZ_PER_PROBE {
    public static void main(String[] args) {

        String predictorName = "z_abc_for_testing";
        MLPredictorFactory factory = new MLPredictorFactory();
        MLPredictor resultPredictor = factory.getPredictor(predictorName);
        if(resultPredictor == null){System.out.println("NULL");}

    }
}


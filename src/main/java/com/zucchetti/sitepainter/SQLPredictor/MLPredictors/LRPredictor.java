package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import java.util.ArrayList;


public class LRPredictor extends MLPredictor {
    private final double[] parameters;

    public LRPredictor(String predictorName, int version, String lastTrain, double[] parameters){
        super(predictorName, version, lastTrain);
        this.parameters = parameters;
    }

    public String getQuery(ArrayList<String> fieldsList){
        int fieldsNum = fieldsList.size();

        if (fieldsNum < 1){
            System.out.println("Fields list must not be empty");
            return null;
        }
        if(fieldsNum != (parameters.length - 1)){
            System.out.println("Request must contain " + (parameters.length - 1) + " fields");
            return null;
        }

        String query = "(";

        for (int i=0; i <= fieldsNum; i++){
            if (i < fieldsNum) { query += fieldsList.get(i) + "*" + parameters[i] + " + "; }
            else { query += parameters[i] + ")"; }
        }

        return query;
    }
}

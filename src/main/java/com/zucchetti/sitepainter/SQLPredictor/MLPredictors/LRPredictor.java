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

        if(fieldsNum < 1){
            System.out.println("Fields list must not be empty");
            return null;
        }
        if(fieldsNum != (parameters.length - 1)){
            System.err.println("Request must contain " + (parameters.length - 1) + " field names");
            return null;
        }

        StringBuilder query = new StringBuilder("(");

        for (int i=0; i <= fieldsNum; i++){
            if(i==0){
                query.append(parameters[i]).append(" + ");
            }
            else if (i < fieldsNum) {
                query.append(fieldsList.get(i-1)).append("*").append(parameters[i]).append(" + ");
            }
            else if (i == fieldsNum){
                query.append(fieldsList.get(i-1)).append("*").append(parameters[i]).append(")");
            }
            /*
            else {
                if (parameters[i]>=0){
                    query.append(" + ").append(parameters[i]).append(")");
                }
                else {
                    query.append(" ").append(parameters[i]).append(")");
                }
            }
            */
        }

        return query.toString();
    }
}

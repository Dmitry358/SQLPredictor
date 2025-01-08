package com.zucchetti.sitepainter.SQLPredictor;

import java.util.ArrayList;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;


public class MLSQLExpander {
    public String translate(String request){
        request = request.substring(1, request.length() - 1);
        String[] result = request.split("\\s*>\\s*\\(\\s*");
        if(result.length < 2){
            System.out.println("Request does not contain all required fields");
            return null;
        }
        if(result[0].trim().isEmpty()){
            System.out.println("Predictor field cannot be empty");
            return null;
        }
        String predictorName = result[0].trim();

        MLPredictorFactory predictorFactory = new MLPredictorFactory();
        MLPredictor predictor = predictorFactory.getPredictor(predictorName);

        ArrayList<String> fieldsList = this.getFieldsList(result[1]);

        if(predictor != null && fieldsList != null) { return predictor.getQuery(fieldsList); }
        else { return null; }
    }

    private ArrayList<String> getFieldsList (String result){
        String[] fields = result.trim().split("\\s*,\\s*");

        ArrayList<String> fieldsList = new ArrayList<String>();

        // !!!!!!! DA RICONTROLLARE INDICE c
        for(int i=0; i < fields.length; i++){
            if(fields[i].trim().isEmpty()) {
                System.out.println("Fields in the fields list cannot be empty");
                return null;
            }
            if (fields[i].contains("(")){
                String component = "(" + fields[i];
                Boolean end = false;
                int c=0;

                for(int j=0; j < fields.length - i && !end; j++) {
                    if(fields[i+j].contains("(")){
                        component += ", " + fields[i+j+1];
                        c++;
                    }
                    else{
                        component += ")";
                        end = true;
                    }
                }
                fieldsList.add(component);
                i+=c;
            }
            else { fieldsList.add("(" + fields[i] + ")"); }
        }
        return fieldsList;
    }
}

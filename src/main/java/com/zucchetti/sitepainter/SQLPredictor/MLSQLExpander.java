package com.zucchetti.sitepainter.SQLPredictor;
//package SQLPredictor;

import java.util.ArrayList;

public class MLSQLExpander {
    public String translate(String request){
        //FARE QUA UNICA VOLTA LEGGERE FILE: CAPISCO TIPO MODELLO; DEFINISCO I CAMPI DA INIZIALIZZARE; CREO PREDICTOR CON CAMPI CORRISPONDENTI
        request = request.substring(1, request.length() - 1);
        String[] result = request.split("\\s*>\\s*\\(\\s*");
        String predictorName = result[0].trim();
        String[] fields = result[1].trim().split("\\s*,\\s*");

        ArrayList<String> fieldsList = new ArrayList<String>();

        for(int i=0; i < fields.length; i++){
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
            else if (fields[i].contains("+")) {
                fieldsList.add("(" + fields[i] + ")");
            }
            else fieldsList.add("(" + fields[i] + ")");
        }

        MLModelFactory mlmf = new MLModelFactory();
        MLPredictor mlp = mlmf.getPredictor(predictorName);
        return mlp.getQuery(fieldsList);
    }
}

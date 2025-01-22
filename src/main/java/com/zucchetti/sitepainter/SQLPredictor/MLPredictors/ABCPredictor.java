package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import java.util.ArrayList;
import java.util.Random;


public class ABCPredictor extends MLPredictor {
    private final String predictionTableName;

    public ABCPredictor(String predictorName, int version, String lastTrain, String predictionTableName){
        super(predictorName, version, lastTrain);
        this.predictionTableName = predictionTableName;
    }

    public String getQuery(ArrayList<String> fieldsList){
        if (fieldsList.size() != 1){
            System.err.println("Request contains incorrect number of fields");
            return null;
        }

        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        int length = 8;
        StringBuilder casualName = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            casualName.append(CHARACTERS.charAt(index));
        }

        return "(SELECT class FROM " + predictionTableName + " AS  " + casualName + " WHERE " + casualName + ".codice_cliente = " + fieldsList.get(0) + ")";
    }
}

package com.zucchetti.sitepainter.SQLPredictor;

import java.util.ArrayList;
import java.util.Random;


public class ABCPredictor extends MLPredictor {
    private String tableName; // !!!!!! const????

    ABCPredictor(){}
    ABCPredictor(String predictorName, int version, String lastTrain, String tableName){
        super(predictorName, version, lastTrain);
        this.tableName = tableName;
    }

    public String getQuery(ArrayList<String> clientCode){
        //!!!! ERRORE SE CI SONO PIU PARAMETRI clientCode
        // !! AGGIUNGERE STRINGA CASUALE A NOME TABELLA
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        int length = 8;
            StringBuilder casualName = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = random.nextInt(CHARACTERS.length());  // Ottiene un indice casuale
                casualName.append(CHARACTERS.charAt(index));  // Aggiunge il carattere casuale
            }
        return "(SELECT classe FROM " + tableName + " " + casualName + " WHERE " + casualName + ".codice_cliente = " + clientCode.get(0) + ")";
    }
}

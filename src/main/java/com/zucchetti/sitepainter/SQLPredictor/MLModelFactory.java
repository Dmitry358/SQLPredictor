package com.zucchetti.sitepainter.SQLPredictor;
//package SQLPredictor;

import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.*;
import java.io.File;

public class MLModelFactory {

    public MLPredictor getPredictor(String predictorName){
        String predictorsModelType = this.getPredictorsModelType(predictorName);
        //cLASSFORNAME, REFLECTION
        if(predictorsModelType != null) {
            switch (predictorsModelType) {
                case "linear_regression":
                    return this.getLRPredictor(predictorName);
                case "abc":
                    return this.getABCPredictor(predictorName);
                default:
                    return null;
            }
        }
        else{
            System.out.println("LRPredictor " + predictorName + " is not founded");
            return null;
        }
    }

    private MLPredictor getABCPredictor(String predictorName){
        int version = 0;
        String lastTrain = "";
        String tableNane = "";

        try {
            Gson gson = new Gson();
            String filePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json"; //!!!!!!!!!!!!!!!!!!!!!!!!!
            //String filePath = "src/main/java/SQLPredictor/predictors/" + predictorName + ".json"; //!!!!!!!!!!!!!!!!!!!!!!!!!

            FileReader reader = new FileReader(filePath);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                //String value = "";
                JsonArray param = new JsonArray();

                for (String key : jsonObject.keySet()) {
                    if(key.equals("version")) version = Integer.parseInt(jsonObject.get(key).getAsString());
                    else if(key.equals("last_train")) lastTrain = jsonObject.get(key).getAsString();
                    else if(key.equals("table_name")) tableNane = jsonObject.get(key).getAsString();
                }
            }
            else{
                System.out.println("Il JSON non è né un oggetto né un array.");
                return null;
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new ABCPredictor(predictorName, version, lastTrain, tableNane);
    }

    private MLPredictor getLRPredictor(String predictorName){
        int version = 0;
        String lastTrain = "";
        List<Double> parameters_l = new ArrayList<Double>();

        try {
            Gson gson = new Gson();
            String filePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json"; //!!!!!!!!!!!!!!!!!!!!!!!!!

            FileReader reader = new FileReader(filePath);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                //String value = "";
                JsonArray param = new JsonArray();

                for (String key : jsonObject.keySet()) {
                    if(key.equals("version")) version = Integer.parseInt(jsonObject.get(key).getAsString());
                    else if(key.equals("last_train")) lastTrain = jsonObject.get(key).getAsString();
                    else if(key.equals("parameters")) param = jsonObject.get(key).getAsJsonArray();
                }

                for (JsonElement element : param) parameters_l.add(element.getAsDouble());
            }
            else{
                System.out.println("Il JSON non è né un oggetto né un array.");
                return null;
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        double[] parameters = new double[parameters_l.size()];
        for (int i=0; i < parameters_l.size(); i++) parameters[i] = parameters_l.get(i);

        return new LRPredictor(predictorName, version, lastTrain, parameters);
    }

    private String getPredictorsModelType(String predictorName){ //!!! lancia eccezione FILE NOT FOUND fuori metodo
        try{
            Gson gson = new Gson();
            String filePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json"; //!!!!!!!!!!!!!!!!!!!!!!!!!
            FileReader reader = new FileReader(filePath);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    if(key.equals("model_type")) return jsonObject.get(key).getAsString();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    //!!! DA TENERE !!!
    /* !!! DA TENERE !!!
    public void isPredictorPresent(String predictorName){
        File directory = new File("predictors/");
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) System.out.println(file.getName());
                }
            }
            else System.out.println("La cartella è vuota o non accessibile.");
        }
        else System.out.println("Il percorso specificato non è una cartella.");
    }
     */
}


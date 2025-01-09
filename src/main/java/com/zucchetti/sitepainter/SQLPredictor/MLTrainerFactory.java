package com.zucchetti.sitepainter.SQLPredictor;

import com.google.gson.*;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MLTrainerFactory {
    public LRTrainer getPredictor(String predictorName){
        String trainerModelType = null;
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";

        try {
            FileReader reader = new FileReader(descriptionFilePath);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if(jsonObject.has("model_type")) { trainerModelType = jsonObject.get("model_type").getAsString(); }

                //CLASSFORNAME, REFLECTION
                if (trainerModelType != null) {
                    switch (trainerModelType) {
                        //case "linear_regression":
                            //return this.getLRPredictor(jsonObject);
                        default:
                            System.out.println("Description file contain unmanageable model type");
                            return null;
                    }
                }
                else {
                    System.out.println("Description file does not contain information on model type");
                    return null;
                }
            }
            else{
                System.out.println("Description file has wrong structure");
                return null;
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Description file of predictor \"" + predictorName + "\" is not found");
            return null;
        }
        catch (JsonIOException e) {
            System.out.println("Error of processing description file");
            return null;
        }
        catch (JsonSyntaxException e) {
            System.out.println("Syntax of description file is incorrect");
            return null;
        }
    }
    /*
    public getLRTrainer(String predictorName, int numX, int numY){
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";
        File descriptionFile = new File(descriptionFilePath);

        if (descriptionFile.exists()) {
            this.predictorName = predictorName;
            //////////////////////////////////////////////////////////////////////////
            try {
                FileReader reader = new FileReader(descriptionFilePath);
                JsonElement jsonElement = JsonParser.parseReader(reader);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    transposeOfXTimesX = this.getXtXMatrix(jsonObject);
                    transposeOfXTimesY = this.getXtYMatrix(jsonObject);
                }
                else{
                    System.out.println("Description file has incorrect structure, reading failed");
                    //return null;
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("Description file of predictor \"" + predictorName + "\" is not found");
                //return null;
            }
            catch (JsonIOException e) {
                System.out.println("Error of processing description file");
                //return null;
            }
            catch (JsonSyntaxException e) {
                System.out.println("Syntax of description file is incorrect");
                //return null;
            }

            this.identity = this.identityMatrix(numX);
            /*
            List<List<Integer>> auto = Arrays.asList( Arrays.asList(1, 2), Arrays.asList(2, 3) );
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("xTx", gson.toJsonTree(auto));

            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(jsonObject, writer);
                System.out.println("File JSON creato con successo!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            */
    /*
        }
        else {
            try {
                if (descriptionFile.createNewFile()) {
                    this.predictorName = predictorName;
                    this.transposeOfXTimesX = this.rectMatrix(numX, numX);
                    this.transposeOfXTimesY = this.rectMatrix(numX, numY);
                    this.identity = this.identityMatrix(numX);
                }
                else {
                    System.out.println("Non è stato possibile creare il file."); return;
                }
            }
            catch (IOException e) {
                System.out.println("Si è verificato un errore durante la creazione del file: " + e.getMessage()); return;
            }
        }
    }
    */
}


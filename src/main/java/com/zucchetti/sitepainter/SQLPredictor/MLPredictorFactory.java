package com.zucchetti.sitepainter.SQLPredictor;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.ABCPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.LRPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.MLPredictor;
import com.zucchetti.sitepainter.SQLPredictor.MLPredictors.SVMPredictor;


public class MLPredictorFactory {
    public MLPredictor getPredictor(String predictorName){
        String predictorModelType = null;
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";

        try {
            FileReader reader = new FileReader(descriptionFilePath);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if(jsonObject.has("model_type")) {
                    predictorModelType = jsonObject.get("model_type").getAsString();
                }

                //CLASSFORNAME, REFLECTION
                if (predictorModelType != null) {
                    switch (predictorModelType) {
                        case "linear_regression":
                            return this.getLRPredictor(jsonObject);
                        case "abc":
                            return this.getABCPredictor(jsonObject);
                        case "svm":
                            return this.getSVMPredictor(jsonObject);
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
    //!!!!!!!!!!!!!!!!!!!! DA AGGIURNARE RISPETTO NUOVA STRUTTURA
    private MLPredictor getLRPredictor(JsonObject jsonObject){
        String predictorName = "";
        int version = 0;
        String lastTrain = "";
        List<Double> parametersList = new ArrayList<Double>();

        JsonArray parametersJson = new JsonArray();

        int f=0;
        for (String key : jsonObject.keySet()) {
            if(key.equals("predictor_name")){
                predictorName = jsonObject.get(key).getAsString(); ++f;
            }
            else if(key.equals("version")) {
                version = Integer.parseInt(jsonObject.get(key).getAsString()); ++f;
            }
            else if(key.equals("last_train")) {
                lastTrain = jsonObject.get(key).getAsString(); ++f;
            }
            else if(key.equals("parametersLR")) {
                parametersJson = jsonObject.get(key).getAsJsonArray(); ++f;
            }
        }

        if (f < 4){
            System.err.println("Description file does not contain all information needed to create object");
            return null;
        }
        /*
        if (f > 4){
            System.out.println("Description file contains multiple fields with same name");
            return null;
        }
        */

        for (JsonElement element : parametersJson) { parametersList.add(element.getAsDouble()); }

        double[] parameters = new double[parametersList.size()];
        for (int i=0; i < parametersList.size(); i++) { parameters[i] = parametersList.get(i); }

        return new LRPredictor(predictorName, version, lastTrain, parameters);
    }

    private MLPredictor getABCPredictor(JsonObject jsonObject){
        String predictorName = "";
        int version = 0;
        String lastTrain = "";
        String tableNane = "";

        int f=0;
        for (String key : jsonObject.keySet()) {
            if(key.equals("predictor_name")) {
                predictorName = jsonObject.get(key).getAsString(); ++f;
            }
            else if(key.equals("version")) {
                version = Integer.parseInt(jsonObject.get(key).getAsString()); ++f;
            }
            else if(key.equals("last_train")){
                lastTrain = jsonObject.get(key).getAsString(); ++f;
            }
            else if(key.equals("table_name")) {
                tableNane = jsonObject.get(key).getAsString(); ++f;
            }
        }
        if (f < 4){
            System.err.println("Description file does not contain all information needed to create object");
            return null;
        }
        return new ABCPredictor(predictorName, version, lastTrain, tableNane);
    }

    private MLPredictor getSVMPredictor(JsonObject jsonObject) {
        String predictorName = "";
        int version = 0;
        String lastTrain = "";

        Map<String, String> modelParameters = new HashMap<>();
        ArrayList<ArrayList<Double>> supportVectors = new ArrayList<>();

        int linKer = 0; int polKer = 0; int rbfKer = 0;
        for (String key : jsonObject.keySet()) {
            if (key.equals("predictor_name")) {
                predictorName = jsonObject.get(key).getAsString();
                ++linKer; ++polKer; ++rbfKer;
            }
            else if (key.equals("version")) {
                version = Integer.parseInt(jsonObject.get(key).getAsString());
                ++linKer; ++polKer; ++rbfKer;
            }
            else if (key.equals("last_train")) {
                lastTrain = jsonObject.get(key).getAsString();
                ++linKer; ++polKer; ++rbfKer;
            }
            else if (key.equals("model_data")) {
                JsonObject modelData = jsonObject.get(key).getAsJsonObject();
                for (String modelKey : modelData.keySet()) {
                    if (modelKey.equals("svm_type")) {
                        modelParameters.put("svm_type", modelData.get(modelKey).getAsString());
                        ++linKer; ++polKer; ++rbfKer;
                    }
                    else if (modelKey.equals("kernel_type")) {
                        modelParameters.put("kernel_type", modelData.get(modelKey).getAsString());
                        ++linKer; ++polKer; ++rbfKer;
                    }
                    else if (modelKey.equals("degree")) {
                        modelParameters.put("degree", modelData.get(modelKey).getAsString());
                        ++polKer;
                    }
                    else if (modelKey.equals("gamma")) {
                        modelParameters.put("gamma", modelData.get(modelKey).getAsString());
                        ++polKer; ++rbfKer;
                    }
                    else if (modelKey.equals("coef0")) {
                        modelParameters.put("coef0", modelData.get(modelKey).getAsString());
                        ++polKer;
                    }
                    else if (modelKey.equals("rho")) {
                        modelParameters.put("rho", modelData.get(modelKey).getAsString());
                        ++linKer; ++polKer; ++rbfKer;
                    }
                    else if (modelKey.equals("support_vectors")) {
                        JsonArray sv = modelData.get(modelKey).getAsJsonArray();
                        int vectorNum = sv.size();
                        int featuresNum = sv.get(0).getAsJsonArray().size();
                        //double[][] vectorsData = new double[vectorNum][featuresNum];

                        for (int i = 0; i < vectorNum; i++) {
                            JsonArray vector = sv.get(i).getAsJsonArray();
                            ArrayList<Double> list = new ArrayList<>();
                            for (int j = 0; j < featuresNum; j++) {
                                list.add(vector.get(j).getAsDouble());
                            }
                            supportVectors.add(list);
                        }
                        ++linKer; ++polKer; ++rbfKer; //!!!!!!!!!!!!!!! FORSE NON SERVE
                    }
                }
            }
        }
        if ((linKer < 7 && modelParameters.get("kernel_type").equals("linear")) || (polKer < 10 && modelParameters.get("kernel_type").equals("polynomial")) || (rbfKer < 8 && modelParameters.get("kernel_type").equals("rbf"))) {
            System.err.println("Description file does not contain all information needed to create object");
            return null;
        }
        return new SVMPredictor(predictorName, version, lastTrain, modelParameters, supportVectors);
    }
}



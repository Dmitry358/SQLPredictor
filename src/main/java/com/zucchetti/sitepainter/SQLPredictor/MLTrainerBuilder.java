package com.zucchetti.sitepainter.SQLPredictor;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.ABCTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.SVMTrainer;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;


public class MLTrainerBuilder {
    // -------------- TUTTI ----------------
    private String predictorName = null;
    private String machineLearningModelType = null;
    private int version = 0;
    private String lastTrain = null;
    private int trainingExpiration = 0;
    private String trainingDataTableName = null;
    private String[] trainingFieldNamesList = null;
    private String classificationField = null;
    // -------------- ABC ----------------
    private double boundA = 0;
    private double boundB = 0;
    private String predictionTableName = null;
    // -------------- LR ----------------
    private double[][] xTx = null;
    private double[][] xTy = null;
    private double[] parametersLR = null;
    // -------------- SVM ----------------
    private String SVMType = null;
    private String kernelType = null;
    private int polDegree = 0;  // for poly
    private double gamma = 0;   // for poly/rbf/sigmoid
    private double coef0 = 0;   // for poly/sigmoid
    private double paramC = 0;  // for C_SVC, EPSILON_SVR and NU_SVR
    private double rho = 0;
    private ArrayList<ArrayList<Double>> supportVectors = null;

    /*  !!!!!!!! NON TOCCARE, DEVONO RESTARE !!!!!!!!!
    private double cacheSize = 0;   // in MB
    private double eps = 0;            // stopping criteria
    private int nrWeight = 0;        // for C_SVC
    private int[] weightLabel = null;    // for C_SVC //!!!!!!!!!!!!!!!!! null
    private double[] weight = {};        // for C_SVC
    private double paramNu = 0;            // for NU_SVC, ONE_CLASS, and NU_SVR
    private double paramP = 0;            // for EPSILON_SVR
    private int shrinking = 0;        // use the shrinking heuristics
    private int probability = 0;     // do probability estimates
    *///!!!!!!!! NON TOCCARE, DEVONO RESTARE !!!!!!!!!


    public MLTrainer build(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList, String classificationField) {
        trainingDataTableName = trainingDataTableName.trim();
        classificationField = classificationField.trim();
        this.trimInputData(trainerData, dataTableFieldNamesList);
        if (this.setTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList, classificationField)){
            if (this.machineLearningModelType.equals("abc")) {
                this.createABCDescriptionFile();
                return new ABCTrainer(this.predictorName, this.version, this.lastTrain, this.trainingExpiration, this.trainingDataTableName, this.trainingFieldNamesList, this.classificationField, this.predictionTableName, this.boundA, this.boundB);
            }
            else if (this.machineLearningModelType.equals("linear_regression")) {
                this.createLRDescriptionFile();
                return new LRTrainer(this.predictorName, this.version, this.lastTrain, this.trainingExpiration, this.xTx, this.xTy, this.parametersLR, this.trainingDataTableName, this.trainingFieldNamesList, this.classificationField);
            }
            else if (this.machineLearningModelType.equals("svm")) {
                this.createSVMDescriptionFile();
                return new SVMTrainer(this.predictorName, this.version, this.lastTrain, this.trainingExpiration, this.trainingDataTableName, this.trainingFieldNamesList, this.classificationField, this.SVMType, this.kernelType, this.polDegree, this.gamma, this.coef0, this.rho, this.paramC);
            }
            else {
                System.err.println("Unknown model type entered, could not create trainer");
                return null;
            }
        }
        System.err.println("Error reading description file, could not create trainer"); // !!!!!!!!!!!!!!!!!!!!! NON PROPRIO VERO
        return null;
    }

    private boolean setTrainerData(Map<String,String> trainerData, String trainingDataTableName,
                                   String[] dataTableFieldNamesList, String classificationField){
        if(!trainerData.containsKey("predictorName")) {
            System.err.println("Unable to create trainer, the request does not contain predictor name");
            return false;
        }
        String predName = trainerData.get("predictorName");
        if(predName == null || predName.isEmpty() || predName.contains(" ")) {
            System.err.println("Unable to create trainer, the value of the \"predictorName\" field in the description file is invalid");
            return false;
        }

        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + trainerData.get("predictorName") + ".json";
        File descriptionFile = new File(descriptionFilePath);
        if (descriptionFile.exists() && trainerData.get("machineLearningModelType").equals("linear_regression")) {
            try {
                FileReader reader = new FileReader(descriptionFilePath);
                JsonElement jsonElement = JsonParser.parseReader(reader);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    for (String key : jsonObject.keySet()) {
                        // ------- ML-TRAINER -----------------------------------------
                        if (key.equals("predictor_name")) {
                            this.setPredictorName(jsonObject.get(key).getAsString());
                        }
                        else if (key.equals("model_type")) {
                            this.setPredictorModelType(jsonObject.get(key).getAsString());
                        }
                        else if (key.equals("version")) {
                            this.setVersion(Integer.parseInt(jsonObject.get(key).getAsString()));
                        }
                        else if (key.equals("last_train")) {
                            this.setLastTrain(jsonObject.get(key).getAsString());
                        }
                        else if (key.equals("training_expiration")) {
                            this.setTrainingExpiration(Integer.parseInt(jsonObject.get(key).getAsString()));
                        }
                        else if (key.equals("training_data_table_name")) {
                            this.setTrainingDataTableName(jsonObject.get(key).getAsString());
                        }
                        else if (key.equals("training_field_names_list")) {
                            JsonArray fieldNamesFromJson = jsonObject.get(key).getAsJsonArray();
                            String[] fieldNamesList = new String[fieldNamesFromJson.size()];
                            for (int i = 0; i < fieldNamesFromJson.size(); i++) {
                                fieldNamesList[i] = fieldNamesFromJson.get(i).getAsString();
                            }
                            this.setTrainingFieldNamesList(fieldNamesList);
                        }
                        else if (key.equals("classification_field_name")) {
                            this.setClassiticationField(jsonObject.get(key).getAsString());
                        }

                        // ------- LR-TRAINER -----------------------------------------
                        else if (key.equals("xTx")) {
                            JsonArray xTxJson = jsonObject.get(key).getAsJsonArray();
                            double[][] xTxFromJson = new double[xTxJson.size()][];

                            for (int i = 0; i < xTxJson.size(); i++) {
                                JsonArray xTxJsonRow = xTxJson.get(i).getAsJsonArray();
                                double[] xTxFromJsonRow = new double[xTxJsonRow.size()];

                                for (int j = 0; j < xTxJsonRow.size(); j++) {
                                    xTxFromJsonRow[j] = xTxJsonRow.get(j).getAsDouble();
                                }
                                xTxFromJson[i] = xTxFromJsonRow;
                            }
                            this.setXtX(xTxFromJson);
                        }
                        else if (key.equals("xTy")) {
                            JsonArray xTyJson = jsonObject.get(key).getAsJsonArray();
                            double[][] xTyFromJson = new double[xTyJson.size()][];
                            for (int i = 0; i < xTyJson.size(); i++) {
                                JsonArray xTyJsonRow = xTyJson.get(i).getAsJsonArray();
                                double[] xTyFromJsonRow = new double[xTyJsonRow.size()];

                                for (int j = 0; j < xTyJsonRow.size(); j++) {
                                    xTyFromJsonRow[j] = xTyJsonRow.get(j).getAsDouble();
                                }
                                xTyFromJson[i] = xTyFromJsonRow;
                            }
                            this.setXtY(xTyFromJson);
                        }
                        else if (key.equals("parametersLR")) {
                            JsonArray parametersJson = jsonObject.get(key).getAsJsonArray();
                            double[] parametersFromJson = new double[parametersJson.size()];

                            for (int i = 0; i < parametersJson.size(); i++) {
                                parametersFromJson[i] = parametersJson.get(i).getAsDouble();
                            }
                            this.setParametersLR(parametersFromJson);
                        }
                    }
                }
                else {
                    System.err.println("Description file has wrong structure");
                    return false;
                }
            }
            catch (FileNotFoundException e) {
                System.err.println("Description file of predictor \"" + predictorName + "\" is not found");
                return false;
            }
            catch (JsonIOException e) {
                System.err.println("Error of processing description file");
                return false;
            }
            catch (JsonSyntaxException e) {
                System.err.println("Syntax of description file is incorrect");
                return false;
            }
        }
        else {
            if(!trainerData.containsKey("machineLearningModelType")) {
                System.err.println("Unable to create trainer, the request does not contain predictor model type");
                return false;
            }
            String modType = trainerData.get("machineLearningModelType");
            if(modType == null || modType.isEmpty() || modType.contains(" ")){
                System.err.println("Unable to create trainer, the value of the \"machineLearningModelType\" field in the description file is invalid");
                return false;
            }
            if(!trainerData.containsKey("trainingExpiration")) {
                System.err.println("Unable to create trainer, the request does not contain training expiration");
                return false;
            }

            if(trainingDataTableName == null || trainingDataTableName.isEmpty() || trainingDataTableName.contains(" ")) {
                System.err.println("Unable to create trainer, invalid training data table name");
                return false;
            }
            if(dataTableFieldNamesList == null || dataTableFieldNamesList.length < 1) {
                System.err.println("Unable to create trainer, no training table field name present");
                return false;
            }
            for (int i =0; i < dataTableFieldNamesList.length; ++i){
                if(dataTableFieldNamesList[i] == null || dataTableFieldNamesList[i].trim().isEmpty() || dataTableFieldNamesList[i].trim().contains(" ")){
                    System.err.println("Unable to create trainer, field name list contains names without information");
                    return false;
                }
            }
            if(classificationField == null || classificationField.isEmpty() || classificationField.contains(" ")) {
                System.err.println("Unable to create trainer, invalid classification field name");
                return false;
            }

            if(this.setMLTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList, classificationField)) {
                if (trainerData.get("machineLearningModelType").equals("linear_regression")) {
                    return setLRTrainerData(dataTableFieldNamesList);
                }
                if (trainerData.get("machineLearningModelType").equals("svm")) {
                    return setSVMTrainerData(trainerData);
                }
                if (trainerData.get("machineLearningModelType").equals("abc")) {
                    return setABCTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList);
                }
                else {
                    System.err.println("Unknown model type entered, could not create trainer");
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

    private boolean setMLTrainerData(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList, String classificationField){
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + trainerData.get("predictorName") + ".json";
        File descriptionFile = new File(descriptionFilePath);
        try {
            descriptionFile.createNewFile();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }

        this.setPredictorName(trainerData.get("predictorName"));
        this.setPredictorModelType(trainerData.get("machineLearningModelType"));
        this.setVersion(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String trainingDateTime = dateTime.format(formatter);
        this.setLastTrain(trainingDateTime);
        this.setTrainingExpiration(Integer.parseInt(trainerData.get("trainingExpiration")));
        this.setTrainingDataTableName(trainingDataTableName);
        this.setTrainingFieldNamesList(dataTableFieldNamesList);
        this.setClassiticationField(classificationField);
        return true;
    }
    private boolean setLRTrainerData(String[] dataTableFieldNamesList){
        int parametersNumber = dataTableFieldNamesList.length + 1;
        this.setXtX(this.rectMatrix(parametersNumber,parametersNumber));
        this.setXtY(this.rectMatrix(parametersNumber,1));

        double[] parametersLR = new double[parametersNumber];
        for(int i =0; i < parametersLR.length; ++i){
            parametersLR[i] = 0;
        }
        this.setParametersLR(parametersLR);

        return true;
    }
    private boolean setSVMTrainerData(Map<String,String> trainerData){
        if(!trainerData.containsKey("SVMType")) {
            System.err.println("Unable to create trainer, the request does not contain information about svm type");
            return false;
        }
        String[] svmTypes = {"c_svc", "nu_svc", "one_class", "epsilon_svr", "nu_svr"};
        if(!Arrays.asList(svmTypes).contains(trainerData.get("SVMType").trim())){
            System.err.println("Unable to create trainer, the svm type entered does not exist");
            return false;
        }
        if(!trainerData.containsKey("kernelType")) {
            System.err.println("Unable to create trainer, the request does not contain information about kernel type");
            return false;
        }
        String[] kernelTypes = {"linear", "polynomial", "rbf", "sigmoid", "precomputed"};
        if(!Arrays.asList(kernelTypes).contains(trainerData.get("kernelType").trim())){
            System.err.println("Unable to create trainer, the kernel type entered does not exist");
            return false;
        }
        if(trainerData.get("kernelType").trim().equals("polynomial") && !trainerData.containsKey("degree")){
            System.err.println("Unable to create trainer, for polynomial kernel must be specified degree");
            return false;
        }
        String[] gammaKernels = {"polynomial", "rbf", "sigmoid"};
        if(Arrays.asList(gammaKernels).contains(trainerData.get("kernelType").trim()) && !trainerData.containsKey("gamma")){
            System.err.println("Unable to create trainer, for this type of kernel you need to specify gamma parameter");
            return false;
        }
        String[] coef0Kernels = {"polynomial", "sigmoid"};
        if(Arrays.asList(coef0Kernels).contains(trainerData.get("kernelType").trim()) && !trainerData.containsKey("coef0")){
            System.err.println("Unable to create trainer, for this type of kernel you need to specify coef0 parameter");
            return false;
        }
        if(trainerData.get("SVMType").trim().equals("c_svc") && !trainerData.containsKey("paramC")){
            System.err.println("Unable to create trainer, for c_svc model you need to specify C parameter");
            return false;
        }

        this.setSvmType(trainerData.get("SVMType").trim());
        this.setKernelType(trainerData.get("kernelType").trim());
        if(trainerData.containsKey("degree")){
            this.setDegree(Integer.parseInt(trainerData.get("degree").trim()));
        }
        if(trainerData.containsKey("gamma")){
            this.setGamma(Double.parseDouble(trainerData.get("gamma").trim()));
        }
        if(trainerData.containsKey("coef0")){
            this.setCoef0(Double.parseDouble(trainerData.get("coef0").trim()));
        }
        if(trainerData.containsKey("paramC")){
            this.setParamC(Double.parseDouble(trainerData.get("paramC").trim()));
        }

        return true;
    }
    private boolean setABCTrainerData(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList){
        if(!trainerData.containsKey("boundA")) {
            System.err.println("Unable to create trainer, the request does not contain information about bound A value");
            return false;
        }
        String boundAval = trainerData.get("boundA");
        if(boundAval == null || boundAval.isEmpty() || boundAval.contains(" ")){
            System.err.println("Unable to create trainer, request contains unacceptable bound A value");
            return false;
        }
        if(!trainerData.containsKey("boundB")) {
            System.err.println("Unable to create trainer, the request does not contain information about bound B value");
            return false;
        }
        String boundBval = trainerData.get("boundB");
        if(boundBval == null || boundBval.isEmpty() || boundBval.contains(" ")){
            System.err.println("Unable to create trainer, request contains unacceptable bound B value");
            return false;
        }
        if(!trainerData.containsKey("predictionTableName")) {
            System.err.println("Unable to create trainer, request does not contain table name where prediction results will be saved");
            return false;
        }
        String predTabName = trainerData.get("predictionTableName");
        if(predTabName == null || predTabName.isEmpty() || predTabName.contains(" ") || predTabName.contains("-")){
            System.err.println("Unable to create trainer, table name where prediction results will be saved is unacceptable");
            return false;
        }

        this.setBoundA(Double.parseDouble(trainerData.get("boundA")));
        this.setBoundB(Double.parseDouble(trainerData.get("boundB")));
        this.setPredictionTableName(trainerData.get("predictionTableName"));

        return true;
    }

    private boolean createABCDescriptionFile(){
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + this.predictorName + ".model";
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.predictorName + ".json";

        File descriptionFile = new File(descriptionFilePath);

        if (!descriptionFile.exists()) {
            try {
                if (!descriptionFile.createNewFile()) {
                    System.err.println("Failed to create description file");
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        JsonObject completeObject = new JsonObject();
        completeObject.add("predictor_name", new JsonPrimitive(this.predictorName));
        completeObject.add("model_type", new JsonPrimitive("abc"));
        completeObject.add("version", new JsonPrimitive(1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String trainingDateTime = dateTime.format(formatter);
        completeObject.add("last_train", new JsonPrimitive(trainingDateTime));
        completeObject.add("training_expiration", new JsonPrimitive(this.trainingExpiration));
        completeObject.add("training_data_table_name", new JsonPrimitive(this.trainingDataTableName));
        completeObject.add("classification_field_name", new JsonPrimitive(this.classificationField));
        JsonArray trainingFieldListJsonArray = convertToJsonArrayFieldNamesList(this.trainingFieldNamesList);
        completeObject.add("training_field_names_list", trainingFieldListJsonArray);

        completeObject.add("bound_A", new JsonPrimitive(this.boundA));
        completeObject.add("bound_B", new JsonPrimitive(this.boundB));
        completeObject.add("prediction_table_name", new JsonPrimitive(this.predictionTableName));

        try (FileWriter writer = new FileWriter(descriptionFilePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(completeObject, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return  true;
    }
    private boolean createSVMDescriptionFile(){
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + this.predictorName + ".model";
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.predictorName + ".json";

        File descriptionFile = new File(descriptionFilePath);

        if (!descriptionFile.exists()) {
            try {
                if (!descriptionFile.createNewFile()) {
                    System.err.println("Failed to create description file");
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        JsonObject completeObject = new JsonObject();
        completeObject.add("predictor_name", new JsonPrimitive(this.predictorName));
        completeObject.add("model_type", new JsonPrimitive("svm"));
        completeObject.add("version", new JsonPrimitive(1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String trainingDateTime = dateTime.format(formatter);
        completeObject.add("last_train", new JsonPrimitive(trainingDateTime));
        completeObject.add("training_expiration", new JsonPrimitive(this.trainingExpiration));
        completeObject.add("training_data_table_name", new JsonPrimitive(this.trainingDataTableName));
        completeObject.add("classification_field_name", new JsonPrimitive(this.classificationField));
        JsonArray trainingFieldListJsonArray = convertToJsonArrayFieldNamesList(this.trainingFieldNamesList);
        completeObject.add("training_field_names_list", trainingFieldListJsonArray);
        JsonObject modelData = new JsonObject();

        try (FileWriter writer = new FileWriter(descriptionFilePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(completeObject, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private boolean createLRDescriptionFile(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("predictor_name", new JsonPrimitive(this.predictorName));
        jsonObject.add("model_type", new JsonPrimitive("linear_regression"));
        jsonObject.add("version", new JsonPrimitive(this.version));
        jsonObject.add("last_train", new JsonPrimitive(this.lastTrain));
        jsonObject.add("training_expiration", new JsonPrimitive(this.trainingExpiration));
        jsonObject.add("training_data_table_name", new JsonPrimitive(this.trainingDataTableName));
        jsonObject.add("classification_field_name", new JsonPrimitive(this.classificationField));
        JsonArray trainingFieldListJsonArray = convertToJsonArrayFieldNamesList(this.trainingFieldNamesList);
        jsonObject.add("training_field_names_list", trainingFieldListJsonArray);

        JsonArray parametersJsonArray = convertToJsonArrayParameters(this.parametersLR);
        jsonObject.add("parametersLR", parametersJsonArray);
        JsonArray xTxJsonArray = convertToJsonArrayMatrices(this.xTx);
        jsonObject.add("xTx", xTxJsonArray);
        JsonArray xTyJsonArray = convertToJsonArrayMatrices(this.xTy);
        jsonObject.add("xTy", xTyJsonArray);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.predictorName + ".json";
        try (FileWriter writer = new FileWriter(descriptionFilePath)) {
            gson.toJson(jsonObject, writer);
        }
        catch (IOException e) {
            System.err.println("Error writing description file, failed to update parameters: ");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void trimInputData(Map<String,String> trainerData, String[] dataTableFieldNamesList){
        for (Map.Entry<String, String> entry : trainerData.entrySet()) {
            entry.setValue(entry.getValue().trim());
        }
        for (int i = 0; i < dataTableFieldNamesList.length; i++) {
            dataTableFieldNamesList[i] = dataTableFieldNamesList[i].trim();
        }
    }
    private double[][] rectMatrix(int numRows, int numColumns){
        double[][] matrix = new double[numRows][];
        for (int r = 0; r < numRows; ++r) {
            double[] row = new double[numColumns];
            matrix[r] = row;
            Arrays.fill(row, 0);
        }
        return matrix;
    }
    private JsonArray convertToJsonArrayFieldNamesList(String[] array) {
        JsonArray jsonArray = new JsonArray();
        for (String value : array) {
            jsonArray.add(new JsonPrimitive(value));
        }
        return jsonArray;
    }
    private JsonArray convertToJsonArrayParameters(double[] array) {
        JsonArray jsonArray = new JsonArray();
        for (double value : array) {
            jsonArray.add(new JsonPrimitive(value));
        }
        return jsonArray;
    }
    private JsonArray convertToJsonArrayMatrices(double[][] array) {
        JsonArray jsonArray = new JsonArray();

        for (double[] row : array) {
            JsonArray rowArray = new JsonArray();
            for (double value : row) {
                rowArray.add(new JsonPrimitive(value));
            }
            jsonArray.add(rowArray);
        }

        return jsonArray;
    }


    //#############################################################################################
    //######################################     SETTERS     ######################################
    //#############################################################################################
    // -------------- ML SETTERS ----------------
    public MLTrainerBuilder setPredictorName(String predictorName){
        this.predictorName = predictorName;
        return this;
    }
    public MLTrainerBuilder setPredictorModelType(String machineLearningModelType){
        this.machineLearningModelType = machineLearningModelType;
        return this;
    }
    public MLTrainerBuilder setVersion(int version){
        this.version = version;
        return this;
    }
    public MLTrainerBuilder setLastTrain(String lastTrain){
        this.lastTrain = lastTrain;
        return this;
    }
    public MLTrainerBuilder setTrainingExpiration(int trainingExpiration){
        this.trainingExpiration = trainingExpiration;
        return this;
    }
    public MLTrainerBuilder setTrainingDataTableName(String trainingDataTableName){
        this.trainingDataTableName = trainingDataTableName;
        return this;
    }
    public MLTrainerBuilder setTrainingFieldNamesList(String[] trainingFieldNamesList){
        this.trainingFieldNamesList = trainingFieldNamesList;
        return this;
    }
    public MLTrainerBuilder setClassiticationField(String classificationField){
        this.classificationField = classificationField;
        return this;
    }

    // -------------- ABC SETTERS ----------------
    public MLTrainerBuilder setPredictionTableName(String predictionTableName){
        this.predictionTableName = predictionTableName;
        return this;
    }
    public MLTrainerBuilder setBoundA(double boundA){
        this.boundA = boundA;
        return this;
    }
    public MLTrainerBuilder setBoundB(double boundB){
        this.boundB = boundB;
        return this;
    }

    // -------------- LR SETTERS ----------------
    public MLTrainerBuilder setXtX(double[][] xTx){
        this.xTx = xTx;
        return this;
    }
    public MLTrainerBuilder setXtY(double[][] xTy){
        this.xTy = xTy;
        return this;
    }
    public MLTrainerBuilder setParametersLR(double[] parametersLR){
        this.parametersLR = parametersLR;
        return this;
    }

    // -------------- SVM SETTERS ----------------
    public MLTrainerBuilder setSvmType(String svmType){
        this.SVMType = svmType;
        return this;
    }
    public MLTrainerBuilder setKernelType(String kernelType){
        this.kernelType = kernelType;
        return this;
    }
    public MLTrainerBuilder setDegree(int polDegree){
        this.polDegree = polDegree;
        return this;
    }
    public MLTrainerBuilder setGamma(double gamma){
        this.gamma = gamma;
        return this;
    }
    public MLTrainerBuilder setCoef0(double coef0){
        this.coef0 = coef0;
        return this;
    }
    public MLTrainerBuilder setRho(double rho){
        this.rho = rho;
        return this;
    }
    public MLTrainerBuilder setParamC(double paramC){
        this.paramC = paramC;
        return this;
    }
    public MLTrainerBuilder setSupportVectors(double[][] xTy){
        this.supportVectors = new ArrayList<>();
        return this;
    }

    /* !!!!!!!! NON TOCCARE, DEVONO RESTARE !!!!!!!!!
    public MLTrainerBuilder setCacheSize(double cacheSize){
        this.cacheSize = cacheSize;
        return this;
    }
    public MLTrainerBuilder setEps(double eps){
        this.eps = eps;
        return this;
    }
    public MLTrainerBuilder setNrWeight(int nrWeight){
        this.nrWeight = nrWeight;
        return this;
    }
    public MLTrainerBuilder setWeightLabel(int[] weightLabel){
        this.weightLabel = weightLabel;
        return this;
    }
    public MLTrainerBuilder setWeight(double[] weight){
        this.weight = weight;
        return this;
    }
    public MLTrainerBuilder setParamNu(double paramNu){
        this.paramNu = paramNu;
        return this;
    }
    public MLTrainerBuilder setParamP(double paramP){
        this.paramP = paramP;
        return this;
    }
    public MLTrainerBuilder setShrinking(int shrinking){
        this.shrinking = shrinking;
        return this;
    }
    public MLTrainerBuilder setProbability(int probability){
        this.probability = probability;
        return this;
    }
    *///!!!!!!!! NON TOCCARE, DEVONO RESTARE !!!!!!!!!
}

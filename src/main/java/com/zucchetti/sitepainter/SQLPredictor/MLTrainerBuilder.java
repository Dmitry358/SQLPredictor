package com.zucchetti.sitepainter.SQLPredictor;

import com.google.gson.*;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.ABCTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.LRTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.MLTrainer;
import com.zucchetti.sitepainter.SQLPredictor.MLTrainers.SVMTrainer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
    private String classificationField;

    // -------------- ABC ----------------
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
    //final private ArrayList<ArrayList<Double>> supportVectors;

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
        if (this.setTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList, classificationField)){
            if (this.machineLearningModelType.equals("abc")) {
                return new ABCTrainer(this.predictorName, this.version, this.lastTrain, this.trainingExpiration, this.trainingDataTableName, this.trainingFieldNamesList, this.predictionTableName, this.classificationField);
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
                System.out.println("Unknown model type entered, could not create trainer");
                return null;
            }
        }
        System.out.println("Error reading description file, could not create trainer"); // !!!!!!!!!!!!!!!!!!!!! NON PROPRIO VERO
        return null;
    }

    private boolean setTrainerData(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList, String classificationField){
        if(!trainerData.containsKey("predictorName")) {
            System.err.println("Unable to create trainer, the request does not contain predictor name"); return false;
        }
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + trainerData.get("predictorName") + ".json";
        File descriptionFile = new File(descriptionFilePath);
        if (descriptionFile.exists()) {
            try {
                FileReader reader = new FileReader(descriptionFilePath); // ??? FORSE SI PUO METTERE COME CONTROLLO ESISTENZA DEL FILE
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
                        // ------- ABC-TRAINER  ---------------------------------------
                        else if (key.equals("prediction_table_name")) {
                            this.setPredictionTableName(jsonObject.get(key).getAsString());
                            //this.predictionTableName = jsonObject.get(key).getAsString();
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

                            //this.xTx = xTxFromJson;
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
                            //this.xTy = xTyFromJson;
                            /*
                            if(xTxFromJson.length > 0) {
                                if (xTxFromJson.length == this.transposeOfXTimesX.length && xTxFromJson[0].length == this.transposeOfXTimesX[0].length) {
                                    for (int i = 0; i < xTxFromJson.length; ++i) {
                                        for (int j = 0; j < xTxFromJson[0].length; ++j) {
                                            this.transposeOfXTimesX[i][j] = xTxFromJson[i][j];
                                        }
                                    }
                                } else {
                                    System.out.println("Number of parameters read from description file does not match number of trainer parameters");
                                }
                            }
                            else {
                                System.out.println("The xTx parameter of description file does not contain any elements");
                            }
                            */
                        }
                        else if (key.equals("parametersLR")) {
                            JsonArray parametersJson = jsonObject.get(key).getAsJsonArray();
                            double[] parametersFromJson = new double[parametersJson.size()];

                            for (int i = 0; i < parametersJson.size(); i++) {
                                parametersFromJson[i] = parametersJson.get(i).getAsDouble();
                            }
                            this.setParametersLR(parametersFromJson);
                            //this.parametersLR = parametersFromJson;
                        }
                        // ------- SVM-TRAINER  ---------------------------------------
                        else if (key.equals("model_data")) {
                            //for (String modelDataRow : jsonObject.keySet()) {
                            JsonObject modelData = jsonObject.get(key).getAsJsonObject();
                            for (String modelDataKey : modelData.keySet()) {
                                if (modelDataKey.equals("svm_type")) { // ??? AS STRING?????
                                    this.setSvmType(modelData.get(modelDataKey).getAsString());
                                }
                                else if (modelDataKey.equals("kernel_type")) {// ??? AS STRING?????
                                    this.setKernelType(modelData.get(modelDataKey).getAsString());
                                }
                                else if (modelDataKey.equals("degree")) {
                                    this.setDegree(Integer.parseInt(modelData.get(modelDataKey).getAsString()));
                                }
                                else if (modelDataKey.equals("gamma")) {
                                    this.setGamma(Double.parseDouble(modelData.get(modelDataKey).getAsString()));
                                }
                                else if (modelDataKey.equals("coef0")) {
                                    this.setCoef0(Double.parseDouble(modelData.get(modelDataKey).getAsString()));
                                }
                                else if (modelDataKey.equals("rho")) {
                                    this.setRho(Double.parseDouble(modelData.get(modelDataKey).getAsString()));
                                }
                                else if (modelDataKey.equals("paramC")) {
                                    this.setParamC(Double.parseDouble(modelData.get(modelDataKey).getAsString()));
                                }
                                else if (modelDataKey.equals("support_vectors")) {
                                    JsonArray svJson = modelData.get(modelDataKey).getAsJsonArray();
                                    double[][] svFromJson = new double[svJson.size()][];

                                    for (int i = 0; i < svJson.size(); i++) {
                                        JsonArray svJsonRow = svJson.get(i).getAsJsonArray();
                                        double[] svFromJsonRow = new double[svJsonRow.size()];

                                        for (int j = 0; j < svJsonRow.size(); j++) {
                                            svFromJsonRow[j] = svJsonRow.get(j).getAsDouble();
                                        }
                                        svFromJson[i] = svFromJsonRow;
                                    }
                                    this.setXtX(svFromJson); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                }
                            }
                        }
                    }
                    /*if (f < 3) { //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println("Description file does not contain all information needed to create object");
                        return false;
                    }*/
                }
                else {
                    System.out.println("Description file has wrong structure");
                    return false;
                }
            }
            catch (FileNotFoundException e) { // SERVE??????????????????
                System.out.println("Description file of predictor \"" + predictorName + "\" is not found");
                return false;
            }
            catch (JsonIOException e) {
                System.out.println("Error of processing description file");
                return false;
            }
            catch (JsonSyntaxException e) {
                System.out.println("Syntax of description file is incorrect");
                return false;
            }
        }
        else {
            if(!trainerData.containsKey("machineLearningModelType")) {
                System.err.println("Unable to create trainer, the request does not contain predictor model type"); return false;
            }
            // !!!!!AGGIUNGERE CONTROLLO SE machineLearningModelType NOT NULL, NOT EMPTY E SENZA SPAZI DENTRO
            if(!trainerData.containsKey("trainingExpiration")) {
                System.err.println("Unable to create trainer, the request does not contain training expiration"); return false;
            }
            if(trainingDataTableName == null || trainingDataTableName.trim().isEmpty() || trainingDataTableName.trim().contains(" ")) {
                System.err.println("Unable to create trainer, invalid training data table name"); return false;
            }
            if(dataTableFieldNamesList == null || dataTableFieldNamesList.length < 1) {
                System.err.println("Unable to create trainer, no training table field name present"); return false;
            }
            for (int i =0; i < dataTableFieldNamesList.length; ++i){
                if(dataTableFieldNamesList[i] == null || dataTableFieldNamesList[i].trim().isEmpty() || dataTableFieldNamesList[i].trim().contains(" ")){
                    System.err.println("Unable to create trainer, field name list contains names without information"); return false;
                }
            }
            if(classificationField == null || classificationField.trim().isEmpty() || classificationField.trim().contains(" ")) {
                System.err.println("Unable to create trainer, invalid classification field name"); return false;
            }

            if(this.setMLTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList, classificationField)) {
                if (trainerData.get("machineLearningModelType").equals("linear_regression")) {
                    return setLRTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList);
                }
                if (trainerData.get("machineLearningModelType").equals("svm")) {
                    return setSVMTrainerData(trainerData, trainingDataTableName, dataTableFieldNamesList);
                }
                // CONTROLLO PER ABC E SVM VLIDITA CLASSIFICATION FIELD (==1, NOT NULL, NOT MPTY, SENZA SPAZI DENTRO)
                else {
                    System.out.println("Unknown model type entered, could not create trainer");
                    return false;
                }
            }
            else {
                return false;
            }

        }

        return true;
    }
    private boolean setSVMTrainerData(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList){
        /*
        private String svmType = null;
        private String kernelType = null;
        private int polDegree = 0;  // for poly
        private double gamma = 0;   // for poly/rbf/sigmoid
        private double coef0 = 0;   // for poly/sigmoid
        private double rho = 0;
        //final private ArrayList<ArrayList<Double>> supportVectors;
        private double paramC = 0;
        */
        // !!!!!AGGIUNGERE CONTROLLO SE SVMType NOT NULL, NOT EMPTY E SENZA SPAZI DENTRO
        if(!trainerData.containsKey("SVMType")) {
            System.err.println("Unable to create trainer, the request does not contain information about svm type"); return false;
        }
        String[] svmTypes = {"c_svc", "nu_svc", "one_class", "epsilon_svr", "nu_svr"};
        if(!Arrays.asList(svmTypes).contains(trainerData.get("SVMType").trim())){
            System.err.println("Unable to create trainer, the svm type entered does not exist"); return false;
        }
        if(!trainerData.containsKey("kernelType")) {
            System.err.println("Unable to create trainer, the request does not contain information about kernel type"); return false;
        }
        String[] kernelTypes = {"linear", "poly", "rbf", "sigmoid", "precomputed"};
        if(!Arrays.asList(kernelTypes).contains(trainerData.get("kernelType").trim())){
            System.err.println("Unable to create trainer, the kernel type entered does not exist"); return false;
        }
        if(trainerData.get("kernelType").trim().equals("poly") && !trainerData.containsKey("degree")){
            System.err.println("Unable to create trainer, for polynomial kernel must be specified degree"); return false;
        }
        String[] gammaKernels = {"poly", "rbf", "sigmoid"};
        if(Arrays.asList(gammaKernels).contains(trainerData.get("kernelType").trim()) && !trainerData.containsKey("gamma")){
            System.err.println("Unable to create trainer, for this type of kernel you need to specify gamma parameter"); return false;
        }
        String[] coef0Kernels = {"poly", "sigmoid"};
        if(Arrays.asList(coef0Kernels).contains(trainerData.get("kernelType").trim()) && !trainerData.containsKey("coef0")){
            System.err.println("Unable to create trainer, for this type of kernel you need to specify coef0 parameter"); return false;
        }
        if(trainerData.get("SVMType").trim().equals("c_svc") && !trainerData.containsKey("paramC")){
            System.err.println("Unable to create trainer, for c_svc model you need to specify C parameter"); return false;
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
            this.setCoef0(Double.parseDouble(trainerData.get("paramC").trim()));
        }

        return true;
    }
    private boolean setLRTrainerData(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList){
        /*
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + trainerData.get("predictorName") + ".json";
        File descriptionFile = new File(descriptionFilePath);
        try {
            if (!descriptionFile.createNewFile()) {
                System.err.println("Error creating description file");
                return false;
            }
        }
        catch (IOException e) {
            // Per errore creazione description file
            e.getMessage(); // !!!!!!!!!!!!!!!!!!!!!!!!!! DA DESCRIVERE MEGLIO ERRORE
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
        */

        int parametersNumber = dataTableFieldNamesList.length + 1;
        this.setXtX(this.rectMatrix(parametersNumber,parametersNumber));
        this.setXtY(this.rectMatrix(parametersNumber,1));

        double[] parametersLR = new double[parametersNumber];
        for(int i =0; i < parametersLR.length; ++i){
            parametersLR[i] = 0;
        }
        this.setParametersLR(parametersLR);

        return true; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    private boolean setMLTrainerData(Map<String,String> trainerData, String trainingDataTableName, String[] dataTableFieldNamesList, String classificationField){
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + trainerData.get("predictorName") + ".json";
        File descriptionFile = new File(descriptionFilePath);
        try {
            if (!descriptionFile.createNewFile()) {
                System.err.println("Error creating description file");
                return false;
            }
        }
        catch (IOException e) {
            // Per errore creazione description file
            e.getMessage(); // !!!!!!!!!!!!!!!!!!!!!!!!!! DA DESCRIVERE MEGLIO ERRORE
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

    private boolean createSVMDescriptionFile(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("predictor_name", new JsonPrimitive(this.predictorName));
        jsonObject.add("model_type", new JsonPrimitive("svm"));
        jsonObject.add("version", new JsonPrimitive(this.version));
        jsonObject.add("last_train", new JsonPrimitive(this.lastTrain));
        jsonObject.add("training_expiration", new JsonPrimitive(this.trainingExpiration));
        jsonObject.add("training_data_table_name", new JsonPrimitive(this.trainingDataTableName));
        jsonObject.add("classification_field_name", new JsonPrimitive(this.classificationField));
        JsonArray trainingFieldListJsonArray = convertToJsonArrayFieldNamesList(this.trainingFieldNamesList);
        jsonObject.add("training_field_names_list", trainingFieldListJsonArray);

        JsonObject modelData = new JsonObject();
        modelData.add("svm_type", new JsonPrimitive(this.SVMType));
        modelData.add("kernel_type", new JsonPrimitive(this.kernelType));
        if (this.kernelType.equals("poly")) {
            modelData.add("degree", new JsonPrimitive(this.polDegree));
        }
        if (this.kernelType.equals("poly") || this.kernelType.equals("rbf") || this.kernelType.equals("sigmoid")) {
            modelData.add("gamma", new JsonPrimitive(this.gamma));
        }
        if (this.kernelType.equals("poly") || this.kernelType.equals("sigmoid")) {
            modelData.add("coef0", new JsonPrimitive(this.coef0));
        }
        jsonObject.add("model_data", modelData);



        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.predictorName + ".json";
        try (FileWriter writer = new FileWriter(descriptionFilePath)) {
            gson.toJson(jsonObject, writer);
        }
        catch (IOException e) {
            System.out.println("Error writing description file, failed to update parameters: ");
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
            System.out.println("Error writing description file, failed to update parameters: ");
            e.printStackTrace();
            return false;
        }
        return true;
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



    //////////////////////////////////////  SETTERS  //////////////////////////////////////////////
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
    /*
    public MLTrainerBuilder set(String ype){
        this
                .Type = Type;
        return this;
    }
    */
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

package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import libsvm.svm_problem;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_model;
import libsvm.svm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SVMTrainer extends MLTrainer{
    private String SVMType = null;
    private String kernelType = null;
    private int degree = 0;
    private double gamma = 0;
    private double coef0 = 0;
    private double paramC = 0;
    private double rho = 0;
    private ArrayList<ArrayList<Double>> supportVectors = null;


    public SVMTrainer(String predictorName, int version, String lastTrain, int trainingExpiration, String trainingDataTableName, String[] trainingFieldNamesList, String classificationField, String svmType, String kernelType, int polDegree, double gamma, double coef0, double rho, double paramC){
        super(predictorName, "svm", version, lastTrain, trainingExpiration, trainingDataTableName, trainingFieldNamesList, classificationField);
        this.SVMType = svmType;
        this.kernelType = kernelType;
        this.degree = polDegree;
        this.gamma = gamma;
        this.coef0 = coef0;
        this.rho = rho;
        this.paramC = paramC;
    }

    public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnetter){
        double[][] samples = dbConnetter.getTrainingData(dataTableName, dataTableFieldNamesList, classificationFieldName);

        int numExamples = samples.length;
        int numFeatures = samples[0].length - 1;

        svm_problem problem = new svm_problem();
        problem.l = numExamples;
        problem.x = new svm_node[numExamples][numFeatures];
        problem.y = new double[numExamples];

        for (int i=0; i < numExamples; i++){
            problem.y[i] = samples[i][numFeatures];
            problem.x[i] = new svm_node[numFeatures];

            for (int j=0; j < numFeatures ; j++){
                problem.x[i][j] = new svm_node();
                problem.x[i][j].index = j+1;
                problem.x[i][j].value = samples[i][j];
            }
        }

        svm_parameter param = new svm_parameter();

        switch (this.SVMType){
            case "c_svc":
                param.svm_type = svm_parameter.C_SVC; break;
            case "nu_svc":
                param.svm_type = svm_parameter.NU_SVC; break;
            case "one_class":
                param.svm_type = svm_parameter.ONE_CLASS; break;
            case "epsilon_svr":
                param.svm_type = svm_parameter.EPSILON_SVR; break;
            case "nu_svr":
                param.svm_type = svm_parameter.NU_SVR; break;
            default:
                System.out.println("Valore tipo SVM non valido");
                return false;
        }
        switch (this.kernelType){
            case "linear":
                param.kernel_type = svm_parameter.LINEAR; break;
            case "polynomial":
                param.kernel_type = svm_parameter.POLY;
                param.degree = this.degree;
                param.gamma = this.gamma;
                param.coef0 = this.coef0;
                break;
            case "rbf":
                param.kernel_type = svm_parameter.RBF;
                param.gamma = this.gamma;
                break;
            case "sigmoid":
                param.kernel_type = svm_parameter.SIGMOID; break;
            case "precomputed":
                param.kernel_type = svm_parameter.PRECOMPUTED; break;
            default:
                System.out.println("Valore tipo kernel non valido");
                return false;
        }

        param.C = this.paramC;

        svm_model model = svm.svm_train(problem, param);

        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + this.getPredictorName() + ".model";
        try {
            File file = new File(modelFilePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }

            svm.svm_save_model(modelFilePath, model);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(!this.createDescriptionFile()){
            System.err.println("Error creating description file");
            return false;
        }
        return true;
    }

    private boolean createDescriptionFile() {
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + this.getPredictorName() + ".model";
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.getPredictorName() + ".json";

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
        completeObject.add("predictor_name", new JsonPrimitive(this.getPredictorName()));
        completeObject.add("model_type", new JsonPrimitive("svm"));
        completeObject.add("version", new JsonPrimitive(1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String trainingDateTime = dateTime.format(formatter);
        completeObject.add("last_train", new JsonPrimitive(trainingDateTime));
        completeObject.add("training_expiration", new JsonPrimitive(this.getTrainingExpiration()));
        completeObject.add("training_data_table_name", new JsonPrimitive(this.getTrainingDataTableName()));
        completeObject.add("classification_field_name", new JsonPrimitive(this.getClassificationField().trim()));
        JsonArray trainingFieldListJsonArray = convertToJsonArrayFieldNamesList(this.getTrainingFieldNamesList());
        completeObject.add("training_field_names_list", trainingFieldListJsonArray);
        JsonObject modelData = new JsonObject();
        try (BufferedReader modelFileReader = new BufferedReader(new FileReader(modelFilePath))) {
            String lineFromModelFile;
            while ((lineFromModelFile = modelFileReader.readLine()) != null) {
                String[] splittedLine = lineFromModelFile.split(" ");
                if (splittedLine[0].equals("svm_type")) {
                    modelData.add("svm_type", new JsonPrimitive(splittedLine[1]));
                }
                else if (splittedLine[0].equals("kernel_type")) {
                    modelData.add("kernel_type", new JsonPrimitive(splittedLine[1]));
                }
                else if (splittedLine[0].equals("degree")) {
                    modelData.add("degree", new JsonPrimitive(Integer.parseInt(splittedLine[1])));
                }
                else if (splittedLine[0].equals("gamma")) {
                    modelData.add("gamma", new JsonPrimitive(Double.parseDouble(splittedLine[1])));
                }
                else if (splittedLine[0].equals("coef0")) {
                    modelData.add("coef0", new JsonPrimitive(Double.parseDouble(splittedLine[1])));
                }
                else if (splittedLine[0].equals("nr_class")) {
                    modelData.add("nr_class", new JsonPrimitive(Integer.parseInt(splittedLine[1])));
                }
                else if (splittedLine[0].equals("total_sv")) {
                    modelData.add("total_sv", new JsonPrimitive(Integer.parseInt(splittedLine[1])));
                }
                else if (splittedLine[0].equals("rho")) {
                    modelData.add("rho", new JsonPrimitive(Double.parseDouble(splittedLine[1])));
                }
                else if (splittedLine[0].equals("label")) {
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 1; i < splittedLine.length; ++i) {
                        jsonArray.add(Double.parseDouble(splittedLine[i]));
                    }
                    modelData.add("label", jsonArray);
                }
                else if (splittedLine[0].equals("nr_sv")) {
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 1; i < splittedLine.length; ++i) {
                        jsonArray.add(Integer.parseInt(splittedLine[i]));
                    }
                    modelData.add("nr_sv", jsonArray);
                    modelData.add("paramC", new JsonPrimitive(this.paramC));
                }
                else if (splittedLine[0].equals("SV")) {
                    JsonArray allVectors = new JsonArray();
                    String vectorsLine;
                    while ((vectorsLine = modelFileReader.readLine()) != null) {
                        JsonArray jsonVector = new JsonArray();
                        String[] splittedVectorsLine = vectorsLine.split(" ");
                        jsonVector.add(Double.parseDouble(splittedVectorsLine[0]));
                        for (int i = 1; i < splittedVectorsLine.length; ++i) {
                            jsonVector.add(Double.parseDouble(splittedVectorsLine[i].split(":")[1]));
                        }
                        allVectors.add(jsonVector);
                    }
                    modelData.add("support_vectors",allVectors);
                }
            }
            completeObject.add("model_data", modelData);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
    private JsonArray convertToJsonArrayFieldNamesList(String[] array) {
        JsonArray jsonArray = new JsonArray();
        for (String value : array) {
            jsonArray.add(new JsonPrimitive(value.trim()));
        }
        return jsonArray;
    }
}




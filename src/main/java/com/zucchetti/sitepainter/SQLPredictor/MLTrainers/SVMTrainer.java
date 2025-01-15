package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.*;
import libsvm.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SVMTrainer extends MLTrainer{
    // --- TUTTI CAMPI FINAL ???

    private String SVMType = ""; // ?????????????????
    private String kernelType = "";
    private int degree = 0;
    private double gamma = 0;
    private double coef0 = 0;
    private double rho = 0;
    private double paramC =0;

    //final private ArrayList<ArrayList<Double>> supportVectors;

    public SVMTrainer(String predictorName, int version, String lastTrain, int trainingExpiration, String trainingDataTableName, String[] trainingFieldNamesList, String svmType, String kernelType, int polDegree, double gamma, double coef0, double rho, double paramC){
        // !!!!!!!!!!!!! CONTROLLO SE PREDICTOR E MODEL NON ESISTONO GIA
        super(predictorName, "svm", version, lastTrain, trainingExpiration, trainingDataTableName, trainingFieldNamesList);

        this.SVMType = svmType;
        this.kernelType = kernelType;
        this.degree = polDegree;
        this.gamma = gamma;
        this.coef0 = coef0;
        this.rho = rho; // !!!!!!!!!! FORSE NON SERVE
        this.paramC = paramC;
    }

    public void train(double[][] data, double[] classType){
        int numExamples = data.length;
        int numFeatures = data[0].length;

        svm_problem problem = new svm_problem();
        problem.l = numExamples;
        problem.x = new svm_node[numExamples][numFeatures];
        problem.y = new double[numExamples];

        for (int i=0; i < numExamples; i++){
            problem.y[i] = classType[i];
            problem.x[i] = new svm_node[numFeatures];

            for (int j=0; j < numFeatures ; j++){
                problem.x[i][j] = new svm_node();
                problem.x[i][j].index = j+1;
                problem.x[i][j].value = data[i][j];
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
                System.out.println("Valore tipo SVM non valido"); break;
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
                System.out.println("Valore tipo kernel non valido"); break;
        }

        param.C = paramC;

        svm_model model = svm.svm_train(problem, param);

        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + this.getPredictorName() + ".model";
        try {
            File file = new File(modelFilePath);
            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        System.out.println("File creato con successo: " + file.getName());
                    }
                    else {
                        System.out.println("Errore nella creazione del file.");
                    }
                }
                catch (IOException e) {
                    System.out.println("Si è verificato un errore: " + e.getMessage());
                }
            }

            svm.svm_save_model(modelFilePath, model);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.createDescriptionFile();
    }

    private void createDescriptionFile() {

        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + this.getPredictorName() + ".model";
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.getPredictorName() + ".json";

        File descriptionFile = new File(descriptionFilePath);

        if (!descriptionFile.exists()) {
            try {
                if (!descriptionFile.createNewFile()) {
                    System.out.println("Failed to create description file");
                    return; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                }
            } catch (IOException e) {
                e.printStackTrace();
                //!!!!!!!!!!!!!!! DA SVRIVERE MEGLIO
            }
        }

        JsonObject completeObject = new JsonObject();
        completeObject.add("predictor_name", new JsonPrimitive(this.getPredictorName()));
        completeObject.add("model_type", new JsonPrimitive("svm"));
        completeObject.add("version", new JsonPrimitive(this.getVersion()));
        completeObject.add("last_train", new JsonPrimitive(this.getPredictorName()));

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
                else if (splittedLine[0].equals("rho")) { //!!!!!!!!!!!!!!!! PUO ESSERE FORMARO E-9
                    modelData.add("rho", new JsonPrimitive(Double.parseDouble(splittedLine[1])));
                    //modelData.add("rho", new JsonPrimitive(splittedLine[1]));
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
                    // !!!!!!!!!!!!!!!! DA PORTARE FUORI
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
            //!!!!!!!!!!!!!!! DA SVRIVERE MEGLIO (errore lettura .model o scrittura in .json)
        }
        catch (IOException e) {
            e.printStackTrace();
            //!!!!!!!!!!!!!!! DA SVRIVERE MEGLIO (errore lettura .model o scrittura in .json)
        }
        try (FileWriter writer = new FileWriter(descriptionFilePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Scrivi l'array JSON nel file di output
            gson.toJson(completeObject, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
            //!!!!!!!!!!!!!!! DA SVRIVERE MEGLIO (errore lettura .model o scrittura in .json)
        }
    }
}




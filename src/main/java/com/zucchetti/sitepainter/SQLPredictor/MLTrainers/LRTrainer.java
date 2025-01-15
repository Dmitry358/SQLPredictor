package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LRTrainer extends MLTrainer{
    // --- TUTTI CAMPI FINAL??
    private double[][] transposeOfXTimesX;
    private double[][] transposeOfXTimesY;
    final private double[][] identity;
    private double[] parametersLR; //???double[][]


    public LRTrainer(String predictorName, int version, String lastTrain, int trainingExpiration, double[][] xTx, double[][] xTy, double[] parametersLR, String trainingDataTableName, String[] trainingFieldNamesList) {
    //public LRTrainer(String predictorName, String trainingModelType, int version, String lastTrain, int numX, int numY) {
        super(predictorName, "linear_regression", version, lastTrain, trainingExpiration, trainingDataTableName, trainingFieldNamesList);
        this.transposeOfXTimesX = xTx;
        this.transposeOfXTimesY = xTy;
        this.identity = identityMatrix(xTx.length);
        this.parametersLR = parametersLR;

        /*
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";
        File descriptionFile = new File(descriptionFilePath);

        if (descriptionFile.exists()) {
            try {
                FileReader reader = new FileReader(descriptionFilePath); // ???FRSE SI PUO METTERE COME CONTROLLO ESISTENZA DEL FILE
                JsonElement jsonElement = JsonParser.parseReader(reader);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    int f=0;
                    for (String key : jsonObject.keySet()) {
                        if(key.equals("predictor_name")){
                            this.predictorName = jsonObject.get(key).getAsString(); ++f;
                        }
                        else if(key.equals("version")) {
                            this.version = Integer.parseInt(jsonObject.get(key).getAsString()); ++f;
                        }
                        else if(key.equals("last_train")) {
                            this.lastTrain = jsonObject.get(key).getAsString(); ++f;
                        }
                        else if(key.equals("parameters")) {
                            JsonArray parametersJson = jsonObject.get(key).getAsJsonArray();
                            double[] parametersFromJson = new double[parametersJson.size()];

                            for (int i = 0; i < parametersJson.size(); i++) {
                                parametersFromJson[i] = parametersJson.get(i).getAsDouble();
                            }
                            this.parametersLR = parametersFromJson; ++f;
                        }
                        else if(key.equals("xTx")) {
                            JsonArray xTxJson = jsonObject.get(key).getAsJsonArray();
                            double[][] xTxFromJson = new double[xTxJson.size()][];

                            for (int i = 0; i < xTxJson.size(); i++) {
                                JsonArray xTxJsonRow = xTxJson.get(i).getAsJsonArray();
                                double[] xTxFromJsonRow = new double[xTxJsonRow.size()] ;

                                for (int j = 0; j < xTxJsonRow.size(); j++) {
                                    xTxFromJsonRow[j] = xTxJsonRow.get(j).getAsDouble();
                                }
                                xTxFromJson[i] = xTxFromJsonRow;
                            }

                            this.transposeOfXTimesX = xTxFromJson; ++f;
                        }
                        else if(key.equals("xTy")) {
                            JsonArray xTyJson = jsonObject.get(key).getAsJsonArray();
                            double[][] xTyFromJson = new double[xTyJson.size()][];

                            for (int i = 0; i < xTyJson.size(); i++) {
                                JsonArray xTyJsonRow = xTyJson.get(i).getAsJsonArray();
                                double[] xTyFromJsonRow = new double[xTyJsonRow.size()] ;

                                for (int j = 0; j < xTyJsonRow.size(); j++) {
                                    xTyFromJsonRow[j] = xTyJsonRow.get(j).getAsDouble();
                                }
                                xTyFromJson[i] = xTyFromJsonRow;
                            }

                            this.transposeOfXTimesY = xTyFromJson; ++f;
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
                            //////////////////////
                        }
                    }
                    if (f < 6){
                        System.out.println("Description file does not contain all information needed to create object");
                    }
                }
                else{
                    System.out.println("Description file has wrong structure");
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("Description file of predictor \"" + predictorName + "\" is not found");
            }
            catch (JsonIOException e) {
                System.out.println("Error of processing description file");
            }
            catch (JsonSyntaxException e) {
                System.out.println("Syntax of description file is incorrect");
            }

            this.identity = this.identityMatrix(numX);
        }
        else { //////////////// description file NON esiste //////////////////////
            try {
                if (descriptionFile.createNewFile()) {
                    this.predictorName = predictorName;
                    this.transposeOfXTimesX = this.rectMatrix(numX, numX);
                    this.transposeOfXTimesY = this.rectMatrix(numX, numY);
                    this.identity = this.identityMatrix(numX);
                    this.parameters = this.calculateCoefficients();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.now();
                    String formattedDateTime = dateTime.format(formatter);

                    JsonArray parametersJsonArray = convertToJsonArrayParameters(this.parameters);
                    JsonArray xTxJsonArray = convertToJsonArrayMatrices(this.transposeOfXTimesX);
                    JsonArray xTyJsonArray = convertToJsonArrayMatrices(this.transposeOfXTimesY);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("predictor_name", new JsonPrimitive(this.predictorName));
                    jsonObject.add("model_type", new JsonPrimitive("linear_regression"));
                    jsonObject.add("version", new JsonPrimitive(1));
                    jsonObject.add("last_train", new JsonPrimitive(formattedDateTime));
                    jsonObject.add("parameters", parametersJsonArray);
                    jsonObject.add("xTx", xTxJsonArray);
                    jsonObject.add("xTy", xTyJsonArray);

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    try (FileWriter writer = new FileWriter(descriptionFilePath)) {
                        gson.toJson(jsonObject, writer);
                    }
                    catch (IOException e) {
                        System.out.println("Error writing description file:");
                        e.printStackTrace();
                    }
                }
                else {
                    System.out.println("The file already exists"); return; // MESSAGGIO CORRETTO???
                }
            }
            catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage()); //!!!!!!!!!! return;
            }
        }
        */
    }

    // !!!!!!!!!!!!!!!!!!!!! passare nome tabella e lista di campi
    public void train(double[][] samples) {
        // !!! CONTROLLO SE DATI COMPATIBILI CON MODELLO
        //!!!!AGGIORNARE FILE JSON (XtX, XtY) (ECCEZIONI: NON TROVA FIL DA SCRIVERE, FILEha )
        if(samples.length > 0){
            if(samples[0].length != transposeOfXTimesX.length){
                System.out.println("Data size is incompatible with model");
            }
            else {
                int sampleDataLength = samples[0].length - 1;
                for (int i = 0; i < samples.length; ++i) {
                    double[] sampleData = new double[sampleDataLength];
                    System.arraycopy(samples[i], 0, sampleData, 0, sampleDataLength);
                    this.addSample(sampleData, samples[i][sampleDataLength]);    // !!!!!! DA VERIFICARE CORRETTEZZA
                }
            }
        }
        else {
            System.out.println("Data size is incompatible with model");
        }

        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.getPredictorName() + ".json";
        File descriptionFile = new File(descriptionFilePath);

        try{
            if (!descriptionFile.exists()){
                if (!descriptionFile.createNewFile()) {
                    System.out.println("Description file does not exist and could not be created");
                    //!!!!!!!!!!!!! INTERRUPT
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.now();
            String trainingDateTime = dateTime.format(formatter);
            this.incrementVersion();
            this.setLastTrain(trainingDateTime);
            JsonArray parametersJsonArray = convertToJsonArrayParameters(this.parametersLR);
            JsonArray xTxJsonArray = convertToJsonArrayMatrices(this.transposeOfXTimesX);
            JsonArray xTyJsonArray = convertToJsonArrayMatrices(this.transposeOfXTimesY);

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("predictor_name", new JsonPrimitive(this.getPredictorName()));
            jsonObject.add("model_type", new JsonPrimitive("linear_regression"));
            jsonObject.add("version", new JsonPrimitive(this.getVersion()));
            jsonObject.add("last_train", new JsonPrimitive(trainingDateTime));
            jsonObject.add("parameters", parametersJsonArray);
            jsonObject.add("xTx", xTxJsonArray);
            jsonObject.add("xTy", xTyJsonArray);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(descriptionFilePath)) {
                gson.toJson(jsonObject, writer);
            }
            catch (IOException e) {
                System.out.println("Error writing description file, failed to update parameters: ");
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            // Per errore creazione description file
            e.getMessage();
        }
    }

    private JsonObject getDescriptionFileData(String predictorName){
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";
        File descriptionFile = new File(descriptionFilePath);

        JsonObject jsonObject = null;

        try {
            FileReader reader = new FileReader(descriptionFilePath);
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            }
            else{
                System.out.println("Description file has incorrect structure, reading failed");
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
        } //???? SERVE ????
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

    private JsonArray convertToJsonArrayParameters(double[] array) {
        JsonArray jsonArray = new JsonArray();
        for (double value : array) {
            jsonArray.add(new JsonPrimitive(value));
        }
        return jsonArray;
    }
    //!!!!!! COSA TORNARE SE NON HA
    private double[][] getXtXMatrixFromJson(JsonObject jsonObject){
        double[][] xTxMatrix;

        if(jsonObject.has("xTx")) {
            JsonArray matrixJsonArray = jsonObject.getAsJsonArray("xTx");
            xTxMatrix = new double[matrixJsonArray.size()][];

            for (int i = 0; i < matrixJsonArray.size(); i++) {
                JsonArray row = matrixJsonArray.get(i).getAsJsonArray();
                xTxMatrix[i] = new double[row.size()];
                for (int j = 0; j < row.size(); j++) {
                    xTxMatrix[i][j] = row.get(j).getAsDouble();
                }
            }
        }
        else {
            System.out.println("Description file does not contain xTx field");
            return null; //???????????
        }
        double[][] matrix =xTxMatrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        return  xTxMatrix;
    }

    private double[][] getXtYMatrixFromJson(JsonObject jsonObject){
        double[][] xTyMatrix;

        if(jsonObject.has("xTy")) {
            JsonArray matrixJsonArray = jsonObject.getAsJsonArray("xTy");
            xTyMatrix = new double[matrixJsonArray.size()][];

            for (int i = 0; i < matrixJsonArray.size(); i++) {

                double[] value = new double[1];
                value[0] = matrixJsonArray.get(i).getAsDouble();
                xTyMatrix[i] = value;
            }
        }
        else {
            System.out.println("Description file does not contain xTy field");
            return null; //???????????
        }
        double[][] matrix =xTyMatrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        return  xTyMatrix;
    }

    private void addSample(double[] sampleData, double sampleValue){
        int dataLenght = sampleData.length + 1;
        double[] dataVector = new double[dataLenght];
        dataVector[0] = 1;
        System.arraycopy(sampleData,0, dataVector,1, sampleData.length);
        /*
            for (int i = 0; i < sampleData.length; ++i) {
                dataVector[i + 1] = sampleData[i];
            }
            */
        double[] valueVector = new double[1];
        valueVector[0] = sampleValue;
        this.addObservation(dataVector, valueVector);
    }

    private double[] calculateCoefficients() {// !!!!!!!! SCRIVRE NEL JSON
        double[][] xTx = this.transposeOfXTimesX;
        double[][] xTy = this.transposeOfXTimesY;
        double[][] inv = this.inverse(xTx, this.identity);
        double[][] coefficients = multiply(inv, xTy);
        double[] parameters = new double[coefficients.length];
        for (int i =0; i < coefficients.length; ++i){
            parameters[i] = coefficients[i][0];
        }
        return parameters;
    }

    private double[][] multiply(double[][] lhs, double[][] rhs) { //!!!!! lsh = inv (X_t*X^-1) dim(FxF); rhs = xTy (X_t*y) dim(Fx1)
        //!!!!!! controllare compatibilità dimensioni paramtri
        double[][] streamingProduct = this.rectMatrix(lhs.length, rhs[0].length);
        for(int x = 0; x < rhs.length; ++x) {
            double[] lhsColumn = new double[lhs.length];
            for(int r = 0; r < lhs.length; ++r) {
                lhsColumn[r] = lhs[r][x];
            }
            double[] rhsRow = rhs[x]; // Get the xth row of rhs.
            this.addRowAndColumn(streamingProduct, lhsColumn, rhsRow);
        }
        return streamingProduct;
    }

    private double[][] inverse(double[][] matrix, double[][] identity) {
        // ???? controllare se matrice è invertibile??
        //controllo se matrix e identity non è vuota (per uso "matrix[0].length e identity[0].length")
        //!!!!controllo compatibilta dimensione matrix, identity
        //matrixRowNum == size
        int matrixRowNum = matrix.length;
        int matrixColumnNum = matrix[0].length;
        int identityColumnNum = identity[0].length;
        double[][] result = new double[matrixRowNum][matrixColumnNum + matrixRowNum]; //???
        for(int r = 0; r < matrixRowNum; ++r){
            /*
            for(int mc = 0; mc < matrixRowNum; ++mc){ result[r][mc] = matrix[r][mc]; }
            for(int ic = 0; ic < identityColumnNum; ++ic){ result[r][matrixColumnNum + ic] = identity[r][ic]; }
            */
            System.arraycopy(matrix[r], 0, result[r],0, matrixRowNum);
            System.arraycopy(identity[r], 0, result[r], matrixColumnNum, identityColumnNum);
        }

        result = this.rref(result);
        double[][] inverse = new double[matrixRowNum][matrixColumnNum];

        for(int r = 0; r < matrixRowNum; ++r){
            System.arraycopy(result[r], matrixRowNum, inverse[r],0, matrixColumnNum);
            /*
            for(int c = 0; c < matrixColumnNum; ++c){
                inverse[r][c] = result[r][c + matrixRowNum];//???????????
            }
            */
        }
        return inverse;
    }

    private double[][] rref(double[][] A) {
        // !! controllo ch dimensioni da A !=0
        int rows = A.length;
        int columns = A[0].length;

        int lead = 0; //?? double
        for (int k = 0; k < rows; ++k) {
            if (columns <= lead) { break; } //return;

            int i = k;
            while (A[i][lead] == 0) {
                i++;
                if (rows == i) {
                    i = k;
                    lead++;
                    if (columns == lead) { break; } //return;
                }
            }
            double[] irow = A[i];
            double[] krow = A[k];
            A[i] = krow;
            A[k] = irow;

            double val = A[k][lead]; //???int
            for (int j = 0; j < columns; ++j) {
                A[k][j] /= val; //?????
            }

            for (int r = 0; r < rows; ++r) { //!!!!!!!!
                if (r == k) { continue; }
                val = A[r][lead];
                for (var j = 0; j < columns; ++j) {
                    A[r][j] -= val * A[k][j];
                }
            }
            lead++;
        }
        return A;

    }

    private void addObservation(double[] x, double[] y){
        this.addRowAndColumn(this.transposeOfXTimesX, x, x);
        this.addRowAndColumn(this.transposeOfXTimesY, x, y);
        this.parametersLR = this.calculateCoefficients(); // !!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private void addRowAndColumn(double[][] product, double[] lhsColumn, double[] rhsRow){
        for(int r = 0; r < lhsColumn.length; ++r) {
            for (int c = 0; c < rhsRow.length; ++c) {
                product[r][c] += lhsColumn[r] * rhsRow[c];
            }
        }
    }

    private double[][] rectMatrix(int numRows, int numColumns){
        double[][] matrix = new double[numRows][];
        for (int r = 0; r < numRows; ++r) {
            double[] row = new double[numColumns];
            matrix[r] = row;
            Arrays.fill(row, 0);
            /*
            for (int c = 0; c < numColumns; c++) {
                row[c] = 0;
            }
            */
        }
        return matrix;
    }

    private double[][] identityMatrix(int size){
        double[][] matrix = this.rectMatrix(size, size);
        for(int i = 0; i < size; ++i) {
            matrix[i][i] = 1;
        }
        return matrix;
    }
}
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
import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LRTrainer extends MLTrainer {
    private double[][] transposeOfXTimesX;
    private double[][] transposeOfXTimesY;
    final private double[][] identity;
    private double[] parametersLR;


    public LRTrainer(String predictorName, int version, String lastTrain, int trainingExpiration, double[][] xTx, double[][] xTy, double[] parametersLR, String trainingDataTableName, String[] trainingFieldNamesList, String classificationField) {
        super(predictorName, "linear_regression", version, lastTrain, trainingExpiration, trainingDataTableName, trainingFieldNamesList, classificationField);
        this.transposeOfXTimesX = xTx;
        this.transposeOfXTimesY = xTy;
        if(xTx != null){
            this.identity = identityMatrix(xTx.length);
        }
        else{
            this.identity = null;
        }
        this.parametersLR = parametersLR;
    }

    public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnetter){
        // public boolean train(double[][] samples, double[] classType) {
        // !!! CONTROLLO SE DATI COMPATIBILI CON MODELLO
        // !!!! AGGIORNARE FILE JSON (XtX, XtY) (ECCEZIONI: NON TROVA FIL DA SCRIVERE, FILEha )
        // !!!!!!!!!!!!! CONTROLLO SE this != null

        double[][] samples = dbConnetter.getTrainingData(dataTableName, dataTableFieldNamesList, classificationFieldName);
        // !!!!!!!!!!!!!!!!!!!! Controllo se samples != null altrimenti samples.length dara errore
        if(samples.length > 0){
            if(samples[0].length != this.transposeOfXTimesX.length){
                System.err.println("Data size is incompatible with model");
                return false;
            }
            else{
              int sampleDataLength = samples[0].length - 1;
              for (double[] sample : samples) {
                double[] sampleData = new double[sampleDataLength];
                System.arraycopy(sample, 0, sampleData, 0, sampleDataLength);
                this.addSample(sampleData, sample[sampleDataLength]);
              }
            }
        }
        else {
            System.err.println("Training data is empty");
            return false;
        }

        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + this.getPredictorName() + ".json";
        File descriptionFile = new File(descriptionFilePath);

        try{
            if (!descriptionFile.exists()){
                if (!descriptionFile.createNewFile()) {
                    System.err.println("Description file does not exist and could not be created");
                    return false;
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
            jsonObject.add("training_expiration", new JsonPrimitive(this.getTrainingExpiration()));
            jsonObject.add("training_data_table_name", new JsonPrimitive(this.getTrainingDataTableName()));
            jsonObject.add("classification_field_name", new JsonPrimitive(this.getClassificationField()));
            JsonArray trainingFieldListJsonArray = convertToJsonArrayFieldNamesList(this.getTrainingFieldNamesList());
            jsonObject.add("training_field_names_list", trainingFieldListJsonArray);
            jsonObject.add("parametersLR", parametersJsonArray);
            jsonObject.add("xTx", xTxJsonArray);
            jsonObject.add("xTy", xTyJsonArray);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(descriptionFilePath)) {
                gson.toJson(jsonObject, writer);
            }
            catch (IOException e) {
                System.err.println("Error writing description file, failed to update parameters: ");
                e.printStackTrace();
                return false;
            }
        }
        catch (IOException e) {
            e.getMessage();
            return false;
        }

        return true;
    }

    // --------------------- METODI TECNICI MIEI ------------------------
    private JsonObject getDescriptionFileData(String predictorName) {
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";
        File descriptionFile = new File(descriptionFilePath);
        JsonObject jsonObject = null;

        try(FileReader reader = new FileReader(descriptionFilePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            //if (jsonElement.isJsonObject()) { //!!!!!!!!!!!! SOSTITUITO DURANTE TESTING 20.05.25
            if (jsonElement != null && jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            }
            else{
                System.err.println("Description file has incorrect structure, reading failed");
                return null;
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Description file of predictor \"" + predictorName + "\" is not found");
            return null;
        }
        catch (JsonIOException e) {
            System.err.println("Error of processing description file");
            return null;
        }
        catch (JsonSyntaxException e) {
            System.err.println("Syntax of description file is incorrect");
            return null;
        } //???? SERVE ????
        catch (IOException e) {
          e.printStackTrace();
          return null;
        }
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
    private JsonArray convertToJsonArrayFieldNamesList(String[] array) {
        JsonArray jsonArray = new JsonArray();
        for (String value : array) {
            jsonArray.add(new JsonPrimitive(value));
        }
        return jsonArray;
    }

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
            System.err.println("Description file does not contain xTx field");
            return null;
        }
        double[][] matrix =xTxMatrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                //System.out.print(matrix[i][j] + " ");
            }
            //System.out.println();
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
            System.err.println("Description file does not contain xTy field");
            return null;
        }
        double[][] matrix =xTyMatrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                //System.out.print(matrix[i][j] + " ");
            }
            //System.out.println();
        }

        return  xTyMatrix;
    }

    // ------------------- METODI TECNICI DI MODELLO --------------------
    private void addSample(double[] sampleData, double sampleValue){
        int dataLenght = sampleData.length + 1;
        double[] dataVector = new double[dataLenght];
        dataVector[0] = 1;
        System.arraycopy(sampleData,0, dataVector,1, sampleData.length);
        double[] valueVector = new double[1];
        valueVector[0] = sampleValue;
        this.addObservation(dataVector, valueVector);
    }

    private void addObservation(double[] x, double[] y){
        this.addRowAndColumn(this.transposeOfXTimesX, x, x);
        this.addRowAndColumn(this.transposeOfXTimesY, x, y);
        this.parametersLR = this.calculateCoefficients(); // !!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private double[] calculateCoefficients() {
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
        //!!!!!! controllare compatibilità dimensioni parametri
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

    private void addRowAndColumn(double[][] product, double[] lhsColumn, double[] rhsRow){
        for(int r = 0; r < lhsColumn.length; ++r) {
            for (int c = 0; c < rhsRow.length; ++c) {
                product[r][c] += lhsColumn[r] * rhsRow[c];
            }
        }
    }

    private double[][] identityMatrix(int size){
        double[][] matrix = this.rectMatrix(size, size);
        for(int i = 0; i < size; ++i) {
            matrix[i][i] = 1;
        }
        return matrix;
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
}
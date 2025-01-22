package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;

public abstract class MLTrainer {
    final private String predictorName;
    final private String trainingModelType;
    private int version;
    private String lastTrain = null;
    final private int trainingExpiration;
    final private String trainingDataTableName;
    private String[] trainingFieldNamesList;
    private String classificationField;

    public MLTrainer(String predictorName, String trainingModelType, int version, String lastTrain, int trainingExpiration, String trainingDataTableName, String[] trainingFieldNamesList, String classificationField){
        this.predictorName = predictorName;
        this.trainingModelType = trainingModelType;
        this.version = version;
        this.lastTrain = lastTrain;
        this.trainingExpiration = trainingExpiration;
        this.trainingDataTableName = trainingDataTableName;
        this.trainingFieldNamesList = trainingFieldNamesList;
        this.classificationField = classificationField;
    }

    public abstract boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnetter);

    protected void incrementVersion(){
        ++this.version;
    }
    protected void setLastTrain(String trainingDateTime){
        this.lastTrain = trainingDateTime;
    }

    protected String getPredictorName(){
        return this.predictorName;
    }
    protected int getVersion(){
        return this.version;
    }
    protected String getLastTrain(){
        return this.lastTrain;
    }

    protected int getTrainingExpiration(){
        return this.trainingExpiration;
    }
    protected String getTrainingDataTableName(){
        return this.trainingDataTableName;
    }
    protected String[] getTrainingFieldNamesList(){
        return this.trainingFieldNamesList;
    }
    protected String getClassificationField(){ return this.classificationField;}

}


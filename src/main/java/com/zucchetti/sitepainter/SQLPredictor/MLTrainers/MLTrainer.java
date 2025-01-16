package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

public abstract class MLTrainer {
    final private String predictorName;
    final private String trainingModelType;
    private int version;
    private String lastTrain = null;
    final private int trainingExpiration;
    final private String trainingDataTableName;
    private String[] trainingFieldNamesList;

    public MLTrainer(String predictorName, String trainingModelType, int version, String lastTrain, int trainingExpiration, String trainingDataTableName, String[] trainingFieldNamesList){
        this.predictorName = predictorName;
        this.trainingModelType = trainingModelType;
        this.version = version;
        this.lastTrain = lastTrain;
        this.trainingExpiration = trainingExpiration;
        this.trainingDataTableName = trainingDataTableName;
        this.trainingFieldNamesList = trainingFieldNamesList;
    }
    public abstract void train(double[][] samples, double[] classType);

    protected String getPredictorName(){
        return this.predictorName;
    }
    protected int getVersion(){
        return this.version;
    }
    protected String getLastTrain(){
        return this.lastTrain;
    }
    protected void incrementVersion(){
        ++this.version;
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
    protected void setLastTrain(String trainingDateTime){
        this.lastTrain = trainingDateTime;
    } //???????????????????????????????????????
}


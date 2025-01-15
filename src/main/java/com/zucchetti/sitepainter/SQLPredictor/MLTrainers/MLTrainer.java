package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

public abstract class MLTrainer {
    // --- TUTTI CAMPI FINAL ???
    final private String predictorName;
    final private String trainingModelType;
    private int version = 0;
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

    public String getPredictorName(){
        return this.predictorName;
    }
    public int getVersion(){
        return this.version;
    }
    public String getLastTrain(){
        return this.lastTrain;
    }
    public void incrementVersion(){
        ++this.version;
    }
    public void setLastTrain(String trainingDateTime){
        this.lastTrain = trainingDateTime;
    } //???????????????????????????????????????
}


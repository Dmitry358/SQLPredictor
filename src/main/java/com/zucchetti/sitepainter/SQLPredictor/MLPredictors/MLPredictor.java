package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import java.util.ArrayList;

public abstract class MLPredictor {
    private final String predictorName;
    private final int version;
    private final String lastTrain;

    public MLPredictor(String predictorName, int version, String lastTrain){
        this.predictorName = predictorName;
        this.version = version;
        this.lastTrain = lastTrain;
    }

    protected String getPredictorName(){ return predictorName; }

    public abstract String getQuery(ArrayList<String> fieldsList);
}

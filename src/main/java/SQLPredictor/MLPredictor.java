package SQLPredictor;

import java.util.ArrayList;

public abstract class MLPredictor {
    private String predictorName;// !!!!!! const????
    private int version;// !!!!!! const????
    private String lastTrain;// !!!!!! const????

    public MLPredictor(){}
    public MLPredictor(String predictorName, int version, String lastTrain){
        this.predictorName = predictorName;
        this.version = version;
        this.lastTrain = lastTrain;
    }

    protected String getPredictorName(){ return predictorName; }
    protected int getVersion(){ return version; }
    protected String getLastTrain(){ return lastTrain; }

    public abstract String getQuery(ArrayList<String> fieldsList);

    //public abstract void printFields(); // SOLO PER TESTING !!!!!!!!!!!!!!!!!!!!!!!!
    //public String getQuery(){ return "";};
}

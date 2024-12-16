package SQLPredictor;

import java.util.ArrayList;

public class LRPredictor extends MLPredictor {
    private double[] parameters; // !!!!!! const????

    LRPredictor(){}
    LRPredictor(String predictorName, int version, String lastTrain, double[] parameters){
        super(predictorName, version, lastTrain);
        this.parameters = parameters;
    }

    public String getQuery(ArrayList<String> fieldsList){
        //!!!!!! CONTROLLO SE fieldsList NON è VUOTO
        //!!! controllo NUMERO PARAMETRI == NUMERO CAMPI + 1
        String query = "";

        int fieldsNum = fieldsList.size();
        for (int i=0; i <= fieldsNum; i++){
            if(i < fieldsNum) query += fieldsList.get(i) + "*" + parameters[i] + " + ";
            else query += parameters[i];
        }

        return query;
    }

    // SOLO PER TESTING !!!!!!!!!!!!!!!!!!!!!!!!
    public void printFields() {
        System.out.println("predictorName: " + this.getPredictorName());
        System.out.println("version: " + this.getVersion());
        System.out.println("lastTrain: " + this.getLastTrain());
        System.out.print("parameters: ");
        for (int i=0; i < parameters.length; i++) System.out.print(parameters[i]+" ");
    }
}

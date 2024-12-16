package SQLPredictor;

import java.util.ArrayList;

public class ABCPredictor extends MLPredictor{
    private String tableName; // !!!!!! const????

    ABCPredictor(){}
    ABCPredictor(String predictorName, int version, String lastTrain, String tableName){
        super(predictorName, version, lastTrain);
        this.tableName = tableName;
    }

    public String getQuery(ArrayList<String> clientCode){
        //!!!! ERRORE SE CISONO PIU PARAMETRI clientCode
        return "SELECT classe FROM " + tableName + " WHERE " + tableName + ".codice_cliente = " + clientCode.get(0);
    }
}

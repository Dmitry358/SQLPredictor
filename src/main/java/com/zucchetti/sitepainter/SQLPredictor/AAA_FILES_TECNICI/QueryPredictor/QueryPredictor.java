package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.QueryPredictor;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryPredictor {
    final private String dataBaseURL;
    final private String username;
    final private String password;

    public QueryPredictor(DataBaseConnecter dbConnecter){
        this.dataBaseURL = dbConnecter.getDataBaseURL();
        this.username = dbConnecter.getUsername();
        this.password = dbConnecter.getPassword();
    }

    public int[][] predict(String sampleTableName, String idFieldName, int sampleId, String predictorQuery){

        String query ="SELECT \n"+
                     "CASE \n"+
                        "WHEN " + predictorQuery + " > 0 THEN '8' \n"+
                        "WHEN " + predictorQuery + " < 0 THEN '3' \n"+
                        "ELSE '0' \n"+
                     "END AS class \n"+
                 "FROM " + sampleTableName + " \n"+
                 "WHERE " + idFieldName + " = " + sampleId + ";\n";

        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(this.dataBaseURL, this.username, this.password);
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            ResultSetMetaData queryResultMetaData = queryResult.getMetaData(); //SUPERFLUO

            List<String> fieldsNameList = new ArrayList<>(); //SUPERFLUO
            for (int i = 1; i <= queryResultMetaData.getColumnCount(); i++) {
                fieldsNameList.add(queryResultMetaData.getColumnName(i));
            }

            while (queryResult.next()) {
                ArrayList<Double> row = new ArrayList<>();
                for(int i=0; i < fieldsNameList.size(); i++) {
                    row.add(queryResult.getDouble(fieldsNameList.get(i)));
                }
                result.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        int[][] resultQuery = new int[result.size()][result.get(0).size()];
        for(int i=0; i < result.size(); ++i){
            for(int j=0; j < result.get(0).size(); ++j){
                resultQuery[i][j] = result.get(i).get(j).intValue();
            }
        }

        return resultQuery;
    }
}

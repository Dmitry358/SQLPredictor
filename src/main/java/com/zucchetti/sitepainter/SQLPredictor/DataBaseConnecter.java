package com.zucchetti.sitepainter.SQLPredictor;

//import java.sql.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnecter {
    final private String dataBaseURL;
    final private String username;
    final private String password;

    public DataBaseConnecter(String dataBaseURL, String username, String password){
        this.dataBaseURL = dataBaseURL;
        this.username = username;
        this.password = password;
    }

    public double[][] getQueryResult(String tableName, String[] fieldsList, String classField){
        /*try {
            Class.forName("org.postgresql.Driver");  // Carica il driver PostgreSQL
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        */
        //!!!! TEST SE CONNESSIONE A DB è ANDATA A BUON FINE
        // !!! TEST CHE QUERY è ANDATA A BUON FINE
        //nomi di campi/tabella sbagliati

        String query = "SELECT ";
        for(int i=0; i < fieldsList.length; i++) { query += fieldsList[i] + ", "; }
        query += classField + " FROM " + tableName;

        ArrayList<ArrayList<Double>> result = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(this.dataBaseURL, this.username, this.password);
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            ResultSetMetaData queryResultMetaData = queryResult.getMetaData();

            List<String> fieldsNameList = new ArrayList<>();
            for (int i = 1; i <= queryResultMetaData.getColumnCount(); i++) {
                fieldsNameList.add(queryResultMetaData.getColumnName(i));
            }

            while (queryResult.next()) {
                ArrayList<Double> row = new ArrayList<>();
                for(int i=0; i < fieldsNameList.size(); i++) {
                    row.add(queryResult.getDouble(fieldsNameList.get(i)));
                }
                result.add(row);
                /*
                ArrayList<Double> result = new ArrayList<>();
                for(int i=0; i < fields.length; i++) result.add(resultSet.getDouble(fields[i]));
                result.add(resultSet.getDouble(classField));
                data.add(result);
                */
            }
            //if (queryResult != null) { queryResult.close();}
            //if (statement != null) { statement.close(); }
            //if (connection != null) { connection.close(); }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        double[][] resultQuery = new double[result.size()][result.get(0).size()];
        for(int i=0; i < result.size(); ++i){
            for(int j=0; j < result.get(0).size(); ++j){
                resultQuery[i][j] = result.get(i).get(j);
            }
        }
        return resultQuery;
    }
}

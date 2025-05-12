package com.zucchetti.sitepainter.SQLPredictor;

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

  public double[][] getTrainingData(String tableName, String[] fieldsList, String classField){
    StringBuilder query = new StringBuilder("SELECT ");
    for (String string : fieldsList) {
      query.append(string).append(", ");
    }
    query.append(classField).append(" FROM ").append(tableName);

    ArrayList<ArrayList<Double>> result = new ArrayList<>();

    try {
      Connection connection = DriverManager.getConnection(this.dataBaseURL, this.username, this.password);
      Statement statement = connection.createStatement();
      ResultSet queryResult = statement.executeQuery(query.toString());
      ResultSetMetaData queryResultMetaData = queryResult.getMetaData();

      List<String> fieldsNameList = new ArrayList<>();
      for (int i = 1; i <= queryResultMetaData.getColumnCount(); i++) {
        fieldsNameList.add(queryResultMetaData.getColumnName(i));
      }

      while (queryResult.next()) {
        ArrayList<Double> row = new ArrayList<>();
        for (String s : fieldsNameList) {
          row.add(queryResult.getDouble(s));
        }
        result.add(row);
      }
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

  public String getDataBaseURL(){
    return this.dataBaseURL;
  }
  public String getUsername(){
    return this.username;
  }
  public String getPassword(){
    return this.password;
  }
}
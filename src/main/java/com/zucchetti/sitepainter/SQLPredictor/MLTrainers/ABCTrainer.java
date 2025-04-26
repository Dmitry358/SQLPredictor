package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import com.zucchetti.sitepainter.SQLPredictor.DataBaseConnecter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class ABCTrainer extends MLTrainer {
    final private String predictionTableName;
    private double boundA = 0;
    private double boundB = 0;

    public ABCTrainer(String predictorName, int version, String lastTrain, int trainingExpiration, String trainingDataTableName, String[] trainingFieldNamesList,  String classificationField, String predictionTableName, double boundA, double boundB) {
        super(predictorName, "abc", version, lastTrain, trainingExpiration, trainingDataTableName, trainingFieldNamesList, classificationField);
        this.predictionTableName = predictionTableName;
    }

    public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnecter){
        // ??? CONTROLLO SE dataTableFieldNamesList ha lunghezza > 0
        String idField = dataTableFieldNamesList[0];

        String trainQuery = "DROP TABLE IF EXISTS " + this.predictionTableName + ";\n" +
                "CREATE TABLE " + this.predictionTableName + " AS \n" +
                "SELECT " + idField + ", 'A' AS class \n" +
                "FROM (\n" +
                    "SELECT " + idField + ", " +
                        "SUM(" + classificationFieldName + ") OVER (ORDER BY " + classificationFieldName + " desc ) AS run_sum, " +
                        "SUM(" + classificationFieldName + ") OVER () AS total \n" +
                    "FROM " + dataTableName + " ) AS subquery \n" +
                "WHERE run_sum <= 0.8 * total \n" +

                "UNION \n" +

                "SELECT sub_ABE." + idField + ", 'B' as class \n" +
                "FROM \n" +
                    "(SELECT sub_AB." + idField + ", " + classificationFieldName + " \n" +
                    "FROM ( \n" +
                        "SELECT " + idField + ", " +
                        classificationFieldName + ", " +
                        "SUM(" + classificationFieldName + ") OVER (ORDER BY " + classificationFieldName + " desc ) AS run_sum, " +
                        "SUM(" + classificationFieldName + ") OVER () AS total \n" +
                        "FROM " + dataTableName + " ) AS sub_AB \n" +
                    "WHERE run_sum <= 0.95 * total) AS sub_ABE \n" +

                "LEFT JOIN \n" +

                "(SELECT sub_A." + idField + ", sub_A." + classificationFieldName + " \n" +
                "FROM ( \n" +
                    "SELECT " + idField + ", " + classificationFieldName + ", " +
                    "SUM(" + classificationFieldName + ") OVER (ORDER BY " + classificationFieldName + " desc) AS run_sum, " +
                    "SUM(" + classificationFieldName + ") OVER () AS total \n" +
                    "FROM " + dataTableName +
                    ") AS sub_A \n" +
                "WHERE run_sum <= 0.8 * total) AS sub_AE \n" +
                "ON sub_ABE." + idField + " = sub_AE." + idField + " \n" +
                "WHERE sub_AE." + idField + " IS NULL \n" +

                "UNION \n" +

                "SELECT sub_ABE." + idField + ", 'C' as class \n" +
                "FROM \n" +
                    "(SELECT sub_AB." + idField + ", " + classificationFieldName + " \n" +
                    "FROM ( \n" +
                        "SELECT " + idField + ", " + classificationFieldName + ", " +
                        "SUM(" + classificationFieldName + ") OVER (ORDER BY " + classificationFieldName + " desc ) AS run_sum, " +
                        "SUM(" + classificationFieldName + ") OVER () AS total \n" +
                        "FROM " + dataTableName + ") AS sub_AB \n" +
                        "WHERE run_sum <= total) AS sub_ABE \n" +
                "LEFT JOIN \n" +
                "(SELECT sub_A." + idField + ", sub_A." + classificationFieldName + " \n" +
                "FROM ( \n" +
                    "SELECT " + idField + ", " + classificationFieldName + ", " +
                        "SUM(" + classificationFieldName + ") OVER (ORDER BY " + classificationFieldName + " desc) AS run_sum, " +
                        "SUM(" + classificationFieldName + ") OVER () AS total \n" +
                    "FROM " + dataTableName + ") AS sub_A \n" +
                    "WHERE run_sum <= 0.95 * total) AS sub_AE \n" +
                "ON sub_ABE." + idField + " = sub_AE." + idField + " \n" +
                "WHERE sub_AE." + idField + " IS NULL \n" +
                "ORDER BY id;";

        try (Connection connection = DriverManager.getConnection(dbConnecter.getDataBaseURL(), dbConnecter.getUsername(), dbConnecter.getPassword())) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(trainQuery);
            }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}



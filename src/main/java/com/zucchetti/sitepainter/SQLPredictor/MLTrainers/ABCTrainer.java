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

        /*
        String descriptionFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/" + predictorName + ".json";
        File descriptionFile = new File(descriptionFilePath);
        if (descriptionFile.exists()) {
            try {
                FileReader reader = new FileReader(descriptionFilePath); // ???FRSE SI PUO METTERE COME CONTROLLO ESISTENZA DEL FILE
                JsonElement jsonElement = JsonParser.parseReader(reader);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    int f = 0;
                    for (String key : jsonObject.keySet()) {
                        if (key.equals("predictor_name")) {
                            predictorName = jsonObject.get(key).getAsString();
                            ++f;
                        } else if (key.equals("version")) {
                            version = Integer.parseInt(jsonObject.get(key).getAsString());
                            ++f;
                        } else if (key.equals("last_train")) {
                            lastTrain = jsonObject.get(key).getAsString();
                            ++f;
                        }
                    }
                    if (f < 3) { //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println("Description file does not contain all information needed to create object");
                    }
                } else {
                    System.out.println("Description file has wrong structure");
                }
            } catch (FileNotFoundException e) { // SERVE??????????????????
                System.out.println("Description file of predictor \"" + predictorName + "\" is not found");
            } catch (JsonIOException e) {
                System.out.println("Error of processing description file");
            } catch (JsonSyntaxException e) {
                System.out.println("Syntax of description file is incorrect");
            }
        }
        else { //////////////// description file NON esiste //////////////////////
            try {
                if (descriptionFile.createNewFile()) {
                    predictorName = predictorName;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.now();
                    String formattedDateTime = dateTime.format(formatter);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("predictor_name", new JsonPrimitive(predictorName));
                    jsonObject.add("model_type", new JsonPrimitive("abc"));
                    jsonObject.add("version", new JsonPrimitive(1));
                    jsonObject.add("last_train", new JsonPrimitive(formattedDateTime));

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    try (FileWriter writer = new FileWriter(descriptionFilePath)) {
                        gson.toJson(jsonObject, writer);
                    } catch (IOException e) {
                        System.out.println("Error writing description file:");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("The file already exists");
                    return; // MESSAGGIO CORRETTO???
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage()); /!!!!!!!!!!!
                return;
            }
        }
        */
    }

    public boolean train(String dataTableName, String[] dataTableFieldNamesList, String classificationFieldName, DataBaseConnecter dbConnecter){
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! CONTROLLO SE this != null
    //*
        //CONNESSIONE CON DB
        //CONTROLLO SE NEL DB NON è GIA PRESENTE TABELLA DI PREDICTOR COME MI COMPORTO??
        //CONTROLLO SE dataTableName
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
    //*/

}



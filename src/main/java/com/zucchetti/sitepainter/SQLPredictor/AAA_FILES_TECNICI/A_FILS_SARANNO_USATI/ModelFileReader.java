package com.zucchetti.sitepainter.SQLPredictor.AAA_FILES_TECNICI.A_FILS_SARANNO_USATI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ModelFileReader {
    public String getQuery(ArrayList<String> fieldsList,String predictorName){
        // !!! MOLTO IMPORTANTE !!! CONTROLLARE CONSISTENZA  clientCode.size()*2 == param.lenght
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/models/" + predictorName + ".model";
        String query = "";

        try{
            String kernelType = "";
            String line = "";
            BufferedReader buffer = new BufferedReader(new FileReader(modelFilePath));

            while((line = buffer.readLine()) != null) {
                String substring = line.substring(line.indexOf(32) + 1);
                if (line.startsWith("kernel_type")) {
                    kernelType = substring;
                }
            }

            switch (kernelType){
                case "linear":
                    query = getLinearKernelQuery(fieldsList, predictorName); break;
                case "polynomial":
                    query = getPolynomialKernelQuery(fieldsList, predictorName); break;
                case "rbf":
                    query = getRBFKernelQuery(fieldsList, predictorName); break;
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!????default
            }

            buffer.close();
        }
        catch (Exception exception){
            System.out.println(exception);
        }

        return query;
    }
    private String getLinearKernelQuery(ArrayList<String> fieldsList, String predictorName){
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/models/" + predictorName + ".model";
        BufferedReader buffer = null;
        String query = "(";
        int SVNumber = 0;
        double rho = 0;
        String line = "";

        try {
            buffer = new BufferedReader(new FileReader(modelFilePath));
            while ((line = buffer.readLine()) != null) {
                String substring = line.substring(line.indexOf(32) + 1);
                if (line.startsWith("total_sv")) SVNumber = Integer.parseInt(substring);
                if (line.startsWith("rho")) rho = Double.parseDouble(substring);
                if (line.startsWith("SV")) {
                    for (int sv = 0; sv < SVNumber; ++sv) {
                        line = buffer.readLine();
                        String[] vector = line.split(" ", 2);
                        double coef = Double.parseDouble(vector[0]);
                        String[] param = vector[1].split("[ :\\t\\n\\r\\f]+");

                        if (sv != 0) {
                            if (coef < 0) {
                                if (coef == -1) query += " - (";
                                else query += " - " + Math.abs(coef) + "*(";
                            }
                            else {
                                if (coef == 1) query += " + (";
                                else query += " + " + coef + "*(";
                            }
                        }
                        else {
                            if (coef == -1) query += "-";
                            else if (coef != 1) query += coef + "*";
                            query += "(";
                        }

                        for (int f = 0; f < fieldsList.size(); ++f) {
                            int p = (f * 2) + 1;
                            query += fieldsList.get(f) + "*" + param[p];
                            if (f < fieldsList.size() - 1) query += " + ";
                        }
                        query += ")";
                    }
                }
            }

            if (rho < 0) query += " + " + Math.abs(rho);
            else query += " - " + rho + ")";
            buffer.close();
        }
        catch (Exception exception){
            System.out.println(exception);
        }

        return query;
    }

    private String getPolynomialKernelQuery(ArrayList<String> fieldsList, String predictorName){
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/models/" + predictorName + ".model";
        BufferedReader buffer = null;
        int degree = 0;
        double gamma = 0;
        double coef0 = 0;
        int SVNumber = 0;
        double rho = 0;
        String line = "";
        String query = "(";

        try {
            buffer = new BufferedReader(new FileReader(modelFilePath));
            while ((line = buffer.readLine()) != null) {

                String substring = line.substring(line.indexOf(32) + 1);
                if (line.startsWith("degree")) degree = Integer.parseInt(substring);
                if (line.startsWith("gamma")) gamma = Double.parseDouble(substring);
                if (line.startsWith("coef0")) coef0 = Double.parseDouble(substring);
                if (line.startsWith("total_sv")) SVNumber = Integer.parseInt(substring);
                if (line.startsWith("rho")) rho = Double.parseDouble(substring);

                if (line.startsWith("SV")) {
                    for (int sv = 0; sv < SVNumber; ++sv) {
                        line = buffer.readLine();
                        String[] vector = line.split(" ", 2);
                        double coef = Double.parseDouble(vector[0]);
                        String[] param = vector[1].split("[ :\\t\\n\\r\\f]+");

                        if (sv != 0) {
                            if (coef < 0) {
                                if (coef == -1) query += " - (";
                                else query += " - " + Math.abs(coef) + "*(";
                            }
                            else {
                                if (coef == 1) query += " + (";
                                else query += " + " + coef + "*(";
                            }
                        }
                        else {
                            if (coef == -1) query += "-";
                            else if (coef != 1) query += coef + "*";
                            query += "(";
                        }

                        String kFunctionValue = "(" + gamma + "*(";
                        for (int f = 0; f < fieldsList.size(); ++f) {
                            int p = (f * 2) + 1;
                            kFunctionValue += fieldsList.get(f) + "*" + param[p];
                            if (f < fieldsList.size() - 1) kFunctionValue += " + ";
                        }
                        kFunctionValue += ") + " + coef0 + ")";

                        query += "POWER (" + kFunctionValue + ", " + degree + ")";
                        /*
                        for (int d=0; d < degree; ++d){
                            query += kFunctionValue;
                            if(d < degree - 1) query += "*";
                        }
                        */
                        query += ")";
                    }
                }
            }

            if (rho < 0) query += " + " + Math.abs(rho);
            else query += " - " + rho;
            query += ")";
            buffer.close();
        }
        catch (Exception exception){
            System.out.println(exception);
        }

        return query;
    }

    private String getRBFKernelQuery(ArrayList<String> fieldsList, String predictorName){
        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/models/" + predictorName + ".model";
        BufferedReader buffer = null;
        String query = "(";
        int SVNumber = 0;
        double rho = 0;
        double gamma = 0;
        String line = "";

        try {
            buffer = new BufferedReader(new FileReader(modelFilePath));
            while ((line = buffer.readLine()) != null) {
                String substring = line.substring(line.indexOf(32) + 1);
                if (line.startsWith("gamma")) gamma = Double.parseDouble(substring);
                if (line.startsWith("total_sv")) SVNumber = Integer.parseInt(substring);
                if (line.startsWith("rho")) rho = Double.parseDouble(substring);
                if (line.startsWith("SV")) {
                    for (int sv = 0; sv < SVNumber; ++sv) {
                        line = buffer.readLine();
                        String[] vector = line.split(" ", 2);
                        double coef = Double.parseDouble(vector[0]);
                        String[] param = vector[1].split("[ :\\t\\n\\r\\f]+");

                        if (sv != 0) {
                            if (coef < 0) {
                                if (coef == -1) query += " - ";
                                else query += " - " + Math.abs(coef) + "*";
                            }
                            else {
                                if (coef == 1) query += " + ";
                                else query += " + " + coef + "*";
                            }
                        }
                        else {
                            if (coef == -1) query += "-";
                            else if (coef != 1) query += coef + "*";
                        }

                        query += "(POWER (EXP(1), -" + gamma + "*(";
                        for (int f = 0; f < fieldsList.size(); ++f) {
                            int p = (f * 2) + 1;
                            query += "POWER ((" + fieldsList.get(f) + " - " + param[p] + "), 2)";
                            //query += "(" + fieldsList.get(f) + " - " + param[p] + ")*(" + fieldsList.get(f) + " - " + param[p] + ")";
                            if (f < fieldsList.size() - 1) query += " + ";
                        }
                        query += ")))";
                    }
                }
            }

            if (rho < 0) query += " + " + Math.abs(rho);
            else query += " - " + rho + ")";
            buffer.close();
        }
        catch (Exception exception){
            System.out.println(exception);
        }

        return query;
    }
}

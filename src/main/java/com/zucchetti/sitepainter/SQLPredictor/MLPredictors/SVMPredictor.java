package com.zucchetti.sitepainter.SQLPredictor.MLPredictors;

import java.util.ArrayList;
import java.util.Map;

public class SVMPredictor extends MLPredictor {
    private String SVMType = "";
    private String kernelType = "";
    private int degree = 0;
    private double gamma = 0;
    private double coef0 = 0;
    private double rho = 0;
    final private ArrayList<ArrayList<Double>> supportVectors;

    public SVMPredictor(String predictorName, int version, String lastTrain, Map<String,String> modelParameters, ArrayList<ArrayList<Double>> supportVectors){
        super(predictorName, version, lastTrain);
        this.SVMType = modelParameters.get("svm_type");
        this.kernelType = modelParameters.get("kernel_type");
        this.rho = Double.parseDouble(modelParameters.get("rho"));
        this.supportVectors = supportVectors;

        if (modelParameters.get("kernel_type").equals("rbf")){
            this.gamma = Double.parseDouble(modelParameters.get("gamma"));
        }
        if (modelParameters.get("kernel_type").equals("polynomial")){
            this.gamma = Double.parseDouble(modelParameters.get("gamma"));
            this.coef0 = Double.parseDouble(modelParameters.get("coef0"));
            this.degree = Integer.parseInt(modelParameters.get("degree"));
        }
    }

    public String getQuery(ArrayList<String> fieldsList){
        int fieldsNum = fieldsList.size();
        if (supportVectors.size() < 1){
            System.out.println("Description file does not contain any vector");
            return null;
        }
        if (fieldsNum < 1){
            System.out.println("Fields list must not be empty");
            return null;
        }
        if(fieldsNum != (supportVectors.get(0).size() - 1)){
            System.err.println("Request must contain " + (supportVectors.get(0).size() - 1) + " field names");
            return null;
        }

        String query = null;

        switch (kernelType){
            case "linear":
                query = getLinearKernelQuery(fieldsList); break;
            case "polynomial":
                query = getPolynomialKernelQuery(fieldsList); break;
            case "rbf":
                query = getRBFKernelQuery(fieldsList); break;
            default:
                System.out.println("Description file contain unmanageable kernel type");
        }

        return query;
    }

    private String getLinearKernelQuery(ArrayList<String> fieldsList){
        StringBuilder query = new StringBuilder("(");
        int SVNumber = supportVectors.size();

        for (int sv = 0; sv < SVNumber; ++sv) {
            double coef = supportVectors.get(sv).get(0);

            if (sv != 0) {
                if (coef < 0) {
                    if (coef == -1) { query.append(" - ("); }
                    else { query.append(" - ").append(Math.abs(coef)).append("*("); }
                }
                else {
                    if (coef == 1) { query.append(" + ("); }
                    else { query.append(" + ").append(coef).append("*("); }
                }
            }
            else {
                if (coef == -1) { query.append("-"); }
                else if (coef != 1) {query.append(coef).append("*"); }
                query.append("(");
            }

            for (int f = 0; f < fieldsList.size(); ++f) {
                query.append(fieldsList.get(f)).append("*").append(supportVectors.get(sv).get(f + 1));
                if (f < fieldsList.size() - 1) { query.append(" + "); }
            }
            query.append(")");
        }

        if (rho < 0) { query.append(" + ").append(Math.abs(rho)); }
        else { query.append(" - ").append(rho); }
        query.append(")");

        return query.toString();
    }

    private String getPolynomialKernelQuery(ArrayList<String> fieldsList){
        int SVNumber = supportVectors.size();

        StringBuilder query = new StringBuilder("(");

        for (int sv = 0; sv < SVNumber; ++sv) {

            double coef = supportVectors.get(sv).get(0);
            if (sv != 0) {
                if (coef < 0) {
                    if (coef == -1) { query.append(" - ("); }
                    else { query.append(" - ").append(Math.abs(coef)).append("*("); }
                }
                else {
                    if (coef == 1) { query.append(" + ("); }
                    else { query.append(" + ").append(coef).append("*("); }
                }
            }
            else {
                if (coef == -1) { query.append("-"); }
                else if (coef != 1) { query.append(coef).append("*"); }
                query.append("(");
            }

            StringBuilder kFunctionValue = new StringBuilder("(" + gamma + "*(");
            for (int f = 0; f < fieldsList.size(); ++f) {
                kFunctionValue.append(fieldsList.get(f)).append("*").append(supportVectors.get(sv).get(f + 1));
                if (f < fieldsList.size() - 1) { kFunctionValue.append(" + "); }
            }
            kFunctionValue.append(") + ").append(coef0).append(")");

            query.append("POWER (").append(kFunctionValue).append(", ").append(degree).append(")");
            query.append(")");
        }

        if (rho < 0) { query.append(" + ").append(Math.abs(rho)); }
        else { query.append(" - ").append(rho); }
        query.append(")");

        return query.toString();
    }

    private String getRBFKernelQuery(ArrayList<String> fieldsList){
        int SVNumber = supportVectors.size();
        StringBuilder query = new StringBuilder("(");

        for (int sv = 0; sv < SVNumber; ++sv) {
            double coef = supportVectors.get(sv).get(0);

            if (sv != 0) {
                if (coef < 0) {
                    if (coef == -1) { query.append(" - "); }
                    else { query.append(" - ").append(Math.abs(coef)).append("*"); }
                }
                else {
                    if (coef == 1) { query.append(" + "); }
                    else {query.append(" + ").append(coef).append("*"); }
                }
            }
            else {
                if (coef == -1) { query.append("-"); }
                else if (coef != 1) { query.append(coef).append("*"); }
            }

            query.append("(POWER (EXP(1), -").append(gamma).append("*(");

            for (int f = 0; f < fieldsList.size(); ++f) {
                query.append("POWER ((").append(fieldsList.get(f)).append(" - ").append(supportVectors.get(sv).get(f + 1)).append("), 2)");
                if (f < fieldsList.size() - 1) { query.append(" + "); }
            }

            query.append(")))");
        }

        if (rho < 0) { query.append(" + ").append(Math.abs(rho)); }
        else { query.append(" - ").append(rho); }
        query.append(")");

        return query.toString();
    }
}

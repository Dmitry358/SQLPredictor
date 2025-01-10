package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

import java.io.IOException;
import java.io.File;
import libsvm.*;


public class SVMTrainer {
    private String predictorName = "";
    private int version = 0;
    private String lastTrain = "";
    private String SVMType = ""; //?????????????????
    private String kernelType = "";
    private int degree = 0;
    private double gamma = 0;
    private double coef0 = 0;
    private double rho = 0;
    private double paramC =0;
    //final private ArrayList<ArrayList<Double>> supportVectors;

    public SVMTrainer(String predictorName, int version, String lastTrain, String svmType, String kernelType, int polDegree, double gamma, double coef0, double rho, double paramC){
        this.predictorName = predictorName;
        this.version = version;
        this.lastTrain = lastTrain;
        this.SVMType = svmType;
        this.kernelType = kernelType;
        this.degree = polDegree;
        this.gamma = gamma;
        this.coef0 = coef0;
        this.rho = rho;
        this.paramC = paramC;
    }

    public void train(double[][] data, double[] classType){
        int numExamples = data.length;
        int numFeatures = data[0].length;

        svm_problem problem = new svm_problem();
        problem.l = numExamples;
        problem.x = new svm_node[numExamples][numFeatures];
        problem.y = new double[numExamples];

        for (int i=0; i < numExamples; i++){
            problem.y[i] = classType[i];
            problem.x[i] = new svm_node[numFeatures];

            for (int j=0; j < numFeatures ; j++){
                problem.x[i][j] = new svm_node();
                problem.x[i][j].index = j+1;
                problem.x[i][j].value = data[i][j];
            }
        }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        svm_parameter param = new svm_parameter();

        switch (this.SVMType){
            case "c_svc":
                param.svm_type = svm_parameter.C_SVC; break;
            case "nu_svc":
                param.svm_type = svm_parameter.NU_SVC; break;
            case "one_class":
                param.svm_type = svm_parameter.ONE_CLASS; break;
            case "epsilon_svr":
                param.svm_type = svm_parameter.EPSILON_SVR; break;
            case "nu_svr":
                param.svm_type = svm_parameter.NU_SVR; break;
            default:
                System.out.println("Valore tipo SVM non valido"); break;
        }

        switch (this.kernelType){
            case "linear":
                param.kernel_type = svm_parameter.LINEAR; break;
            case "polynomial":
                param.kernel_type = svm_parameter.POLY;
                param.degree = this.degree;
                param.gamma = this.gamma;
                param.coef0 = this.coef0;
                break;
            case "rbf":
                param.kernel_type = svm_parameter.RBF;
                param.gamma = this.gamma;
                break;
            case "sigmoid":
                param.kernel_type = svm_parameter.SIGMOID; break;
            case "precomputed":
                param.kernel_type = svm_parameter.PRECOMPUTED; break;
            default:
                System.out.println("Valore tipo kernel non valido"); break;
        }

        param.C = paramC;

        svm_model model = svm.svm_train(problem, param);

        String modelFilePath = "src/main/java/com/zucchetti/sitepainter/SQLPredictor/predictors/svm_models/" + predictorName + ".model";
        try {
            File file = new File(modelFilePath);

            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        System.out.println("File creato con successo: " + file.getName());
                    }
                    else {
                        System.out.println("Errore nella creazione del file.");
                    }
                }
                catch (IOException e) {
                    System.out.println("Si è verificato un errore: " + e.getMessage());
                }
            }

            svm.svm_save_model(modelFilePath, model);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


}

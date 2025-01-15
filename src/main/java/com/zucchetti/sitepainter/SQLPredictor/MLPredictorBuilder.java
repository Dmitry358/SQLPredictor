package com.zucchetti.sitepainter.SQLPredictor;

public class MLPredictorBuilder {
    public int svmType;
    public int kernelType;
    public int degree;	  // for poly
    public double gamma;  // for poly/rbf/sigmoid
    public double coef0;  // for poly/sigmoid

    public double cacheSize;   // in MB
    public double eps;	        // stopping criteria
    public double paramC;	        // for C_SVC, EPSILON_SVR and NU_SVR
    public int nrWeight;		// for C_SVC
    public int[] weightLabel;	// for C_SVC
    public double[] weight;		// for C_SVC
    public double paramNu;	        // for NU_SVC, ONE_CLASS, and NU_SVR
    public double paramP;	        // for EPSILON_SVR
    public int shrinking;	    // use the shrinking heuristics
    public int probability;     // do probability estimates

    public MLPredictorBuilder setSvmType(int svmType){
        this.svmType = svmType;
        return this;
    }
    public MLPredictorBuilder setKernelType(int kernelType){
        this.kernelType = kernelType;
        return this;
    }
    public MLPredictorBuilder setDegree(int degree){
        this.degree = degree;
        return this;
    }
    public MLPredictorBuilder setGamma(double gamma){
        this.gamma = gamma;
        return this;
    }
    public MLPredictorBuilder setCoef0(double coef0){
        this.coef0 = coef0;
        return this;
    }
    public MLPredictorBuilder setCacheSize(double cacheSize){
        this.cacheSize = cacheSize;
        return this;
    }
    public MLPredictorBuilder setEps(double eps){
        this.eps = eps;
        return this;
    }
    public MLPredictorBuilder setParamC(double paramC){
        this.paramC = paramC;
        return this;
    }
    public MLPredictorBuilder setNrWeight(int nrWeight){
        this.nrWeight = nrWeight;
        return this;
    }
    public MLPredictorBuilder setWeightLabel(int[] weightLabel){
        this.weightLabel = weightLabel;
        return this;
    }
    public MLPredictorBuilder setWeight(double[] weight){
        this.weight = weight;
        return this;
    }
    public MLPredictorBuilder setParamNu(double paramNu){
        this.paramNu = paramNu;
        return this;
    }
    public MLPredictorBuilder setParamP(double paramP){
        this.paramP = paramP;
        return this;
    }
    public MLPredictorBuilder setShrinking(int shrinking){
        this.shrinking = shrinking;
        return this;
    }
    public MLPredictorBuilder setProbability(int probability){
        this.probability = probability;
        return this;
    }

}

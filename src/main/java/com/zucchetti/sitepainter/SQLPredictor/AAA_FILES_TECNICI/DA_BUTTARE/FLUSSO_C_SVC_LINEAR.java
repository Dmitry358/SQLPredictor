/*public class SVM_PARAMETER implements Cloneable,java.io.Serializable
{
    //svm_type
    public static final int C_SVC = 0;
    public static final int NU_SVC = 1;
    public static final int ONE_CLASS = 2;
    public static final int EPSILON_SVR = 3;
    public static final int NU_SVR = 4;
    */
    /* kernel_type
    public static final int LINEAR = 0;
    public static final int POLY = 1;
    public static final int RBF = 2;
    public static final int SIGMOID = 3;
    public static final int PRECOMPUTED = 4;
    */
    /*
	public int svm_type;
	public int kernel_type;
	public int degree;	  // for poly
	public double gamma;  // for poly/rbf/sigmoid
	public double coef0;  // for poly/sigmoid

	// these are for training only
	public double cache_size;   // in MB
	public double eps;	        // stopping criteria
	public double C;	        // for C_SVC, EPSILON_SVR and NU_SVR
	public int nr_weight;		// for C_SVC
	public int[] weight_label;	// for C_SVC
	public double[] weight;		// for C_SVC
	public double nu;	        // for NU_SVC, ONE_CLASS, and NU_SVR
	public double p;	        // for EPSILON_SVR
	public int shrinking;	    // use the shrinking heuristics
	public int probability;     // do probability estimates
}

public class SVM_MODEL implements java.io.Serializable
{
	public svm_parameter param;	// parameter
	public int nr_class;	    // number of classes, = 2 in regression/one class svm
	public int l;			    // total #SV
	public svm_node[][] SV;	    // SVs (SV[l])
	public double[][] sv_coef;  // coefficients for SVs in decision functions (sv_coef[k-1][l])
	public double[] rho;	    // constants in decision functions (rho[k*(k-1)/2])
	public double[] probA;      // pariwise probability information
	public double[] probB;
	public double[] prob_density_marks;	// probability information for ONE_CLASS
	public int[] sv_indices;            // sv_indices[0,...,nSV-1] are values in [1,...,num_traning_data] to indicate SVs in the training set

	// for classification only

	public int[] label;		// label of each class (label[k])
	public int[] nSV;		// number of SVs for each class (nSV[k])
				            // nSV[0] + nSV[1] + ... + nSV[k-1] = l
};

public class MAIN {
	// SVMProcessor.class
    SVMProcessor.prediction(String predictorName, double[] testData);
	// svm.class
	svm_model model = svm.svm_load_model(String modelFilePath);
----int prediction = (int) svm.SVM_PREDICT((model)->svm_model var0, (testData)->svm_node[] var1);
		int var2 = var0.nr_class; //var2 = # classi
		double[] var3;
		if(var0.param.svm_type !=2 && var0.param.svm_type !=3 && var0.param.svm_type !=4){
			var3 = new double[var2 * (var2 - 1) / 2]; //var3 = # combinazioni copie classi
		} else{
			var3 = new double[1];
		}
		// svm.class
		// double var4 = SVM_PREDICT_VALUES((var0)->svm_model var0, (var1)->svm_node[] var1, (var3)->double[] var2);
------------{
			// var0 = modello; svm_node[] var1 = testData; var2 = # combinazioni copie classi
			int var3;
			if (var0.param.svm_type != 2 && var0.param.svm_type != 3 && var0.param.svm_type != 4) {
				int var20 = var0.nr_class;
				int var21 = var0.l;
				double[] var6 = new double[var21];

				// var20 = # CLASSI
				// var21 = # VETTORI
				// var6 = array double lungo # VETTORI
				// var3 = 0
				// svm_node[] var1 = testData
				for (var3 = 0; var3 < var21; ++var3) {
					// svm_node[][] var0.SV -> vettori di supporto
					var6[var3] = Kernel.k_function(var1, var0.SV[var3], var0.param);
				}

				int[] var7 = new int[var20];
				var7[0] = 0;
				// var7 = array int lungo # CLASSI; OGNI CELLA CONTIENE INDICE DEL VETTORE DA QUALE COMINCIA NUOVA CLASSE
				for (var3 = 1; var3 < var20; ++var3) { // var7 riempie con # SV per classe
					var7[var3] = var7[var3 - 1] + var0.nSV[var3 - 1];
				}

				int[] var8 = new int[var20]; // var8 = array int lungo # CLASSI
				for (var3 = 0; var3 < var20; ++var3) {
					var8[var3] = 0; // var8 riempie di 0
				}

				int var9 = 0;
				int var10;
				for (var3 = 0; var3 < var20; ++var3) {
					for (var10 = var3 + 1; var10 < var20; ++var10) {
						double var11 = 0.0;
						int var13 = var7[var3];
						int var14 = var7[var10];
						int var15 = var0.nSV[var3];
						int var16 = var0.nSV[var10];
						// sv_coef = coefficients for SVs in decision functions (sv_coef[k-1][l])
						double[] var18 = var0.sv_coef[var10 - 1];
						double[] var19 = var0.sv_coef[var3];

						int var17;
						for (var17 = 0; var17 < var15; ++var17) {
							var11 += var18[var13 + var17] * var6[var13 + var17];
						}
						for (var17 = 0; var17 < var16; ++var17) {
							var11 += var19[var14 + var17] * var6[var14 + var17];
						}

						var11 -= var0.rho[var9];
						var2[var9] = var11;
						int var10002;
						if (var2[var9] > 0.0) {
							var10002 = var8[var3]++;
						} else {
							var10002 = var8[var10]++;
						}

						++var9;
					}
				}

				var10 = 0;
				for (var3 = 1; var3 < var20; ++var3) {
					if (var8[var3] > var8[var10]) {
						var10 = var3;
					}
				}

				return (double) var0.label[var10];
			} else {
				double[] var4 = var0.sv_coef[0];
				double var5 = 0.0;

				for (var3 = 0; var3 < var0.l; ++var3) {
					var5 += var4[var3] * Kernel.k_function(var1, var0.SV[var3], var0.param);
				}

				var5 -= var0.rho[0];
				var2[0] = var5;
				if (var0.param.svm_type == 2) {
					return var5 > 0.0 ? 1.0 : -1.0;
				} else {
					return var5;
				}
			}
		}
	}

	return var4;
	*/// FINE double var4 = SVM_PREDICT_VALUES((var0)->svm_model var0, (var1)->svm_node[] var1, (var3)->double[] var2);


            
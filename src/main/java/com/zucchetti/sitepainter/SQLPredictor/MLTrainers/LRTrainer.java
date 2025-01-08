package com.zucchetti.sitepainter.SQLPredictor.MLTrainers;

public class LRTrainer {
    private double[][] transposeOfXTimesX;
    private double[][] transposeOfXTimesY;
    final private double[][] identity;
    private double[][] coefficients; //???double[][]

    public LRTrainer(int numX, int numY){
        this.transposeOfXTimesX = this.rectMatrix(numX, numX);
        this.transposeOfXTimesY = this.rectMatrix(numX, numY);
        this.identity = this.identityMatrix(numX);
        /*
        *     if(!options)
      throw new Error('missing options')
    if(!('numX' in options))
      throw new Error('you must give the width of the X dimension as the property numX')
    if(!('numY' in options))
      throw new Error('you must give the width of the X dimension as the property numY')
    this.transposeOfXTimesX = this.rectMatrix({numRows: options.numX,numColumns: options.numX})
    this.transposeOfXTimesY = this.rectMatrix({numRows: options.numX,numColumns: options.numY})
    this.identity = this.identityMatrix(options.numX)*/
    }

    public void add(double[] sampleData, double sampleValue){
        if(sampleData.length != transposeOfXTimesX.length - 1){
            System.out.println("Data size is incompatible with model");
        }
        else {
            int dataLenght = sampleData.length + 1;
            double[] dataVector = new double[dataLenght];
            dataVector[0] = 1;
            for (int i = 0; i < sampleData.length; ++i) {
                dataVector[i + 1] = sampleData[i];
            }
            double[] valueVector = new double[1];
            valueVector[0] = sampleValue;
            this.addObservation(dataVector, valueVector);
        }
    }
    /*
    calculate(){return this.calculateCoefficients()}
    predict(xs){return this.hypothesize({x:[1].concat(xs)})}
    push(options){this.addObservation(options)}
     */

    public double[][] calculateCoefficients() {
        double[][] xTx = this.transposeOfXTimesX;
        double[][] xTy = this.transposeOfXTimesY;
        double[][] inv = this.inverse(xTx, this.identity);
        this.coefficients = this.multiply(inv, xTy);
        return this.coefficients;
    }

    private double[][] multiply(double[][] lhs, double[][] rhs) {//!!!!! lsh = inv (X_t*X^-1) dim(FxF); rhs = xTy (X_t*y) dim(Fx1)
        //!!!!!! controllare compatibilità dimensioni paramtri
        double[][] streamingProduct = this.rectMatrix(lhs.length, rhs[0].length);
        for(int x = 0; x < rhs.length; ++x) {
            double[] lhsColumn = new double[lhs.length];
            for(int r = 0; r < lhs.length; ++r) {
                lhsColumn[r] = lhs[r][x]; // Get the xth column of lhs.
            }
            double[] rhsRow = rhs[x]; // Get the xth row of rhs.
            this.addRowAndColumn(streamingProduct, lhsColumn, rhsRow);
        }
        return streamingProduct;
    }

    private double[][] inverse(double[][] matrix, double[][] identity) {
        // ???? controllare se matrice è invertibile??
        //controllo se matrix e identity non è vuota (per uso "matrix[0].length e identity[0].length")
        //!!!!controllo compatibilta dimensione matrix, identity
        //matrixRowNum == size
        int matrixRowNum = matrix.length;
        int matrixColumnNum = matrix[0].length;
        int identityColumnNum = identity[0].length;
        double[][] result = new double[matrixRowNum][matrixColumnNum + matrixRowNum]; //???
        for(int r = 0; r < matrixRowNum; ++r){
            for(int mc = 0; mc < matrixRowNum; ++mc){ result[r][mc] = matrix[r][mc]; }
            for(int ic = 0; ic < identityColumnNum; ++ic){ result[r][matrixColumnNum + ic] = identity[r][ic]; }
        }

        result = this.rref(result);
        double[][] inverse = new double[matrixRowNum][matrixColumnNum];

        for(int r = 0; r < matrixRowNum; ++r){
            for(int c = 0; c < matrixColumnNum; ++c){
                inverse[r][c] = result[r][c + matrixRowNum];//???????????
            }
        }
        return inverse;
    }

    private double[][] rref(double[][] A) {
        // !! controllo ch dimensioni da A !=0
        int rows = A.length;
        int columns = A[0].length;

        int lead = 0; //?? double
        for (int k = 0; k < rows; ++k) {
            if (columns <= lead) break; //return;

            int i = k;
            while (A[i][lead] == 0) {
                i++;
                if (rows == i) {
                    i = k;
                    lead++;
                    if (columns == lead) break; //return;
                }
            }
            double[] irow = A[i];
            double[] krow = A[k];
            A[i] = krow;
            A[k] = irow;

            double val = A[k][lead]; //???int
            for (int j = 0; j < columns; ++j) {
                A[k][j] /= val; //?????
            }

            for (int r = 0; r < rows; ++r) { //!!!!!!!!
                if (r == k) continue;
                val = A[r][lead];
                for (var j = 0; j < columns; ++j) {
                    A[r][j] -= val * A[k][j];
                }
            }
            lead++;
        }
        return A;

    }

    private void addObservation(double[] x, double[] y){
        this.addRowAndColumn(this.transposeOfXTimesX, x, x);
        this.addRowAndColumn(this.transposeOfXTimesY, x, y);
        // Adding an observation invalidates our coefficients.
        //!!!!!!!!!!!!!!!!!!!!!!!delete this.coefficients
    }

    private void addRowAndColumn(double[][] product, double[] lhsColumn, double[] rhsRow){
        for(int r = 0; r < lhsColumn.length; ++r) {
            for (int c = 0; c < rhsRow.length; ++c) {
                product[r][c] += lhsColumn[r] * rhsRow[c];
            }
        }
    }

    private double[][] rectMatrix(int numRows, int numColumns){
        double[][] matrix = new double[numRows][];
        for (int r = 0; r < numRows; ++r) {
            double[] row = new double[numColumns];
            matrix[r] = row;
            for (var c = 0; c < numColumns; c++) {
                row[c] = 0;
            }
        }
        return matrix;
    }

    private double[][] identityMatrix(int size){
        double[][] matrix = this.rectMatrix(size, size);
        for(int i = 0; i < size; ++i)
            matrix[i][i] = 1;
        return matrix;
    }


    /*
    private double hypothesize(double[] options) {
        /*
        if(!options)
            throw new Error('missing options')
        if(!(options.x instanceof Array))
            throw new Error('x property must be given as an array')
            */
    /*/if(!this.coefficients) //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //this.calculateCoefficients(); //!!!!!!!!!!!!!!!!!!!!!!!!
        double[] hypothesis;
        for(var x = 0; x < this.coefficients.length; x++) {
            double[] coefficientRow = this.coefficients[x];
            for(int y = 0; y < coefficientRow.length; y++)
                hypothesis[y] = (hypothesis[y] || 0) + coefficientRow[y] * options.x[x]
        }
        return hypothesis;
    }
    */
    /*
    hypothesize(options) {
        if(!options)
            throw new Error('missing options')
        if(!(options.x instanceof Array))
            throw new Error('x property must be given as an array')
        if(!this.coefficients)
            this.calculateCoefficients()
        var hypothesis = []
        for(var x = 0; x < this.coefficients.length; x++) {
            var coefficientRow = this.coefficients[x]
            for(var y = 0; y < coefficientRow.length; y++)
                hypothesis[y] = (hypothesis[y] || 0) + coefficientRow[y] * options.x[x]
        }
        return hypothesis
    }
    */
}
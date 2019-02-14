public class foo{
        @Override
        public void setValues(double[] y, double[] x) {
            if (x.length != y.length) {
                throw new IllegalArgumentException(String.format("The numbers of y and x values must be equal (%d != %d)",y.length,x.length));
            }
            double[][] xData = new double[x.length][];
            for (int i = 0; i < x.length; i++) {
                // the implementation determines how to produce a vector of predictors from a single x
                xData[i] = xVector(x[i]);
            }
            if(logY()) { // in some models we are predicting ln y, so we replace each y with ln y
                y = Arrays.copyOf(y, y.length); // user might not be finished with the array we were given
                for (int i = 0; i < x.length; i++) {
                    y[i] = Math.log(y[i]);
                }
            }
            OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
            ols.setNoIntercept(true); // let the implementation include a constant in xVector if desired
            ols.newSampleData(y, xData); // provide the data to the model
            coef = MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters()); // get our coefs
            last_error_rate = ols.estimateErrorVariance();
            Log.d(TAG, getClass().getSimpleName()+" Forecast Error rate: errorvar:"
                    +JoH.qs(last_error_rate,4)
                    + " regssionvar:" +JoH.qs(ols.estimateRegressandVariance(),4)
                    +"  stderror:" +JoH.qs(ols.estimateRegressionStandardError(),4));
        }
}
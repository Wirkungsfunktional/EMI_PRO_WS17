package wirkungsfunktional.de.emiProjectWS17.utils;

/**
 * Created by mk on 25.11.17.
 */

public class Simulator {

    float[] dataArray;



    private static int NUMBER_OF_POINTS;


    OrbitDataBundle initData;
    float[] initPoints;
    float[] simSettings;
    int[] simOptions;
    float TWOPI = 2.0f* (float) Math.PI;
    private boolean parallelPerspectiveActive = true;


    public Simulator(int Iterations) {
        NUMBER_OF_POINTS = Iterations;
        dataArray = new float[(GeneralConstants.POSITION_COMPONENT_COUNT + 0) * NUMBER_OF_POINTS
                                + (GeneralConstants.POSITION_COMPONENT_COUNT + 0)*6];
        initData = new OrbitDataBundle();

    }


    private void calcData() {
        dataArray[0] = initPoints[0];
        dataArray[1] = initPoints[1];
        dataArray[2] = initPoints[2];
        float p2 = initPoints[3];
        float sinQ12;
        float A = simSettings[0] / TWOPI;
        float K1 = simSettings[1] / TWOPI;
        float K2 = simSettings[2] / TWOPI;
        float sign = (float) simOptions[1];
        int n = 3;
        float perspectiveScale = 1.0f;
        float dist = 0.2f;

        for (int i=1; i < NUMBER_OF_POINTS ; i++) {
            dataArray[n*i] = (dataArray[n*i - 3] + dataArray[n*i - 1]
                    + GeneralConstants.MODUL_SAFTY_GUARD) % GeneralConstants.TORUS_MODUL_SIZE;
            dataArray[n*i+1] = (dataArray[3*i - 2] + sign*p2 + GeneralConstants.MODUL_SAFTY_GUARD)
                    % GeneralConstants.TORUS_MODUL_SIZE;
            sinQ12 = (float) Math.sin(TWOPI * (dataArray[3*i] + dataArray[3*i+1]));


            dataArray[n*i+2] = ((dataArray[3*i - 1] + K1 *  (float) Math.sin(TWOPI * dataArray[3*i])
                        + A * sinQ12 + GeneralConstants.MODUL_SAFTY_GUARD
                        + GeneralConstants.P_INTERVALL_END) % GeneralConstants.TORUS_MODUL_SIZE )
                        + GeneralConstants.P_INTERVALL_START;
            p2 = ((p2 + K2 * (float) Math.sin(TWOPI * dataArray[3*i+1])
                        + A * sinQ12 + GeneralConstants.MODUL_SAFTY_GUARD
                        + GeneralConstants.P_INTERVALL_END) % GeneralConstants.TORUS_MODUL_SIZE )
                        + GeneralConstants.P_INTERVALL_START;

            p2 = (((p2 + 0.5f) % 1.0f) - 0.5f);
            if (!parallelPerspectiveActive) {
                perspectiveScale = (dist / (p2 + dist));
            }
            dataArray[n*i - 3] = (dataArray[3*i - 3] + GeneralConstants.P_INTERVALL_START) * perspectiveScale
                                    * GeneralConstants.SCALING_FACTOR;
            dataArray[n*i - 2] = (dataArray[3*i - 2] + GeneralConstants.P_INTERVALL_START) * perspectiveScale
                                    * GeneralConstants.SCALING_FACTOR;
            dataArray[n*i - 1] = (dataArray[3*i - 1]) *  perspectiveScale
                                    * GeneralConstants.SCALING_FACTOR;
        }
        dataArray[3*NUMBER_OF_POINTS - 3] = (dataArray[3*NUMBER_OF_POINTS - 3]
                        + GeneralConstants.P_INTERVALL_START) * GeneralConstants.SCALING_FACTOR;
        dataArray[3*NUMBER_OF_POINTS - 2] = (dataArray[3*NUMBER_OF_POINTS - 2]
                        + GeneralConstants.P_INTERVALL_START) * GeneralConstants.SCALING_FACTOR;
        dataArray[3*NUMBER_OF_POINTS - 1] = (dataArray[3*NUMBER_OF_POINTS - 1])
                        * GeneralConstants.SCALING_FACTOR;
    }


    private void makeSlice() {
        float q1, q2, p1, p2;
        int i = 0;
        int overflowCheck = 0;
        q1 = initPoints[0];
        q2 = initPoints[1];
        p1 = initPoints[2];
        p2 = initPoints[3];
        float sinQ12;
        float A = simSettings[0]/ TWOPI;
        float K1 = simSettings[1]/ TWOPI;
        float K2 = simSettings[2]/ TWOPI;
        float sign = (float) simOptions[1];
        float pSlice = initData.getpSlice();



        while (i < NUMBER_OF_POINTS) {
            overflowCheck++;
            q1 = (q1 + p1 + GeneralConstants.MODUL_SAFTY_GUARD) % GeneralConstants.TORUS_MODUL_SIZE;
            q2 = (q2 + sign*p2 + GeneralConstants.MODUL_SAFTY_GUARD) % GeneralConstants.TORUS_MODUL_SIZE;
            sinQ12 = (float) Math.sin(TWOPI * (q1 + q2));

            p1 = (float) ((p1 + K1 * Math.sin(TWOPI * q1) + A * sinQ12
                    + GeneralConstants.MODUL_SAFTY_GUARD + GeneralConstants.P_INTERVALL_END)
                    % GeneralConstants.TORUS_MODUL_SIZE ) + GeneralConstants.P_INTERVALL_START;
            p2 = (float) ((p2 + K2 * Math.sin(TWOPI * q2) + A * sinQ12
                    + GeneralConstants.MODUL_SAFTY_GUARD + GeneralConstants.P_INTERVALL_END)
                    % GeneralConstants.TORUS_MODUL_SIZE ) + GeneralConstants.P_INTERVALL_START;


            if (Math.abs(p2 - pSlice) < GeneralConstants.SLICE_SIZE) {
                i++;
                dataArray[3*i - 3] = (q1 + GeneralConstants.P_INTERVALL_START)
                                        * GeneralConstants.SCALING_FACTOR;
                dataArray[3*i - 2] = (q2 + GeneralConstants.P_INTERVALL_START)
                                        * GeneralConstants.SCALING_FACTOR;
                dataArray[3*i - 1] = (p1) * GeneralConstants.SCALING_FACTOR;
            }
            if (overflowCheck > GeneralConstants.OVERFLOW_BOUNDARY) {
                while (i < NUMBER_OF_POINTS) {
                    i++;
                    dataArray[3*i - 3] = 0.0f;
                    dataArray[3*i - 2] = 0.0f;
                    dataArray[3*i - 1] = 0.0f;
                }
                break;
            }
        }
    }


    private void unpackOrbitDataBundle() {
        initPoints = initData.getOrbitPoints();
        simSettings = initData.getSimulationSettings();
        simOptions = initData.getSimulationOptions();
    }

    public void run() {
        unpackOrbitDataBundle();
        switch (simOptions[0]) {
            case 1:
                calcData();
                break;
            case 2:
                makeSlice();
                break;
        }


    }



    public float[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(float[] dataArray) {
        this.dataArray = dataArray;
    }

    public OrbitDataBundle getInitData() {
        return initData;
    }

    public void setInitData(OrbitDataBundle initData) {
        this.initData = initData;
    }

    public void switchSliceOption() {
        if (initData.getSlice() == GeneralConstants.NORMAL_PLOT_FLAG) {
            initData.setSlice(GeneralConstants.SLICE_PLOT_FLAG);
            return;
        } else if (initData.getSlice() == GeneralConstants.SLICE_PLOT_FLAG) {
            initData.setSlice(GeneralConstants.NORMAL_PLOT_FLAG);
            return;
        }
    }
    public void switchMinusOption() {
        if (initData.getMinus() == GeneralConstants.POSITIV_SIGN) {
            initData.setMinus(GeneralConstants.NEGATIV_SIGN);
            return;
        } else if (initData.getMinus() == GeneralConstants.NEGATIV_SIGN) {
            initData.setMinus(GeneralConstants.POSITIV_SIGN);
            return;
        }
    }

    public static int getNumberOfPoints() {
        return NUMBER_OF_POINTS;
    }

    public static void setNumberOfPoints(int numberOfPoints) {
        NUMBER_OF_POINTS = numberOfPoints;
    }

    public void setPerspective() {
        if (parallelPerspectiveActive) {
            parallelPerspectiveActive = false;
        } else {
            parallelPerspectiveActive = true;
        }
    }


}

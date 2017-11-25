package wirkungsfunktional.de.emiProjectWS17.utils;

/**
 * Created by mk on 25.11.17.
 */

public class Simulator {


    float[] dataArray;
    private static int NUMBER_OF_POINTS;
    private static final int POSITION_COMPONENT_COUNT = 3;


    OrbitDataBundle initData;
    float[] initPoints;
    float[] simSettings;
    int[] simOptions;
    float twoPi = 2.0f* (float) Math.PI;


    public Simulator(int Iterations) {
        NUMBER_OF_POINTS = Iterations;
        dataArray = new float[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + POSITION_COMPONENT_COUNT*6];
        initData = new OrbitDataBundle();

    }


    private void calcData() {
        dataArray[0] = initPoints[0];
        dataArray[1] = initPoints[1];
        dataArray[2] = initPoints[2];
        float p2 = initPoints[3];
        float sinQ12;
        float A = simSettings[0] / twoPi;
        float K1 = simSettings[1] / twoPi;
        float K2 = simSettings[2] / twoPi;
        float sign = (float) simOptions[1];


        for (int i=1; i < NUMBER_OF_POINTS ; i++) {
            dataArray[3*i] = (dataArray[3*i - 3] + dataArray[3*i - 1]+ 10.0f) % 1.0f;
            dataArray[3*i+1] = (dataArray[3*i - 2] + sign*p2 + 10.0f) % 1.0f;
            sinQ12 = (float) Math.sin(twoPi * (dataArray[3*i] + dataArray[3*i+1]));


            dataArray[3*i+2] = ((dataArray[3*i - 1] + K1 *  (float) Math.sin(twoPi * dataArray[3*i])
                        + A * sinQ12 + 10.5f) % 1.0f ) - 0.5f;
            p2 = ((p2 + K2 * (float) Math.sin(twoPi * dataArray[3*i+1])
                        + A * sinQ12 + 10.5f) % 1.0f )  - 0.5f;


            dataArray[3*i - 3] = (dataArray[3*i - 3] - 0.5f) * 2.0f;
            dataArray[3*i - 2] = (dataArray[3*i - 2] - 0.5f) * 2.0f;
            dataArray[3*i - 1] = (dataArray[3*i - 1]       ) * 2.0f;
        }
        dataArray[3*NUMBER_OF_POINTS - 3] = (dataArray[3*NUMBER_OF_POINTS - 3] - 0.5f) * 2.0f;
        dataArray[3*NUMBER_OF_POINTS - 2] = (dataArray[3*NUMBER_OF_POINTS - 2] - 0.5f) * 2.0f;
        dataArray[3*NUMBER_OF_POINTS - 1] = (dataArray[3*NUMBER_OF_POINTS - 1]       ) * 2.0f;
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
        float A = simSettings[0]/ twoPi;
        float K1 = simSettings[1]/ twoPi;
        float K2 = simSettings[2]/ twoPi;
        float sign = (float) simOptions[1];
        float pSlice = initData.getpSlice();



        while (i < NUMBER_OF_POINTS) {
            overflowCheck++;
            q1 = (q1 + p1 + 10.0f) % 1.0f;
            q2 = (q2 + sign*p2 + 10.0f) % 1.0f;
            sinQ12 = (float) Math.sin(twoPi * (q1 + q2));

            p1 = (float) ((p1 + K1 * Math.sin(twoPi * q1) + A * sinQ12 + 10.5f) % 1.0f ) - 0.5f;
            p2 = (float) ((p2 + K2 * Math.sin(twoPi * q2) + A * sinQ12 + 10.5f) % 1.0f ) - 0.5f;


            if (Math.abs(p2 - pSlice) < 0.001f) {
                i++;
                dataArray[3*i - 3] = (q1 - 0.5f) * 2.0f;
                dataArray[3*i - 2] = (q2 - 0.5f) * 2.0f;
                dataArray[3*i - 1] = (p1       ) * 2.0f;
            }
            if (overflowCheck > 100000L) {
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
        if (initData.getSlice() == 1) {
            initData.setSlice(2);
        } else if (initData.getSlice() == 2) {
            initData.setSlice(1);
        }
    }
    public void switchMinusOption() {
        if (initData.getMinus() == 1) {
            initData.setMinus(-1);
        } else if (initData.getSlice() == -1) {
            initData.setMinus(1);
        }
    }


}

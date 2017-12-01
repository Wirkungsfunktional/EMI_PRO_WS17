package wirkungsfunktional.de.emiProjectWS17.utils;


import java.io.Serializable;


/**
 * Created by mk on 25.11.17.
 */

public class OrbitDataBundle implements Serializable {


    private float[] orbitPoints = new float[4];                // q1, q2, p1, p2
    private float[] simulationSettings = new float[3];         // A, K1, K2
    private int[] simulationOptions = new int[2];            // slice, minus
    float pSlice;

    private String name = "noName";
    private String comment = "";
    private Integer id = 0;

    public OrbitDataBundle() {
        orbitPoints[0] = 0.0f;
        orbitPoints[1] = 0.0f;
        orbitPoints[2] = 0.0f;
        orbitPoints[3] = 0.0f;
        simulationSettings[0] = 1.0f;
        simulationSettings[1] = 2.25f;
        simulationSettings[2] = 3.00f;
        simulationOptions[0] = 1;
        simulationOptions[1] = 1;
        pSlice = 0.5f;
        name = "noName";
        comment = "";
        id = 0;
    }
    OrbitDataBundle(float q1, float q2, float p1, float p2, float A, float K1, float K2, int slice,
                    int minus, float pSliceVal) {
        orbitPoints[0] = q1;
        orbitPoints[1] = q2;
        orbitPoints[2] = p1;
        orbitPoints[3] = p2;
        simulationSettings[0] = A;
        simulationSettings[1] = K1;
        simulationSettings[2] = K2;
        simulationOptions[0] = slice;
        simulationOptions[1] = minus;
        pSlice = pSliceVal;
        name = "noName";
        comment = "";
        id = 0;
    }


    public void writeDataToFile(){}
    public void readDataFromFile(){}



    public float[] getOrbitPoints() {
        return orbitPoints;
    }
    public void setOrbitPoints(float[] orbitPoints) {
        this.orbitPoints = orbitPoints;
    }
    public float[] getSimulationSettings() {
        return simulationSettings;
    }
    public void setSimulationSettings(float[] simulationSettings) {
        this.simulationSettings = simulationSettings;
    }
    public int[] getSimulationOptions() {
        return simulationOptions;
    }
    public void setSimulationOptions(int[] simulationOptions) {
        this.simulationOptions = simulationOptions;
    }
    public float getpSlice() {
        return pSlice;
    }
    public void setpSlice(float pSlice) {
        this.pSlice = pSlice;
    }
    public void setQ1(float v) {
        this.orbitPoints[0] = v;
    }
    public float getQ1() {return this.orbitPoints[0];}
    public float getQ2() {return this.orbitPoints[1];}
    public float getP1() {return this.orbitPoints[2];}
    public float getP2() {return this.orbitPoints[3];}
    public float getA() {return this.simulationSettings[0];}
    public float getK1() {return this.simulationSettings[1];}
    public float getK2() {return this.simulationSettings[2];}
    public float getSliceP() {return this.pSlice;}
    public int getSliceSetting() {return simulationOptions[0];}
    public int getMinusSetting() {return simulationOptions[1];}

    public void setQ2(float v) {
        this.orbitPoints[1] = v;
    }
    public void setP1(float v) {
        this.orbitPoints[2] = v;
    }
    public void setP2(float v) {
        this.orbitPoints[3] = v;
    }
    public void setA(float v) {
        this.simulationSettings[0] = v;
    }
    public void setK1(float v) {
        this.simulationSettings[1] = v;
    }
    public void setK2(float v) {
        this.simulationSettings[2] = v;
    }
    public void setSlice(int v) {
        this.simulationOptions[0] = v;
    }
    public void setMinus(int v) {
        this.simulationOptions[1] = v;
    }
    public int getSlice() {
        return simulationOptions[0];
    }
    public int getMinus() {
        return simulationOptions[1];
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }






}

package wirkungsfunktional.de.emiProjectWS17.utils;

/**
 * Created by mk on 29.10.17.
 */

public class SpecialMatrixContainer {
    private static final float TWOPI = 2.0f * (float) Math.PI;

    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {
        final float angleInRadians = (float)  (yFovInDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f+n) / (f-n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n)/ (f - n));
        m[15] = 0f;


    }

    public static float[][] linearMap(float q1, float q2, float p1, float p2, float A, float K1,
                                    float K2, float sign) {
        float K11 = K1 * (float) Math.cos(TWOPI * (q1 + p1));
        float K22 = K2 * (float) Math.cos(TWOPI * (q2 + sign*p2));
        float AA = A * (float) Math.cos(TWOPI * (q1 + p1 + q2 + sign*p2));
        float[][] m = new float[4][4];
        m[0][0] = 1.0f + K11 + AA;
        m[0][1] = sign*AA;
        m[0][2] = K11 + AA;
        m[0][3] = AA;

        m[1][0] = AA;
        m[1][1] = 1.0f + sign*K22 + sign*AA;
        m[1][2] = AA;
        m[1][3] = K22 + AA;

        m[2][0] = 1.0f;
        m[2][1] = 0.0f;
        m[2][2] = 1.0f;
        m[2][3] = 0.0f;

        m[3][0] = 0.0f;
        m[3][1] = sign*1.0f;
        m[3][2] = 0.0f;
        m[3][3] = 1.0f;
        return m;
    }

    public static float trace(float[][] m, int l) {
        float sum = 0;
        for (int i = 0; i<l; i++) {
            sum += m[i][i];
        }
        return sum;
    }

    public static float[][] dot(float[][] A, float[][] B, int n) {
        float[][] m = new float[n][n];
        float rowSum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rowSum = 0.0f;
                for (int k = 0; k < n; k++) {
                    rowSum += A[i][k] * B[k][i];
                }
                m[i][j] = rowSum;
            }
        }
        return m;
    }



}
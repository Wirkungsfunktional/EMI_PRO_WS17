package wirkungsfunktional.de.emiProjectWS17.utils;

/**
 * Created by mk on 25.11.17.
 */

public class GeneralConstants {
    public static final int COMPLEX_UNSTABLE = 1;
    public static final int ELLIPTIC_ELLIPTIC = 2;
    public static final int ELLIPTIC_HYPERBOLIC = 3;
    public static final int HYPERBOLIC_ELLIPTIC = 4;
    public static final int HYPERBOLIC_HYPERBOLIC = 5;
    public static final String COMPLEX_UNSTABLE_STRING = "Complex Unstable";
    public static final String ELLIPTIC_ELLIPTIC_STRING = "Elliptic Elliptic";
    public static final String ELLIPTIC_HYPERBOLIC_STRING = "Elliptic Hyperbolic";
    public static final String HYPERBOLIC_ELLIPTIC_STRING = "Hyperbolic Elliptic";
    public static final String HYPERBOLIC_HYPERBOLIC_STRING = "Hyperbolic Hyperbolic";
    public static final int REQUEST_CODE_LOAD_ACTIVITY = 1;
    public static int POSITION_COMPONENT_COUNT = 3;
    public static final int PRECI_OF_SEEK_BARS = 100000;
    public static final float P_INTERVALL_START = -0.5f;
    public static final float P_INTERVALL_END = 0.5f;
    public static final float TORUS_MODUL_SIZE = 1.0f;
    public static final float SCALING_FACTOR = 2.0f;
    public static final float MODUL_SAFTY_GUARD = 10.0f;
    public static final float SLICE_SIZE = 0.001f;
    public static final long OVERFLOW_BOUNDARY = 100000L;
    public static final int NORMAL_PLOT_FLAG = 1;
    public static final int SLICE_PLOT_FLAG = 2;
    public static final int POSITIV_SIGN = 1;
    public static final int NEGATIV_SIGN = -1;
    public static final int ITERATIONS = 1000;


    public static String decodeStabilityState(int state) {
        switch (state) {
            case COMPLEX_UNSTABLE:
                return COMPLEX_UNSTABLE_STRING;
            case ELLIPTIC_ELLIPTIC:
                return ELLIPTIC_ELLIPTIC_STRING;
            case ELLIPTIC_HYPERBOLIC:
                return ELLIPTIC_HYPERBOLIC_STRING;
            case HYPERBOLIC_ELLIPTIC:
                return HYPERBOLIC_ELLIPTIC_STRING;
            case HYPERBOLIC_HYPERBOLIC:
                return HYPERBOLIC_HYPERBOLIC_STRING;
        }
        return "";
    }
}

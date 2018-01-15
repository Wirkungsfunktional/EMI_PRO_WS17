package wirkungsfunktional.de.emiProjectWS17;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import wirkungsfunktional.de.emiProjectWS17.utils.GeneralConstants;
import wirkungsfunktional.de.emiProjectWS17.utils.LogContainer;
import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;
import wirkungsfunktional.de.emiProjectWS17.utils.ProgramConstructionContainer;
import wirkungsfunktional.de.emiProjectWS17.utils.Simulator;
import wirkungsfunktional.de.emiProjectWS17.utils.SpecialMatrixContainer;
import wirkungsfunktional.de.emiProjectWS17.utils.TextResourceReader;

import static android.content.Context.SENSOR_SERVICE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by mk on 29.10.17.
 */

class OpenGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private static final String U_COLOR = "u_Color";
    private static final String A_COLOR = "a_Color";

    private final Context context;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int COLOR_COMPONENT_COUNT = 0;
    private static final int STRIDE = (GeneralConstants.POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT) *BYTES_PER_FLOAT;
    private static int NUMBER_OF_POINTS;

    private final FloatBuffer vertexData;
    private int program;
    private int aPositionLocation;
    private int uMatrixLocation;
    private int aColorLocation;
    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private int uColorLocation;
    private float[] mRotationMatrix1 = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float z = 0f, x=0f, y=0f;

    float[] pointArray;

    private SensorManager sensorManager;



    public OpenGLRenderer(Context context, Simulator simulator) {
        this.context = context;
        NUMBER_OF_POINTS = simulator.getNumberOfPoints();
        simulator.setInitData(new OrbitDataBundle());
        simulator.run();
        pointArray = simulator.getDataArray();

        // Add points for the lines of the coordinate system
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS    ] = -1f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 1] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 2] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 3] =  1f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 4] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 5] =  0f;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 6] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 7] = -1f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 8] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 9] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +10] =  1f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +11] =  0f;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +12] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +13] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +14] = -1f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +15] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +16] =  0f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +17] =  1f;

        float lw = 0.5f;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +18] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +19] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +20] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +21] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +22] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +23] =  lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +24] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +25] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +26] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +27] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +28] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +29] =  lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +30] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +31] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +32] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +33] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +34] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +35] =  -lw;


        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +36] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +37] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +38] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +39] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +40] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +41] =  lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +42] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +43] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +44] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +45] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +46] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +47] =  -lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +48] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +49] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +50] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +51] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +52] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +53] =  -lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +54] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +55] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +56] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +57] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +58] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +59] =  lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +60] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +61] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +62] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +63] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +64] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +65] =  -lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +66] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +67] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +68] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +69] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +70] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +71] =  lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +72] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +73] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +74] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +75] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +76] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +77] =  -lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +78] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +79] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +80] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +81] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +82] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +83] =  -lw;

        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +84] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +85] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +86] =  -lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +87] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +88] =  lw;
        pointArray[GeneralConstants.POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +89] =  -lw;



        vertexData = ByteBuffer
                .allocateDirect(pointArray.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(pointArray);

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }




    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ProgramConstructionContainer.compileVertexShader(vertexShaderSource);
        int fragmentShader = ProgramConstructionContainer.compileFragmentShader(fragmentShaderSource);

        program = ProgramConstructionContainer.linkProgram(vertexShader, fragmentShader);

        if (LogContainer.ON) {
            ProgramConstructionContainer.validateProgram(program);
        }

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
        //aColorLocation = glGetAttribLocation(program, A_COLOR);

        //aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, GeneralConstants.POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        //vertexData.position(GeneralConstants.POSITION_COMPONENT_COUNT);
        //glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        //glEnableVertexAttribArray(aColorLocation);

    }
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0,0, width, height);

        SpecialMatrixContainer.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, 0f, 1f, 0f, 1f);

        final float[] tmp = new float[16];
        multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        float[] scratch = new float[16];
        float[] tmp = new float[16];

        long time = SystemClock.uptimeMillis() % 1000000L;
        float angle1 = 0.009f * ((int) time);
        Matrix.setRotateM(mRotationMatrix1, 0, angle1, x/10f, y/10f, z/10f);


        Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, mRotationMatrix1, 0);
        System.arraycopy(scratch, 0, mRotationMatrix, 0, scratch.length);

        glUniformMatrix4fv(uMatrixLocation, 1, false, mRotationMatrix, 0);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 0, NUMBER_OF_POINTS);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 3; i<GeneralConstants.NUMBER_OF_LINES/2; i++) {
            glDrawArrays(GL_LINES, NUMBER_OF_POINTS+2*i, 2);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        x = values[0];
        y = values[1];
        z = values[2];
    }

    public void updateData(Simulator simulator) {
        simulator.run();
        pointArray = simulator.getDataArray();
        vertexData.put(pointArray);
        vertexData.position(0);
    }


}


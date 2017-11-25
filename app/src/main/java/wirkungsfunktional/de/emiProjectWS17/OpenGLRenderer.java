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

    private final Context context;

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int COLOR_COMPONENT_COUNT = 0;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) *BYTES_PER_FLOAT;
    private static final int NUMBER_OF_POINTS = 1000;

    private final FloatBuffer vertexData;
    private int program;
    private int aPositionLocation;
    private int uMatrixLocation;
    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private int uColorLocation;
    private float[] mRotationMatrix1 = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float z = 0f, x=0f, y=0f;

    float[] pointArray;

    private SensorManager sensorManager;
    private static final float PRECI_OF_SEEK_BARS_DIV = (float) MainActivity.PRECI_OF_SEEK_BARS;






    public OpenGLRenderer(Context context, Simulator simulator) {
        this.context = context;
        simulator.setInitData(new OrbitDataBundle());
        simulator.run();
        pointArray = simulator.getDataArray();

        // Add points for the lines of the coordinate system
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS    ] = -1f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 1] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 2] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 3] =  1f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 4] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 5] =  0f;

        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 6] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 7] = -1f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 8] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS + 9] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +10] =  1f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +11] =  0f;

        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +12] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +13] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +14] = -1f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +15] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +16] =  0f;
        pointArray[POSITION_COMPONENT_COUNT * NUMBER_OF_POINTS +17] =  1f;



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

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

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
        float angle1 = 0.009f  * ((int) time);
        Matrix.setRotateM(mRotationMatrix1, 0, angle1, x/10f, y/10f, z/10f);


        Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, mRotationMatrix1, 0);
        System.arraycopy(scratch, 0, mRotationMatrix, 0, scratch.length);

        glUniformMatrix4fv(uMatrixLocation, 1, false, mRotationMatrix, 0);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 0, NUMBER_OF_POINTS);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, NUMBER_OF_POINTS, 2);
        glDrawArrays(GL_LINES, NUMBER_OF_POINTS+2, 2);
        glDrawArrays(GL_LINES, NUMBER_OF_POINTS+4, 2);
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


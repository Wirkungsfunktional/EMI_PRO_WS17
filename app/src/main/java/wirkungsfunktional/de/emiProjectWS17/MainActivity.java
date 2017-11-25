package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;
import wirkungsfunktional.de.emiProjectWS17.utils.Simulator;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private TextView textView1;
    private OpenGLRenderer openGLRenderer;
    private static final int NUMBER_OF_SEEK_BARS = 8;
    public static final int PRECI_OF_SEEK_BARS = 100000;
    private SeekBar[] seekBarsList = new SeekBar[NUMBER_OF_SEEK_BARS];
    private String[] seekBarID = {"seekBarQ1", "seekBarP1","seekBarQ2", "seekBarP2", "seekBarK",
            "seekBarK1", "seekBarK2", "seekBarSlice"};
    private Button sliceOptionButton;
    private Button minusOptionButton;
    private Simulator simulator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        simulator = new Simulator(1000);

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            openGLRenderer = new OpenGLRenderer(this, simulator);
            glSurfaceView.setRenderer(openGLRenderer);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This Device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(R.layout.activity_open_gl);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mainLayout);
        frameLayout.addView(glSurfaceView, 0);



        for (int i=0; i<NUMBER_OF_SEEK_BARS;i++) {
            int resID = getResources().getIdentifier(seekBarID[i], "id", getPackageName());
            seekBarsList[i] = (SeekBar) findViewById(resID);
            seekBarsList[i].setMax(PRECI_OF_SEEK_BARS);
            seekBarsList[i].setOnSeekBarChangeListener(this);
        }
        textView1 = (TextView) findViewById(R.id.textShow);

        sliceOptionButton = findViewById(R.id.sliceOptionButton);
        sliceOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulator.switchSliceOption();
                Toast.makeText(getApplicationContext(), "Change the Plot Option", Toast.LENGTH_LONG).show();

            }
        });
        minusOptionButton = findViewById(R.id.minusOptionButton);
        minusOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulator.switchMinusOption();
                Toast.makeText(getApplicationContext(), "Change the Sign Option", Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        OrbitDataBundle data = simulator.getInitData();
        float value = (float) i / PRECI_OF_SEEK_BARS;

        switch (seekBar.getId()) {
            case R.id.seekBarQ1:
                data.setQ1(value);
                break;
            case R.id.seekBarP1:
                data.setP1(value - 0.5f);
                break;
            case R.id.seekBarP2:
                data.setP2(value - 0.5f);
                break;
            case R.id.seekBarQ2:
                data.setQ2(value);
                break;
            case R.id.seekBarK:
                data.setA(3.0f * value);
                break;
            case R.id.seekBarK1:
                data.setK1(3.0f * value);
                break;
            case R.id.seekBarK2:
                data.setK2(3.0f * value);
                break;
            case R.id.seekBarSlice:
                data.setpSlice(value - 0.5f);
        }
        simulator.setInitData(data);
        openGLRenderer.updateData(simulator);


        textView1.setText("q1:");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //openGLRenderer.updateData(q1, q2, p1, p2);
    }






}

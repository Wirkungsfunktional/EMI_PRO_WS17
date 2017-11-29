package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import wirkungsfunktional.de.emiProjectWS17.utils.GeneralConstants;
import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;
import wirkungsfunktional.de.emiProjectWS17.utils.Simulator;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private TextView textView1;
    private OpenGLRenderer openGLRenderer;
    private static final int NUMBER_OF_SEEK_BARS = 8;
    private SeekBar[] seekBarsList = new SeekBar[NUMBER_OF_SEEK_BARS];
    private String[] seekBarID = {"seekBarQ1", "seekBarP1","seekBarQ2", "seekBarP2", "seekBarK",
            "seekBarK1", "seekBarK2", "seekBarSlice"};
    private Button sliceOptionButton;
    private Button minusOptionButton;
    private Button savedFileButton;
    private Button perspectiveButton;
    private Button loadButton;
    private Simulator simulator;
    private OrbitDataBundle currentData = new OrbitDataBundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        simulator = new Simulator(GeneralConstants.ITERATIONS);

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
            seekBarsList[i].setMax(GeneralConstants.PRECI_OF_SEEK_BARS);
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

        savedFileButton = (Button) findViewById(R.id.savedFileButton);
        savedFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SaveFileActivity.class);
                intent.putExtra("data", currentData);
                startActivity(intent);
                //startSaveFileActivity();
            }
        });

        loadButton = (Button) findViewById(R.id.loadFileButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFileSelection();
            }
        });

        perspectiveButton = (Button) findViewById(R.id.perspectiveButton);
        perspectiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulator.setPerspective();
            }
        });
    }

    private void startSaveFileActivity() {
        startActivity(new Intent(this, SaveFileActivity.class));
    }

    private void startFileSelection() {
        startActivity(new Intent(this, FileSelection.class));
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
        float value = (float) i / GeneralConstants.PRECI_OF_SEEK_BARS;

        switch (seekBar.getId()) {
            case R.id.seekBarQ1:
                data.setQ1(value);
                break;
            case R.id.seekBarP1:
                data.setP1(value + GeneralConstants.P_INTERVALL_START);
                break;
            case R.id.seekBarP2:
                data.setP2(value + GeneralConstants.P_INTERVALL_START);
                break;
            case R.id.seekBarQ2:
                data.setQ2(value);
                break;
            case R.id.seekBarK:
                data.setA(2.0f * value);
                break;
            case R.id.seekBarK1:
                data.setK1(4.0f * value);
                break;
            case R.id.seekBarK2:
                data.setK2(4.0f * value);
                break;
            case R.id.seekBarSlice:
                data.setpSlice(value + GeneralConstants.P_INTERVALL_START);
        }
        simulator.setInitData(data);
        currentData = data;
        openGLRenderer.updateData(simulator);


        float[] orbitInitPoints = data.getOrbitPoints();
        float[] orbitInitSetting = data.getSimulationSettings();
        textView1.setText(  "q1: " + orbitInitPoints[0] + " " +
                            "q2: " + orbitInitPoints[1] + " " +
                            "p1: " + orbitInitPoints[2] + " " +
                            "p2: " + orbitInitPoints[3] + " " +
                            "A: " + orbitInitSetting[0] + " " +
                            "K1: " + orbitInitSetting[1] + " " +
                            "K2: " + orbitInitSetting[2] + " "
        );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //openGLRenderer.updateData(q1, q2, p1, p2);
    }






}

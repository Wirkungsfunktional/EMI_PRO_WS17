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

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private TextView textView1;
    private int p1, p2, q1, q2;
    private OpenGLRenderer openGLRenderer;
    private static final int NUMBER_OF_SEEK_BARS = 7;
    public static final int PRECI_OF_SEEK_BARS = 100000;
    private SeekBar[] seekBarsList = new SeekBar[NUMBER_OF_SEEK_BARS];
    private String[] seekBarID = {"seekBarQ1", "seekBarP1","seekBarQ2", "seekBarP2", "seekBarK", "seekBarK1", "seekBarK2"};
    private Button sliceOptionButton;
    private boolean sliceOpt = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            openGLRenderer = new OpenGLRenderer(this);
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
                if (sliceOpt == false) {
                    openGLRenderer.setPlotOption(2);
                    sliceOpt = true;
                } else {
                    openGLRenderer.setPlotOption(1);
                    sliceOpt = false;
                }
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
        int K=0, opt=0;

        switch (seekBar.getId()) {
            case R.id.seekBarQ1:
                q1 = i;
                break;
            case R.id.seekBarP1:
                p1 = i;
                break;
            case R.id.seekBarP2:
                p2 = i;
                break;
            case R.id.seekBarQ2:
                q2 = i;
                break;
            case R.id.seekBarK:
                opt = 1;
                K = i;
                break;
            case R.id.seekBarK1:
                opt = 2;
                K = i;
                break;
            case R.id.seekBarK2:
                opt = 3;
                K = i;
                break;
        }
        openGLRenderer.updateData(q1, q2, p1, p2, K, opt);
        textView1.setText("q1: " + openGLRenderer.getQ01() + " q2: " + openGLRenderer.getQ02()
                + " p1: " + openGLRenderer.getP01() + " p2: " + openGLRenderer.getP02() + " A: "
                + openGLRenderer.getA() + " K1: " + openGLRenderer.getK1() + " K2: "
                + openGLRenderer.getK2());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //openGLRenderer.updateData(q1, q2, p1, p2);
    }






}

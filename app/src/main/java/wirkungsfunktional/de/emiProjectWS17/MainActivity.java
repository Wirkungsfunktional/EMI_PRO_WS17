package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
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
    private OpenGLRenderer openGLRenderer;
    private static final int NUMBER_OF_SEEK_BARS = 8;
    private SeekBar[] seekBarsList = new SeekBar[NUMBER_OF_SEEK_BARS];
    private String[] seekBarID = {"seekBarQ1", "seekBarP1","seekBarQ2", "seekBarP2", "seekBarK",
            "seekBarK1", "seekBarK2", "seekBarSlice"};
    private Button showCommentButton;
    private Simulator simulator;
    private OrbitDataBundle currentData = new OrbitDataBundle();
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private View spaceSliderView, paramSliderView, sliceSliderView;
    private int spaceParamActive = 0;
    private static final int NUMBER_OF_TEXT_VIEWS = 7;
    private TextView[] parameterView = new TextView[NUMBER_OF_TEXT_VIEWS];



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

        spaceSliderView = findViewById(R.id.spaceSliderView);
        paramSliderView = findViewById(R.id.paramSliderView);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        MenuItem itemSelected = item;
                        mDrawerLayout.closeDrawers();

                        switch (item.getItemId()) {
                            case R.id.loadFileButton:
                                startFileSelection();
                                return true;
                            case R.id.savedFileButtonLabel:
                                Intent intent = new Intent(getApplicationContext(), SaveFileActivity.class);
                                intent.putExtra("data", currentData);
                                startActivity(intent);
                                return true;
                            case R.id.changeSpaceParamID:
                                if (spaceParamActive == 0){
                                    spaceParamActive = 1;
                                    spaceSliderView.setVisibility(View.INVISIBLE);
                                    paramSliderView.setVisibility(View.VISIBLE);
                                } else {
                                    spaceParamActive = 0;
                                    spaceSliderView.setVisibility(View.VISIBLE);
                                    paramSliderView.setVisibility(View.INVISIBLE);
                                }

                                return true;
                            case R.id.sliceOptionSwitch:
                                simulator.switchSliceOption();
                                Toast.makeText(getApplicationContext(), "Change the Plot Option", Toast.LENGTH_LONG).show();
                                return true;
                            case R.id.imprintLabel:
                                Toast.makeText(getApplicationContext(), "Imprint", Toast.LENGTH_LONG).show();
                                return true;
                        }
                        return true;
                    }

                });


        for (int i=0; i<NUMBER_OF_SEEK_BARS;i++) {
            int resID = getResources().getIdentifier(seekBarID[i], "id", getPackageName());
            seekBarsList[i] = (SeekBar) findViewById(resID);
            seekBarsList[i].setMax(GeneralConstants.PRECI_OF_SEEK_BARS);
            seekBarsList[i].setOnSeekBarChangeListener(this);
        }

        parameterView[0] = findViewById(R.id.textViewQ1);
        parameterView[1] = findViewById(R.id.textViewP1);
        parameterView[2] = findViewById(R.id.textViewQ2);
        parameterView[3] = findViewById(R.id.textViewP2);
        parameterView[4] = findViewById(R.id.textViewA);
        parameterView[5] = findViewById(R.id.textViewK1);
        parameterView[6] = findViewById(R.id.textViewK2);

        parameterView[0].setText("Q1 = 0.0");
        parameterView[1].setText("P1 = 0.0");
        parameterView[2].setText("Q2 = 0.0");
        parameterView[3].setText("P2 = 0.0");
        parameterView[4].setText("A  = 0.0");
        parameterView[5].setText("K1 = 0.0");
        parameterView[6].setText("K2 = 0.0");

        showCommentButton = (Button) findViewById(R.id.buttonShowComment);
        showCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCommentDialog();
            }
        });
    }

    private void startCommentDialog() {
        Intent intent = new Intent(getApplicationContext(), CommentView.class);
        intent.putExtra("data", currentData);
        startActivityForResult(intent, GeneralConstants.REQUEST_CODE_COMMMENT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent OrbitData) {
        if (requestCode == GeneralConstants.REQUEST_CODE_LOAD_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                currentData = (OrbitDataBundle) OrbitData.getExtras().get("loadData");
                simulator.setInitData(currentData);
                openGLRenderer.updateData(simulator);
                setSliderPosition(currentData);
            }
        }
        if (requestCode == GeneralConstants.REQUEST_CODE_COMMMENT_ACTIVITY) {
            if (requestCode == RESULT_OK) {
                currentData = (OrbitDataBundle) OrbitData.getExtras().get("loadData");
                simulator.setInitData(currentData);
                openGLRenderer.updateData(simulator);
                setSliderPosition(currentData);
            }
        }
    }


    private void startSaveFileActivity() {
        startActivity(new Intent(this, SaveFileActivity.class));
    }

    private void startFileSelection() {

        startActivityForResult(new Intent(this, FileSelection.class), GeneralConstants.REQUEST_CODE_LOAD_ACTIVITY);
    }

    private void setSliderPosition(OrbitDataBundle dataBundle) {
        seekBarsList[0].setProgress( (int) (dataBundle.getQ1() * GeneralConstants.PRECI_OF_SEEK_BARS));
        seekBarsList[1].setProgress( (int) ((dataBundle.getP1() - GeneralConstants.P_INTERVALL_START)
                * GeneralConstants.PRECI_OF_SEEK_BARS));
        seekBarsList[2].setProgress( (int) (dataBundle.getQ2() * GeneralConstants.PRECI_OF_SEEK_BARS));
        seekBarsList[3].setProgress( (int) ((dataBundle.getP2() - GeneralConstants.P_INTERVALL_START)
                * GeneralConstants.PRECI_OF_SEEK_BARS));
        seekBarsList[4].setProgress( (int) (dataBundle.getA() * GeneralConstants.PRECI_OF_SEEK_BARS / 2.0f));
        seekBarsList[5].setProgress( (int) (dataBundle.getK1() * GeneralConstants.PRECI_OF_SEEK_BARS / 4.0f));
        seekBarsList[6].setProgress( (int) (dataBundle.getK2() * GeneralConstants.PRECI_OF_SEEK_BARS / 4.0f));
        seekBarsList[7].setProgress( (int) ((dataBundle.getpSlice() - GeneralConstants.P_INTERVALL_START)
                * GeneralConstants.PRECI_OF_SEEK_BARS));
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
                break;
        }
        simulator.setInitData(data);
        currentData = data;
        openGLRenderer.updateData(simulator);


        float[] orbitInitPoints = data.getOrbitPoints();
        float[] orbitInitSetting = data.getSimulationSettings();
        int stabilityState = simulator.getStabilityState();

        parameterView[0].setText("Q1 = " + orbitInitPoints[0]);
        parameterView[1].setText("P1 = " + orbitInitPoints[2]);
        parameterView[2].setText("Q2 = " + orbitInitPoints[1]);
        parameterView[3].setText("P2 = " + orbitInitPoints[3]);
        parameterView[4].setText("A  = " + orbitInitSetting[0]);
        parameterView[5].setText("K1 = " + orbitInitSetting[1]);
        parameterView[6].setText("K2 = " + orbitInitSetting[2]);


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //openGLRenderer.updateData(q1, q2, p1, p2);
    }
}
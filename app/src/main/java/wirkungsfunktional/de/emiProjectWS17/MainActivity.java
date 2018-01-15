package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import wirkungsfunktional.de.emiProjectWS17.utils.GeneralConstants;
import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;
import wirkungsfunktional.de.emiProjectWS17.utils.ShowCommentDialog;
import wirkungsfunktional.de.emiProjectWS17.utils.Simulator;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private TextView textView1;
    private OpenGLRenderer openGLRenderer;
    private static final int NUMBER_OF_SEEK_BARS = 11;
    private SeekBar[] seekBarsList = new SeekBar[NUMBER_OF_SEEK_BARS];
    private String[] seekBarID = {"seekBarQ1", "seekBarP1","seekBarQ2", "seekBarP2", "seekBarK",
            "seekBarK1", "seekBarK2", "seekBarSlice", "seekBarSpaceX","seekBarSpaceY","seekBarSpaceZ"};
    private Button sliceOptionButton;
    private Button minusOptionButton;
    private Button savedFileButton;
    private Button perspectiveButton;
    private Button loadButton;
    private Button showCommentButton;
    private Simulator simulator;
    private OrbitDataBundle currentData = new OrbitDataBundle();
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;



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


        /*mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Calling OnClick", Toast.LENGTH_LONG).show();
            }
        });*/


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
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

                }
        );


        for (int i=0; i<NUMBER_OF_SEEK_BARS;i++) {
            int resID = getResources().getIdentifier(seekBarID[i], "id", getPackageName());
            seekBarsList[i] = (SeekBar) findViewById(resID);
            seekBarsList[i].setMax(GeneralConstants.PRECI_OF_SEEK_BARS);
            seekBarsList[i].setOnSeekBarChangeListener(this);
        }
        textView1 = (TextView) findViewById(R.id.textShow);

        /*sliceOptionButton = findViewById(R.id.sliceOptionButton);
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

        showCommentButton = (Button) findViewById(R.id.buttonShowComment);
        showCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCommentDialog();
            }
        });*/
    }

    private void startCommentDialog() {
        FragmentManager manager = getFragmentManager();
        ShowCommentDialog dialog = new ShowCommentDialog();
        dialog.show(manager, "Test");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent OrbitData) {
        if (requestCode == GeneralConstants.REQUEST_CODE_LOAD_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                currentData = (OrbitDataBundle) OrbitData.getExtras().get("loadData");
                textView1.setText("Joo klapt");
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
            case R.id.seekBarSpaceX:
                data.setX(value + GeneralConstants.P_INTERVALL_START);
                break;
            case R.id.seekBarSpaceY:
                data.setY(value + GeneralConstants.P_INTERVALL_START);
                break;
            case R.id.seekBarSpaceZ:
                data.setZ(value + GeneralConstants.P_INTERVALL_START);
                break;
        }
        simulator.setInitData(data);
        currentData = data;
        openGLRenderer.updateData(simulator);


        float[] orbitInitPoints = data.getOrbitPoints();
        float[] orbitInitSetting = data.getSimulationSettings();
        int stabilityState = simulator.getStabilityState();
        textView1.setText(  "q1: " + orbitInitPoints[0] + " " +             //TODO: Make String write Function
                            "q2: " + orbitInitPoints[1] + " " +
                            "p1: " + orbitInitPoints[2] + " " +
                            "p2: " + orbitInitPoints[3] + " " +
                            "A: " + orbitInitSetting[0] + " " +
                            "K1: " + orbitInitSetting[1] + " " +
                            "K2: " + orbitInitSetting[2] + " " +
                            "Stability: " + GeneralConstants.decodeStabilityState(stabilityState)
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
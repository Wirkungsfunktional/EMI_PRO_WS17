package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import wirkungsfunktional.de.emiProjectWS17.utils.DataBaseContainer;
import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;

/**
 * Created by mk on 28.11.17.
 */

public class FileSelection extends Activity {
    private ListView listView;
    private Button deleteButton, loadButton;
    private TextView textView;
    DataBaseContainer db;
    String selectedFile;
    private OrbitDataBundle selectedData = new OrbitDataBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_selection_layout);

        listView = (ListView) findViewById(R.id.fileSelection);
        deleteButton = (Button) findViewById(R.id.buttonDeleteFiles);
        loadButton = (Button) findViewById(R.id.buttonLoadData);
        textView = (TextView) findViewById(R.id.textViewShowDetail);


        db = new DataBaseContainer(this);
        updateListView();



        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = listView.getItemAtPosition(i).toString();
                selectedData = db.getOrbitData(selectedFile);
                textView.setText(   "Q1: " + Float.toString(selectedData.getQ1()) +
                                    " Q2: " + Float.toString(selectedData.getQ2()) +
                                    " P1: " + Float.toString(selectedData.getP1()) +
                                    " P2: " + Float.toString(selectedData.getP2()) +
                                    " A: " + Float.toString(selectedData.getA()) +
                                    " K1: " + Float.toString(selectedData.getK1()) +
                                    " K2: " + Float.toString(selectedData.getK2()));
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("loadData", selectedData);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteDataByName(selectedFile);
                updateListView();
            }


        });


    };

    private void updateListView() {
        ArrayList array_list = db.getAllData();
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}

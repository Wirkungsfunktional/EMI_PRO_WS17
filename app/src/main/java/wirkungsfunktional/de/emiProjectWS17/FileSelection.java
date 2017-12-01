package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
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
    private Button deleteButton;
    private TextView textView;
    DataBaseContainer db;
    String selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_selection_layout);

        listView = (ListView) findViewById(R.id.fileSelection);
        deleteButton = (Button) findViewById(R.id.buttonDeleteFiles);
        textView = (TextView) findViewById(R.id.textViewShowDetail);


        db = new DataBaseContainer(this);
        updateListView();



        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = listView.getItemAtPosition(i).toString();
                OrbitDataBundle data = db.getOrbitData(selectedFile);
                textView.setText(   "Q1: " + Float.toString(data.getQ1()) +
                                    " Q2: " + Float.toString(data.getQ2()) +
                                    " P1: " + Float.toString(data.getP1()) +
                                    " P2: " + Float.toString(data.getP2()) +
                                    " A: " + Float.toString(data.getA()) +
                                    " K1: " + Float.toString(data.getK1()) +
                                    " K2: " + Float.toString(data.getK2()));
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
}

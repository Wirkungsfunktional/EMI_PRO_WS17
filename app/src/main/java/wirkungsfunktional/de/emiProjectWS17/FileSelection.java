package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import wirkungsfunktional.de.emiProjectWS17.utils.DataBaseContainer;

/**
 * Created by mk on 28.11.17.
 */

public class FileSelection extends Activity {
    private ListView listView;
    DataBaseContainer db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_selection_layout);

        listView = (ListView) findViewById(R.id.fileSelection);
        db = new DataBaseContainer(this);
        ArrayList array_list = db.getAllData();
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        listView.setAdapter(arrayAdapter);


    }
;

}

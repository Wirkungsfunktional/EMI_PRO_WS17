package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by mk on 28.11.17.
 */

public class FileSelection extends Activity {
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_selection_layout);

        listView = (ListView) findViewById(R.id.fileSelection);
    }
}

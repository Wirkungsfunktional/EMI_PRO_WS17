package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import wirkungsfunktional.de.emiProjectWS17.utils.DataBaseContainer;
import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;

/**
 * Created by mk on 29.11.17.
 */

public class SaveFileActivity extends Activity {
    Button      saveButton;
    EditText    nameEdit,
                commentEdit;
    DataBaseContainer db;
    TextView dataTextView;
    OrbitDataBundle dataBundle = new OrbitDataBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_data_entry_layout);
        db = new DataBaseContainer(this);



        dataTextView = (TextView) findViewById(R.id.textViewData);
        saveButton = (Button) findViewById(R.id.buttonSaveNewEntry);
        nameEdit = (EditText) findViewById(R.id.editTextName);
        commentEdit = (EditText) findViewById(R.id.editTextComment);




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataBundle = (OrbitDataBundle) bundle.get("data");
            dataTextView.setText(dataBundle.getName());
            nameEdit.setText((CharSequence) dataBundle.getName());
            commentEdit.setText((CharSequence) dataBundle.getComment());
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrbitDataBundle data = new OrbitDataBundle();
                data.setName(nameEdit.getText().toString());
                data.setComment(commentEdit.getText().toString());


                if(db.insertEntry(data)){
                          Toast.makeText(getApplicationContext(), "done",
                                   Toast.LENGTH_SHORT).show();
                    } else {
                       Toast.makeText(getApplicationContext(), "not done",
                               Toast.LENGTH_SHORT).show();
                    }

            }
        });

    }

}

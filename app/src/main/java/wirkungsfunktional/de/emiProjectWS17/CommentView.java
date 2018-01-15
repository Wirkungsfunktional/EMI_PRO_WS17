package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import wirkungsfunktional.de.emiProjectWS17.utils.DataBaseContainer;
import wirkungsfunktional.de.emiProjectWS17.utils.OrbitDataBundle;


/**
 * Created by mk on 31.12.17.
 */

public class CommentView extends Activity {
    private Button changeButton;
    private EditText commentBox;
    OrbitDataBundle dataBundle = new OrbitDataBundle();
    DataBaseContainer db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        db = new DataBaseContainer(this);

        commentBox = (EditText) findViewById(R.id.commentEditBox);
        changeButton = (Button) findViewById(R.id.changeButton);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataBundle = (OrbitDataBundle) bundle.get("data");
            commentBox.setText(dataBundle.getComment());
        }

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBundle.setComment(commentBox.getText().toString());

                db.deleteDataByName(dataBundle.getName());
                if(db.insertEntry(dataBundle)){
                          Toast.makeText(getApplicationContext(), "Update Comment",
                                   Toast.LENGTH_SHORT).show();
                    } else {
                       Toast.makeText(getApplicationContext(), "ERROR in CommentView",
                               Toast.LENGTH_SHORT).show();
                    }


                Intent data = new Intent();
                data.putExtra("loadData", dataBundle);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

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

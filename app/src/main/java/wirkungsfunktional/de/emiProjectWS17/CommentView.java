package wirkungsfunktional.de.emiProjectWS17;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by mk on 31.12.17.
 */

public class CommentView extends Activity {
    private Button changeButton;
    private EditText commentBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        commentBox = (EditText) findViewById(R.id.commentEditBox);
        changeButton = (Button) findViewById(R.id.changeButton);

    }






}

package com.gsu.csc.recycleviewsample1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtDescription;
    private TextView txtRating;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        txtName = (TextView) findViewById(R.id.name);
        txtDescription = (TextView) findViewById(R.id.description);
        txtRating = (TextView) findViewById(R.id.rating);

        extras = getIntent().getExtras();

        if ( extras != null) {
            txtName.setText(extras.getString("name"));
            txtDescription.setText(extras.getString("description"));
            txtRating.setText(extras.getString("rating"));
        }
    }
}

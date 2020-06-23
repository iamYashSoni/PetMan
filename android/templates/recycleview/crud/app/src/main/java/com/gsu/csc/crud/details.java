package com.gsu.csc.crud;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextInputLayout txtName;
    private TextInputLayout txtBreed;
    private EditText txtDOB;
    private Spinner gender;
    private Spinner petType;
    private int ownerId;
    private int petId;
    private String actionType;
    private Bundle extras;
    private Button saveChanges;
    private int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtName = (TextInputLayout) findViewById(R.id.txtName);
        txtBreed = (TextInputLayout) findViewById(R.id.txtBreed);
        txtDOB = (EditText) findViewById(R.id.txtDOB);

        gender = (Spinner) findViewById(R.id.gender);
        ArrayAdapter<CharSequence> genderArrayAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderArrayAdapter);
        gender.setOnItemSelectedListener(this);

        petType = (Spinner) findViewById(R.id.type);
        ArrayAdapter<CharSequence> petArrayAdapter = ArrayAdapter.createFromResource(this, R.array.pet_type, android.R.layout.simple_spinner_item);
        petArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petType.setAdapter(petArrayAdapter);
        petType.setOnItemSelectedListener(this);

        saveChanges = (Button) findViewById(R.id.btnSave);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecord();
            }
        });

        extras = getIntent().getExtras();

        if ( extras != null ){

            actionType = extras.getString("action");
            position = extras.getInt("position", -1);

            txtName.getEditText().setText(extras.getString("name"));
            txtBreed.getEditText().setText(extras.getString("breed"));
            txtDOB.setText(extras.getString("dob"));

            gender.setSelection(genderArrayAdapter.getPosition(extras.getString("sex")));
            petType.setSelection(petArrayAdapter.getPosition(extras.getString("type")));


            ownerId = extras.getInt("owner_id");
            petId = extras.getInt("id", -1);

            txtDOB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar cal = Calendar.getInstance();

                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);


                    if (actionType.equalsIgnoreCase("change")) {
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
                            cal.setTime(df.parse(txtDOB.getText().toString()));

                            year = cal.get(Calendar.YEAR);
                            month = cal.get(Calendar.MONTH);
                            day = cal.get(Calendar.DAY_OF_MONTH);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar c = Calendar.getInstance();

                            c.set(Calendar.YEAR, i);
                            c.set(Calendar.MONTH, i1);
                            c.set(Calendar.DAY_OF_MONTH, i2);

                            txtDOB.setText(DateFormat.getDateInstance().format(c.getTime()));

                        }
                    }, year, month, day);

                    datePickerDialog.setTitle("Date of Birth");
                    datePickerDialog.show();
                }
            });
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();

        //Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean saveRecord() {
        RequestQueue requestQueue;
        JSONObject payload = new JSONObject();
        int httpMethod = 0;
        String url = null;

        try {
            payload.put("name", txtName.getEditText().getText());
            payload.put("sex", gender.getSelectedItem());
            payload.put("type", petType.getSelectedItem());
            payload.put("breed", txtBreed.getEditText().getText());
            payload.put("owner_id", ownerId);
            payload.put("dob", txtDOB.getText());

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (actionType.equals("add")) {
            httpMethod = Request.Method.POST;
            url = getString(R.string.api_url) + "/pet";

        } else if ( actionType.equals("change")) {
            httpMethod = Request.Method.PUT;
            url = getString(R.string.api_url) + "/pet/" + petId + "/" + ownerId;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(httpMethod, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String payloadResult = response.getJSONObject("record").toString();

                    Intent result = new Intent();

                    result.putExtra("payload", payloadResult);
                    result.putExtra("position", position);
                    setResult(RESULT_OK, result);

                    finish();

                } catch (JSONException e) {
                    Intent result = new Intent();

                    result.putExtra("payload", e.toString());
                    result.putExtra("position", position);
                    setResult(RESULT_CANCELED, result);

                    finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error adding record " + error.toString(), Toast.LENGTH_LONG).show();
                Intent result = new Intent();

                result.putExtra("payload", error.toString());
                setResult(RESULT_CANCELED, result);

                finish();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);

        return true;
    }


}

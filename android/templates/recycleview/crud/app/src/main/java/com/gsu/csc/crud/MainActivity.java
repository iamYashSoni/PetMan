package com.gsu.csc.crud;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.PetAdapter;
import models.PetModel;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<PetModel> petModelList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parseJSON(true);

                petAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), details.class);
                intent.putExtra("owner_id", 1);
                intent.putExtra("action", "add");

                //v.getContext().startActivityForResult(intent);
                startActivityForResult(intent, 1);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        petModelList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(this);
        parseJSON(false);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void parseJSON(final boolean reload) {
        String url = getString(R.string.api_url).concat("/pets/1");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("pets");

                    //Log.d("PETS", "Array size : " + jsonArray.length());

                    if (reload) petModelList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject pet = jsonArray.getJSONObject(i);

                        try{
                            Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(pet.getString("dob"));

                            petModelList.add(new PetModel(pet.getInt("id"),
                                    pet.getString("name"),
                                    pet.getString("sex"),
                                    pet.getString("breed"),
                                    pet.getInt("owner"),
                                    dob,
                                    pet.getString("type"))
                            );

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }

                    petAdapter = new PetAdapter(MainActivity.this, petModelList);
                    recyclerView.setAdapter(petAdapter);
                    //petAdapter.setOnItemClickListener(MainActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            final int position = viewHolder.getAdapterPosition();

            AlertDialog.Builder alertDialogBuilder;

            switch (i){
                case ItemTouchHelper.LEFT:

                    alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.dialog_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    int ownerId = petModelList.get(position).getOwner_id();
                                    int petId = petModelList.get(position).getId();

                                    deleteRecord(ownerId,petId );

                                    petModelList.remove(position);
                                    petAdapter.notifyItemRemoved(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_btn_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    petAdapter.notifyDataSetChanged();
                                    dialogInterface.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    break;

                case ItemTouchHelper.RIGHT:
                    break;
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if ( resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();

                if ( extras != null ) {
                    Log.d("petman", extras.getString("payload").toString());
                    updateRecord(extras.getString("payload"), extras.getInt("position"));
                }
            }
        }
    }

    private boolean updateRecord(String payload, int position) {

        try {
            JSONObject record = new JSONObject(payload);

            if ( position == -1) {

                try {
                    Date dob = new SimpleDateFormat("yyyy-mm-dd").parse(record.getString("dob"));

                    PetModel pet = new PetModel(record.getInt("id"),
                            record.getString("name"),
                            record.getString("sex"),
                            record.getString("breed"),
                            record.getInt("owner"),
                            dob,
                            record.getString("type"));

                    petModelList.add(pet);


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else{
                try{
                    PetModel pet = petModelList.get(position);

                    pet.setName(record.getString("name"));
                    pet.setSex(record.getString("sex"));
                    pet.setBreed(record.getString("breed"));
                    pet.setType(record.getString("type"));

                    pet.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(record.getString("dob")));


                }  catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
            return false;
        }


        petAdapter.notifyDataSetChanged();
        return true;
    }

    private boolean deleteRecord(int ownerId, int petId) {
        String url = getString(R.string.api_url).concat("/pet/" + petId + "/" + ownerId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Record removed without issues", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error removing record - " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,  DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        return true;
    }
}

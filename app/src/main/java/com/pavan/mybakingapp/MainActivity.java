package com.pavan.mybakingapp;

import android.appwidget.AppWidgetProvider;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pavan.mybakingapp.Adapters.MainAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    int RecipeIamges[]={R.drawable.nutellapie,R.drawable.brownies,R.drawable.yellowcake,R.drawable.cheeesecake};
    RequestQueue requestQueue;
    String[] recepienames;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerview);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                {




                    try {
                        JSONArray rootArray=new JSONArray(response);
                        recepienames=new String[rootArray.length()];
                        for(int index=0;index<rootArray.length();index++)
                        {
                            JSONObject recipeObject=rootArray.getJSONObject(index);
                            recepienames[index]=recipeObject.optString("name");


                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(new MainAdapter(getApplicationContext(),recepienames,RecipeIamges));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, " OOPS...No Response", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(stringRequest);
       /* recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(new MainAdapter(getApplicationContext(),recepienames,RecipeIamges));*/
    }

}

package com.pavan.mybakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pavan.mybakingapp.Adapters.MainAdapter;
import com.pavan.mybakingapp.POJOs.IngredientsModel;
import com.pavan.mybakingapp.POJOs.StepsModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    RequestQueue requestQueue;
    String recipePosition;
    String[] recipeNames;
    ArrayList<IngredientsModel> ingredientsModelList;
    ArrayList<StepsModel> stepsModelList;
    TextView Ingredients;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static  final String INGREDIENTSSTRING="ingredientslist";
    StringBuffer ingredientsListString ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toast.makeText(this, "Click on the Ingredients List to diplay widgets ", Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recipePosition= getIntent().getStringExtra("positions");
        recipeNames=getIntent().getStringArrayExtra("names");
        setTitle(recipeNames[Integer.parseInt(recipePosition)]);
        sharedPreferences=getSharedPreferences(this.getPackageName(),MODE_PRIVATE);
        editor=sharedPreferences.edit();




        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        final View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        ingredientsModelList=new ArrayList<>();
        stepsModelList=new ArrayList<>();
        ingredientsListString=new StringBuffer();

        Ingredients=findViewById(R.id.ingredientslist);


        Log.i("position", recipePosition);
        requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray rootArray=new JSONArray(response);
                    JSONObject reipeObject=rootArray.getJSONObject(Integer.parseInt(recipePosition));
                    JSONArray jsonArray=reipeObject.getJSONArray("ingredients");
                    for(int index=0;index<jsonArray.length();index++)
                    {
                        JSONObject ingredientsObject=jsonArray.getJSONObject(index);
                        String quantity=ingredientsObject.optString("quantity");
                        String measure=ingredientsObject.optString("measure");
                        String ingredient=ingredientsObject.optString("ingredient");
                        ingredientsModelList.add(new IngredientsModel(quantity,measure,ingredient));

                    }
                    JSONArray stepsArray=reipeObject.getJSONArray("steps");
                    for(int index=0;index<jsonArray.length();index++)
                    {
                        JSONObject stepsObject=stepsArray.getJSONObject(index);
                        String shortDescription=stepsObject.optString("shortDescription");
                        String description=stepsObject.optString("description");
                        String url;
                        if((stepsObject.optString("videoURL")!=""))
                        {
                            url=stepsObject.optString("videoURL");
                        }
                        else {
                            url=stepsObject.optString("thumbnailURL");
                        }

                        stepsModelList.add(new StepsModel(shortDescription,description,url));
                        Log.i("data",shortDescription+"__"+description+"__"+url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                buildIngredients(Ingredients);
                Ingredients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString(INGREDIENTSSTRING, ingredientsListString.toString());
                        editor.apply();
                        Intent intent=new Intent(getApplicationContext(),NewAppWidget.class);
                        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        int[] ids= AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(),NewAppWidget.class));
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                        sendBroadcast(intent);



                    }
                });
                setupRecyclerView((RecyclerView) recyclerView);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ItemListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);


    }
    private void buildIngredients(TextView textView) {

        for (int i = 0; i < ingredientsModelList.size(); i++) {

            ingredientsListString.append(i+1+"."+ ingredientsModelList.get(i).getIngredient() + "\n -->"  + ingredientsModelList.get(i).getQuantity()+"-"+ingredientsModelList.get(i).getMeasure()+"\n");
            textView.setText(ingredientsListString);
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this,stepsModelList, mTwoPane));
    }


    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {

        private final ItemListActivity mParentActivity;
        private final List<StepsModel> mValues;
        private final boolean mTwoPane;

        public SimpleItemRecyclerViewAdapter(ItemListActivity mParentActivity, List<StepsModel> mValues, boolean mTwoPane) {
            this.mParentActivity = mParentActivity;
            this.mValues = mValues;
            this.mTwoPane = mTwoPane;
        }





        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Log.i("ingerdients",mValues.get(position).getShortDescription());
            holder.mIdView.setText(mValues.get(position).getShortDescription());

            //holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StepsModel stepsModel=mValues.get(position);
                    String[] eachStepModel={stepsModel.getDescription(),stepsModel.getShortDescription(),stepsModel.getVideoURL()};
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();


                        arguments.putStringArray(ItemDetailFragment.ARG_ITEM_ID,eachStepModel);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {

                        Intent intent = new Intent(mParentActivity, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID,eachStepModel);

                        mParentActivity.startActivity(intent);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;


            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);

            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

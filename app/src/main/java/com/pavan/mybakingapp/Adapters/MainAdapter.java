package com.pavan.mybakingapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.pavan.mybakingapp.ItemDetailActivity;
import com.pavan.mybakingapp.ItemListActivity;
import com.pavan.mybakingapp.MainActivity;
import com.pavan.mybakingapp.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    public  static  final String ITEMPOSITION="itemposition";
    Context context;
    String[] recepienames;
    int[] recipeIamges;

    public MainAdapter(Context applicationContext, String[] recepienames, int[] recipeIamges) {
        context=applicationContext;
        this.recepienames=recepienames;
        this.recipeIamges=recipeIamges;
        Log.i("recipenames",recepienames.toString());

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view=LayoutInflater.from(context).inflate( R.layout.recipecarddesign,viewGroup,false);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(recipeIamges[i]);
        viewHolder.textView.setText(recepienames[i]);

    }

    @Override
    public int getItemCount() {
        return recepienames.length ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;



        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.recipeimage);
            textView=itemView.findViewById(R.id.recipetext);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String i= String.valueOf(getAdapterPosition());
                    Intent intent=new Intent(context, ItemListActivity.class);
                    intent.putExtra("positions",i);
                    intent.putExtra("names",recepienames);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });

        }




    }
}

package com.example.gustaftegner.shouldwetest;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {

    private List<ShouldWeVenue> itemList;
    private Context context;

    public SolventRecyclerViewAdapter(Context context, List<ShouldWeVenue> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
        SolventViewHolders rcv = new SolventViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(SolventViewHolders holder, int position) {
        String name = itemList.get(position).getName();
        holder.countryName.setText(name);
        holder.ratingBar.setRating(3);




        //holder.barPhoto.setParseFile(itemList.get(position).getImage());
        try {
            ParseFile image = itemList.get(position).getImage();

            Uri imageuri = Uri.parse(image.getUrl());
            Log.d("Solvent", "Actually got an imagnve for: " + name);

            Log.d("Solvent", imageuri.toString());
            Picasso.with(context).load(imageuri.toString()).centerCrop().fit().into(holder.countryPhoto);
            //notifyDataSetChanged();
        }catch (NullPointerException e){
            Log.d("Solvent", "Couldnt find image for: " + itemList.get(position).getName());
            Picasso.with(context).load(R.drawable.chaffet).centerCrop().fit().into(holder.countryPhoto);

        }




        //holder.barPhoto.loadInBackground();
        //notifyDataSetChanged();
        //notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
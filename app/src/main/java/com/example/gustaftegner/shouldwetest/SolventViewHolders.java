package com.example.gustaftegner.shouldwetest;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseImageView;

public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView countryName;
    public ImageView countryPhoto;
    public RatingBar ratingBar;
    public RelativeLayout relativeLayout;
    public ParseImageView barPhoto;

    public boolean clicked = false;

    public SolventViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        countryName = (TextView) itemView.findViewById(R.id.country_name);
        countryName.setShadowLayer(2, 1, 1, Color.BLACK);

        countryPhoto = (ImageView) itemView.findViewById(R.id.country_photo);
        ratingBar = (RatingBar) itemView.findViewById(R.id.rating_text);
        //barPhoto = (ParseImageView) itemView.findViewById(R.id.icon);

        //relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relLayout);







    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        Log.d("solvent", "Clicked it, now expand relativelayout");
        /**RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.height = 150;
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.setLayoutParams(params);**/



    }
}

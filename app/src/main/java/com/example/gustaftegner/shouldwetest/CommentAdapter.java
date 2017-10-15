package com.example.gustaftegner.shouldwetest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by gustaftegner on 09/11/15.
 */
public class CommentAdapter extends BaseAdapter {


    LayoutInflater inflater;
    Context context;
    protected List<UserReview> detailsList;

    public CommentAdapter(Context context, List<UserReview> detailsList) {
        this.context = context;
        this.detailsList = detailsList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return detailsList.size();
    }

    @Override
    public Object getItem(int position){
        return detailsList.get(position);
    }
    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();

            convertView = this.inflater.inflate(R.layout.comment_item_layout, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.user_name);
            holder.profilePictureView= (ProfilePictureView) convertView.findViewById(R.id.profileImage);
            holder.review = (TextView) convertView.findViewById(R.id.review_text);

            holder.likeCount = (TextView) convertView.findViewById(R.id.like_count);
            holder.upvote = (ImageView) convertView.findViewById(R.id.comment_upvote);
            holder.downvote = (ImageView) convertView.findViewById(R.id.comment_downvote);


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final UserReview result = detailsList.get(position);

        holder.name.setText(result.getFromFBUserName());

        holder.profilePictureView.setPresetSize(ProfilePictureView.SMALL);
        //String fbID = result.getFromUser().getString(ParseConstants.KEY_FACEBOOK_ID);
        holder.profilePictureView.setProfileId(result.getFromFBUserId());
        //holder.other.setText(String.valueOf(result.getRating()));
        holder.review.setText(result.getReviewText());

        holder.likeCount.setText(String.valueOf(result.getLikeCount()));



        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CommentAdapter", "upvote clicked");
                result.increment("LikeCount", 1);
                result.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            holder.upvote.setEnabled(false);
                        }
                    }
                });
                notifyDataSetChanged();
            }
        });

        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CommentAdapter", "downvote clicked");

                result.increment("LikeCount", -1);
                result.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            holder.downvote.setEnabled(false);
                        }
                    }
                });
                notifyDataSetChanged();
            }
        });
        return convertView;

    }

    public class ViewHolder{
        TextView name;
        ProfilePictureView profilePictureView;
        TextView review;

        TextView likeCount;
        ImageView upvote;
        ImageView downvote;

    }
}

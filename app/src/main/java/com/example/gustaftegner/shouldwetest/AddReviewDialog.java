package com.example.gustaftegner.shouldwetest;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by gustaftegner on 01/03/16.
 */
public class AddReviewDialog extends DialogFragment {

    public static final String TAG = AddReviewDialog.class.getSimpleName();
    Button mButton;
    EditText mReviewText;
    TextView mCharCount;
    RatingBar mRatingBar;
    SeekBar mSeekBar;
    RelativeLayout textLayout;
    ParseObject venueReview;
    ParseUser mCurrentUser;

    String venueId;

    public static AddReviewDialog newInstance(int myIndex) {
        AddReviewDialog addReviewDialog = new AddReviewDialog();

        //example of passing args
        Bundle args = new Bundle();
        args.putInt("anIntToSend", myIndex);
        addReviewDialog.setArguments(args);

        return addReviewDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mCurrentUser = ParseUser.getCurrentUser();

        //lat = getArguments().getDouble("Latitude");
        //lon = getArguments().getDouble("Longitude");
        venueId = getArguments().getString("venueid");


        /**currentLocation = new Location("Location");
        currentLocation.setLongitude(lon);
        currentLocation.setLatitude(lat);**/

        Log.d(TAG, "oncreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //read the int from args
        int myInteger = getArguments().getInt("anIntToSend");

        View rootView = inflater.inflate(R.layout.fragment_venue_review, container, false);

        mButton = (Button) rootView.findViewById(R.id.button);
        mReviewText = (EditText) rootView.findViewById(R.id.reviewText);
        //mCharCount = (TextView) rootView.findViewById(R.id.charcount);
        mRatingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.venue_seekbar);


        InputFilter[] filterArray  =new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(300);
        mReviewText.setFilters(filterArray);

        /**TextWatcher mTextEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCharCount.setText(String.valueOf(s.length() + "/300"));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        };**/

        //mReviewText.addTextChangedListener(mTextEditorWatcher);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReview();
            }
        });

        return rootView;


    }

    public void sendReview(){
        String reviewText = mReviewText.getText().toString();
        int rating = (int) mRatingBar.getRating();
        Log.d(TAG, "Rating is: " + String.valueOf(rating));
        /**venueReview = new ParseObject(ParseConstants.KEY_REVIEW);
         venueReview.put("VenueID", idNameList.get(0));
         venueReview.put("VenueName", idNameList.get(1));
         venueReview.put("ReviewText", reviewText);
         venueReview.put("FromUserId", mCurrentUser.getObjectId());
         venueReview.put("FromFBUserId", mCurrentUser.getString(ParseConstants.KEY_FACEBOOK_ID));
         venueReview.put("LikeCount", 0);**/

        //New Stuff
        UserReview userReview = new UserReview();

        userReview.setFromUser(ParseUser.getCurrentUser());
        userReview.setVenueId(venueId);

        userReview.setVenueName("teztar");
        userReview.setReviewText(reviewText);
        //userReview.setFromUserId(mCurrentUser.getObjectId());

        userReview.setFromFBUserId(mCurrentUser.getString(ParseConstants.KEY_FACEBOOK_ID));
        userReview.setFromFBUserName(mCurrentUser.getUsername());

        userReview.setReviewRating(rating);
        userReview.setCrowdVolume(mSeekBar.getProgress());

        userReview.setLikeCount(0);


        //if(mCurrentUser.getInt(ParseConstants.KEY_POINTS) )
        mCurrentUser.increment(ParseConstants.KEY_POINTS, 10);
        mCurrentUser.saveInBackground();
        final ProgressDialog progress;

        progress = new ProgressDialog(getActivity());
        progress.setTitle("Sending review");
        progress.setMessage("Wait!!");
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        /**
         venueReview.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
        if(e == null){
        progress.dismiss();
        Log.d(TAG, "Review saved: ");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else{
        Log.d(TAG, "failed to upload review");
        }
        }
        });**/

        userReview.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    progress.dismiss();
                    Log.d(TAG, "Review saved: ");
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }else{
                    Log.d(TAG, "failed to upload review");
                }
            }
        });


    }
}

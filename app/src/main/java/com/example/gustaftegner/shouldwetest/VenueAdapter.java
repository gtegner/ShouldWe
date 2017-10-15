package com.example.gustaftegner.shouldwetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result;
import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gustaftegner on 27/10/15.
 */
public class VenueAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    protected List<ShouldWeVenue> detailsList;

    public VenueAdapter(Context context, List<ShouldWeVenue> detailsList) {
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

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();

            convertView = this.inflater.inflate(R.layout.result_item_layout, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.venue_name);
            holder.address= (TextView) convertView.findViewById(R.id.venue_address);
            holder.other = (TextView) convertView.findViewById(R.id.venue_other);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ShouldWeVenue result = detailsList.get(position);

        holder.name.setText(result.getName());
        holder.address.setText(result.getAdress());
        //holder.other.setText(String.valueOf(result.get));

        return convertView;

    }

    public class ViewHolder{
        TextView name;
        TextView address;
        TextView other;
    }
}

package com.campusrecruit.adapter;

import java.util.ArrayList;

import com.krislq.sliding.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PostAdapter extends BaseAdapter {

    Context context;
    ArrayList list;
    private LayoutInflater inflater;
    public PostAdapter(Context context,ArrayList list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.popup_window_item, null);          
            holder.popItem = (TextView) convertView.findViewById(R.id.pop_item);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.popItem.setText(list.get(position).toString());
        holder.popItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, list.get(position).toString(), Toast.LENGTH_LONG).show();
            }
        });
        return convertView;
    }

    protected class Holder{
        TextView popItem;
    }
}
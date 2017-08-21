package com.edu.schooltask.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edu.schooltask.R;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/12.
 */

public class TextListAdapter extends BaseAdapter {
    private List<String> list;

    public TextListAdapter(List<String> list){
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text,null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(list.get(position));
        return view;
    }
}

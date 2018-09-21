package com.a2t.autobpmprompt.app.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Arrays;
import java.util.List;

public abstract class CustomArrayAdapter<T> extends BaseAdapter {
    List<T> mElements;
    Context mContext;

    public CustomArrayAdapter(Context mContext, List<T> elements){
        this.mContext = mContext;
        this.mElements = elements;
    }

    public CustomArrayAdapter(Context mContext, T[] elements){
        this.mContext = mContext;
        this.mElements = Arrays.asList(elements);
    }

    public LayoutInflater getLayoutInflater(){
        return (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public String getResourceString(int res){
        return mContext.getResources().getString(res);
    }

    public Context getContext(){
        return mContext;
    }

    public List<T> getElements(){
        return mElements;
    }

    @Override
    public int getCount() {
        return mElements.size();
    }

    @Override
    public T getItem(int position) {
        if(position < mElements.size()) {
            return mElements.get(position);
        } else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }*/
}

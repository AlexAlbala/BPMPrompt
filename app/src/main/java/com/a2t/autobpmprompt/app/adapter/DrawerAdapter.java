package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2t.autobpmprompt.app.lib.CustomArrayAdapter;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.SetList;

import java.util.List;

/**
 * Created by Alex on 29/10/16.
 */

public class DrawerAdapter extends CustomArrayAdapter<SetList> {
    public DrawerAdapter(Context mContext, List<SetList> elements) {
        super(mContext, elements);
    }

    public DrawerAdapter(Context mContext, SetList[] elements) {
        super(mContext, elements);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.drawer_list_title);
        SetList s = getItem(position);
        title.setText(s.getTitle() + " (" + s.getPrompts().size() + ")");
        return convertView;
    }
}

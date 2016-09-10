package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2t.a2tlib.content.adapter.CustomArrayAdapter;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.PromptSettings;

import java.util.List;

public class SimplePromptListAdapter extends CustomArrayAdapter<PromptSettings> {
    public SimplePromptListAdapter(Context mContext, List<PromptSettings> elements) {
        super(mContext, elements);
    }

    public SimplePromptListAdapter(Context mContext, PromptSettings[] elements) {
        super(mContext, elements);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.row_simple_prompt, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.row_simple_prompt_title)).setText(getItem(position).getName());
        return convertView;
    }
}

package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2t.a2tlib.content.adapter.CustomArrayAdapter;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.PromptSettings;

import java.util.List;

public class PromptsAdapter extends CustomArrayAdapter<PromptSettings> {
    public PromptsAdapter(Context mContext, List<PromptSettings> elements) {
        super(mContext, elements);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.row_prompt, parent, false);
        }

        PromptSettings ps = getItem(position);
        if (ps != null) {
            TextView title = (TextView) convertView.findViewById(R.id.row_prompt_name);
            TextView bpm = (TextView) convertView.findViewById(R.id.row_prompt_bpm);
            TextView beat = (TextView) convertView.findViewById(R.id.row_prompt_beat);
            TextView slist = (TextView) convertView.findViewById(R.id.row_prompt_setlist);
            title.setText(ps.getName());
            bpm.setText(String.valueOf(ps.getBpm()));
            beat.setText(ps.getCfgBarUpper() + "/" + ps.getCfgBarLower());
            slist.setText(ps.getSetList());
        }

        return convertView;
    }
}

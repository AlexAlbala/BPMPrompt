package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2t.autobpmprompt.app.lib.CustomArrayAdapter;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.TempoRecord;

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
            CardView card = (CardView) convertView.findViewById(R.id.card_view);
            TextView title = (TextView) convertView.findViewById(R.id.row_prompt_name);
            TextView bpm = (TextView) convertView.findViewById(R.id.row_prompt_bpm);
            TextView beat = (TextView) convertView.findViewById(R.id.row_prompt_beat);
            TextView slist = (TextView) convertView.findViewById(R.id.row_prompt_setlist);
            TextView pos = (TextView) convertView.findViewById(R.id.row_prompt_number);

            convertView.findViewById(R.id.row_prompt_delete_btn).setVisibility(View.GONE);
            convertView.findViewById(R.id.row_prompt_move_up).setVisibility(View.GONE);
            convertView.findViewById(R.id.row_prompt_move_down).setVisibility(View.GONE);
            convertView.findViewById(R.id.row_prompt_show_hide_btn).setVisibility(View.GONE);
            title.setText(ps.getName());
            TempoRecord tr = ps.getTempoTrack().get(0);
            bpm.setText(String.valueOf(tr.getBpm()));
            beat.setText(tr.getUpper() + "/" + tr.getLower());
            slist.setText(ps.getSetList());
            pos.setText(String.valueOf(ps.getSetListPosition() + 1));
            if (!ps.isEnabled()) {
                card.setCardBackgroundColor(getContext().getResources().getColor(R.color.prompt_disabled));
                pos.setVisibility(View.INVISIBLE);
            } else{
                card.setCardBackgroundColor(getContext().getResources().getColor(android.R.color.white));
                pos.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }
}

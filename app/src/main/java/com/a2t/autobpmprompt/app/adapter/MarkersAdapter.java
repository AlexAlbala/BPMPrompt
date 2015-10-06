package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.MarkerAdapterCallback;
import com.a2t.autobpmprompt.app.model.Marker;

import java.util.List;

public class MarkersAdapter extends BaseAdapter {
    private Context mContext;
    private List<Marker> mItems;
    private boolean mIsEdit;
    private MarkerAdapterCallback mCallback;

    static class ViewHolderItem {
        TextView markerTitle;
        TextView markerNote;
        TextView markerBar;
        TextView markerBeat;
        ImageButton markerDelete;
    }

    public MarkersAdapter(Context context, List<Marker> objects, boolean isEdit, MarkerAdapterCallback callback) {
        mContext = context;
        mItems = objects;
        mIsEdit = isEdit;
        mCallback = callback;
    }

    @Override
    public int getCount() {
        return mIsEdit ? mItems.size() + 1 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem cellView;

        if (mIsEdit && position == mItems.size()) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_emptymarker, parent, false);
            Button b = (Button) convertView.findViewById(R.id.marker_create_button);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCreateMarkerClick();
                }
            });

        } else {
            final Marker marker = (Marker) getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_marker, parent, false);

                // well set up the ViewHolder
                cellView = new ViewHolderItem();

                cellView.markerTitle = (TextView)convertView.findViewById(R.id.marker_title);
                cellView.markerNote = (TextView)convertView.findViewById(R.id.marker_note);
                cellView.markerBar = (TextView)convertView.findViewById(R.id.marker_bar);
                cellView.markerBeat = (TextView)convertView.findViewById(R.id.marker_beat);
                cellView.markerDelete = (ImageButton)convertView.findViewById(R.id.marker_delete);

                // store the holder with the view.
                convertView.setTag(cellView);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                cellView = (ViewHolderItem) convertView.getTag();
            }

            if (marker != null) {
                cellView.markerTitle.setText(marker.getTitle());
                cellView.markerNote.setText(marker.getNote());
                cellView.markerBar.setText(String.valueOf(marker.getBar()));
                cellView.markerBeat.setText(String.valueOf(marker.getBeat()));

                if(mIsEdit){
                    cellView.markerDelete.setVisibility(View.VISIBLE);
                    cellView.markerDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallback.onMarkerRemoved(marker);
                            mItems.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                } else{
                    cellView.markerDelete.setVisibility(View.GONE);
                }

            }
        }
        return convertView;

    }
}

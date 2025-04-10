package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2t.autobpmprompt.app.lib.CustomArrayAdapter;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.MarkerAdapterCallback;
import com.a2t.autobpmprompt.app.model.Marker;

import java.util.List;
import java.util.Random;

public class MarkersAdapter extends CustomArrayAdapter<Marker> {
    private static final String TAG = "MARKERSADAPTER";
    private boolean mIsEdit;
    private MarkerAdapterCallback mCallback;
    private Random r;
    private final int MAX_ROT_DEGREES = 15;
    private final int MIN_ROT_DEGREES = -15;

    static class ViewHolderItem {
        TextView markerTitle;
        TextView markerBar;
        TextView markerBeat;
        //ImageButton markerDelete;
    }

    public MarkersAdapter(Context context, List<Marker> objects, boolean isEdit, MarkerAdapterCallback callback) {
        super(context, objects);
        mIsEdit = isEdit;
        mCallback = callback;

        r = new Random();
    }

    @Override
    public int getCount() {
        return mIsEdit ? getElements().size() + 1 : getElements().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem cellView;

        if (mIsEdit && position == getElements().size()) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_emptymarker, parent, false);
            Button b = (Button) convertView.findViewById(R.id.marker_create_button);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCreateMarkerClick();
                }
            });

        } else {
            final Marker marker = getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_marker, parent, false);

                // well set up the ViewHolder
                cellView = new ViewHolderItem();

                cellView.markerTitle = (TextView) convertView.findViewById(R.id.marker_title);
                cellView.markerBar = (TextView) convertView.findViewById(R.id.marker_bar);
                cellView.markerBeat = (TextView) convertView.findViewById(R.id.marker_beat);
                //cellView.markerDelete = (ImageButton) convertView.findViewById(R.id.marker_delete);

                //Typeface digitalTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/pencil.ttf");

                //cellView.markerTitle.setTypeface(digitalTypeface);
                //currentBpm.setTypeface(digitalTypeface);
                //currentBar.setTypeface(digitalTypeface);

                // store the holder with the view.
                convertView.setTag(cellView);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                cellView = (ViewHolderItem) convertView.getTag();
            }

            /*convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onMarkerClicked(marker);
                }
            });*/

            if (marker != null) {
                Resources res = getContext().getResources();
                cellView.markerTitle.setText(marker.getTitle());


                cellView.markerBar.setText(String.format(res.getString(R.string.prompt_bar_number), marker.getBar()));
                cellView.markerBeat.setText(String.format(res.getString(R.string.prompt_beat_number), marker.getBeat()));

                /*if (mIsEdit) {
                    cellView.markerDelete.setVisibility(View.VISIBLE);
                    cellView.markerDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItems.remove(position);
                            notifyDataSetChanged();
                            mCallback.onMarkerRemoved(marker);
                        }
                    });
                } else {
                    cellView.markerDelete.setVisibility(View.GONE);
                }*/

                //int rotation = r.nextInt(MAX_ROT_DEGREES - MIN_ROT_DEGREES) - MAX_ROT_DEGREES;
                //convertView.setRotation(rotation);

            }
        }
        return convertView;

    }
}

package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.MarkerAdapterCallback;
import com.a2t.autobpmprompt.app.controller.MarkerDialog;
import com.a2t.autobpmprompt.app.controller.PDFDialog;
import com.a2t.autobpmprompt.app.model.Marker;

import java.util.List;

public class MarkersAdapter extends BaseAdapter {
    private Context mContext;
    private List<Marker> mItems;
    private boolean mHasCreate;
    private MarkerAdapterCallback mCallback;

    static class ViewHolderItem {

    }

    public MarkersAdapter(Context context, List<Marker> objects, boolean hasCreate, MarkerAdapterCallback callback) {
        mContext = context;
        mItems = objects;
        mHasCreate = hasCreate;
        mCallback = callback;

    }

    @Override
    public int getCount() {
        return mHasCreate ? mItems.size() + 1 : mItems.size();
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

        if (mHasCreate && position == mItems.size()) {
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


                // store the holder with the view.
                convertView.setTag(cellView);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                cellView = (ViewHolderItem) convertView.getTag();
            }

            if (marker != null) {

            }
        }
        return convertView;

    }
}

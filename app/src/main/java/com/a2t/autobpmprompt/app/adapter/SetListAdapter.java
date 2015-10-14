package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PDFSelectCallback;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SetListAdapter extends BaseAdapter {
    private Context mContext;
    private List<SetList> mItems;
    private SetListAdapterCallback mCallback;
    private boolean mEditMode;

    static class ViewHolderItem {
        TextView setListItem;
        GridView pdfGridItem;
        ImageButton renameBtn;
        ImageButton deleteBtn;
    }

    public SetListAdapter(Context context, List<SetList> objects, boolean editMode, SetListAdapterCallback callback) {
        mContext = context;
        mItems = objects;
        mCallback = callback;
        mEditMode = editMode;
    }

    @Override
    public int getCount() {
        return mItems.size();
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
        final SetList setList = (SetList) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_setlist, parent, false);

            // well set up the ViewHolder
            cellView = new ViewHolderItem();
            cellView.setListItem = (TextView) convertView.findViewById(R.id.setlist_title);
            cellView.pdfGridItem = (GridView) convertView.findViewById(R.id.setlist_pdfgrid);
            cellView.renameBtn = (ImageButton) convertView.findViewById(R.id.setlist_rename_btn);
            cellView.deleteBtn = (ImageButton) convertView.findViewById(R.id.setlist_delete_btn);

            // store the holder with the view.
            convertView.setTag(cellView);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            cellView = (ViewHolderItem) convertView.getTag();
        }

        if (setList != null) {
            cellView.renameBtn.setVisibility(mEditMode ? View.VISIBLE : View.INVISIBLE);
            cellView.deleteBtn.setVisibility(mEditMode ? View.VISIBLE : View.INVISIBLE);


            cellView.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSetListRemovedClicked(setList.getTitle());
                }
            });

            cellView.renameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSetListRenamedClicked(setList.getTitle());
                }
            });

            cellView.setListItem.setText(setList.getTitle());

            List<File> pdflist = new ArrayList<>();
            for (PromptSettings ps : setList.getPrompts()) {
                pdflist.add(new File(ps.getPdfFullPath()));
            }

            PDFGridAdapter pdfAdapter = new PDFGridAdapter(mContext, pdflist, true, new PDFSelectCallback() {
                @Override
                public void onPDFSelected(String fullPath, int pos) {
                    //Toast.makeText(mContext, "SETLISTADAPTER - FILE SELECTED " + fullPath, Toast.LENGTH_LONG).show();
                    mCallback.onPromptSelected(setList.getTitle(), setList.getPrompts().get(pos), position);
                }

                @Override
                public void onCreatePDFClicked() {
                    mCallback.onCreatePromptClicked(setList.getTitle());
                }
            });

            cellView.pdfGridItem.setAdapter(pdfAdapter);


        }

        return convertView;
    }
}

package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PDFSelectCallback;
import com.a2t.autobpmprompt.media.prompt.PromptViewManager;
import com.joanzapata.pdfview.PDFView;

import java.io.File;
import java.util.List;

public class PDFGridAdapter extends BaseAdapter {
    static class ViewHolderItem {
        TextView fileNameItem;
        PDFView pdfView;
        Button container;
    }

    private Context mContext;
    private PDFSelectCallback mCallback = null;
    private List<File> mFiles = null;
    private boolean mAddInsertButton;

    // Gets the context so it can be used later
    public PDFGridAdapter(Context c, List<File> files, boolean addInsertButton, PDFSelectCallback callback) {
        mContext = c;
        mFiles = files;
        mCallback = callback;
        mAddInsertButton = addInsertButton;
    }

    // Total number of things contained within the adapter
    public int getCount() {
        return mAddInsertButton ? mFiles.size() + 1 : mFiles.size();
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderItem cellView;

        if(mAddInsertButton && position == mFiles.size()){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_emptypdf, parent, false);
            ImageButton imgB = (ImageButton)convertView.findViewById(R.id.cell_createprompt);

            imgB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCreatePDFClicked();
                }
            });
        }
        else {
            final File f = (File) getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.cell_pdflist, parent, false);

                // well set up the ViewHolder
                cellView = new ViewHolderItem();
                cellView.fileNameItem = (TextView) convertView.findViewById(R.id.cell_pdftitle);
                cellView.pdfView = (PDFView) convertView.findViewById(R.id.cell_pdfpreview);
                cellView.container = (Button) convertView.findViewById(R.id.cell_pdfbutton);

                // store the holder with the view.
                convertView.setTag(cellView);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                cellView = (ViewHolderItem) convertView.getTag();
            }

            cellView.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPDFSelected(f.getAbsolutePath(), position);
                }
            });

            if (f != null) {
                cellView.fileNameItem.setText(f.getName());
                PromptViewManager.loadThumbnail(f, cellView.pdfView);
            }
        }

        return convertView;
    }
}

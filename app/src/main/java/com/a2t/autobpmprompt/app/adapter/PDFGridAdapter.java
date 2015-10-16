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
import com.a2t.autobpmprompt.app.callback.PDFGridCallback;
import com.a2t.autobpmprompt.media.prompt.PromptPDFFile;

import java.util.List;

public class PDFGridAdapter extends BaseAdapter {
    static class ViewHolderItem {
        TextView fileNameItem;
        //        PDFView pdfView;
        Button container;
        ImageButton deleteButton;
    }

    private Context mContext;
    private PDFGridCallback mCallback = null;
    private List<PromptPDFFile> mFiles = null;
    private boolean mAddInsertButton;
    private boolean mRemovableElements;

    // Gets the context so it can be used later
    public PDFGridAdapter(Context c, List<PromptPDFFile> files, boolean addInsertButton, boolean removableElements, PDFGridCallback callback) {
        mContext = c;
        mFiles = files;
        mCallback = callback;
        mAddInsertButton = addInsertButton;
        mRemovableElements = removableElements;
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
        final ViewHolderItem cellView;

        if (mAddInsertButton && position == mFiles.size()) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_emptypdf, parent, false);
            ImageButton imgB = (ImageButton) convertView.findViewById(R.id.cell_createprompt);

            imgB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCreatePDFClicked();
                }
            });
        } else {
            final PromptPDFFile f = (PromptPDFFile) getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.cell_pdflist, parent, false);

                // well set up the ViewHolder
                cellView = new ViewHolderItem();
                cellView.fileNameItem = (TextView) convertView.findViewById(R.id.cell_pdftitle);
//                cellView.pdfView = (PDFView) convertView.findViewById(R.id.cell_pdfpreview);
                cellView.container = (Button) convertView.findViewById(R.id.cell_pdfbutton);
                cellView.deleteButton = (ImageButton) convertView.findViewById(R.id.cell_deleteButton);

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
                    mCallback.onPDFSelected(f.file.getAbsolutePath(), position);
                }
            });

            if (f != null) {
                cellView.fileNameItem.setText(f.displayName);
//                PromptViewManager.loadThumbnail(f, cellView.pdfView);
//                PromptViewManager.loadThumbnail(f, mContext);

                cellView.deleteButton.setVisibility(mRemovableElements ? View.VISIBLE : View.GONE);
                cellView.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onPDFRemoveClicked(f.file.getAbsolutePath(), position);
                    }
                });
            }
        }

        return convertView;
    }
}

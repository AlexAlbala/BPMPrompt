package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.controller.PDFSelectCallback;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.List;

public class PDFGridAdapter extends BaseAdapter {
    static class ViewHolderItem {
        TextView fileNameItem;
        PDFView pdfView;
    }

    private Context mContext;
    private PDFSelectCallback mCallback = null;
    private List<File> mFiles = null;

    // Gets the context so it can be used later
    public PDFGridAdapter(Context c, List<File> files, PDFSelectCallback callback) {
        mContext = c;
        mFiles = files;
        mCallback = callback;
    }

    // Total number of things contained within the adapter
    public int getCount() {
        return mFiles.size();
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
        final File f = (File) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_pdflist, parent, false);

            // well set up the ViewHolder
            cellView = new ViewHolderItem();
            cellView.fileNameItem = (TextView) convertView.findViewById(R.id.cell_pdftitle);
            cellView.pdfView = (PDFView) convertView.findViewById(R.id.cell_pdfpreview);

            // store the holder with the view.
            convertView.setTag(cellView);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            cellView = (ViewHolderItem) convertView.getTag();
        }

        if (f != null) {
            cellView.fileNameItem.setText(f.getName());

            cellView.fileNameItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPDFSelected(f.getAbsolutePath(), position);
                }
            });

            cellView.pdfView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPDFSelected(f.getAbsolutePath(), position);
                }
            });

            cellView.pdfView.fromFile(f)
                    .defaultPage(0)
                    .pages(0)
                    .showMinimap(false)
                    .enableSwipe(false)
                    .load();
        }

        return convertView;
    }
}

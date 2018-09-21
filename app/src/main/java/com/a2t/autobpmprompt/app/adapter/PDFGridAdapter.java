package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.a2t.autobpmprompt.app.lib.CustomArrayAdapter;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PDFGridCallback;
import com.a2t.autobpmprompt.media.prompt.PromptPDFFile;

import java.util.List;

public class PDFGridAdapter extends CustomArrayAdapter<PromptPDFFile> {
    public PDFGridAdapter(Context mContext, List<PromptPDFFile> elements, PDFGridCallback callback) {
        super(mContext, elements);
        this.mCallback = callback;
    }

    static class ViewHolderItem {
        TextView fileNameItem;
        TextView filePathItem;
        //        PDFView pdfView;
    }

    private PDFGridCallback mCallback = null;

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderItem cellView;

            final PromptPDFFile f = getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_pdflist, parent, false);

                // well set up the ViewHolder
                cellView = new ViewHolderItem();
                cellView.fileNameItem = (TextView) convertView.findViewById(R.id.row_pdf_title);
                cellView.filePathItem = (TextView) convertView.findViewById(R.id.row_pdf_path);

                // store the holder with the view.
                convertView.setTag(cellView);
            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                cellView = (ViewHolderItem) convertView.getTag();
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPDFSelected(f.file.getAbsolutePath(), position);
                }
            });

            if (f != null) {
                cellView.fileNameItem.setText(f.displayName);
                cellView.filePathItem.setText(f.file.getAbsolutePath());
            }

        return convertView;
    }
}

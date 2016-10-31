package com.a2t.autobpmprompt.app.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PromptCardCallbacks;
import com.a2t.autobpmprompt.app.controller.AreYouSureDialog;
import com.a2t.autobpmprompt.app.controller.MainActivity;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;
import com.a2t.autobpmprompt.app.helpers.ItemTouchHelperAdapter;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.media.PromptManager;
import com.a2t.autobpmprompt.media.prompt.PromptViewManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PromptListAdapter extends RecyclerView.Adapter<PromptListAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    private List<PromptSettings> promptsList;
    private PromptCardCallbacks mCallback;
    private A2TActivity mContext;
    private List<MyViewHolder> items;

    private int imageWidth = 0;
    private int imageHeight = 0;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(promptsList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(promptsList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        if (promptsList.size() > 0) {
            PromptManager.reorderPromptInSetList(mContext, promptsList.get(0).getSetList(), fromPosition, toPosition);
            MainActivity ma = (MainActivity) mContext;
            ma.reloadData();
        }
        return true;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView root;
        public View mask;
        //public View clickArea;
        //public PDFView pdf;
        public ImageView pdfThumb;
        public TextView title;
        public TextView setList;
        public TextView number;
        public Toolbar toolbar;

        public MyViewHolder(View view) {
            super(view);
            root = (CardView) view.findViewById(R.id.prompt_item_card_view);
            mask = view.findViewById(R.id.prompt_item_mask);
            //clickArea = view.findViewById(R.id.prompt_item_clickable);
            //pdf = (PDFView) view.findViewById(R.id.prompt_item_pdf);
            title = (TextView) view.findViewById(R.id.prompt_item_name);
            setList = (TextView) view.findViewById(R.id.prompt_item_setlist);
            number = (TextView) view.findViewById(R.id.prompt_item_number);
            toolbar = (Toolbar) view.findViewById(R.id.prompt_item_toolbar);

            pdfThumb = (ImageView) view.findViewById(R.id.prompt_item_thumb);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclePdfs();
                    PromptSettings prompt = promptsList.get(getAdapterPosition());
                    if (prompt.isEnabled()) {
                        PromptManager.openPrompt(mContext, prompt);
                        if (mCallback != null) {
                            mCallback.onPromptSelected(prompt);
                        }
                    }
                }
            });

            if (!items.contains(this)) {
                items.add(this);
            }
        }
    }

    public void recyclePdfs() {
        /*for (MyViewHolder holder : items) {
            if (!holder.pdf.isRecycled()) {
                holder.pdf.recycle();
            }
        }*/
    }


    public PromptListAdapter(List<PromptSettings> moviesList, PromptCardCallbacks mCallback, Activity mContext) {
        this.promptsList = moviesList;
        this.mCallback = mCallback;
        this.mContext = (A2TActivity) mContext;
        items = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prompt_list_elem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PromptSettings prompt = promptsList.get(position);

        holder.title.setText(prompt.getName());
        holder.setList.setText(prompt.getSetList());
        holder.number.setText(String.valueOf(prompt.getSetListPosition() + 1));

        //PromptViewManager.loadThumbnail(new File(prompt.getPdfFullPath()), holder.pdf);

        holder.pdfThumb.post(new Runnable() {
            @Override
            public void run() {
                if (imageHeight == 0 || imageWidth == 0) {
                    imageHeight = holder.pdfThumb.getMeasuredHeight();
                    imageWidth = holder.pdfThumb.getMeasuredWidth();
                    LogUtils.v("RecyclerView", "New measured size : " + imageWidth + " : " + imageHeight);
                }
                try {
                    PromptViewManager.loadThumbnailImage(mContext, prompt.getPdfFullPath(), holder.pdfThumb, imageWidth, imageHeight);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.toolbar.getMenu().clear();
        holder.toolbar.inflateMenu(R.menu.prompt_card_menu);

        holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_prompt:
                        DialogFragment newFragment = new AreYouSureDialog();
                        Bundle b = new Bundle();
                        b.putString("type", "prompt");
                        b.putLong("promptId", prompt.getId());
                        newFragment.setArguments(b);
                        newFragment.show(mContext.getSupportFragmentManager(), "areyousureprompt");
                        if (mCallback != null) {
                            mCallback.onRemovePromptClicked(prompt);
                        }
                        break;
                    case R.id.action_toggle_active_prompt:
                        PromptSettings copied = new PromptSettings();
                        RealmIOHelper.getInstance().CopyPromptSettings(prompt, copied);
                        copied.setEnabled(!prompt.isEnabled());
                        PromptManager.update(mContext, copied);
                        PromptManager.reorderPromptInSetList(mContext, prompt.getSetList(), -1, -1);
                        if (mCallback != null) {
                            mCallback.onActivePromptChanged(prompt);
                        }
                        break;
                }

                return false;
            }
        });

        if (!prompt.isEnabled()) {
            holder.mask.setVisibility(View.VISIBLE);
            holder.number.setVisibility(View.INVISIBLE);
        } else {
            holder.mask.setVisibility(View.GONE);
            holder.number.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return promptsList.size();
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);

        Drawable drawable = holder.pdfThumb.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap.recycle();
        }

        //LogUtils.v("ADAPTER", "RECYCLE " + holder.pdf);
        //holder.pdf.recycle();
    }
}
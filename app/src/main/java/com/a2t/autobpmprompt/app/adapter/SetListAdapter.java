package com.a2t.autobpmprompt.app.adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.a2t.a2tlib.content.compat.A2TActivity;
import com.a2t.a2tlib.views.ExpandedExpandableListView;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.PDFGridCallback;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.media.prompt.PromptPDFFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SetListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<SetList> mItems;
    private SetListAdapterCallback mCallback;
    private boolean mEditMode;

    static class GroupViewHolderItem {
        TextView setListItem;
        ImageButton renameBtn;
        ImageButton deleteBtn;
    }

    static class ChildViewHolderItem {
        TextView pdfItem;
        TextView bpmItem;
        TextView barItem;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mItems.get(groupPosition).getPrompts().size() + 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (childPosition == mItems.get(groupPosition).getPrompts().size()) {
            return null;
        } else {
            return mItems.get(groupPosition).getPrompts().get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 100 * groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolderItem cellView;
        final SetList setList = (SetList) getGroup(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_setlist, parent, false);

            // well set up the ViewHolder
            cellView = new GroupViewHolderItem();
            cellView.setListItem = (TextView) convertView.findViewById(R.id.setlist_title);
            //cellView.renameBtn = (ImageButton) convertView.findViewById(R.id.setlist_rename_btn);
            //cellView.deleteBtn = (ImageButton) convertView.findViewById(R.id.setlist_delete_btn);

            // store the holder with the view.
            convertView.setTag(cellView);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            cellView = (GroupViewHolderItem) convertView.getTag();
        }

        if (setList != null) {
            cellView.renameBtn.setVisibility(mEditMode ? View.VISIBLE : View.INVISIBLE);
            cellView.deleteBtn.setVisibility(mEditMode ? View.VISIBLE : View.INVISIBLE);


            cellView.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onRemoveSetListClicked(setList.getTitle());
                }
            });

            cellView.renameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSetListRenamedClicked(setList.getTitle());
                }
            });

            cellView.setListItem.setText(setList.getTitle());
        }

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(position);

        //DISABLE COLLAPSE GROUPS
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int position, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolderItem cellView;
        final PromptSettings prompt = (PromptSettings) getChild(groupPosition, position);

        //if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_setlist_inner, parent, false);

        // well set up the ViewHolder
        cellView = new ChildViewHolderItem();
        cellView.pdfItem = (TextView) convertView.findViewById(R.id.row_setlist_name);
        cellView.barItem = (TextView) convertView.findViewById(R.id.row_setlist_beat);
        cellView.bpmItem = (TextView) convertView.findViewById(R.id.row_setlist_bpm);

        // store the holder with the view.
        convertView.setTag(cellView);
        //} else {
        // we've just avoided calling findViewById() on resource everytime
        // just use the viewHolder
        //    cellView = (ChildViewHolderItem) convertView.getTag();
        //}

        if (prompt != null) {
            cellView.pdfItem.setText(prompt.getName());
            cellView.bpmItem.setText(prompt.getBpm() + " bpm");
            cellView.barItem.setText(prompt.getCfgBarUpper() + " / " + prompt.getCfgBarLower());
            cellView.pdfItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.heading3));

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mCallback.onPromptSelected(((SetList) getGroup(groupPosition)).getTitle(), prompt, groupPosition);

                }
            };

            cellView.pdfItem.setOnClickListener(onClickListener);
            convertView.setOnClickListener(onClickListener);

            final View finalConvertView = convertView;
            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((A2TActivity) mContext).openContextMenu(finalConvertView);
                    return true;
                }
            };
            convertView.setOnLongClickListener(onLongClickListener);
            cellView.pdfItem.setOnLongClickListener(onLongClickListener);

        } else if (position == mItems.get(groupPosition).getPrompts().size()) {
            cellView.pdfItem.setText(R.string.prompt_create_new);
            cellView.pdfItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.normal_text));

            cellView.pdfItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCreatePromptClicked(((SetList) getGroup(groupPosition)).getTitle(), position);

                }
            });
        }

        cellView.pdfItem.requestLayout();


        /*if (setList != null) {
            List<PromptPDFFile> pdflist = new ArrayList<>();
            for (PromptSettings ps : setList.getPrompts()) {
                PromptPDFFile pdf = new PromptPDFFile();
                pdf.file = new File(ps.getPdfFullPath());
                pdf.displayName = ps.getName();
                pdflist.add(pdf);
            }

            PDFGridAdapter pdfAdapter = new PDFGridAdapter(mContext, pdflist, true, mEditMode, new PDFGridCallback() {
                @Override
                public void onPDFSelected(String fullPath, int pos) {
                    mCallback.onPromptSelected(setList.getTitle(), setList.getPrompts().get(pos), position);
                }

                @Override
                public void onCreatePDFClicked() {
                    mCallback.onCreatePromptClicked(setList.getTitle());
                }

                @Override
                public void onPDFRemoveClicked(String fullPath, int pos) {
                    mCallback.onRemovePromptClicked(setList.getTitle(), setList.getPrompts().get(pos), position);
                }
            });

            cellView.pdfGridItem.setAdapter(pdfAdapter);

            cellView.pdfGridItem.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        return true;
                    }
                    return false;
                }

            });
        }*/

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public SetListAdapter(Context context, List<SetList> objects, boolean editMode, SetListAdapterCallback callback) {
        mContext = context;
        mItems = objects;
        mCallback = callback;
        mEditMode = editMode;
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }
}

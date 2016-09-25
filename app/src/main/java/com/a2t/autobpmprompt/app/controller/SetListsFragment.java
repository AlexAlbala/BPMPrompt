package com.a2t.autobpmprompt.app.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2t.a2tlib.content.compat.A2TFragment;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.callback.SetListAdapterCallback;
import com.a2t.autobpmprompt.app.model.PromptSettings;
import com.a2t.autobpmprompt.app.model.SetList;
import com.a2t.autobpmprompt.app.model.TempoRecord;
import com.a2t.autobpmprompt.app.database.RealmIOHelper;
import com.a2t.autobpmprompt.media.PromptManager;

import java.util.List;

import io.realm.RealmList;

public class SetListsFragment extends A2TFragment {
    boolean editMode = false;
    LinearLayout setListsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setlists, container, false);
        if (setListsView == null) {
            setListsView = (LinearLayout) rootView.findViewById(R.id.main_setlists);
        }
        loadSetLists(getActivity());
        getActivity().registerForContextMenu(setListsView);
        return rootView;
    }

    private void inflateGroupView(final SetList setList, ViewGroup parent, final int position, LayoutInflater inflater, final SetListAdapterCallback mCallback) {
        View convertView;

        convertView = inflater.inflate(R.layout.row_setlist, parent, false);

        // well set up the ViewHolder
        TextView setListItem = (TextView) convertView.findViewById(R.id.setlist_title);
        ImageButton renameBtn = (ImageButton) convertView.findViewById(R.id.setlist_rename_btn);
        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.setlist_delete_btn);


        if (setList != null) {
            //renameBtn.setVisibility(mEditMode ? View.VISIBLE : View.INVISIBLE);
            //deleteBtn.setVisibility(mEditMode ? View.VISIBLE : View.INVISIBLE);


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onRemoveSetListClicked(setList.getTitle());
                }
            });

            renameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onSetListRenamedClicked(setList.getTitle());
                }
            });

            final View finalConvertView = convertView;
            MenuTag m = new MenuTag();
            m.type = "setlist";
            m.childPosition = -1;
            m.groupPosition = position;
            finalConvertView.setTag(m);
            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    getActivity().openContextMenu(finalConvertView);
                    return true;
                }
            };
            convertView.setOnLongClickListener(onLongClickListener);

            setListItem.setText(setList.getTitle());

            setListsView.addView(convertView);
            int promptPosition = 0;
            List<PromptSettings> ps = PromptManager.getAllPtomptsFromSetList(getActivity(), setList.getTitle());
            for (PromptSettings p : ps) {
                inflateChildView(p, parent, false, setList.getTitle(), position, promptPosition++, ps.size(), inflater, mCallback);
            }
            inflateChildView(null, parent, true, setList.getTitle(), position, promptPosition, -1, inflater, mCallback);
        }
    }

    private void inflateChildView(final PromptSettings prompt, ViewGroup parent, boolean last, final String setListTitle, final int setListPosition, final int positionInSetList, final int totalCount, LayoutInflater inflater, final SetListAdapterCallback mCallback) {

        View convertView = inflater.inflate(R.layout.row_prompt, parent, false);
        CardView card = (CardView) convertView.findViewById(R.id.card_view);
        TextView pdfItem = (TextView) convertView.findViewById(R.id.row_prompt_name);
        TextView barItem = (TextView) convertView.findViewById(R.id.row_prompt_beat);
        TextView bpmItem = (TextView) convertView.findViewById(R.id.row_prompt_bpm);
        TextView setListItem = (TextView) convertView.findViewById(R.id.row_prompt_setlist);
        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.row_prompt_delete_btn);
        //ImageView thumb = (ImageView) convertView.findViewById(R.id.row_prompt_thumb);
        TextView pos = (TextView) convertView.findViewById(R.id.row_prompt_number);

        ImageView moveUp = (ImageView) convertView.findViewById(R.id.row_prompt_move_up);
        ImageView moveDown = (ImageView) convertView.findViewById(R.id.row_prompt_move_down);

        final ImageView showHide = (ImageView) convertView.findViewById(R.id.row_prompt_show_hide_btn);

        setListItem.setVisibility(View.GONE);

        if (positionInSetList == 0) {
            moveUp.setVisibility(View.INVISIBLE);
        }

        if (positionInSetList == totalCount - 1) {
            moveDown.setVisibility(View.INVISIBLE);
        }

        moveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromptManager.reorderPromptInSetList(getActivity(), setListTitle, positionInSetList, positionInSetList - 1);
                reloadData();
            }
        });

        moveDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromptManager.reorderPromptInSetList(getActivity(), setListTitle, positionInSetList, positionInSetList + 1);
                reloadData();
            }
        });

        if (prompt != null) {
            TempoRecord tr = prompt.getTempoTrack().get(0);
            pdfItem.setText(prompt.getName());
            bpmItem.setText(tr.getBpm() + " bpm");
            barItem.setText(tr.getUpper() + " / " + tr.getLower());
            pdfItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.heading3));

            pos.setText(String.valueOf(prompt.getSetListPosition() + 1));

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPromptSelected(setListTitle, prompt);
                }
            };

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onRemovePromptClicked(setListTitle, prompt, positionInSetList);
                }
            });

            //pdfItem.setOnClickListener(onClickListener);
            convertView.setOnClickListener(onClickListener);

            final View finalConvertView = convertView;
            MenuTag m = new MenuTag();
            m.type = "prompt";
            m.childPosition = positionInSetList;
            m.groupPosition = setListPosition;
            finalConvertView.setTag(m);
            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    getActivity().openContextMenu(finalConvertView);
                    return true;
                }
            };
            convertView.setOnLongClickListener(onLongClickListener);
            //pdfItem.setOnLongClickListener(onLongClickListener);

            showHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PromptSettings copied = new PromptSettings();
                    RealmIOHelper.getInstance().CopyPromptSettings(prompt, copied);
                    copied.setEnabled(!prompt.isEnabled());
                    PromptManager.update(getActivity(), copied);
                    PromptManager.reorderPromptInSetList(getActivity(), setListTitle, -1, -1);
                    reloadData();
                }
            });

            showHide.setImageResource(prompt.isEnabled() ? R.drawable.eye_close : R.drawable.eye_open);
            if (!prompt.isEnabled()) {
                card.setCardBackgroundColor(getResources().getColor(R.color.prompt_disabled));
                pos.setVisibility(View.INVISIBLE);
            }
        } else if (last) {
            showHide.setVisibility(View.GONE);
            pdfItem.setText(R.string.prompt_create_new);
            pdfItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, 2 * getResources().getDimensionPixelSize(R.dimen.normal_text));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onCreatePromptClicked(setListTitle, positionInSetList);
                }
            });

            deleteBtn.setVisibility(View.GONE);
            pos.setVisibility(View.GONE);

            moveUp.setVisibility(View.GONE);
            moveDown.setVisibility(View.GONE);
        }

        setListsView.addView(convertView);
    }

    private void reloadData() {
        MainActivity m = (MainActivity) getActivity();
        m.reloadData();
    }


    public void loadSetLists(final Context ctx) {
        List<SetList> setLists = RealmIOHelper.getInstance().getAllSetLists(ctx);

        if (setLists.size() == 0) {
            SetList s = new SetList();
            s.setTitle(getString(R.string.first_set_list_title));
            RealmIOHelper.getInstance().insertSetList(ctx, s);

            //Set the first set list also in the local setlists list
            s.setPrompts(new RealmList<PromptSettings>());
            setLists.add(s);
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();

        setListsView.removeAllViews();
        int position = 0;
        for (SetList setList : setLists) {
            SetListAdapterCallback scb = new SetListAdapterCallback() {
                @Override
                public void onPromptSelected(String setList, PromptSettings prompt) {
                    PromptManager.openPrompt(getActivity(), setList, prompt.getId());
                }

                @Override
                public void onCreatePromptClicked(String setList, int position) {
                    Intent i = new Intent(ctx, CreateActivity.class);
                    i.putExtra("setListName", setList);
                    i.putExtra("setListPosition", position);
                    startActivity(i);
                }

                @Override
                public void onSetListRenamedClicked(String setList) {
                    DialogFragment newFragment = new SetListDialog();
                    Bundle args = new Bundle();
                    args.putString("setListName", setList);
                    newFragment.setArguments(args);
                    newFragment.show(getFragmentManager(), "renamesetlist");
                }

                @Override
                public void onRemoveSetListClicked(String setList) {
                    DialogFragment newFragment = new AreYouSureDialog();
                    Bundle b = new Bundle();
                    b.putString("type", "setlist");
                    b.putString("setListName", setList);
                    newFragment.setArguments(b);
                    newFragment.show(getFragmentManager(), "areyousuresetlist");
                }

                @Override
                public void onRemovePromptClicked(String setList, PromptSettings prompt, int position) {
                    DialogFragment newFragment = new AreYouSureDialog();
                    Bundle b = new Bundle();
                    b.putString("type", "prompt");
                    b.putLong("promptId", prompt.getId());
                    newFragment.setArguments(b);
                    newFragment.show(getFragmentManager(), "areyousureprompt");
                }
            };

            inflateGroupView(setList, setListsView, position++, inflater, scb);
        }

        setListsView.requestLayout();
    }

    public void addSetList() {
        DialogFragment newFragment = new SetListDialog();
        newFragment.show(getFragmentManager(), "createsetlist");
    }

    /*public SetListAdapter getSetListViewAdapter() {
        return (SetListAdapter) setListsView.getExpandableListAdapter();
    }*/
}

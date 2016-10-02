package com.a2t.autobpmprompt.app.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.a2t.a2tlib.tools.LogUtils;
import com.a2t.a2tlib.tools.PhoneUtils;
import com.a2t.a2tlib.tools.SharedPreferencesManager;
import com.a2t.a2tlib.tools.StringUtils;
import com.a2t.a2tlib.tools.VersionUtils;
import com.a2t.autobpmprompt.R;
import com.a2t.autobpmprompt.app.VARIABLES;
import com.a2t.autobpmprompt.app.model.Marker;
import com.a2t.autobpmprompt.app.model.MarkerType;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;


public class MarkerDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    MarkerDialogListener mListener;
    EditText title;
    EditText note;
    EditText bar;
    EditText beat;

    int currentMarkerColor;
    int originalMarkerColor;
    int markerTextSize;

    Spinner markerDialogText;
    int mTextOption;

    TextInputLayout title_layout;
    TextInputLayout note_layout;
    TextInputLayout bar_layout;
    TextInputLayout beat_layout;
    NumberPicker number_picker;

    CheckBox notify;
    CheckBox showAlways;
    CheckBox printOnCanvas;

    RadioButton typeMarkerRadio;
    RadioButton typeNoteRadio;

    View color_picker;
    float mX;
    float mY;
    int mPage;
    int mId;

    public static MarkerDialog editMarkerDialog(Marker m) {
        MarkerDialog md = new MarkerDialog();
        Bundle args = new Bundle();
        args.putBoolean("edit", true);

        args.putString("title", m.getTitle());
        args.putString("note", m.getNote());
        args.putInt("bar", m.getBar());
        args.putInt("beat", m.getBeat());
        args.putInt("id", m.getId());

        args.putFloat("xOffset", m.getOffsetX());
        args.putFloat("yOffset", m.getOffsetY());
        args.putInt("page", m.getPage());

        args.putInt("textOption", m.getPrintTitle());

        args.putInt("color", m.getColor());
        args.putInt("textSize", m.getTextSize());
        args.putInt("type", m.getType());

        args.putBoolean("showAlways", m.isShowAlways());
        args.putBoolean("notify", m.isNotify());
        args.putBoolean("printOnCanvas", m.isPrintInCanvasOnMatch());

        md.setArguments(args);
        return md;
    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_marker, null);

        final Bundle b = getArguments();
        final boolean isEdit = b.getBoolean("edit");

        mX = b.getFloat("xOffset");
        mY = b.getFloat("yOffset");
        mPage = b.getInt("page");
        mId = b.getInt("id");
        mTextOption = b.getInt("textOption", 0);
        markerTextSize = b.getInt("textSize", 0);

        int lastSize = SharedPreferencesManager.getInt(getActivity(), VARIABLES.SHARED_PREFERENCES_LAST_MARKER_SIZE, -1);
        if (markerTextSize == 0) {
            if(lastSize == -1) {
                markerTextSize = 14;
            } else{
                markerTextSize = lastSize;
            }
        }

        int lastColor = SharedPreferencesManager.getInt(getActivity(), VARIABLES.SHARED_PREFERENCES_LAST_MARKER_COLOR, -1);
        if(lastColor == -1) {
            if (VersionUtils.isMarshmallowAndOver()) {
                originalMarkerColor = b.getInt("color", getResources().getColor(android.R.color.black, getActivity().getTheme()));
            } else {
                originalMarkerColor = b.getInt("color", getResources().getColor(android.R.color.black));
            }
        } else{
            originalMarkerColor = b.getInt("color", lastColor);
        }
        currentMarkerColor = originalMarkerColor;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setMessage(getString(isEdit ? R.string.edit_marker_title : R.string.create_marker_title))
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener.onMarkerCreated();
                        String mTitle = title.getText().toString();
                        String mNote = note.getText().toString();
                        if (StringUtils.isEmpty(mTitle)) {
                            title_layout.setError(getString(R.string.field_required));
                        } else if (StringUtils.isEmpty(bar.getText().toString()) && typeMarkerRadio.isChecked()) {
                            bar_layout.setError(getString(R.string.field_required));
                        } else if (StringUtils.isEmpty(beat.getText().toString()) && typeMarkerRadio.isChecked()) {
                            beat.setError(getString(R.string.field_required));
                        } else {
                            Marker m = new Marker();
                            m.setId(mId);
                            m.setTitle(mTitle);
                            m.setPage(mPage);
                            m.setOffsetX(mX);
                            m.setOffsetY(mY);
                            m.setColor(currentMarkerColor);
                            m.setTextSize(markerTextSize);
                            m.setPrintTitle(markerDialogText.getSelectedItemPosition());

                            SharedPreferencesManager.set(getActivity(), VARIABLES.SHARED_PREFERENCES_LAST_MARKER_COLOR, currentMarkerColor);
                            SharedPreferencesManager.set(getActivity(), VARIABLES.SHARED_PREFERENCES_LAST_MARKER_SIZE, markerTextSize);

                            if (typeMarkerRadio.isChecked()) {
                                m.setShowAlways(showAlways.isChecked());
                                m.setPrintInCanvasOnMatch(printOnCanvas.isChecked());
                                m.setNotify(notify.isChecked());
                                m.setType(MarkerType.MARKER);

                                int mBar = Integer.parseInt(bar.getText().toString());
                                int mBeat = Integer.parseInt(beat.getText().toString());
                                m.setBar(mBar);
                                m.setBeat(mBeat);
                                m.setNote(mNote);
                            } else {
                                m.setNotify(false);
                                m.setShowAlways(true);
                                m.setPrintInCanvasOnMatch(true);
                                m.setType(MarkerType.TEXT_ONLY);
                            }

                            if (isEdit) {
                                mListener.onMarkerEdited(MarkerDialog.this, m);
                            } else {
                                mListener.onMarkerCreated(MarkerDialog.this, m);
                            }

                            dialog.dismiss();
                        }
                    }
                });

        builder.setNeutralButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                mListener.onMarkerCancelled(MarkerDialog.this);
                dialog.dismiss();
            }
        });

        title = (EditText) dialogView.findViewById(R.id.marker_title);
        note = (EditText) dialogView.findViewById(R.id.marker_note);
        bar = (EditText) dialogView.findViewById(R.id.marker_bar);
        beat = (EditText) dialogView.findViewById(R.id.marker_beat);

        title_layout = (TextInputLayout) dialogView.findViewById(R.id.marker_title_layout);
        note_layout = (TextInputLayout) dialogView.findViewById(R.id.marker_note_layout);
        bar_layout = (TextInputLayout) dialogView.findViewById(R.id.marker_bar_layout);
        beat_layout = (TextInputLayout) dialogView.findViewById(R.id.marker_beat_layout);

        markerDialogText = (Spinner) dialogView.findViewById(R.id.marker_text_options);
        number_picker = (NumberPicker) dialogView.findViewById(R.id.marker_number_picker_view);
        color_picker = dialogView.findViewById(R.id.marker_color_picker_view);

        showAlways = (CheckBox) dialogView.findViewById(R.id.marker_show_always);
        notify = (CheckBox) dialogView.findViewById(R.id.marker_notify);
        printOnCanvas = (CheckBox) dialogView.findViewById(R.id.marker_print);

        typeMarkerRadio = (RadioButton) dialogView.findViewById(R.id.marker_type_marker);
        typeNoteRadio = (RadioButton) dialogView.findViewById(R.id.marker_type_note);

        color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickColor();
            }
        });

        color_picker.setBackgroundColor(originalMarkerColor);

        number_picker.setMinValue(5);
        number_picker.setMaxValue(100);
        number_picker.setValue(markerTextSize);
        number_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                markerTextSize = newVal;
            }
        });

        View.OnClickListener radioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.marker_type_marker) {
                    bar_layout.setVisibility(View.VISIBLE);
                    beat_layout.setVisibility(View.VISIBLE);
                    markerDialogText.setVisibility(View.VISIBLE);
                    showAlways.setVisibility(View.VISIBLE);
                    notify.setVisibility(View.VISIBLE);
                    note_layout.setVisibility(View.VISIBLE);
                    printOnCanvas.setVisibility(View.VISIBLE);
                } else if (v.getId() == R.id.marker_type_note) {
                    bar_layout.setVisibility(View.GONE);
                    beat_layout.setVisibility(View.GONE);
                    markerDialogText.setVisibility(View.GONE);
                    showAlways.setVisibility(View.GONE);
                    notify.setVisibility(View.GONE);
                    note_layout.setVisibility(View.GONE);
                    printOnCanvas.setVisibility(View.GONE);
                }
            }
        };

        typeMarkerRadio.setOnClickListener(radioClickListener);
        typeNoteRadio.setOnClickListener(radioClickListener);


        if (isEdit) {
            if (StringUtils.isNotEmpty(b.getString("title"))) {
                final String markerTitle = b.getString("title");
                title.setText(markerTitle);
                builder.setNegativeButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onMarkerDeleted(MarkerDialog.this, markerTitle);
                        dialog.dismiss();
                    }
                });
            }

            markerDialogText.setSelection(mTextOption);

            if (StringUtils.isNotEmpty(b.getString("note"))) {
                note.setText(b.getString("note"));
            }

            if (b.getInt("bar", -1) != -1) {
                bar.setText(String.valueOf(b.getInt("bar")));
            }

            if (b.getInt("beat", -1) != -1) {
                beat.setText(String.valueOf(b.getInt("beat")));
            }

            notify.setChecked(b.getBoolean("notify", false));
            showAlways.setChecked(b.getBoolean("showAlways", false));
            printOnCanvas.setChecked(b.getBoolean("printOnCanvas", false));

            int t = b.getInt("type", -1);
            if (t == MarkerType.MARKER) {
                typeMarkerRadio.performClick();
            } else {
                typeNoteRadio.performClick();
            }
        }


        final AlertDialog alertDialog = builder.create();
        /*alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        //Dismiss once everything is OK.
                        alertDialog.dismiss();
                    }
                });
            }
        });*/

        return alertDialog;
    }

    public interface MarkerDialogListener {
        void onMarkerCreated(DialogFragment dialog, Marker m);

        void onMarkerEdited(DialogFragment dialog, Marker m);

        void onMarkerCancelled(DialogFragment dialog);

        void onMarkerDeleted(DialogFragment dialog, String title);
    }

    // Override the Fragment.onAttach() method to instantiate the PDFDialogResultListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PDFDialogResultListener so we can send events to the host
            mListener = (MarkerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MarkerDialogListener");
        }
    }

    private void pickColor() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(originalMarkerColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showAlphaSlider(true)
                .showColorPreview(true)
                .showLightnessSlider(true)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        currentMarkerColor = selectedColor;
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        originalMarkerColor = currentMarkerColor;
                        color_picker.setBackgroundColor(originalMarkerColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentMarkerColor = originalMarkerColor;
                    }
                })
                .build()
                .show();

    }
}
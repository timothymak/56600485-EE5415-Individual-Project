package com.example.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;

public class SettingsDialog extends DialogFragment {

    private DialogAdapter dialogAdapter;
    private HashMap<Integer, Integer> settings;

    public SettingsDialog(HashMap<Integer, Integer> settings) {
        super();
        this.settings = settings;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_dialog, null);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumHeight((int)(displayRectangle.height()* 0.75f));
        initComponents(view);
        builder.setCustomTitle(view).setPositiveButton(R.string.settings_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)(getActivity())).changeSettings(settings);
                dismiss();
            }
        }).setNegativeButton(R.string.settings_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }

    private void initComponents(View view) {
        ListView listView = (ListView) view.findViewById(R.id.settings_dialog_listview);

        SettingsDialogItem lang = new SettingsDialogItem(getResources().getString(R.string.settings_language), getResources().getStringArray(R.array.settings_languages));
        SettingsDialogItem diff = new SettingsDialogItem(getResources().getString(R.string.settings_difficulty), getResources().getStringArray(R.array.settings_difficulties));
        SettingsDialogItem order = new SettingsDialogItem(getResources().getString(R.string.settings_first_hand), getResources().getStringArray(R.array.settings_first_hand_options));
        SettingsDialogItem resetScore = new SettingsDialogItem(getResources().getString(R.string.settings_reset_score));

        dialogAdapter = new DialogAdapter(this.getContext(), new SettingsDialogItem[]{lang, diff, order, resetScore});
        listView.setAdapter(dialogAdapter);
    }

    public class DialogAdapter extends BaseAdapter {

        private Context mContext;
        private SettingsDialogItem[] settingsDialogItems;

        public DialogAdapter(Context c, SettingsDialogItem[] settingsDialogItems) {
            this.mContext = c;
            this.settingsDialogItems = settingsDialogItems;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return settingsDialogItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (position == 3) {
                view = inflater.inflate(R.layout.settings_dialog_item_1, null);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.settings_dialog_item_checkbox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            settings.put(position, 1);
                        }
                        else {
                            settings.put(position, 0);
                        }
                    }
                });
            }
            else {
                view = inflater.inflate(R.layout.settings_dialog_item, null);

                Spinner spinner = (Spinner) view.findViewById(R.id.settings_dialog_item_spinner);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
//                    Log.v("click spinner", settingsDialogItems[position].getItemName() + " " + spinner.getSelectedItem().toString());
                        settings.put(position, p);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, settingsDialogItems[position].getSpinnerItemNames());
                spinner.setAdapter(spinnerAdapter);
                spinner.setSelection(settings.get(position));
            }
            TextView itemName = (TextView) view.findViewById(R.id.settings_dialog_item_textview);
            itemName.setText(settingsDialogItems[position].getItemName());

            return view;
        }

    }

}


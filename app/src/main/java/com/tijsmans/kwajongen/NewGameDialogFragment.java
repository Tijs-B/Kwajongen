package com.tijsmans.kwajongen;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class NewGameDialogFragment extends DialogFragment {

    public static final String EXTRA_STARTING_POINTS = "com.tijsmans.kwajongen.starting_points";

    private NumberPicker mNumberPicker;

    public static NewGameDialogFragment newInstance() {
        return new NewGameDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_new_game, null);

        mNumberPicker = (NumberPicker) v.findViewById(R.id.dialog_new_game_number_picker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(20);
        mNumberPicker.setValue(10);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.new_game_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int startingPoints = mNumberPicker.getValue();
                        sendResult(Activity.RESULT_OK, startingPoints);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private void sendResult(int resultCode, int startingPoints) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_STARTING_POINTS, startingPoints);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

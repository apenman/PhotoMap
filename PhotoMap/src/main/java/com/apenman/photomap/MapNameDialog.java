
package com.apenman.photomap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;


public class MapNameDialog extends DialogFragment {

    public interface MapNameDialogListener {
        void onFinishMapDialog(String inputText);
    }

    public MapNameDialog() {
        // Empty constructor required for DialogFragment
    }

    public static MapNameDialog newInstance(String title) {
        MapNameDialog frag = new MapNameDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        final EditText input = new EditText(getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Map Name:");
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                MapNameDialogListener listener = (MapNameDialogListener) getActivity();
                listener.onFinishMapDialog(input.getText().toString());
                dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}
package com.apenman.photomap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by apenman on 3/6/15.
 */
public class FacebookUploadDialog extends DialogFragment {
    public interface FacebookUploadDialogListener {
        void onFinishFacebookDialog(String titleText, String descText);
    }

    public FacebookUploadDialog() {
    }

    public static FacebookUploadDialog newInstance(String title, String desc) {
        FacebookUploadDialog frag = new FacebookUploadDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("desc", desc);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Share To Facebook");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleText = new EditText(context);
        titleText.setHint("Map Name");
        titleText.setText(GlobalList.getGlobalInstance().getCurrMap().getName());
        layout.addView(titleText);

        final EditText descriptionText = new EditText(context);
        descriptionText.setHint("Map Description");
        descriptionText.setText(GlobalList.getGlobalInstance().getCurrMap().getDescription());
        layout.addView(descriptionText);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                FacebookUploadDialogListener listener = (FacebookUploadDialogListener) getActivity();
                listener.onFinishFacebookDialog(titleText.getText().toString(), descriptionText.getText().toString());
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

package com.example.visuasic.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.visuasic.R;

public class AddColorDialog extends AppCompatDialogFragment {
    private EditText inputText;

    private AddDialogInterface listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        inputText = view.findViewById(R.id.inputText);

        builder.setView(view)
                .setTitle(getString(R.string.dialog_title))
                .setPositiveButton(getText(R.string.button_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = inputText.getText().toString();
                        listener.getColorPhrase(input);
                    }
                })
                .setNegativeButton(getText(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddDialogInterface) getTargetFragment();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddDilaogInterface");
        }
    }

    public interface AddDialogInterface {
        void getColorPhrase(String colorPhrase);
    }
}

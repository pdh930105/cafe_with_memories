package com.example.myfirstapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyAlertDialogFragment extends DialogFragment{
    public interface MyDialogListener{
        public void onDialogPositiveClick(DialogFragment dialogFragment);
        public void onDialogNegativeClick(DialogFragment dialogFragment);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("힌트를 사용하시겠습니까?\n힌트를 사용할 경우 사용 횟수가 기록됩니다.")
                .setTitle("주의사항")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDialogListener.onDialogPositiveClick(MyAlertDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDialogListener.onDialogNegativeClick(MyAlertDialogFragment.this);
                    }
                });
        return builder.create();
    }
    MyDialogListener myDialogListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            myDialogListener = (MyDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()+ " must implement NoticeDialogListener");
        }
    }

}
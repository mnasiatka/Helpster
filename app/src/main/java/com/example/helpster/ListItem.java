package com.example.helpster;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ListItem {

    private EditText mEditText;
    private TextView mChildText;
    private CheckBox mCheckBox;
    private boolean mIsChecked;

    public ListItem() {

    }

    public ListItem(EditText et, TextView tv, CheckBox cb, boolean b) {
        mEditText = et;
        mChildText = tv;
        mCheckBox = cb;
        mIsChecked = b;
    }

    public EditText getmEditText() {
        return mEditText;
    }

    public void setmEditText(EditText mEditText) {
        this.mEditText = mEditText;
    }

    public TextView getmChildText() {
        return mChildText;
    }

    public void setmChildText(TextView mChildText) {
        this.mChildText = mChildText;
    }

    public void setText(String s) {
        this.mChildText.setText(s);
    }

    public CheckBox getmCheckBox() {
        return mCheckBox;
    }

    public void setmCheckBox(CheckBox mCheckBox) {
        this.mCheckBox = mCheckBox;
    }

    public void setVisibility(int vis) {
        this.mEditText.setVisibility(vis);
    }

    public boolean getmIsChecked() {
        return mIsChecked;
    }

    public void setmIsChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }

}

package com.example.helpster;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class FilterItems {

    CheckBox checkbox;
    EditText edittext;
    TextView textview;

    public FilterItems(CheckBox cxbx, EditText edtxt, TextView txt) {
        checkbox = cxbx;
        edittext = edtxt;
        textview = txt;
    }

    public CheckBox getCheckBox() {
        return checkbox;
    }

    public EditText getEditText() {
        return edittext;
    }

    public TextView getTextView() {
        return textview;
    }
}

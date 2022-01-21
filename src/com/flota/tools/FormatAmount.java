package com.flota.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.newpos.libpay.Logger;

import java.util.StringTokenizer;

/**
 * Created by http://stackoverflow.com/questions/12338445/how-to-automatically-add-thousand-separators-as-number-is-input-in-edittext on 01/11/2016.
 */

public class FormatAmount implements TextWatcher {

    private static final String TAG = "FormatAmount.java";
    EditText editText;

    public FormatAmount(EditText editText) {
        this.editText = editText;
    }

    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        StringBuilder str3 = new StringBuilder();
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            j--;
            str3 = new StringBuilder(".");
        }
        for (int k = j; ; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3.append(".").append(str2);
                return str3.toString();
            }
            if (i == 3) {
                str3.insert(0, ",");
                i = 0;
            }
            str3.insert(0, str1.charAt(k));
            i++;
        }

    }

    public static String trimCommaOfString(String string) {

        String ret = "";

        if (string.contains(",")) {
            ret = string.replace(",", "");
        } else {
            ret = string;
        }

        return ret;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Método no implementado
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Método no implementado
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            editText.removeTextChangedListener(this);
            String value = editText.getText().toString();

            if (value != null && !value.equals("")) {

                if (value.startsWith(".")) {
                    editText.setText("0.");
                }
                if (value.startsWith("0") && !value.startsWith("0.")) {
                    editText.setText("");

                }

                String str = editText.getText().toString().replace(",", "");
                editText.setText(getDecimalFormattedString(str));
                editText.setSelection(editText.getText().toString().length());
            }
            editText.addTextChangedListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.exception(TAG, ex);
            Logger.error("Exception" + ex.toString());
            editText.addTextChangedListener(this);
        }
    }
}

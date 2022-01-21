package com.flota.model.widgets;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.wposs.flota.R;

public class EdiTextWidgetModel extends WidgetModel implements ItemWidget {

    private final boolean enable;
    EditText editText;
    Context ctx;

    public EdiTextWidgetModel(String title, int typeWidget, int lengthMax, int typeInput, Boolean enabled, Context context) {
        super(title, typeWidget, context);
        this.ctx = context;
        this.enable = enabled;
        editText = new EditText(context);

        if (lengthMax != 0) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lengthMax)});
        }
        editText.setInputType(typeInput);
        editText.setGravity(Gravity.END);
        editText.setEnabled(enabled);
    }

    public EditText getEditText() {
        return editText;
    }

    public void setHint(String string) {
        oldValue = string;
        editText.setHint(string);
    }


    public void clearFocus() {
        editText.requestFocus();
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

    }


    public void setBackground(Drawable background) {
        editText.setBackground(background);
    }

    public void setDesign(int i) {
        if (i == 0) {
            editText.setLayoutParams(lp);
            editText.setPadding(32, 24, 32, 0);
            editText.setBackground(ctx.getDrawable(R.drawable.fondo_editext));
            if (enable) {
                editText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        editText.setText(" ");
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public EditText getWidget() {
        return editText;
    }

    @Override
    public String getTituloItem() {
        return title;
    }

    @Override
    public View getWidgetItem() {
        return editText;
    }

    @Override
    public String getResultado() {
        if (!editText.getText().toString().isEmpty()) {
            return editText.getText().toString();
        } else {
            return editText.getHint().toString();
        }

    }

    @Override
    public boolean isNewValor() {
        if (!editText.getText().toString().isEmpty()) {
            return !oldValue.contentEquals(editText.getText());
        } else {
            return false;
        }
    }


}

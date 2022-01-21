package com.flota.screen.inputs;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.flota.screen.inputs.base.ScreenInput;
import com.wposs.flota.R;

public class ScreenSelectFromTwoOptions extends ScreenInput {

    private String title;
    private String question;
    private CharSequence data;
    private String textOption1;
    private String textOption2;

    public ScreenSelectFromTwoOptions(int timeout) {
        super(timeout, null);
    }

    public String getTitle() {
        return title;
    }

    public ScreenSelectFromTwoOptions setTitle(String title) {
        this.title = title;
        return this;
    }

    public CharSequence getData() {
        return data;
    }

    public ScreenSelectFromTwoOptions setData(CharSequence data) {
        this.data = data;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public ScreenSelectFromTwoOptions setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getTextOption1() {
        return textOption1;
    }

    public ScreenSelectFromTwoOptions setTextOption1(String textOption1) {
        this.textOption1 = textOption1;
        return this;
    }

    public String getTextOption2() {
        return textOption2;
    }

    public ScreenSelectFromTwoOptions setTextOption2(String textOption2) {
        this.textOption2 = textOption2;
        return this;
    }

    @Override
    public int getLayout() {
        return data == null ?
                R.layout.screen_input_select_from_two_options :
                R.layout.screen_input_select_from_two_options_style_2;
    }

    @Override
    public TypeScreen getTypeScreen() {
        return TypeScreen.SELECT_FROM_TWO_OPTIONS;
    }
}

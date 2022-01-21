package com.flota.screen.inputs.methods;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public enum FormatInput {
    CODE,
    NUMBER,
    CARD,
    DATE,
    PASSWORD;

    public static String formatNumber(String str) {
        if (!str.trim().isEmpty()) try {
            double value = Double.parseDouble(str);
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(value).replace(',', '.');
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String formatCard(String str) {
        try {
            return str.replaceAll("(?s).{4}(?!$)", "$0-");
        } catch (Exception e) {
            return str;
        }
    }

    public static String formatDate(String str) {
        try {
            return str.replaceAll("(?s).{2}(?!$)", "$0/");
        } catch (Exception e) {
            return str;
        }
    }

    public static boolean isValidDate(String dateStr) {
        try {
            @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("ddMMyy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static String formatPassword(String str) {
        StringBuilder strResp = new StringBuilder("");
        for (int i = 0; i < str.length(); i++) strResp.append("*");
        return strResp.toString();
    }

    public static String formatSerial(String str) {
        try {
            return str.replaceAll("(?s).{5}(?!$)", "$0-");
        } catch (Exception e) {
            return str;
        }
    }

    public String applyFormat(String str) {
        switch (FormatInput.valueOf(super.name())) {
            case NUMBER:
                return FormatInput.formatNumber(str);
            case CARD:
                return FormatInput.formatCard(str);
            case DATE:
                return FormatInput.formatDate(str);
            case PASSWORD:
                return FormatInput.formatPassword(str);
            default:
                return str;
        }
    }
}

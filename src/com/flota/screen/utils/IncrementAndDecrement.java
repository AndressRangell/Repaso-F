package com.flota.screen.utils;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_UP;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * https://stackoverflow.com/questions/7938516/continuously-increase-integer-value-as-the-button-is-pressed
 */
public class IncrementAndDecrement {

    private final Listener listener;
    private final Handler handler = new Handler();
    private boolean isAutoIncrement = false;
    private boolean isAutoDecrement = false;

    @SuppressLint("ClickableViewAccessibility")
    public IncrementAndDecrement(Button increment, Button decrement, final Listener listener) {
        this.listener = listener;
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.increment();
            }
        });
        increment.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                isAutoIncrement = true;
                handler.post(new Updater());
                return false;
            }
        });
        increment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == ACTION_UP || event.getAction() == ACTION_CANCEL) && isAutoIncrement) {
                    isAutoIncrement = false;
                }
                return false;
            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.decrement();
            }
        });
        decrement.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                isAutoDecrement = true;
                handler.post(new Updater());
                return false;
            }
        });
        decrement.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == ACTION_UP || event.getAction() == ACTION_CANCEL) && isAutoDecrement) {
                    isAutoDecrement = false;
                }
                return false;
            }
        });
    }

    public static interface Listener {
        void increment();

        void decrement();
    }

    private class Updater implements Runnable {

        private static final long REP_DELAY = 100;

        public void run() {
            if (isAutoIncrement) {
                listener.increment();
                handler.postDelayed(new Updater(), REP_DELAY);
            } else if (isAutoDecrement) {
                listener.decrement();
                handler.postDelayed(new Updater(), REP_DELAY);
            }
        }
    }
}

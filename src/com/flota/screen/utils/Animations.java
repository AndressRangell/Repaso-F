package com.flota.screen.utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.wposs.flota.R;

public class Animations {

    private Animations() {
    }

    public static void moreApps(CardView cardMoreApps, Context context) {
        if (cardMoreApps.getVisibility() == View.VISIBLE) {
            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.more_apps_out);
            cardMoreApps.setVisibility(View.GONE);
            cardMoreApps.setAnimation(animation);

        } else {
            cardMoreApps.setVisibility(View.VISIBLE);
            cardMoreApps.setAnimation(AnimationUtils.loadAnimation(context, R.anim.more_apps_in));
        }
    }
}

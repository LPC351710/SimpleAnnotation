package com.ppm.simpleannotation;

import android.app.Activity;

public class ButterKnife {

    public static void bind(Activity target) {
        String className = target.getClass().getName() + "$ViewBinder";
        try {
            Class<?> viewBindClass = Class.forName(className);
            ViewBinder viewBinder = (ViewBinder) viewBindClass.newInstance();
            viewBinder.bind(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

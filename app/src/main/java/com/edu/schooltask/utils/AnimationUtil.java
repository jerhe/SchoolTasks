package com.edu.schooltask.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;

/**
 * Created by 夜夜通宵 on 2017/8/13.
 */

public class AnimationUtil {
    public static void colorAnimation(Object target, String propertyName, int duration, int...values){
        ObjectAnimator animator = ObjectAnimator.ofInt(target, propertyName, values);
        animator.setDuration(duration);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
    }
}

package com.beanslab.colorblindhelper.helper.utils;

import android.content.Context;
import android.graphics.Typeface;

public final class TYPEFACE {

    public static final Typeface AvantGarde(Context ctx) {
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/avantgarde.ttf");
        return typeface;
    }
} 
package com.symphony_ecrm.utils;

import android.util.Log;

/**
 * Created by indianic on 30/01/17.
 */

public class WriteLog {
    public static final void V(final String tag, final String message) {
        Log.v(tag, message);
    }

    public static final void I(final String tag, final String message) {
        Log.i(tag, message);
    }

    public static final void E(final String tag, final String message) {
        Log.e(tag, message);
    }

    public static final void D(final String tag, final String message) {
        Log.d(tag, message);
    }

    public static final void W(final String tag, final String message) {
        Log.w(tag, message);
    }
}

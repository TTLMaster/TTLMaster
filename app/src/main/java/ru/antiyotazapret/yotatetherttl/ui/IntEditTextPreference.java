package ru.antiyotazapret.yotatetherttl.ui;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Надстройка над стандартной реализацией настроек для работы с числами.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 * @see <a href="http://stackoverflow.com/questions/3721358/#3755608">
 * PreferenceActivity: save value as integer</a>
 */
public class IntEditTextPreference extends EditTextPreference {

    public IntEditTextPreference(Context context) {
        super(context);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(-1));
    }

    @Override
    protected boolean persistString(String value) {
        return persistInt(Integer.valueOf(value));
    }

}

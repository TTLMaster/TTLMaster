package io.github.ttlmaster.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import io.github.ttlmaster.R;

@SuppressWarnings("ALL")
public class SettingsActivity extends PreferenceActivity {

    private PendingIntent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);
        toolbar.setClickable(true);
        toolbar.setNavigationIcon(getResIdFromAttribute(this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        intent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getIntent()), 0);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(getString(R.string.prefs_misc_restart_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                System.exit(1);
                return true;
            }
        });

    }

    private static int getResIdFromAttribute(final Activity activity) {
        if (R.attr.homeAsUpIndicator == 0) {
            return 0;
        }
        final TypedValue typedvalueattr = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.homeAsUpIndicator, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

}
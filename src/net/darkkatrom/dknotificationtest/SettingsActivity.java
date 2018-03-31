/*
 * Copyright (C) 2018 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.darkkatrom.dknotificationtest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.UiModeManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.view.View;

import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dknotificationtest.fragments.SettingsFragment;

public class SettingsActivity extends Activity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback {

    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    private boolean mUseOptionalLightStatusBar;
    private boolean mUseOptionalLightNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    private void updateTheme() {
        mUseOptionalLightStatusBar = ThemeHelper.themeSupportsOptionalĹightSB(this)
                && ThemeHelper.useLightStatusBar(this);
        mUseOptionalLightNavigationBar = ThemeHelper.themeSupportsOptionalĹightNB(this)
                && ThemeHelper.useLightNavigationBar(this);
        int themeResId = 0;

        if (mUseOptionalLightStatusBar && mUseOptionalLightNavigationBar) {
            themeResId = R.style.ThemeOverlay_LightStatusBar_LightNavigationBar;
        } else if (mUseOptionalLightStatusBar) {
            themeResId = R.style.ThemeOverlay_LightStatusBar;
        } else if (mUseOptionalLightNavigationBar) {
            themeResId = R.style.ThemeOverlay_LightNavigationBar;
        } else {
            themeResId = R.style.AppTheme;
        }
        setTheme(themeResId);

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!mUseOptionalLightStatusBar) {
            // Possibly we are using the Whiteout theme
            boolean isWhiteoutTheme =
                    ThemeHelper.getTheme(this) == UiModeManager.MODE_NIGHT_NO_WHITEOUT;
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set,
            // and we are not using the Whiteout theme,
            // (Whiteout theme should always use a light status bar).
            if (isLightStatusBar && !isWhiteoutTheme) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!mUseOptionalLightNavigationBar) {
            // Check if light navigation bar flag was set
            boolean isLightNavigationBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            if (isLightNavigationBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        if (oldFlags != newFlags) {
            getWindow().getDecorView().setSystemUiVisibility(newFlags);
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        if (pref instanceof ColorPickerPreference) {
            startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                    pref.getTitle(), caller, ColorPickerPreference.RESULT_REQUEST_CODE);
        } else {
            startPreferencePanel(pref.getFragment(), pref.getExtras(), pref.getTitleRes(),
                    pref.getTitle(), null, 0);
        }
        return true;
    }

    public void startPreferencePanel(String fragmentClass, Bundle args, int titleRes,
        CharSequence titleText, Fragment resultTo, int resultRequestCode) {
        Fragment f = Fragment.instantiate(this, fragmentClass, args);
        if (resultTo != null) {
            f.setTargetFragment(resultTo, resultRequestCode);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, f);
        if (titleRes != 0) {
            transaction.setBreadCrumbTitle(titleRes);
        } else if (titleText != null) {
            transaction.setBreadCrumbTitle(titleText);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean useOptionalLightStatusBar = ThemeHelper.themeSupportsOptionalĹightSB(this)
                && ThemeHelper.useLightStatusBar(this);
        boolean useOptionalLightNavigationBar = ThemeHelper.themeSupportsOptionalĹightNB(this)
                && ThemeHelper.useLightNavigationBar(this);
        if (mUseOptionalLightStatusBar != useOptionalLightStatusBar
                || mUseOptionalLightNavigationBar != useOptionalLightNavigationBar) {
            recreate();
        }
    }
}

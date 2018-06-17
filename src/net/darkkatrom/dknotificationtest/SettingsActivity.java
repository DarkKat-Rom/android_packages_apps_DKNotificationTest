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
import android.graphics.drawable.ColorDrawable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.view.View;

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dknotificationtest.fragments.SettingsFragment;

public class SettingsActivity extends Activity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback, ColorPickerPreference.OwnerActivity {

    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    private int mThemeResId = 0;
    private boolean mCustomizeColors = false;
    private int mStatusBarColor = 0;
    private int mPrimaryColor = 0;
    private int mNavigationColor = 0;
    private boolean mColorizeNavigationBar = false;
    private boolean mLightStatusBar = false;
    private boolean mLightActionBar = false;
    private boolean mLightNavigationBar = false;
    private boolean mIsBlackoutTheme = false;
    private boolean mIsWhiteoutTheme = false;
    private int mThemeOverlayAccentResId = 0;

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
        mCustomizeColors = ThemeColorHelper.customizeColors(this);
        mStatusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, 0xff2196f3);
        mPrimaryColor = ThemeColorHelper.getPrimaryColor(this, 0xff2196f3);
        mNavigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, 0xff2196f3);
        mColorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
        mLightStatusBar = ThemeColorHelper.lightStatusBar(this, 0xff2196f3);
        mLightActionBar = ThemeColorHelper.lightActionBar(this, 0xff2196f3);
        mLightNavigationBar = ThemeColorHelper.lightNavigationBar(this, 0xff2196f3);
        mIsBlackoutTheme = ThemeHelper.isBlackoutTheme(this);
        mIsWhiteoutTheme = ThemeHelper.isWhiteoutTheme(this);

        if (mLightActionBar && mLightNavigationBar) {
            mThemeResId = mLightStatusBar
                    ? R.style.AppTheme_LightStatusBar_LightNavigationBar
                    : R.style.AppTheme_LightActionBar_LightNavigationBar;
        } else if (mLightActionBar) {
            mThemeResId = mLightStatusBar
                    ? R.style.AppTheme_LightStatusBar
                    : R.style.AppTheme_LightActionBar;
        } else if (mLightNavigationBar) {
            mThemeResId = R.style.AppTheme_LightNavigationBar;
        } else {
            mThemeResId = R.style.AppTheme;
        }

        setTheme(mThemeResId);

        mThemeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
        if (mThemeOverlayAccentResId > 0) {
            getTheme().applyStyle(mThemeOverlayAccentResId, true);
        }

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!mLightStatusBar) {
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set.
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!mLightNavigationBar) {
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

        if (mCustomizeColors && !mIsBlackoutTheme && !mIsWhiteoutTheme) {
            getWindow().setStatusBarColor(mStatusBarColor);
            getActionBar().setBackgroundDrawable(new ColorDrawable(mPrimaryColor));
        }
        if (mNavigationColor != 0) {
            getWindow().setNavigationBarColor(mNavigationColor);
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

        boolean customizeColors = ThemeColorHelper.customizeColors(this);
        int primaryColor = ThemeColorHelper.getPrimaryColor(this, 0xff2196f3);
        boolean colorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);
        boolean lightStatusBar = ThemeColorHelper.lightStatusBar(this, 0xff2196f3);
        boolean lightActionBar = ThemeColorHelper.lightActionBar(this, 0xff2196f3);
        boolean lightNavigationBar = ThemeColorHelper.lightNavigationBar(this, 0xff2196f3);
        int themeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);

        if (mThemeOverlayAccentResId != themeOverlayAccentResId
                || mCustomizeColors != customizeColors
                || mPrimaryColor != primaryColor
                || mColorizeNavigationBar != colorizeNavigationBar
                || mLightStatusBar != lightStatusBar
                || mLightActionBar != lightActionBar
                || mLightNavigationBar != lightNavigationBar) {
            recreate();
        }
    }

    @Override
    public boolean supportsTheming() {
        return true;
    }

    @Override
    public int getThemeResId() {
        return R.style.AppTheme;
    }

    @Override
    public int getThemeLightActionBarResId() {
        return R.style.AppTheme_LightActionBar;
    }

    @Override
    public int getThemeLightStatusBarResId() {
        return R.style.AppTheme_LightStatusBar;
    }

    @Override
    public int getThemeLightNavigationBarResId() {
        return R.style.AppTheme_LightNavigationBar;
    }

    @Override
    public int getThemeLightActionBarLightNavigationBarResId() {
        return R.style.AppTheme_LightActionBar_LightNavigationBar;
    }

    @Override
    public int getThemeLightStatusBarLightNavigationBarResId() {
        return R.style.AppTheme_LightStatusBar_LightNavigationBar;
    }
}

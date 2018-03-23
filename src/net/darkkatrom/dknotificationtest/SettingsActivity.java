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
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;

import net.darkkatrom.dkcolorpicker.fragment.ColorPickerFragment;
import net.darkkatrom.dkcolorpicker.preference.ColorPickerPreference;
import net.darkkatrom.dknotificationtest.fragments.SettingsFragment;

public class SettingsActivity extends Activity implements
        PreferenceFragment.OnPreferenceStartFragmentCallback {

    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
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
}

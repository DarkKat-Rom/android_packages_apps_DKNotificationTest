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

package net.darkkatrom.dknotificationtest.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.SwitchPreference;

import net.darkkatrom.dkcolorpicker.fragment.SettingsColorPickerFragment;
import net.darkkatrom.dknotificationtest.R;
import net.darkkatrom.dknotificationtest.utils.PreferenceUtils;

public class SettingsFragment extends SettingsColorPickerFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreference mShowEmphasizedActions;
    private SwitchPreference mShowTombstoneActions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        PreferenceUtils.getInstance(getActivity()).setOnSharedPreferenceChangeListener(this);

        mShowEmphasizedActions =
                (SwitchPreference) findPreference(PreferenceUtils.SHOW_EMPHASIZED_ACTIONS);
        mShowTombstoneActions =
                (SwitchPreference) findPreference(PreferenceUtils.SHOW_TOMBSTONE_ACTIONS);

        mShowTombstoneActions.setEnabled(!mShowEmphasizedActions.isChecked());
        mShowEmphasizedActions.setEnabled(!mShowTombstoneActions.isChecked());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
             String key) {
        if (key.equals(PreferenceUtils.SHOW_EMPHASIZED_ACTIONS)) {
            mShowTombstoneActions.setEnabled(!mShowEmphasizedActions.isChecked());
        } else if (key.equals(PreferenceUtils.SHOW_TOMBSTONE_ACTIONS)) {
            mShowEmphasizedActions.setEnabled(!mShowTombstoneActions.isChecked());
        }
    }
}
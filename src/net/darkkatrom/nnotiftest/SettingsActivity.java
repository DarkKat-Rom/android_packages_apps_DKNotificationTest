/*
 * Copyright (C) 2016 DarkKat
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

package net.darkkatrom.nnotiftest;

import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import net.darkkatrom.nnotiftest.R;
import net.darkkatrom.nnotiftest.fragments.SettingsFragment;

public class SettingsActivity extends PreferenceActivity implements
        FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        if (getBackStackEntryCount() == 0) {
            setTitle(R.string.settings_title);
        } else {
            setTitle(R.string.color_picker_fragment_title);
        }
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getBackStackEntryCount() {
        return getFragmentManager().getBackStackEntryCount();
    }
}

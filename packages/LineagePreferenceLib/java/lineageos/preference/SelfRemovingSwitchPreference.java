/*
 * Copyright (C) 2016 The CyanogenMod Project
 * Copyright (C) 2018-2024 The LineageOS Project
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
package lineageos.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreferenceCompat;

import lineageos.preference.R;

/**
 * A SwitchPreferenceCompat which can automatically remove itself from the hierarchy
 * based on constraints set in XML.
 */
public abstract class SelfRemovingSwitchPreference extends SwitchPreferenceCompat {

    private final ConstraintsHelper mConstraints;

    public SelfRemovingSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mConstraints = new ConstraintsHelper(context, attrs, this);
        setPreferenceDataStore(new DataStore());
        init(context, attrs);
    }

    public SelfRemovingSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mConstraints = new ConstraintsHelper(context, attrs, this);
        setPreferenceDataStore(new DataStore());
        init(context, attrs);
    }

    public SelfRemovingSwitchPreference(Context context) {
        super(context);
        mConstraints = new ConstraintsHelper(context, null, this);
        setPreferenceDataStore(new DataStore());
    }

    @Override
    public void onAttached() {
        super.onAttached();
        mConstraints.onAttached();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mConstraints.onBindViewHolder(holder);
    }

    public void setAvailable(boolean available) {
        mConstraints.setAvailable(available);
    }

    public boolean isAvailable() {
        return mConstraints.isAvailable();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray b = context.obtainStyledAttributes(attrs, R.styleable.PreferenceLayout);
        int position = b.getInt(R.styleable.PreferenceLayout_position, 4);
        b.recycle();

        switch (position) {
            case 0: // Top
                setLayoutResource(R.layout.tenx_preference_top);
                break;
            case 1: // Middle
                setLayoutResource(R.layout.tenx_preference_middle);
                break;
            case 2: // Bottom
                setLayoutResource(R.layout.tenx_preference_bottom);
                break;
            case 3: // Full
                setLayoutResource(R.layout.tenx_preference);
                break;
            case 4: // None
                return;
        }
    }

    protected abstract boolean isPersisted();
    protected abstract void putBoolean(String key, boolean value);
    protected abstract boolean getBoolean(String key, boolean defaultValue);

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        final boolean checked;
        if (!restorePersistedValue || !isPersisted()) {
            boolean defValue = defaultValue == null ? false : (boolean) defaultValue;
            checked = getBoolean(getKey(), defValue);
            if (shouldPersist()) {
                persistBoolean(checked);
            }
        } else {
            // Note: the default is not used because to have got here
            // isPersisted() must be true.
            checked = getBoolean(getKey(), false /* not used */);
        }
        setChecked(checked);
    }

    private class DataStore extends PreferenceDataStore {
        @Override
        public void putBoolean(String key, boolean value) {
            SelfRemovingSwitchPreference.this.putBoolean(key, value);
        }

        @Override
        public boolean getBoolean(String key, boolean defaultValue) {
            return SelfRemovingSwitchPreference.this.getBoolean(key, defaultValue);
        }
    }
}

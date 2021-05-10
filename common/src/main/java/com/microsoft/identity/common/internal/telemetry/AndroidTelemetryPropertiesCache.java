// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package com.microsoft.identity.common.internal.telemetry;

import android.content.Context;

import com.microsoft.identity.common.internal.cache.SharedPreferencesFileManager;
import com.microsoft.identity.common.java.interfaces.IKeyPairStorage;
import com.microsoft.identity.common.java.telemetry.TelemetryPropertiesCache;

import lombok.NonNull;

/**
 * Telemetry properties cache for Android.
 * Use Shared Preference as storage.
 * */
public class AndroidTelemetryPropertiesCache extends TelemetryPropertiesCache {

    private static final String SHARED_PREFS_NAME = "com.microsoft.common.telemetry-properties";

    public AndroidTelemetryPropertiesCache(@NonNull final Context context) {
        super(new IKeyPairStorage() {
            final SharedPreferencesFileManager mSharedPrefs =
                    SharedPreferencesFileManager.getSharedPreferences(context, SHARED_PREFS_NAME, -1, null);

            @Override
            public String get(@NonNull String key) {
                return mSharedPrefs.getString(key);
            }

            @Override
            public void put(@lombok.NonNull String key, String value) {
                mSharedPrefs.putString(key, value);
            }
        });
    }
}

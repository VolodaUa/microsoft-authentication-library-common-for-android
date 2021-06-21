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
package com.microsoft.identity.common.internal.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to serialize and deserialize query parameters from List<Pair<String, String>> to json String
 * and vice versa
 */
public class QueryParamsAdapter extends TypeAdapter<List<AbstractMap.SimpleEntry<String, String>>> {

    private static final Gson mGson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(
                QueryParamsAdapter.class,
                new QueryParamsAdapter()
        );
        mGson = gsonBuilder.create();
    }

    @Override
    public void write(final JsonWriter out, final List<AbstractMap.SimpleEntry<String, String>> queryParams) throws IOException {
        out.beginObject();

        for(final AbstractMap.SimpleEntry<String, String> keyValuePair : queryParams){
            out.name(keyValuePair.getKey());
            out.value(keyValuePair.getValue());
        }
        out.endObject();
    }

    @Override
    public List<AbstractMap.SimpleEntry<String, String>> read(final JsonReader in) throws IOException {
        in.beginObject();
        final List<AbstractMap.SimpleEntry<String, String>> result = new ArrayList<>();
        while (in.hasNext()){
            final String key = in.nextName();
            final String value = in.nextString();
            final AbstractMap.SimpleEntry<String, String> keyValuePair = new AbstractMap.SimpleEntry<>(key, value);
            result.add(keyValuePair);
        }
        in.endObject();
        return result;
    }

    public static String _toJson(final List<AbstractMap.SimpleEntry<String, String>> extraQueryStringParameters) {
        final Type listType = new TypeToken<List<AbstractMap.SimpleEntry<String, String>>>(){}.getType();
        return mGson.toJson(extraQueryStringParameters, listType);
    }

    public static List<AbstractMap.SimpleEntry<String, String>> _fromJson(final String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return new ArrayList<>();
        }
        final Type listType = new TypeToken<List<AbstractMap.SimpleEntry<String, String>>>(){}.getType();
        return mGson.fromJson(jsonString, listType);
    }

}

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

import android.util.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.microsoft.identity.common.adal.internal.util.StringExtensions.ENCODING_UTF8;

/**
 * Util class for encoding related tasks.
 */
public final class EncodingUtil {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private EncodingUtil() {
        // Utility class.
    }

    /**
     * Base64 encodes the supplied String.
     *
     * @param message The String to encode.
     * @return The encoded String.
     */
    public static String base64UrlEncodeToString(final String message) {
        return Base64.encodeToString(message.getBytes(UTF8), Base64.URL_SAFE | Base64.NO_WRAP);
    }

}

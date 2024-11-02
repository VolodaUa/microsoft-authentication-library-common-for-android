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
package com.microsoft.identity.common.java

import com.microsoft.identity.common.java.base64.Base64Flags
import com.microsoft.identity.common.java.base64.Base64Util
import com.microsoft.identity.common.java.base64.IBase64
import cz.msebera.android.httpclient.extras.Base64

/**
 * If you need to rename or change the namespace of this class,
 * you'll need to make change in [Base64Util] too.
 *
 * see [Base64Util] for more info.
 **/
class MseberaBase64ForCommon4jTests : IBase64 {
    override fun encode(input: ByteArray, vararg flags: Base64Flags): ByteArray {
        return Base64.encode(input, combineFlags(*flags))
    }

    override fun decode(input: ByteArray, vararg flags: Base64Flags): ByteArray {
        return Base64.decode(input, combineFlags(*flags))
    }

    private fun combineFlags(vararg flags: Base64Flags): Int {
        var combinedFlag = Base64.DEFAULT

        if (flags.contains(Base64Flags.URL_SAFE)) {
            combinedFlag = combinedFlag or Base64.URL_SAFE
        }
        if (flags.contains(Base64Flags.NO_WRAP)) {
            combinedFlag = combinedFlag or Base64.NO_WRAP
        }
        if (flags.contains(Base64Flags.NO_PADDING)) {
            combinedFlag = combinedFlag or Base64.NO_PADDING
        }

        return combinedFlag
    }
}

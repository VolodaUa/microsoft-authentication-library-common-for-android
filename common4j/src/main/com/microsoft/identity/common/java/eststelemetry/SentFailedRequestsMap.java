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
package com.microsoft.identity.common.java.eststelemetry;

import com.microsoft.identity.common.java.interfaces.INameValueStorage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.NonNull;

public class SentFailedRequestsMap implements INameValueStorage<Set<FailedRequest>> {
    private final ConcurrentHashMap<String, Set<FailedRequest>> sentFailedRequestsMap = new ConcurrentHashMap<>();

    @Override
    public Set<FailedRequest> get(@NonNull String name) {
        return sentFailedRequestsMap.get(name);
    }

    @Override
    public void put(@NonNull String name, Set<FailedRequest> value) {
        sentFailedRequestsMap.put(name, value);
    }

    @Override
    public void remove(@NonNull String name) {
        sentFailedRequestsMap.remove(name);
    }

    @Override
    public void clear() {
        sentFailedRequestsMap.clear();
    }
}
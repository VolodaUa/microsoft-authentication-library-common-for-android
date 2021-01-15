//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.identity.common;

/**
 * A Plain Object class containing information of a code marker which is an event in code.
 * marker is a string which is prefixed by scenario code and is defined in class CodeMarkersConstants.
 */
public class CodeMarker {

    private String marker;
    // timeInMilliseconds represents time in milliseconds where zero(0) means the time when first codemarker was captured.
    private long timeInMilliseconds;
    // timeStamp is the system time at the time of the capture of the codemarker.
    private String timeStamp;
    private long threadId;

    public CodeMarker(final String marker, final long timeInMilliseconds, final String timeStamp, final long id) {
        this.marker = marker;
        this.timeInMilliseconds = timeInMilliseconds;
        this.timeStamp = timeStamp;
        this.threadId = id;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMarker() {
        return marker;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

}

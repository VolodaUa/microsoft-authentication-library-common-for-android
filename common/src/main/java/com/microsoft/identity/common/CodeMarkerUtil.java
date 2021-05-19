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

import java.util.List;

/**
 * This class holds static methods which provides functionalities related to codemarkers.
 */
public class CodeMarkerUtil {
    /**
     * This method converts list of codemarkers to csv content which can be written to a file.
     *
     * @param codeMarkers
     * @return
     */
    public static String getCSVContent(List<CodeMarker> codeMarkers) {
        if (codeMarkers == null) return "";

        StringBuilder stringToWrite = new StringBuilder();
        if (codeMarkers.size() > 0) {
            stringToWrite.append(codeMarkers.get(0).getCSVHeader());
        }

        for (CodeMarker codeMarker : codeMarkers) {
            stringToWrite.append('\n');
            stringToWrite.append(codeMarker.getCSVLine());
        }
        return stringToWrite.toString();
    }
}
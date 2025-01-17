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
package com.microsoft.identity.common.internal.providers.oauth2

import android.os.Build
import android.webkit.PermissionRequest
import com.microsoft.identity.common.logging.Logger

/**
 * Class responsible for handling camera permission requests.
 * Note: This class is compatible only with API level 21 and above;
 * functionality will not behave as expected on lower versions.
 */
class CameraPermissionRequest(private val mCameraPermissionRequest: PermissionRequest) {
    /**
     * Check if the permission was granted.
     *
     * @return true if the permission is granted.
     */
    var isGranted = false
        private set

    /**
     * Call this method to grant the permission to access the camera resource.
     * The granted permission is only valid for the current WebView.
     *
     *
     * Note: This method is only available on API level 21 or higher.
     */
    fun grant() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cameraResource = arrayOf(
                PermissionRequest.RESOURCE_VIDEO_CAPTURE
            )
            mCameraPermissionRequest.grant(cameraResource)
        }
        isGranted = true
    }

    /**
     * Call this method to deny the permission to access the camera resource.
     *
     *
     * Note: This method is only available on API level 21 or higher.
     */
    fun deny() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraPermissionRequest.deny()
        }
        isGranted = false
    }

    companion object {
        private val TAG = CameraPermissionRequest::class.java.simpleName

        /**
         * Determines whatever if the given permission request is for the camera resource.
         *
         *
         * Note: This method is only available on API level 21 or higher.
         * Devices running on lower API levels will not be able to grant or deny camera permission requests.
         * getResources() method is only available on API level 21 or higher.
         *
         * @param request The permission request.
         * @return true if the given permission request is for camera, false otherwise.
         */
        @JvmStatic
        fun isValidRequest(request: PermissionRequest): Boolean {
            val methodTag = "$TAG:validateRequest"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return request.resources.size == 1 && PermissionRequest.RESOURCE_VIDEO_CAPTURE == request.resources[0]
            }
            Logger.warn(
                methodTag, "PermissionRequest.getResources() method is not available on API:"
                        + Build.VERSION.SDK_INT + ". We cannot determine if the request is for camera."
            )
            return false
        }
    }
}

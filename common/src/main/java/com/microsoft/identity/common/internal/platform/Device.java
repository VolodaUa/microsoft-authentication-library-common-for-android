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
package com.microsoft.identity.common.internal.platform;

import com.microsoft.identity.common.exception.ClientException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A static class holding device data.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS",
        justification = "This class kept its original name to avoid breaking change during the refactoring process." +
                "Once the process is done, this class will be removed entirely. ")
public final class Device extends com.microsoft.identity.common.java.platform.Device {

    private static IDevicePopManager sDevicePoPManager;

    // Note: this needs to be invoked at the beginning of an android flow.
    static {
        com.microsoft.identity.common.java.platform.Device.setDeviceMetadata(new AndroidDeviceMetadata());
    }

    public static synchronized IDevicePopManager getDevicePoPManagerInstance() throws ClientException {
        try {
            if (null == sDevicePoPManager) {
                sDevicePoPManager = new DevicePopManager();
            }
            return sDevicePoPManager;
        } catch (CertificateException
                | NoSuchAlgorithmException
                | KeyStoreException
                | IOException e) {
            throw new ClientException(
                    ClientException.KEYSTORE_NOT_INITIALIZED,
                    "Failed to initialize DevicePoPManager = " + e.getMessage(),
                    e
            );
        }
    }

}

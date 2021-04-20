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
package com.microsoft.identity.common.internal.commands.parameters;

import com.google.gson.annotations.Expose;
import com.microsoft.identity.common.exception.ArgumentException;
import com.microsoft.identity.common.internal.authorities.AzureActiveDirectoryB2CAuthority;
import com.microsoft.identity.common.internal.providers.microsoft.azureactivedirectory.AzureActiveDirectory;
import com.microsoft.identity.common.internal.providers.microsoft.azureactivedirectory.AzureActiveDirectoryCloud;
import com.microsoft.identity.common.logging.Logger;

import java.io.IOException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class SilentTokenCommandParameters extends TokenCommandParameters {

    private static final String TAG = SilentTokenCommandParameters.class.getSimpleName();

    private static final Object sLock = new Object();

    @Expose()
    private final boolean refreshDueToRefreshIn;

    @Override
    public void validate() throws ArgumentException {
        super.validate();

        if (getAccount() == null) {
            Logger.warn(TAG, "The account set on silent operation parameters is NULL.");
            // if the authority is B2C, then we do not need check if matches with the account enviroment
            // as B2C only exists in one cloud and can use custom domains
        } else if (!isAuthorityB2C() && !authorityMatchesAccountEnvironment()) {
            throw new ArgumentException(
                    ArgumentException.ACQUIRE_TOKEN_SILENT_OPERATION_NAME,
                    ArgumentException.AUTHORITY_ARGUMENT_NAME,
                    "Authority passed to silent parameters does not match with the cloud associated to the account."
            );
        }
    }

    private boolean isAuthorityB2C() {
        return getAuthority() instanceof AzureActiveDirectoryB2CAuthority;
    }

    private boolean authorityMatchesAccountEnvironment() {
        final String methodName = ":authorityMatchesAccountEnvironment";
        try {
            if (!AzureActiveDirectory.isInitialized()) {
                performCloudDiscovery();
            }
            final AzureActiveDirectoryCloud cloud = AzureActiveDirectory.getAzureActiveDirectoryCloudFromHostName(getAccount().getEnvironment());
            return cloud != null && cloud.getPreferredNetworkHostName().equals(getAuthority().getAuthorityURL().getAuthority());
        } catch (IOException e) {
            Logger.error(
                    TAG + methodName,
                    "Unable to perform cloud discovery",
                    e);
            return false;
        }
    }

    private static void performCloudDiscovery() throws IOException {
        final String methodName = ":performCloudDiscovery";
        Logger.verbose(
                TAG + methodName,
                "Performing cloud discovery..."
        );
        synchronized (sLock) {
            AzureActiveDirectory.performCloudDiscovery();
        }
    }
}

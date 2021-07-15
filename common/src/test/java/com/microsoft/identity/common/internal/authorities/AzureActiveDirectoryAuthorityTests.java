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
package com.microsoft.identity.common.internal.authorities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AzureActiveDirectoryAuthorityTests {
    @Test
    public void isSamePreferredNetworkHostAsAuthority_Returns_True_For_Authority_With_ValidAliases_For_SameCloud() throws IOException {
        String[] cloudAliasesWW = new String[]{"https://login.microsoftonline.com", "https://login.windows.net", "https://login.microsoft.com", "https://sts.windows.net"};
        String[] cloudAliasesCN = new String[]{"https://login.chinacloudapi.cn", "https://login.partner.microsoftonline.cn"};
        String[] cloudAliasesUSGov = new String[]{"https://login.microsoftonline.us", "https://login.usgovcloudapi.net"};

        AzureActiveDirectoryAuthority authorityWW = new AzureActiveDirectoryAuthority(new AllAccounts(cloudAliasesWW[0]));
        for (String cloudUrl : cloudAliasesWW) {
            AzureActiveDirectoryAuthority authority = new AzureActiveDirectoryAuthority(new AnyOrganizationalAccount(cloudUrl));
            assertTrue(authorityWW.isSamePreferredNetworkHostAsAuthority(authority));
        }

        AzureActiveDirectoryAuthority authorityCN = new AzureActiveDirectoryAuthority(new AllAccounts(cloudAliasesCN[0]));
        for (String cloudUrl : cloudAliasesCN) {
            AzureActiveDirectoryAuthority authority = new AzureActiveDirectoryAuthority(new AnyOrganizationalAccount(cloudUrl));
            assertTrue(authorityCN.isSamePreferredNetworkHostAsAuthority(authority));
        }

        AzureActiveDirectoryAuthority authorityUSGov = new AzureActiveDirectoryAuthority(new AllAccounts(cloudAliasesUSGov[0]));
        for (String cloudUrl : cloudAliasesUSGov) {
            AzureActiveDirectoryAuthority authority = new AzureActiveDirectoryAuthority(new AnyOrganizationalAccount(cloudUrl));
            assertTrue(authorityUSGov.isSamePreferredNetworkHostAsAuthority(authority));
        }
    }

    @Test
    public void isSamePreferredNetworkHostAsAuthority_Returns_False_For_Authorities_From_Different_Clouds() throws IOException {
        AzureActiveDirectoryAuthority authorityWW = new AzureActiveDirectoryAuthority(new AllAccounts("https://login.microsoftonline.com"));
        AzureActiveDirectoryAuthority authorityCN = new AzureActiveDirectoryAuthority(new AllAccounts("https://login.partner.microsoftonline.cn"));
        AzureActiveDirectoryAuthority authorityUSGov = new AzureActiveDirectoryAuthority(new AllAccounts("https://login.microsoftonline.us"));
        assertFalse(authorityWW.isSamePreferredNetworkHostAsAuthority(authorityCN));
        assertFalse(authorityCN.isSamePreferredNetworkHostAsAuthority(authorityUSGov));
        assertFalse(authorityUSGov.isSamePreferredNetworkHostAsAuthority(authorityWW));
    }
}
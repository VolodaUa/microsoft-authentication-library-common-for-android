package com.microsoft.identity.common.internal.commands.parameters;

import com.microsoft.identity.common.internal.dto.IAccountRecord;
import com.microsoft.identity.common.internal.ui.browser.BrowserDescriptor;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RemoveAccountCommandParameters extends CommandParameters {

    private IAccountRecord account;
    private List<BrowserDescriptor> browserSafeList;
}

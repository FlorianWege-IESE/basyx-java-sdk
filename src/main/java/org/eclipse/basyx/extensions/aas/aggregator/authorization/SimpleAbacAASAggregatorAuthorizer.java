/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.IAbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Simple attribute based implementation for {@link IAASAggregatorAuthorizer}.
 *
 * @author wege
 */
public class SimpleAbacAASAggregatorAuthorizer<SubjectInformationType> implements IAASAggregatorAuthorizer<SubjectInformationType> {
  protected IAbacRuleChecker abacRuleChecker;
  protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

  public SimpleAbacAASAggregatorAuthorizer(final IAbacRuleChecker abacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
    this.abacRuleChecker = abacRuleChecker;
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public Collection<IAssetAdministrationShell> enforceGetAASList(SubjectInformationType subjectInformation, Supplier<Collection<IAssetAdministrationShell>> aasListSupplier) throws InhibitException {
    return aasListSupplier.get();
  }

  @Override
  public IAssetAdministrationShell enforceGetAAS(SubjectInformationType subjectInformation, IIdentifier aasId, Supplier<IAssetAdministrationShell> aasSupplier) throws InhibitException {
    if (!abacRuleChecker.checkAbacRuleIsSatisfied(
        roleAuthenticator.getRoles(subjectInformation),
        AASAggregatorScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
    return aasSupplier.get();
  }

  @Override
  public IModelProvider enforceGetAASProvider(SubjectInformationType subjectInformation, IIdentifier aasId, Supplier<IModelProvider> modelProviderSupplier) throws InhibitException {
    if (!abacRuleChecker.checkAbacRuleIsSatisfied(
        roleAuthenticator.getRoles(subjectInformation),
        AASAggregatorScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
    return modelProviderSupplier.get();
  }

  @Override
  public void enforceCreateAAS(SubjectInformationType subjectInformation, IIdentifier aasId) throws InhibitException {
    if (!abacRuleChecker.checkAbacRuleIsSatisfied(
        roleAuthenticator.getRoles(subjectInformation),
        AASAggregatorScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUpdateAAS(SubjectInformationType subjectInformation, IIdentifier aasId) throws InhibitException {
    if (!abacRuleChecker.checkAbacRuleIsSatisfied(
        roleAuthenticator.getRoles(subjectInformation),
        AASAggregatorScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceDeleteAAS(SubjectInformationType subjectInformation, IIdentifier aasId) throws InhibitException {
    if (!abacRuleChecker.checkAbacRuleIsSatisfied(
        roleAuthenticator.getRoles(subjectInformation),
        AASAggregatorScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }
}

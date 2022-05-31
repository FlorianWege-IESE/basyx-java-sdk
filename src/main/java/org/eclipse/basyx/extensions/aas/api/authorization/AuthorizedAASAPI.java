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
package org.eclipse.basyx.extensions.aas.api.authorization;

import java.util.Optional;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Implementation variant for the AASAPI that authorizes each access to the API
 *
 * @author espen
 */
public class AuthorizedAASAPI<SubjectInformationType> implements IAASAPI {
	public static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.WRITE_SCOPE;

	protected final IAASAPI decoratedAASAPI;
	protected final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedAASAPI(
			final IAASAPI decoratedAASAPI,
			final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.decoratedAASAPI = decoratedAASAPI;
		this.aasAPIAuthorizer = aasAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	public AuthorizedAASAPI(final IAASAPI decoratedAASAPI) {
		this(
			decoratedAASAPI,
			(IAASAPIAuthorizer<SubjectInformationType>) new GrantedAuthorityAASAPIAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		try {
			return enforceGetAAS();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected IAssetAdministrationShell enforceGetAAS() throws InhibitException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();
		final IIdentifier aasId = Optional.ofNullable(aas).map(IIdentifiable::getIdentification).orElse(null);
		return aasAPIAuthorizer.enforceGetAAS(
				subjectInformationProvider.get(),
				aasId,
				() -> aas
		);
	}

	@Override
	public void addSubmodel(final IReference submodel) {
		try {
			enforceAddSubmodel(submodel);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAASAPI.addSubmodel(submodel);
	}

	protected void enforceAddSubmodel(final IReference smId) throws InhibitException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();
		final IIdentifier aasId = Optional.ofNullable(aas).map(IIdentifiable::getIdentification).orElse(null);
		aasAPIAuthorizer.enforceAddSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void removeSubmodel(final String smIdShortPath) {
		try {
			enforceRemoveSubmodel(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAASAPI.removeSubmodel(smIdShortPath);
	}

	protected void enforceRemoveSubmodel(final String smIdShortPath) throws InhibitException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();
		final IIdentifier aasId = Optional.ofNullable(aas).map(IIdentifiable::getIdentification).orElse(null);
		aasAPIAuthorizer.enforceRemoveSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smIdShortPath
		);
	}
}

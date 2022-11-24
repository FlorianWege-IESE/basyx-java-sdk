/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;

/**
 * Factory decorating AASAggregator for authorization
 * 
 * @author fischer, fried, wege
 */
public class AuthorizedDecoratingAASAggregatorFactory<SubjectInformationType> implements IAASAggregatorFactory {
	protected final IAASAggregatorFactory apiFactory;
	protected final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedDecoratingAASAggregatorFactory(
			final IAASAggregatorFactory factoryToBeDecorated,
			final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.apiFactory = factoryToBeDecorated;
		this.aasAggregatorAuthorizer = aasAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated please use {@link AuthorizedDecoratingAASAggregatorFactory#AuthorizedDecoratingAASAggregatorFactory(IAASAggregatorFactory, IAASAggregatorAuthorizer, ISubjectInformationProvider)} instead, which uses more parameters for the authorization
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public AuthorizedDecoratingAASAggregatorFactory(final IAASAggregatorFactory factoryToBeDecorated) {
		this(
				factoryToBeDecorated,
				(IAASAggregatorAuthorizer<SubjectInformationType>) new GrantedAuthorityAASAggregatorAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
				(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public IAASAggregator create() {
		IAASAggregator aggregator = apiFactory.create();
		aggregator = new AuthorizedAASAggregator<>(aggregator, aasAggregatorAuthorizer, subjectInformationProvider);
		return aggregator;
	}

}

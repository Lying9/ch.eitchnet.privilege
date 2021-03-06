/*
 * Copyright 2013 Robert von Burg <eitch@eitchnet.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.eitchnet.privilege.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.eitchnet.privilege.base.AccessDeniedException;
import ch.eitchnet.privilege.base.PrivilegeException;
import ch.eitchnet.privilege.i18n.PrivilegeMessages;
import ch.eitchnet.privilege.policy.PrivilegePolicy;

/**
 * This context gives access to a logged in user's privilege data e.g. the {@link UserRep}, {@link Certificate} and the
 * user's list of {@link PrivilegeRep}
 * 
 * <p>
 * Note: This is an internal object which is not to be serialized to clients
 * </p>
 * 
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
public class PrivilegeContext {

	//
	// object state
	//

	private UserRep userRep;
	private Certificate certificate;
	private Map<String, IPrivilege> privileges;
	private Map<String, PrivilegePolicy> policies;

	public PrivilegeContext(UserRep userRep, Certificate certificate, Map<String, IPrivilege> privileges,
			Map<String, PrivilegePolicy> policies) {
		this.userRep = userRep;
		this.certificate = certificate;
		this.privileges = Collections.unmodifiableMap(new HashMap<String, IPrivilege>(privileges));
		this.policies = Collections.unmodifiableMap(new HashMap<String, PrivilegePolicy>(policies));
	}

	public UserRep getUserRep() {
		return this.userRep;
	}

	public Certificate getCertificate() {
		return this.certificate;
	}

	public String getUsername() {
		return this.userRep.getUsername();
	}

	public Set<String> getPrivilegeNames() {
		return this.privileges.keySet();
	}

	public IPrivilege getPrivilege(String privilegeName) {
		return this.privileges.get(privilegeName);
	}

	public List<String> getFlatAllowList() {
		List<String> allowList = new ArrayList<>();
		for (IPrivilege privilege : this.privileges.values()) {
			allowList.addAll(privilege.getAllowList());
		}
		return allowList;
	}

	// 
	// business logic
	//

	/**
	 * Validates if the user for this context has the privilege to access to the given {@link Restrictable}. If the user
	 * has the privilege, then this method returns with no exception and void, if the user does not have the privilege,
	 * then a {@link AccessDeniedException} is thrown.
	 * 
	 * @param restrictable
	 *            the {@link Restrictable} which the user wants to access
	 * 
	 * @throws AccessDeniedException
	 *             if the user does not have access
	 * @throws PrivilegeException
	 *             if there is an internal error due to wrongly configured privileges or programming errors
	 */
	public void validateAction(Restrictable restrictable) throws AccessDeniedException, PrivilegeException {

		// the privilege for the restrictable
		String privilegeName = restrictable.getPrivilegeName();
		IPrivilege privilege = this.privileges.get(privilegeName);
		if (privilege == null) {
			String msg = MessageFormat.format(PrivilegeMessages.getString("Privilege.accessdenied.noprivilege"), //$NON-NLS-1$
					getUsername(), privilegeName, restrictable.getClass().getName());
			throw new AccessDeniedException(msg);
		}

		// get the policy referenced by the restrictable
		String policyName = privilege.getPolicy();
		PrivilegePolicy policy = this.policies.get(policyName);
		if (policy == null) {
			String msg = "The PrivilegePolicy {0} does not exist on the PrivilegeContext!"; //$NON-NLS-1$
			throw new PrivilegeException(MessageFormat.format(msg, policyName));
		}

		// delegate to the policy
		policy.validateAction(this, privilege, restrictable);
	}
}

package SpringAPI.security;

import static SpringAPI.security.ApplicationUserPermission.*;

import java.util.Set;

import com.google.common.collect.Sets;

public enum ApplicationUserRole {
	USER(Sets.newHashSet()),
	ADMIN(Sets.newHashSet(COURSE_READ, COURSE_WRITE, USER_READ, USER_WRITE));

	private final Set<ApplicationUserPermission> permissions;

	ApplicationUserRole(Set<ApplicationUserPermission> permissions) {this.permissions = permissions;}

	public Set<ApplicationUserPermission> getPermissions() {
		return permissions;
	}
}

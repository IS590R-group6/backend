package SpringAPI.auth;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ApplicationUser implements UserDetails {

	private final Set<? extends GrantedAuthority> grantedAuthorities;
	private final String password;
	private final boolean isAccountNonExpired;
	private final boolean isAccountNonLocked;
	private final boolean isCredentialsNonExpired;
	private final boolean isEnabled;
	private final String id;

	public ApplicationUser(
					String id,
					String password,
					Set<? extends GrantedAuthority> grantedAuthorities,
					boolean isAccountNonExpired,
					boolean isAccountNonLocked,
					boolean isCredentialsNonExpired,
					boolean isEnabled) {
		this.id = id;
		this.grantedAuthorities = grantedAuthorities;
		this.password = password;
		this.isAccountNonExpired = isAccountNonExpired;
		this.isAccountNonLocked = isAccountNonLocked;
		this.isCredentialsNonExpired = isCredentialsNonExpired;
		this.isEnabled = isEnabled;
	}

	@Override public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override public String getPassword() {
		return password;
	}

	@Override public String getUsername() {
		return id;
	}

	@Override public boolean isAccountNonExpired() {
		return isAccountNonExpired;
	}

	@Override public boolean isAccountNonLocked() {
		return isAccountNonLocked;
	}

	@Override public boolean isCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	@Override public boolean isEnabled() {
		return isEnabled;
	}
}

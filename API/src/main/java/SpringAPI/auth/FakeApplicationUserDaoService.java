package SpringAPI.auth;

import static SpringAPI.security.ApplicationUserRole.ADMIN;
import static SpringAPI.security.ApplicationUserRole.USER;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao{

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
		return getApplicationUsers()
						.stream()
						.filter(applicationUser -> username.equals(applicationUser.getUsername()))
						.findFirst();
	}

	private List<ApplicationUser> getApplicationUsers() {
		List<ApplicationUser> applicationUsers = Lists.newArrayList(
						new ApplicationUser(
										"annasmith",
										passwordEncoder.encode("password"),
										USER.getGrantedAuthorities(),
										true,
										true,
										true,
										true
						),
						new ApplicationUser(
										"linda",
										passwordEncoder.encode("password"),
										ADMIN.getGrantedAuthorities(),
										true,
										true,
										true,
										true
						)
		);

		return applicationUsers;
	}
}

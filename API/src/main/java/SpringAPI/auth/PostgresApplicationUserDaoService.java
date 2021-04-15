package SpringAPI.auth;

import static SpringAPI.security.ApplicationUserRole.ADMIN;
import static SpringAPI.security.ApplicationUserRole.USER;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import SpringAPI.model.User;
import SpringAPI.security.ApplicationUserRole;

@Repository("postgres")
public class PostgresApplicationUserDaoService implements ApplicationUserDao{

	private final PasswordEncoder passwordEncoder;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public PostgresApplicationUserDaoService(PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
		this.passwordEncoder = passwordEncoder;
		this.jdbcTemplate = jdbcTemplate;
	}

	//from DB
	@Override
	public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
		final String sql = "SELECT userId, email, password, role FROM users WHERE email = ?";
		ApplicationUser user = jdbcTemplate.queryForObject( //switch out for the one that uses sql, object, int[], class
						sql,
						new Object[]{username},
						(resultSet, i) -> {
							String id = resultSet.getString("userId");
							ApplicationUserRole role = ApplicationUserRole.valueOf(resultSet.getString("role"));
							String password = resultSet.getString("password");
							return new ApplicationUser(id, password, role.getGrantedAuthorities(), true, true, true, true);
						});
		return Optional.ofNullable(user);
	}
}

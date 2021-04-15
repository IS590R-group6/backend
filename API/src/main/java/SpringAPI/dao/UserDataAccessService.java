package SpringAPI.dao;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import SpringAPI.model.User;
import SpringAPI.security.ApplicationUserRole;

@Repository("postgresUser")
public class UserDataAccessService implements UserDao {

	private final JdbcTemplate jdbcTemplate;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserDataAccessService(JdbcTemplate jdbcTemplate,
								 PasswordEncoder passwordEncoder) {
		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public int insertUser(UUID id, User user) {
		return jdbcTemplate.update(
				"INSERT INTO users (userId, name, email, password, role) VALUES (?, ?, ?, ?, ?)",
				id,
				user.getName(),
				user.getEmail(),
				passwordEncoder.encode(user.getPassword()),
				ApplicationUserRole.USER.name());
	}

	/*need to come back to this when our front end is finished.  Not sure what the use case will be here but I'm thinking we'll need to not show password.
		just including it now to practice */
	@Override
	public List<User> selectAllUsers() {
		final String sql = "SELECT userId, name, email, password FROM users";
		return jdbcTemplate.query(sql, (resultSet, i) -> {
			UUID id = UUID.fromString( resultSet.getString("userId"));
			String name = resultSet.getString("name");
			String email = resultSet.getString("email");
			String password = resultSet.getString("password");
			return new User(id, name, email, password);
		});
	}

	@Override
	public Optional<User> selectUserById(UUID id) {
		final String sql = "SELECT userId, name, email, password FROM users WHERE userId = ?";
		User user = jdbcTemplate.queryForObject( //switch out for the one that uses sql, object, int[], class
						sql,
						new Object[]{id},(resultSet, i) -> {
			UUID personId = UUID.fromString( resultSet.getString("userId"));
			String name = resultSet.getString("name");
			String email = resultSet.getString("email");
			String password = resultSet.getString("password");
			return new User(personId, name, email, password);
		});
		return Optional.ofNullable(user);
	}

	@Override
	public int updateUserById(UUID id, User user) {
		Optional<User> userMaybe = selectUserById(id);
		if (userMaybe.isEmpty()) {
			return 0;
		}
		String sql = "UPDATE users SET name = COALESCE(?, name), email = COALESCE(?, email), password = COALESCE(?, password) WHERE userId = ?";
		Object[] args = new Object[] {user.getName(), user.getEmail(), user.getPassword(), id};
		return jdbcTemplate.update(sql,args);
	}

	@Override
	public int deleteUserById(UUID id) {
		Optional<User> userMaybe = selectUserById(id);
		if (userMaybe.isEmpty()) {
			return 0;
		}
		String sql = "DELETE FROM users WHERE userId = ?";
		Object[] args = new Object[] {id};
		return jdbcTemplate.update(sql,args);
	}
}

package SpringAPI.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import SpringAPI.model.Entry;

@Repository("postgresEntry")
public class EntryDataAccessService implements EntryDao {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public EntryDataAccessService(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

	@Override
	public int insertEntry(UUID id, Entry entry, UUID userId) {
		return jdbcTemplate.update(
				"INSERT INTO entry (entryId, userId, title, markdown, html) VALUES (?, ?, ?, ?, ?)",
				id,
				userId,
				entry.getTitle(),
				entry.getMarkdown(),
				entry.getHtml());
	}

	@Override
	public List<Entry> selectAllEntries(String id) {
		final String sql = "SELECT entryId, userId, title, markdown, html FROM entry WHERE userId = ?";
		return jdbcTemplate.query(sql, new Object[]{UUID.fromString(id)}, (resultSet, i) -> {
			UUID entryId = UUID.fromString( resultSet.getString("entryId"));
			UUID userId = UUID.fromString( resultSet.getString("userId"));
			String title = resultSet.getString("title");
			String markdown = resultSet.getString("markdown");
			String html = resultSet.getString("html");
			return new Entry(entryId, userId, title, markdown, html);
		});
	}

	@Override
	public Optional<Entry> selectEntryById(UUID id) {
		final String sql = "SELECT entryId, userId, title, markdown, html FROM entry WHERE entryId = ?";
		Entry entry = jdbcTemplate.queryForObject( //switch out for the one that uses sql, object, int[], class
						sql,
						new Object[]{id},(resultSet, i) -> {
			UUID entryId = UUID.fromString( resultSet.getString("entryId"));
			UUID userId = UUID.fromString( resultSet.getString("userId"));
			String title = resultSet.getString("title");
			String markdown = resultSet.getString("markdown");
			String html = resultSet.getString("html");
			return new Entry(entryId, userId, title, markdown, html);
		});
		return Optional.ofNullable(entry);
	}

	@Override
	public int updateEntryById(UUID id, Entry entry) {
		Optional<Entry> entryMaybe = selectEntryById(id);
		if (entryMaybe.isEmpty()) {
			return 0;
		}
		String sql = "UPDATE entry SET userId = COALESCE(?, userId), title = COALESCE(?, title), markdown = COALESCE(?, markdown), html = COALESCE(?, html) WHERE entryId = ?";
		Object[] args = new Object[] {entry.getUserId(), entry.getTitle(), entry.getMarkdown(), entry.getHtml(), id};
		return jdbcTemplate.update(sql,args);
	}

	@Override
	public int deleteEntryById(UUID id) {
		Optional<Entry> entryMaybe = selectEntryById(id);
		if (entryMaybe.isEmpty()) {
			return 0;
		}
		String sql = "DELETE FROM entry WHERE entryId = ?";
		Object[] args = new Object[] {id};
		return jdbcTemplate.update(sql,args);
	}
}

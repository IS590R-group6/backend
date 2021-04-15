package SpringAPI.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import SpringAPI.model.Entry;

public interface EntryDao {
	int insertEntry(UUID id, Entry entry, UUID userId);

	default int insertEntry(Entry entry, UUID userId) {
		UUID id = UUID.randomUUID();
		return insertEntry(id, entry, userId);
	}

	List<Entry> selectAllEntries(String id);

	Optional<Entry> selectEntryById(UUID id);

	int deleteEntryById(UUID id);

	int updateEntryById(UUID id, Entry entry);
}

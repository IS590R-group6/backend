package SpringAPI.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import SpringAPI.model.Entry;

public interface EntryDao {
	int insertEntry(UUID id, Entry entry);

	default int insertEntry(Entry entry) {
		UUID id = UUID.randomUUID();
		return insertEntry(id, entry);
	}

	List<Entry> selectAllEntries();

	Optional<Entry> selectEntryById(UUID id);

	int deleteEntryById(UUID id);

	int updateEntryById(UUID id, Entry entry);
}

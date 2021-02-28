package SpringAPI.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import SpringAPI.dao.EntryDao;
import SpringAPI.dao.UserDao;
import SpringAPI.model.Entry;
import SpringAPI.model.User;

@Service
public class EntryService {

	private final EntryDao entryDao;

	@Autowired
	public EntryService(@Qualifier("postgresEntry") EntryDao entryDao) {
		this.entryDao = entryDao;
	}

	public int addEntry(Entry entry) {
		return entryDao.insertEntry(entry);
	}

	public List<Entry> getAllEntries() {
		return entryDao.selectAllEntries();
	}

	public Optional<Entry> getEntryById(UUID id) {
		return entryDao.selectEntryById(id);
	}

	public int deleteEntry(UUID id) {
		return entryDao.deleteEntryById(id);
	}

	public int updateEntry(UUID id, Entry newEntry) {
		return entryDao.updateEntryById(id, newEntry);
	}
}

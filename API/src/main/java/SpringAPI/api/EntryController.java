package SpringAPI.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SpringAPI.model.Entry;
import SpringAPI.service.EntryService;

@RequestMapping("api/v1/entry")
@RestController
public class EntryController {

	private final EntryService entryService;

	@Autowired
	public EntryController(EntryService entryService) {
		this.entryService = entryService;
	}

	@PostMapping
	public void addEntry(@Valid @NonNull @RequestBody Entry entry) {
		entryService.addEntry(entry);
	}

	@GetMapping
	public List<Entry> getAllEntries() {
		return entryService.getAllEntries();
	}

	@GetMapping(path = "{id}")
	public Entry getEntryById(@PathVariable("id") UUID id){
		return entryService.getEntryById(id)
			.orElse(null);

	}

	@DeleteMapping(path = "{id}")
	public void deleteEntryById(@PathVariable("id") UUID id) {
		entryService.deleteEntry(id);
	}

	@PutMapping(path = "{id}")
	public void updateEntry(@PathVariable UUID id, @Valid @NonNull @RequestBody Entry entryToUpdate) {
		entryService.updateEntry(id, entryToUpdate);
	}
}

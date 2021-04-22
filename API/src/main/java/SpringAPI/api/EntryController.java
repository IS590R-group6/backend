package SpringAPI.api;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SpringAPI.auth.ApplicationUser;
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UUID id;
		if (authentication == null) {
			id = UUID.fromString("658ef391-8ce6-4c11-8ffb-5c69c329e58d");
		}
		id = UUID.fromString((String) authentication.getPrincipal());

		entryService.addEntry(entry, id);
	}

	@GetMapping
	public List<Entry> getAllEntries() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id;
		if (authentication == null) {
			id = "658ef391-8ce6-4c11-8ffb-5c69c329e58d";
		}
		id = (String) authentication.getPrincipal();

		return entryService.getAllEntries(id);
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

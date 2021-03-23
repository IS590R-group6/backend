package SpringAPI.model;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Entry {

	private final UUID id;
	private final UUID userId;

	@NotBlank
	private final String title;
	private final String markdown;
	private final String html;

	public Entry(@JsonProperty("entryId") UUID id,
				 @JsonProperty("userId") UUID userId,
                 @JsonProperty("title") String title,
                 @JsonProperty("markdown") String markdown,
                 @JsonProperty("html") String html) {
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.markdown = markdown;
		this.html = html;
	}

	public UUID getId() {
		return id;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getTitle() {
		return title;
	}

	public String getMarkdown() { return markdown; }

	public String getHtml() { return html; }
}

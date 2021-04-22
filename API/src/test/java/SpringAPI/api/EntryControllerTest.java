package SpringAPI.api;

import static SpringAPI.api.TestingUtils.asJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import SpringAPI.model.Entry;

//import org.springframework.security.test.context.support.WithUserDetails;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
				//@WithUserDetails("test")



class EntryControllerTest {

	private WebApplicationContext context;
	private MockMvc mvc;
	private String entryID = "df97b038-10ac-45c8-bfea-d1635c9b46bb";

	@Autowired
	public EntryControllerTest(WebApplicationContext context) {
		this.mvc = MockMvcBuilders
						.webAppContextSetup(context)
						//.apply(springSecurity())
						.build();
	}

	@Test
	public void addEntry() throws Exception{
		var testEntry = new Entry(
						UUID.randomUUID(),
						UUID.randomUUID(),
						"My Test Title",
						"## Test Title Here",
						""
		);

		this.mvc.perform( MockMvcRequestBuilders
						.post("/api/v1/entry/")
						.content(asJsonString(testEntry))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
						//.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
						//.andExpect(MockMvcResultMatchers.jsonPath("$.title").value(testEntry.getTitle()));
	}

	@Test
	void getAllEntries() throws Exception{
		this.mvc.perform( MockMvcRequestBuilders
			.get("/api/v1/entry")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	void getEntryById() throws Exception{
		mvc.perform( MockMvcRequestBuilders
			.get("/api/v1/entry/{id}", entryID)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}


	@Test
	void updateEntry() throws Exception{
		var testEntry2 = new Entry(
				null,
				UUID.fromString("f0dfcad0-eb40-4c9d-b1b7-91e72dc72f27"),
				"My Test Title",
				"## Test Title Here",
				""
		);

		this.mvc.perform( MockMvcRequestBuilders
				.put("/api/v1/entry/{id}", entryID)
				.content(asJsonString(testEntry2))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		//.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
		//.andExpect(MockMvcResultMatchers.jsonPath("$.title").value(testEntry.getTitle()));
	}

	@Test
	void deleteEntryById() throws Exception{
		mvc.perform( MockMvcRequestBuilders
						.delete("/api/v1/entry/{id}",entryID)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
	}

}
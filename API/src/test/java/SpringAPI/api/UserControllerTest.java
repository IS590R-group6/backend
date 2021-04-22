package SpringAPI.api;

import static SpringAPI.api.TestingUtils.asJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;
import javax.swing.text.StringContent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import SpringAPI.model.User;

//import org.springframework.security.test.context.support.WithUserDetails;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
				//@WithUserDetails("test")


public class UserControllerTest {

	private WebApplicationContext context;
	private MockMvc mvc;

	@Autowired
	public UserControllerTest(WebApplicationContext context) {
		this.mvc = MockMvcBuilders
			.webAppContextSetup(context)
			//.apply(springSecurity())
			.build();
	}

	@Test
	public void addUser() throws Exception{
		var testUser = new User(
						null,
						"Scott",
						"scottc726@gmail.com",
						"password"
		);

		this.mvc.perform( MockMvcRequestBuilders
			.post("/api/v1/user/")
			.content(asJsonString(testUser))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
			//.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
			//.andReturn();
			//.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testUser.getName()));
	}

	@Test
	public void getAllUsers() throws Exception{
		mvc.perform( MockMvcRequestBuilders
			.get("/api/v1/user")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
//			.andReturn();
//			JSONParser parser = new JSONParser();
//			JSONObject json = (JSONObject) parser.parse(stringToParse);
//
//		JSONArray content = (JSONArray) result.getResponse().getContentAsString();
//			JSONObject json = new JSONObject(content);
//			var scott = "scott";
	}

	@Test
	public void getUserById() throws Exception{
		mvc.perform( MockMvcRequestBuilders
			.get("/api/v1/user/{id}", "9fc3861b-af2b-4664-a928-4a5ef03ba516")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void updateUser() throws Exception{
		var testUser2 = new User(
						null,
						"Kent",
						"Kent@gmail.com",
						"password"
		);

		mvc.perform( MockMvcRequestBuilders
			.put("/api/v1/user/{id}", "9fc3861b-af2b-4664-a928-4a5ef03ba516")
			.content(asJsonString(testUser2))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void deleteUserById() throws Exception{
		mvc.perform( MockMvcRequestBuilders
						.delete("/api/v1/user/{id}", "9fc3861b-af2b-4664-a928-4a5ef03ba516")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
	}
}
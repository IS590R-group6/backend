package SpringAPI.api;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestingUtils {
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
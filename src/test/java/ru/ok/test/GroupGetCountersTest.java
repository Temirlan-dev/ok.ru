package ru.ok.test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GroupGetCountersTest {
	private final static String url = "https://api.ok.ru/fb.do";
	private final static String method = "group.getCounters";
	private final static String accessToken = "-n-KoELZZ9QkYbRuKRcWQcm16AT6nXR6OQ03fPbziCx71PgoHzlvMflMSRXJSOGut31qQyuCBbvGY8Qxr1f4";
	private final static String groupId = "53038939046008";
	private final static String counterTypes = "MEMBERS,JOIN_REQUESTS";
	private final static String application_id = "512002493069";
	private final static String application_key = "COILALLGDIHBABABA";
	private final static String application_secret_key = "DAE5B158A11702234EB2A44E";
	private final static String format = "json";

	// Проверка правильности успешного ответа
	@Test
	public void testGetCounters() {
		Response response = RestAssured.given()
				.queryParam("method", method)
				.queryParam("access_token", accessToken)
				.queryParam("group_id", groupId)
				.queryParam("application_id", application_id)
				.queryParam("application_key", application_key)
				.queryParam("application_secret_key", application_secret_key)
				.queryParam("counterTypes", counterTypes)
				.queryParam("format", format)
				.when()
				.get(url);

		assertEquals(200, response.getStatusCode());
		System.out.println(response.getBody().asString());
	}

	// Проверка некорректных данных - Без access_token
	@Test
	public void testGetCountersWithInvalidToken() {
		Response response = RestAssured.given()
				.queryParam("method", method)
				.queryParam("group_id", groupId)
				.queryParam("application_id", application_id)
				.queryParam("application_key", application_key)
				.queryParam("application_secret_key", application_secret_key)
				.queryParam("counterTypes", counterTypes)
				.queryParam("format", format)
				.when()
				.get(url);

		String responseBody = response.getBody().asString();
		System.out.println("Response Body: " + responseBody);

        JsonPath jsonPath = response.jsonPath();
        assertEquals(100, jsonPath.getInt("error_code"), "Response should contain error_code 103");
        assertEquals("PARAM : Missed required parameter: access_token", jsonPath.getString("error_msg"), "Response should contain error message 'Missed session key'");
	}

	// Проверка некорректных данных - Неверный group_id
	@Test
	public void testGetCountersWithInvalidGroupId() {
		String invalidGroupId = "INVALID_GROUP_ID";

		Response response = RestAssured.given()
				.queryParam("method", method)
				.queryParam("access_token", accessToken)
				.queryParam("group_id", invalidGroupId)
				.queryParam("application_id", application_id)
				.queryParam("application_key", application_key)
				.queryParam("application_secret_key", application_secret_key)
				.queryParam("counterTypes", counterTypes)
				.queryParam("format", format)
				.when()
				.get(url);

		String responseBody = response.getBody().asString();
		System.out.println("Response Body: " + responseBody);

		JsonPath jsonPath = response.jsonPath();
		assertEquals(160, jsonPath.getInt("error_code"), "Response should contain error_code 160");
		assertTrue(jsonPath.getString("error_msg").contains("Invalid group_id"), "Response should contain error message 'Invalid group_id'");
	}

//	Проверка на высокие нагрузки (тесты производительности):
	@Test
	public void testGetCountersPerformance() {
		long startTime = System.currentTimeMillis();

		// Выполнение 100 запросов
		for (int i = 0; i < 100; i++) {
			Response response = RestAssured.given()
					.queryParam("method", method)
					.queryParam("access_token", accessToken)
					.queryParam("group_id", groupId)
					.queryParam("application_id", application_id)
					.queryParam("application_key", application_key)
					.queryParam("application_secret_key", application_secret_key)
					.queryParam("counterTypes", counterTypes)
					.queryParam("format", format)
					.when()
					.get(url);

			assertEquals(200, response.getStatusCode(), "Expected status code 200");
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Performance test duration: " + duration + "ms");
	}
}
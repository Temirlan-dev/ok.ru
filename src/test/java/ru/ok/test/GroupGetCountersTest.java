package ru.ok.test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
	public void testGetCounters() throws NoSuchAlgorithmException {
		Map<String, String> params = new HashMap<>();
		params.put("method", method);
		params.put("access_token", accessToken);
		params.put("group_id", groupId);
		params.put("application_id", application_id);
		params.put("application_key", application_key);
		params.put("counterTypes", counterTypes);
		params.put("format", format);

		// Рассчитываем session_secret_key и подпись
		String sessionSecretKey = accessToken + application_secret_key;
		String sessionSecretKeyMd5 = calculateMD5(sessionSecretKey).toLowerCase();
		String sig = calculateSignature(params, sessionSecretKeyMd5);

		Response response = RestAssured.given()
				.queryParams(params)
				.queryParam("sig", sig)
				.when()
				.get(url);

		assertEquals(200, response.getStatusCode());
		System.out.println(response.getBody().asString());
	}

	// Проверка некорректных данных - Без access_token
	@Test
	public void testGetCountersWithInvalidToken() throws NoSuchAlgorithmException {
		Map<String, String> params = new HashMap<>();
		params.put("method", method);
		params.put("group_id", groupId);
		params.put("application_id", application_id);
		params.put("application_key", application_key);
		params.put("application_secret_key", application_secret_key);
		params.put("counterTypes", counterTypes);
		params.put("format", format);

		// Устанавливаем некорректный токен
		String invalidAccessToken = "INVALID_TOKEN";
		params.put("access_token", invalidAccessToken);

		// Рассчитываем session_secret_key и подпись
		String sessionSecretKey = calculateMD5(invalidAccessToken + application_secret_key).toLowerCase();
		String sig = calculateSignature(params, sessionSecretKey);

		Response response = RestAssured.given()
				.queryParams(params)
				.queryParam("sig", sig)
				.when()
				.get(url);

		String responseBody = response.getBody().asString();
		System.out.println("Response Body: " + responseBody);

		JsonPath jsonPath = response.jsonPath();
		assertEquals(103, jsonPath.getInt("error_code"), "Response should contain error_code 103");
		assertEquals("PARAM_SESSION_KEY : Invalid session key", jsonPath.getString("error_msg"), "Response should contain error message 'Invalid session key'");
	}

	// Проверка некорректных данных - Неверный group_id
	@Test
	public void testGetCountersWithInvalidGroupId() throws NoSuchAlgorithmException {
		Map<String, String> params = new HashMap<>();
		params.put("method", method);
		params.put("access_token", accessToken);
		params.put("group_id", "INVALID_GROUP_ID"); // Некорректный group_id
		params.put("application_id", application_id);
		params.put("application_key", application_key);
		params.put("application_secret_key", application_secret_key);
		params.put("counterTypes", counterTypes);
		params.put("format", format);

		// Рассчитываем session_secret_key и подпись
		String sessionSecretKey = calculateMD5(accessToken + application_secret_key).toLowerCase();
		String sig = calculateSignature(params, sessionSecretKey);

		Response response = RestAssured.given()
				.queryParams(params)
				.queryParam("sig", sig)
				.when()
				.get(url);

		String responseBody = response.getBody().asString();
		System.out.println("Response Body: " + responseBody);

		JsonPath jsonPath = response.jsonPath();
		assertEquals(160, jsonPath.getInt("error_code"), "Response should contain error_code 160");
		assertTrue(jsonPath.getString("error_msg").contains("Invalid group_id"), "Response should contain error message 'Invalid group_id'");
	}

	//	Проверка на высокие нагрузки (тесты производительности)
	@Test
	public void testGetCountersPerformance() throws NoSuchAlgorithmException {
		Map<String, String> params = new HashMap<>();
		params.put("method", method);
		params.put("access_token", accessToken);
		params.put("group_id", groupId);
		params.put("application_id", application_id);
		params.put("application_key", application_key);
		params.put("application_secret_key", application_secret_key);
		params.put("counterTypes", counterTypes);
		params.put("format", format);

		// Рассчитываем session_secret_key и подпись
		String sessionSecretKey = calculateMD5(accessToken + application_secret_key).toLowerCase();
		String sig = calculateSignature(params, sessionSecretKey);

		long startTime = System.currentTimeMillis();

		// Выполнение 100 запросов
		for (int i = 0; i < 100; i++) {
			Response response = RestAssured.given()
					.queryParams(params)
					.queryParam("sig", sig)
					.when()
					.get(url);

			assertEquals(200, response.getStatusCode(), "Expected status code 200");
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Performance test duration: " + duration + "ms");
	}

	private String calculateSignature(Map<String, String> params, String sessionSecretKey) throws NoSuchAlgorithmException {
		// Сортируем параметры лексикографически по ключам
		String sortedParams = params.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));

		// Формируем строку для подписи
		String signatureString = sortedParams + sessionSecretKey;

		// Генерируем подпись
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(signatureString.getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString().toLowerCase();
	}

	private String calculateMD5(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(input.getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
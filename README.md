Описание
GroupGetCountersTest — это набор тестов для проверки метода group.getCounters API "Одноклассников" с использованием Java 17, Maven и библиотеки RestAssured. Тесты проверяют корректность работы API метода при различных входных данных и сценариях.

Инструменты и технологии
Java 17: Используется как язык программирования для написания тестов.
Maven: Система управления проектами и сборки для управления зависимостями и сборки проекта.
RestAssured: Библиотека для тестирования REST API, обеспечивающая удобный и понятный способ выполнения запросов и проверки ответов.
Тесты
testGetCounters:

Описание: Проверяет успешное выполнение метода group.getCounters с корректными параметрами.
Что тестирует: Проверяет, что API возвращает статус-код 200 OK и корректный ответ при использовании действительного access_token, group_id и других параметров.
Параметры:
method: group.getCounters
access_token: Действительный токен доступа.
group_id: Действительный идентификатор группы.
counterTypes: Типы счетчиков, которые нужно получить (MEMBERS, JOIN_REQUESTS).
application_id, application_key, application_secret_key: Ключи приложения.
format: Формат ответа (json).
testGetCountersWithInvalidToken:

Описание: Проверяет реакцию API на запрос с недействительным access_token.
Что тестирует: Проверяет, что API возвращает статус-код 100 и сообщение об ошибке, указывающее на отсутствие токена доступа.
Параметры:
method: group.getCounters
group_id: Действительный идентификатор группы.
application_id, application_key, application_secret_key: Ключи приложения.
counterTypes: Типы счетчиков.
format: Формат ответа (json).
access_token: Неверный токен доступа.
testGetCountersWithInvalidGroupId:

Описание: Проверяет реакцию API на запрос с неверным group_id.
Что тестирует: Проверяет, что API возвращает статус-код 160 и сообщение об ошибке, указывающее на неверный идентификатор группы.
Параметры:
method: group.getCounters
access_token: Действительный токен доступа.
group_id: Неверный идентификатор группы.
application_id, application_key, application_secret_key: Ключи приложения.
counterTypes: Типы счетчиков.
format: Формат ответа (json).

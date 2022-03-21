// Дисциплина: Java Core для тестировщиков
// Домашнее задание №6 "Работа с сетью"
// Студент: Алексей Пирогов
// Дата: 21.03.2022

import okhttp3.*;
import java.io.IOException;

// 1. С помощью http запроса получить в виде json строки погоду в Санкт-Петербурге на период времени,
// пересекающийся со следующим занятием (например, выборка погода на следующие 5 дней - подойдет).

// 2. Подобрать источник самостоятельно. Можно использовать api accuweather, порядок следующий: зарегистрироваться,
// зарегистрировать тестовое приложение для получения api ключа, найдите нужный endpoint и изучите документацию.
// Бесплатный тарифный план предполагает получение погоды не более чем на 5 дней вперед (этого достаточно для вы-
// полнения д/з).

// Примечание:
// Для корректной работы программы могут понадобиться библиотеки okkhttp-4.8.1.jar, kotlin-stdlib-1.4.0.jar,
// okio-2.8.0. Репозиторий: https://mvnrepository.com/

public class HWApp {

    public static void main(String[] args) throws IOException {

        // Подкласса HttpURLConnection, произвольный класс от URLConnection и поддерживает соединение по
        // сетевому протоколу HTTP.
        OkHttpClient okHttpClient = new OkHttpClient();

        // Создание объекта класса HttpUrl c использованием паттерна проектироания "строитель"
        // для тестирования API Яндекс.Погода:
        HttpUrl urlYandex = new HttpUrl.Builder()
                .scheme("https")                                    /* схема подключения: потокол HTTPS */
                .host("api.weather.yandex.ru")                      /* адрес хоста: в формате DNS-имени */
                .addPathSegment("v2")                               /* сегмент пути: версия API */
                .addPathSegment("forecast")                         /* сегмент пути: прогноз */
                .addQueryParameter("lat", "59.9386")    /* аргумент №1 - координата шроты для Санкт-Петербурга */
                .addQueryParameter("lon", "30.3141")    /* аргумент №2 - координата долготы для Санкт-Петербурга */
                .addQueryParameter("lang", "ru_RU")     /* аргумент №3 - язык ответа */
                .addQueryParameter("limit", "5")        /* аргумент №4 - срок прогноза от 1 до 7 дней */
                .addQueryParameter("hours", "false")    /* аргумент №5 - возворащается почасовой (true) */
                .addQueryParameter("extra", "false")    /* аргумент №6 - расширенная информация об осадках */
                .build();

        // Формирование запроса: добавление заголовка к запросу
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Yandex-API-Key", "c7aafa93-6e95-48a8-aa5d-f24ea875a8c4")
                .url(urlYandex)
                .build();

        // Вывод итогового запроса в виде строки:
        System.out.println("\nЗапрос ресурса:\t" + urlYandex + "\n");

        // Вывод ответа от сервера в виде строки:
        System.out.println("Ответ сервера:\t" + okHttpClient.newCall(request).execute() + "\n");
        System.out.println("Заголовки:\n" + okHttpClient.newCall(request).execute().headers());

        // Тело ответа от сервера:
        String responceServer = okHttpClient.newCall(request).execute().body().string();
        System.out.println("Ответ от сервера в формате JSON:\n" + responceServer);

        // Форматированный вывод ответа от сервера:
        System.out.println("\n\nОтвет от сервера в формате JSON, адаптирован для чтения:");
        char[] charBuffer = responceServer.toCharArray();
        int paddingLeft = -2;   // Переменная для управления форматированием вывода

        for (int i = 0; i < charBuffer.length; i++) {
            switch (charBuffer[i]) {
                case '{':
                    if(i != 0 && charBuffer[i-1] == ',') {
                        paddingLeft += 2;
                        paddingLeft(paddingLeft);
                        System.out.print(charBuffer[i] + "\n");
                        for (int t = 0; t < paddingLeft; t++)
                            System.out.print("\t");
                    } else {
                        System.out.print(charBuffer[i] + "\n");
                        paddingLeft += 2;
                        paddingLeft(paddingLeft);
                    }
                    break;
                case ',':
                    if (charBuffer[i+1] == '"') {
                        System.out.print(charBuffer[i] + "\n");
                        paddingLeft(paddingLeft);
                    } else if (charBuffer[i+1] == '{') {
                        System.out.print(charBuffer[i] + "\n");
                        paddingLeft(paddingLeft);
                    } else {
                        System.out.print(charBuffer[i]);
                    }
                    break;
                case '}':
                    System.out.print("\n");
                    paddingLeft(paddingLeft);
                    System.out.print(charBuffer[i]);
                    paddingLeft -= 2;
                    break;
                default:
                    System.out.print(charBuffer[i]);
                    break;
            }
        }
    }

    // Метод для вывода отступа слева, форматирования текста
    public static void paddingLeft(int paddingLeft) {
        for (int t = 0; t < paddingLeft; t++)
            System.out.print("\t");
    }

}


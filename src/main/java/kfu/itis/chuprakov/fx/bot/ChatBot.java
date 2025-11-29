package kfu.itis.chuprakov.fx.bot;

import kfu.itis.chuprakov.fx.ChatApplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatBot {
    private ChatApplication chatApplication;
    private WeatherService weatherService;
    private ExchangeService exchangeService;
    private ObjectMapper objectMapper;

    public ChatBot(ChatApplication chatApplication) {
        this.chatApplication = chatApplication;
        this.weatherService = new WeatherService();
        this.exchangeService = new ExchangeService();
        this.objectMapper = new ObjectMapper();
    }

    public void processCommand(String message, String username) {
        if (message.startsWith("/")) {
            String command = message.substring(1).toLowerCase();
            String response = executeCommand(command, username);
            chatApplication.appendMessage("Бот: " + response);
        }
    }

    private String executeCommand(String command, String username) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "list":
                return getCommandsList();
            case "weather":
                return getWeather(argument);
            case "exchange":
                return getExchangeRate(argument);
            case "quit":
                return "Возврат в главное меню...";
            case "help":
                return getHelpMessage(username);
            default:
                return "Неизвестная команда. Введите /list для списка команд";
        }
    }

    private String getCommandsList() {
        return "Доступные команды:\n" +
                "/list - список команд\n" +
                "/weather <город> - погода в городе\n" +
                "/exchange <валюта> - курс валюты к рублю\n" +
                "/quit - выход в главное меню\n" +
                "/help - справка";
    }

    private String getHelpMessage(String username) {
        return "Привет, " + username + "! Я чат-бот.\n" +
                "Используйте команды с символом / в начале.\n" +
                "Примеры:\n" +
                "/weather Москва\n" +
                "/exchange USD\n" +
                "/quit - выход в меню";
    }

    private String getWeather(String city) {
        if (city.isEmpty()) {
            return "Укажите город. Пример: /weather Москва";
        }
        return weatherService.getWeather(city);
    }

    private String getExchangeRate(String currency) {
        if (currency.isEmpty()) {
            return "Укажите валюту. Пример: /exchange USD";
        }
        return exchangeService.getExchangeRate(currency);
    }

    private class WeatherService {
        private static final String API_KEY = "de0e33285bda6ffce57ee47a6a16abc5";
        private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";

        public String getWeather(String city) {
            try {
                String urlString = String.format("%s?q=%s&appid=%s&units=metric&lang=ru",
                        API_URL, city.replace(" ", "%20"), API_KEY);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8"));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return parseWeatherResponse(response.toString(), city);
                } else {
                    return "Ошибка API: " + responseCode;
                }

            } catch (Exception e) {
                return "Ошибка: " + e.getMessage();
            }
        }

        private String parseWeatherResponse(String jsonResponse, String city) {
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);

                JsonNode main = root.get("main");
                JsonNode weatherArray = root.get("weather");
                JsonNode wind = root.get("wind");

                double tempKelvin = main.get("temp").asDouble();
                double feelsLikeKelvin = main.get("feels_like").asDouble();
                double tempCelsius = tempKelvin - 273.15;
                double feelsLikeCelsius = feelsLikeKelvin - 273.15;

                int humidity = main.get("humidity").asInt();
                int pressure = main.get("pressure").asInt();

                String description = weatherArray.get(0).get("description").asText();

                double windSpeed = 0;
                if (wind != null && wind.has("speed")) {
                    windSpeed = wind.get("speed").asDouble();
                }

                city = city.replace(city.charAt(0), city.toUpperCase().charAt(0));

                return String.format(
                        "Погода в %s: %s, %.1f°C (ощущается как %.1f°C), влажность %d%%, давление %d hPa, ветер %.1f м/с",
                        city, capitalize(description), tempCelsius, feelsLikeCelsius, humidity, pressure, windSpeed
                );

            } catch (Exception e) {
                return "Ошибка при обработке данных о погоде: " + e.getMessage();
            }
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return str;
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    private class ExchangeService {
        private static final String API_URL = "https://www.cbr-xml-daily.ru/daily_json.js";

        public String getExchangeRate(String currency) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(800);
                connection.setReadTimeout(800);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8"));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return parseExchangeResponse(response.toString(), currency);
                } else {
                    return "Ошибка при получении курса валют: " + responseCode;
                }

            } catch (Exception e) {
                return "Ошибка, попробуйте еще раз";
            }
        }

        private String parseExchangeResponse(String jsonResponse, String targetCurrency) {
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                JsonNode rates = root.get("Valute");
                String upperCurrency = targetCurrency.toUpperCase();

                if (!rates.has(upperCurrency)) {
                    StringBuilder available = new StringBuilder("Доступные валюты: ");
                    String[] popular = {"USD", "EUR", "GBP", "JPY", "CNY", "CHF", "TRY"};
                    for (String curr : popular) {
                        if (rates.has(curr)) {
                            available.append(curr).append(", ");
                        }
                    }
                    return "Валюта " + upperCurrency + " не найдена. " + available.toString();
                }

                JsonNode currencyData = rates.get(upperCurrency);
                double rate = currencyData.get("Value").asDouble();
                String name = currencyData.get("Name").asText();

                return String.format("%s (%s): %.2f RUB", name, upperCurrency, rate);

            } catch (Exception e) {
                return "Ошибка, попробуйте еще раз";
            }
        }
    }
}
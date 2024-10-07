import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class APIConverter implements Runnable {

    private double amount;
    private String fromCurrency;
    private String toCurrency;
    private final Map<String, Double> exchangeRates = new HashMap<>();

    public APIConverter(double amount, String fromCurrency, String toCurrency) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public APIConverter() {}

    @Override
    public void run() {
        fetchCurrenciesFromApi();
    }

    public void fetchCurrenciesFromApi() {
        try {
            URL url = new URL("http://localhost:5001/api/currencies");
            // type assertion
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            processApiResponse(response.toString());

            if (fromCurrency != null && toCurrency != null && amount > 0) {
                double convertedAmount = convertCurrency(amount, fromCurrency, toCurrency);
                System.out.printf("Конвертированная сумма: %.2f %s = %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении валют: " + e.getMessage());
        }
    }

    private void processApiResponse(String jsonResponse) {
        jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
        String[] currencyRates = jsonResponse.split("\\},\\{");

        for (String rateString : currencyRates) {
            rateString = rateString.replace("{", "").replace("}", "");
            String[] parts = rateString.split(",");

            String fromCurrency = "";
            String toCurrency = "";
            double rate = 0;

            for (String part : parts) {
                String[] keyValue = part.split(":");
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();

                switch (key) {
                    case "from":
                        fromCurrency = value;
                        break;
                    case "to":
                        toCurrency = value;
                        break;
                    case "rate":
                        rate = Double.parseDouble(value);
                        break;
                }
            }

            String value = fromCurrency + "_" + toCurrency;
            exchangeRates.put(value, rate);
        }
    }

    private double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        String key = fromCurrency + "_" + toCurrency;
        if (exchangeRates.containsKey(key)) {
            return amount * exchangeRates.get(key);
        } else {
            System.err.println("Курс для " + fromCurrency + " в " + toCurrency + " не найден.");
            return amount; // Возвращаем исходную сумму, если курс не найден
        }
    }

    public Set<String> getAvailableCurrencies() {
        Set<String> currencies = new HashSet<>();
        for (String key : exchangeRates.keySet()) {
            String[] parts = key.split("_");
            currencies.add(parts[0]); // from
            currencies.add(parts[1]); // to
        }
        return currencies;
    }
}

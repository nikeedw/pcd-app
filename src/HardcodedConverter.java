import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HardcodedConverter implements Runnable {

    private double amount;
    private String fromCurrency;
    private String toCurrency;
    private final Map<String, Double> exchangeRates = new HashMap<>();

    public HardcodedConverter(double amount, String fromCurrency, String toCurrency) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        initializeRates();
    }

    public HardcodedConverter() {
        initializeRates();
    }

    private void initializeRates() {
        // from => to
        exchangeRates.put("USD_EUR", 0.85);
        exchangeRates.put("EUR_USD", 1.18);
        exchangeRates.put("USD_RUB", 74.36);
        exchangeRates.put("RUB_USD", 0.013);
        exchangeRates.put("EUR_RUB", 87.50);
        exchangeRates.put("RUB_EUR", 0.011);
        exchangeRates.put("USD_MDL", 18.00);
        exchangeRates.put("MDL_USD", 0.055);
        exchangeRates.put("EUR_MDL", 21.00);
        exchangeRates.put("MDL_EUR", 0.048);
        exchangeRates.put("RUB_MDL", 0.24);
        exchangeRates.put("MDL_RUB", 4.17);
    }

    @Override
    public void run() {
        convertCurrency();
    }

    private void convertCurrency() {
        String conversionKey = fromCurrency + "_" + toCurrency;
        if (exchangeRates.containsKey(conversionKey)) {
            double rate = exchangeRates.get(conversionKey);
            double convertedAmount = amount * rate;
            System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
        } else {
            System.err.println("Неизвестная валюта для конвертации.");
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
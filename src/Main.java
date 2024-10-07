import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите режим загрузки валют:");
        System.out.println("1. Статический (hardcoded)");
        System.out.println("2. Динамический (API)");
        int choice = scanner.nextInt();

        List<String> currencyList;
        if (choice == 2) {
            System.out.println("Загрузка данных с API...");
            APIConverter apiConverter = new APIConverter();
            apiConverter.fetchCurrenciesFromApi();
            currencyList = new ArrayList<>(apiConverter.getAvailableCurrencies());
            System.out.println("Данные с API загружены.");

            ConversionData obj = handleCurrencyConversion(scanner, currencyList);
            Thread converterThread = new Thread(new APIConverter(obj.amount, obj.fromCurrency, obj.toCurrency));
            converterThread.start();
        } else if (choice == 1) {
            System.out.println("Используются hardcoded данные.");
            currencyList = new ArrayList<>(new HardcodedConverter().getAvailableCurrencies());

            ConversionData obj = handleCurrencyConversion(scanner, currencyList);
            Thread converterThread = new Thread(new HardcodedConverter(obj.amount, obj.fromCurrency, obj.toCurrency));
            converterThread.start();
        } else {
            System.err.println("Неверный выбор: " + choice);
        }
    }

    private static ConversionData handleCurrencyConversion(Scanner scanner, List<String> currencyList) {
        System.out.println("Доступные валюты:");
        for (int i = 0; i < currencyList.size(); i++) {
            System.out.println((i + 1) + ". " + currencyList.get(i));
        }

        System.out.println("Выберите исходную валюту:");
        String fromCurrency = currencyList.get(getChoice(scanner, currencyList.size()) - 1);

        double amount = 0;
        boolean validInput = false; // flag

        while (!validInput) {
            System.out.println("Введите сумму для конвертации:");
            if (scanner.hasNextDouble()) { // prevents string input
                amount = scanner.nextDouble();
                if (amount > 0) { // prevents negative numbers input
                    validInput = true;
                } else {
                    System.err.println("Ошибка: сумма должна быть положительным числом. Попробуйте снова.");
                }
            } else {
                System.err.println("Ошибка: введите число. Попробуйте снова.");
                scanner.next(); // buffer cleanup
            }
        }

        String toCurrency;
        while (true) {
            System.out.println("Выберите целевую валюту (введите номер):");
            toCurrency = currencyList.get(getChoice(scanner, currencyList.size()) - 1);

            if (!fromCurrency.equals(toCurrency)) {
                break;
            } else {
                System.err.println("Ошибка: исходная и целевая валюты не могут совпадать. Попробуйте снова.");
            }
        }

        return new ConversionData(fromCurrency, toCurrency, amount);
    }


    private static int getChoice(Scanner scanner, int max) {
        int choice;
        do {
            choice = scanner.nextInt();
        } while (choice < 1 || choice > max);
        return choice;
    }

    private static class ConversionData {
        String fromCurrency;
        String toCurrency;
        double amount;

        ConversionData(String fromCurrency, String toCurrency, double amount) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
        }
    }
}

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // ClientsProvider clientsProvider = new ClientsProvider();
        // clientsProvider.startGettingGroupsOfClients(10);

        // BlockingQueue<String> kolejkaDoPizzermana = null;
        // kolejkaDoPizzermana.put("siema");
        // Tworzenie kolejki komunikatów o maksymalnej pojemności 2
        BlockingQueue<String> kolejka = new LinkedBlockingQueue<>(2);

        // Wątek producenta
        Thread producent = new Thread(() -> {
            try {
                System.out.println("Producent: Wysyłam wiadomość 1");
                kolejka.put("Wiadomość 1"); // Dodanie pierwszej wiadomości

                System.out.println("Producent: Wysyłam wiadomość 2");
                kolejka.put("Wiadomość 2"); // Dodanie drugiej wiadomości

                System.out.println("Producent: Wysyłam wiadomość 3 (czeka na miejsce w kolejce)");
                kolejka.put("Wiadomość 3"); // Producent czeka, aż w kolejce zwolni się miejsce
                System.out.println("Producent: Wysłano wiadomość 3");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Producent: Przerwano");
            }
        });

        // Wątek konsumenta
        Thread konsument = new Thread(() -> {
            try {
                Thread.sleep(2000); // Symulacja opóźnienia w odbiorze wiadomości
                System.out.println("Konsument: Odebrano - " + kolejka.take());
                Thread.sleep(2000); // Symulacja czasu przetwarzania
                System.out.println("Konsument: Odebrano - " + kolejka.take());
                Thread.sleep(2000); // Kolejne przetwarzanie
                System.out.println("Konsument: Odebrano - " + kolejka.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Konsument: Przerwano");
            }
        });

        // Uruchamianie wątków
        producent.start();
        konsument.start();
    }
}


# Raport z projektu: Symulacja zarządzania pizzerią

## 1. Założenia projektowe
Celem projektu było stworzenie symulacji zarządzania pizzerią z wykorzystaniem wielowątkowości w Javie.
Główne elementy systemu to:

- **Goście**: Przybywają do pizzerii w losowych grupach o rozmiarach od 1 do 3 osób, w określonych interwałach czasowych.
- **Pizzerman**: Przydziela grupy do odpowiednich stolików, przestrzegając zasad:
    - Grupy o różnych rozmiarach nie mogą dzielić tego samego stolika.
    - Grupy mogą zajmować stolik tylko wtedy, gdy jest wystarczająca liczba wolnych miejsc.
- **Strażak**: Po określonym czasie symuluje sytuację alarmu pożarowego, ewakuuje klientów i zamyka pizzerię.
- **Stoliki**: Różnią się pojemnością (1-4 osoby), a ich zajętość jest dynamicznie zarządzana w trakcie symulacji.
- **Kolejka**: Obsługuje grupy oczekujące na przydzielenie stolika.

Projekt został zrealizowany zgodnie z opisem tematu, wykorzystując wymagane konstrukcje systemowe i mechanizmy synchronizacji.

---

## 2. Ogólny opis kodu

System składa się z kilku klas:

1. **app.Main**:
    - Odpowiada za inicjalizację pizzerii, stolików, kolejki i wątków (gości, pizzermana, strażaka).
    - Zarządza współdzielonymi zasobami za pomocą mechanizmów wielowątkowości, takich jak `BlockingQueue` i `AtomicBoolean`.

2. **Group**:
    - Reprezentuje grupę gości, przechowując rozmiar grupy oraz czas przybycia.
    - Zawiera metody do generowania losowych rozmiarów grup oraz porównywania ich wielkości.

3. **Table**:
    - Reprezentuje stolik w pizzerii, przechowując informacje o jego pojemności, zajętości i aktualnie przypisanych grupach.

4. **Pizzeria**:
    - Zarządza logiką usuwania grup, które spędziły więcej czasu przy stoliku, niż jest to dozwolone.

5. **Managers.FileManager**:
    - Obsługuje zapis i odczyt stanu stolików do/z pliku.
    - Synchronizuje dostęp do pliku za pomocą semaforów.

6. **gui.GUI**:
    - Centralizuje wyświetlanie komunikatów w konsoli, ułatwiając zarządzanie wyjściem.

---

## 3. Co udało się zrobić

- Zaimplementowano wszystkie wymagane funkcjonalności:
    - Obsługę gości, pizzermana i strażaka w osobnych wątkach.
    - Synchronizację kolejki gości za pomocą `BlockingQueue`.
    - Obsługę stolików i przestrzeganie zasad przydzielania grup.
    - Mechanizm ewakuacji i resetowania stanu pizzerii w przypadku alarmu pożarowego.
- Wykorzystano mechanizmy synchronizacji (`Semaphore`, `BlockingQueue`, `AtomicBoolean`).
- Zaimplementowano zapisywanie stanu stolików do pliku oraz odczyt stanu po restarcie aplikacji.

---

## 4. Problemy napotkane podczas realizacji

- **Synchronizacja dostępu do pliku**: Początkowe błędy w obsłudze wielowątkowego zapisu i odczytu plików zostały rozwiązane za pomocą semaforów.
- **Obsługa wyjątków w wątkach**: Konieczne było dokładne zarządzanie przerwaniem wątków i obsługą wyjątków `InterruptedException`.
- **Testowanie scenariusza alarmu pożarowego**: Trudności z symulacją dynamicznego zachowania wątków w momencie ustawienia flagi `isFireAlarmTriggered`.

---

## 5. Dodane elementy specjalne

- Wprowadzenie klasy `gui.GUI` w celu centralizacji wyświetlania komunikatów.
- Mechanizm dynamicznego przydzielania czasu pojawiania się grup (`Thread.sleep` z losowymi interwałami).
- Możliwość dynamicznego ustawiania maksymalnego czasu, po którym grupa jest usuwana ze stolika.

---

## 6. Problemy z testami

- W scenariuszach o dużej liczbie gości występowały sytuacje, w których kolejka `BlockingQueue` była pełna, co powodowało blokowanie wątku `guestsThread`. Problem ten został rozwiązany przez kontrolowanie flagi alarmu przed dodaniem nowej grupy do kolejki.
- Testowanie ewakuacji wymagało dodania komunikatów diagnostycznych w wątku strażaka.

---

## 7. Linki do istotnych fragmentów kodu

- **Tworzenie i obsługa plików**:
    - [Managers.FileManager: `writeTablesToFile`](https://github.com/TwojeRepozytorium/FileManager.java#L45)
    - [Managers.FileManager: `readTablesFromFile`](https://github.com/TwojeRepozytorium/FileManager.java#L25)

- **Tworzenie procesów i wątków**:
    - [app.Main: wątki gości, pizzermana, strażaka](https://github.com/TwojeRepozytorium/Main.java#L15)

- **Synchronizacja wątków**:
    - [Managers.FileManager: użycie `Semaphore`](https://github.com/TwojeRepozytorium/FileManager.java#L10)
    - [app.Main: użycie `AtomicBoolean`](https://github.com/TwojeRepozytorium/Main.java#L13)

- **Obsługa sygnałów i stanu awaryjnego**:
    - [Firefighter: obsługa alarmu pożarowego](https://github.com/TwojeRepozytorium/Main.java#L67)

---

## 8. Ocena zgodności z wymaganiami

- **Zgodność z opisem tematu**: 10%  
  Wszystkie wymagane funkcjonalności zostały zaimplementowane.

- **Poprawność funkcjonalna**: 20%  
  Przeprowadzono testy w scenariuszach normalnego działania i alarmu pożarowego. System działa poprawnie, bez zakleszczeń i błędów synchronizacji.

- **Wykorzystanie konstrukcji systemowych**: 20%  
  W projekcie wykorzystano następujące konstrukcje:
    1. Obsługa plików (`Managers.FileManager`).
    2. Tworzenie i obsługa wątków (`Thread`, `Runnable`).
    3. Synchronizacja wątków (`Semaphore`, `BlockingQueue`, `AtomicBoolean`).
    4. Obsługa sygnałów i sytuacji awaryjnych (`isFireAlarmTriggered`).

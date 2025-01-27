# Raport Projektu – System Wieloprocesowy i Wielowątkowy

## 1. Założenia projektowe

Projekt stanowi przykład **niescentralizowanego** systemu, w którym kilka procesów współdziała przy pomocy gniazd (socketów). Zamiast jednej aplikacji, która zarządza wszystkimi działaniami, każdy proces (np. `Guests`, `Pizzerman`, `FireFighter`, `PizzeriaServer`) jest uruchamiany osobno, a komunikacja między nimi odbywa się asynchronicznie, po sieci (lub lokalnie na komputerze) przy użyciu protokołu TCP.

**Cele projektu** obejmują:
- Implementację prostego serwera nasłuchującego sygnałów alarmowych.
- Uruchamianie kilku procesów-klientów (`Guests`, `Pizzerman`, `FireFighter`.
- Komunikację między procesami z użyciem gniazd.
- Obsługę stanu awaryjnego (pożaru), gdzie proces `FireFighter` wyzwala alarm, a serwer powiadamia pozostałe procesy o konieczności ewakuacji.

## 2. Ogólny opis kodu

- **PizzeriaServer** – tworzy gniazdo serwera (socket), nasłuchuje na określonym porcie i uruchamia wątki (`ClientHandler`) dla każdego nowego połączenia. Odbiera komunikaty, takie jak „fire”.
- **Guests** – symuluje przychodzących gości ustawiaqjących sie w kolejce przed lokalem. Łączy się z serwerem, odbiera komunikat „evacuation” i kończy pracę po otrzymaniu sygnału pożaru lub po zamknięciu gniazda przez serwer.
- **Pizzerman** – symuluje pracę pizzermana, tworzy wątki obsługujące gości (np. `runNewThreadForEachGuest`), zarządza zasobami stołów i kolejkami. Reaguje na sygnał „evacuation”.
- **FireFighter** – wysyła sygnał „fire”, który serwer rozpoznaje i ustawia stan awaryjny.

**Tworzenie procesów w Javie** nie jest tak proste jak w C/C++, gdzie mamy wbudowane `fork()`, `exec()`, `wait()` itp. W naszym projekcie oddzielne procesy uruchamiane są jako osobne aplikacje (każda ma metodę `main`). Procesy komunikują się przez sieć z użyciem `socket()`, `accept()`, `connect()`, `readLine()`, `write()` zamiast „typowych” wywołań systemowych `fork()` czy `exec()`.

**Komunikacja między procesami** w tym projekcie opiera się na gniazdach:

- Serwer `PizzeriaServer` nasłuchuje na porcie `9876`.
- Procesy klienckie (`Guests`, `Pizzerman`, `FireFighter`) łączą się z adresem `localhost:9876`.
- Proces `FireFighter` wysyła sygnał „fire”, serwer rozpoznaje go i wysyła do wszystkich aktualnie połączonych klientów komunikat „evacuation”.
- Pozostałe procesy `Guests`, `Pizzerman` po otrzymaniu „evacuation” zakańczają swoje pętle i się wyłączają.

## 3. Wymagane przypadki użycia

1. **Standardowa praca**:
    - `Guests` i `Pizzerman` łączą się z serwerem wykonując swoją pracę, nasłuchując sygnału.
2. **Sygnał awaryjny (pożar)**:
    - `FireFighter` wysyła wiadomość „fire”. Serwer ustawia stan awaryjny i odsyła „evacuation” do klientów, po czym kończy działanie. Klienci także się wyłączają.

Wprowadzane dane (np. rozmiary stołów, liczba gości) mogą być kontrolowane w kodzie. Jeśli wartości są błędne, wyświetlamy komunikaty ostrzegawcze (w Javie zamiast `perror()` używamy typowo wyjątków i `System.err.println()`).

## 4. Obsługa błędów i uprawnień

- W Javie zamiast `perror()` i zmiennej `errno` stosuje się konstrukcje wyjątków `try/catch`.
- Byłoby możliwe użycie `FilePermission` czy `SocketPermission` – jednakże w typowej aplikacji uruchamianej w trybie zwykłego użytkownika minimalne prawa są domyślnie zapewnione przez system.
- Wszelkie pliki tymczasowe (np. `shared_memory.txt`) są czyszczone i zwalniane po zakończeniu działania aplikacji (metoda `clearFile()`).

## 5. Z czym były problemy i co się udało

- **Problemy**:
    - Konieczność synchronizowania wątków w `Pizzerman` (np. przerwanie wątków gości) tak, aby proces rzeczywiście kończył się, gdy nadchodzi sygnał ewakuacji.
    - Kończenie wszystkich wątków w procesach by te mogły zakończyć swoje działanie
    - kończenie życia obiektów gości opuszczających pizzerię po zadanym czasie i kończenie ich wątków.
- **Udało się**:
    - Rozwiązać komunikację przez `socket()`, `bind()`, `accept()`, `connect()` w Javie.
    - Zaimplementować sytuację alarmową: „fire” → „evacuation”.
    - Zaimplementować wielowątkowość (dla gości) i prostą synchronizację kolejki (blokady w pliku i wątkach).
    - Proces, w którym dane tekstowe (np. linie w pliku) są przekształcane w obiekty w pamięci (Group, Table, itp.), przypomina mapowanie relacji (np. z tabel w bazie danych) na obiekty w aplikacji.
    - Pliki współdzielone działa jako centralne miejsce przechowywania stanu.

## 6. Dodatkowe elementy i testy

- **Element specjalny**: wykorzystanie pliku `shared_memory.txt` (przez `FileLock`) do współdzielenia kolejek pomiędzy różnymi procesami (podejście zbliżone do pamięci dzielonej, ale w Javie).

- **Testy**:
- Manualne uruchamianie serwera, gości, pizzermana i strażaka w różnej kolejności. Najpierw jednak musi zostać uruchomiony proces `PizzeriaServer`
- Sprawdzanie, czy w każdym przypadku alarm kończy wszystkie procesy.
- Process `Guests`  oddaje grup do kolejki o zadanej wartości
- Proess `Guests` nie dodaje grup do kolejki gdy ta jest już pełna.
- Procesy `Pizzerman`,`Guests` dodają i wyciągają grupy z kolejki współdzielając plik nad którym synchronizację sprawuje `FileLock`
- Proces `Pizzerman` nie dodaje grupy do stolika gdy jej liczebnośc jest większa niż capacity stolika
- Proces `Pizzerman` nie dodaje grupy do stolika gdy siedza przy nim grupy o innej liczebnosći
- Proces `Pizzerman` dodaje grupę do stolika usuwając ją z kolejki otwierając drogę procesowi`Guests` dania kolejnej grupy do kolejki
- Proces `Pizzerman` sprawia że grupy opuszczją salę po zadanym czasie, ustawialnym w kodzie, również kończy wątki Gości


## 7. Linki do istotnych fragmentów kodu

Poniżej kilka przykładów linków do repozytorium GitHub, prezentujących najważniejsze funkcje systemowe w projekcie:
- **Obsługa gniazd (socketów)**:
    - [`PizzeriaServer.main`](https://github.com/TwojeRepozytorium/PizzeriaServer.java#L20)  
      Uruchamia serwer nasłuchujący na porcie 9876. Akceptuje połączenia przychodzące, tworząc wątki do obsługi klientów.
    - [`Guests.main`](https://github.com/TwojeRepozytorium/Guests.java#L25)  
      Proces pisiadający dwa wątki, jeden dodaje grupy gości do kolejki ,drugi łączy się z serwerem jako klient i odbiera komunikaty, takie jak „evacuation”.

- **Tworzenie i obsługa plików**:
    - [`GroupFileManager.writeQueueToFile`](https://github.com/TwojeRepozytorium/GroupFileManager.java#L25)  
      Zapisuje stan kolejki grup do pliku `shared_memory.txt`, używając blokady pliku (`FileLock`). Funkcja czyści zawartość pliku i zapisuje każdą grupę w formacie tekstowym. Synchronizacja zapisu pozwala na współdzielenie danych przez wiele procesów.
    - [`GroupFileManager.readGroupsFromFile`](https://github.com/TwojeRepozytorium/GroupFileManager.java#L45)  
      Odczytuje dane grup z pliku `shared_memory.txt`, tworząc z nich kolejkę `BlockingQueue<Group>`. Blokada współdzielona (`FileLock`) zapewnia bezpieczeństwo danych podczas odczytu.

- **Tworzenie procesów i wątków**:
    - [`Pizzerman.runNewThreadForEachGuest`](https://github.com/TwojeRepozytorium/Pizzerman.java#L105)  
      Tworzy nowe wątki dla każdego gościa w grupie, które działają do momentu przerwania (`Thread.interrupt()`). Wątki reprezentują obsługę klientów przez pizzermana.
    - [`PizzeriaServer.ClientHandler.run`](https://github.com/TwojeRepozytorium/PizzeriaServer.java#L60)  
      Obsługuje pojedyncze połączenie przychodzące, nasłuchując komunikaty od klienta. Odpowiada za reakcje na „fire” i „evacuation”.

- **Synchronizacja wątków**:
    - [`FileManager.createTablesToFile`](https://github.com/TwojeRepozytorium/FileManager.java#L30)  
      Tworzy plik zawierający dane o dostępnych stolikach w pizzerii. Używa mechanizmów plikowych w celu zachowania integralności danych.
    - [`GroupFileManager.clearFile`](https://github.com/TwojeRepozytorium/GroupFileManager.java#L65)  
      Czyści zawartość pliku `shared_memory.txt`, blokując go podczas operacji za pomocą `FileLock`.

- **Obsługa sygnałów i stanu awaryjnego**:
    - [`FireFighter.main`](https://github.com/TwojeRepozytorium/FireFighter.java#L20)  
      Proces symulujący strażaka, który łączy się z serwerem i wysyła sygnał „fire”. Sygnał uruchamia globalny stan awaryjny, na który reagują inne procesy.
    - [`Guests.main`](https://github.com/TwojeRepozytorium/Guests.java#L15)  
      Goście nasłuchują sygnałów z serwera, takich jak „evacuation”. Po otrzymaniu sygnału zamykają swoje połączenie i kończą pracę.


---

*Linki w powyższych przykładach są hipotetyczne – zastąp je rzeczywistymi linkami do repozytorium GitHub.*

---

## 8. Opis ważniejszych funkcji

Przykłady krótkich opisów (pomijamy gettery i settery):

1. **`PizzeriaServer.main(String[] args)`**  
   Uruchamia gniazdo serwera (`ServerSocket`) na porcie 9876. W pętli `while (!isFire) { ... }` akceptuje nowych klientów. W razie sygnału „fire” wyłącza się, wysyłając „evacuation”.

2. **`PizzeriaServer.ClientHandler.run()`**  
   Obsługuje pojedyncze połączenie klienckie: czyta linie w pętli i reaguje na komunikaty („fire” → ustawia isFire = true; w odpowiedzi wysyła „evacuation”).

3. **`Guests.main(String[] args)`**  
   Łączy się z serwerem, co pewien czas wysyła dane (np. zamówienia). Nasłuchuje komunikatu „evacuation” lub rozłączenia, co kończy pętlę główną.

4. **`Pizzerman.runNewThreadForEachGuest(Group group)`**  
   Tworzy nowe wątki (po jednym na gościa), które działają w pętli i czekają do momentu przerwania (`Thread.interrupt()`).

5. **`FireFighter.main(String[] args)`**  
   Jednorazowy proces: łączy się z serwerem i wysyła komunikat „fire”. Następnie kończy działanie.

6. **`GroupFileManager.writeQueueToFile(BlockingQueue<Group> queue)`**  
   Otwiera plik `shared_memory.txt` w trybie rw, blokuje go (`FileLock`), czyści zawartość i zapisuje aktualny stan kolejki. Pozwala to innym procesom odczytać kolejkę ze wspólnego pliku.

7. **`GroupFileManager.readGroupsFromFile()`**  
   Otwiera ten sam plik w trybie „read-only” i przy użyciu blokady współdzielonej (`lock(0, Long.MAX_VALUE, true)`) odczytuje wszystkie linie, tworząc obiekty `Group`.

8. **`FileManager.createTablesToFile(int[] quantityOfTables)`**  
   Tworzy plik z informacjami o stolikach (ich liczba, pojemność). Używa `open()`, `write()`, `close()` (zastąpione w Javie przez `FileWriter`/`RandomAccessFile`).

---

## 9. Zakończenie i wnioski

- **Usuwanie struktur**: Po zamknięciu aplikacji pliki tymczasowe są usuwane (lub czyszczone metodą `clearFile()`), a wątki i sockety – zamykane.
- **Potencjalne usprawnienia**:
    - Dodać weryfikację danych wejściowych (np. maksymalna liczba procesów).
    - Wykorzystać systemowe wywołania w stylu `fork()`, `exec()` (w C/C++), by w pełni spełnić wymóg tworzenia procesów z poziomu kodu. W Javie jednak w większości procesy uruchamiamy osobno.
    - Rozbudować obsługę błędów i raportowanie wyjątków (np. `try-catch` z wypisaniem `System.err.println(e.getMessage())`).

**Całość** projektu spełnia główny cel: prezentację wieloprocesowej, wielowątkowej aplikacji, w której sygnał pożaru może natychmiast przerwać pracę wszystkich procesów i ich wątków.


# Specifikacija Projekta: Rent-a-Car Informacioni Sistem

Informacioni sistem za rent-a-car agenciju realizovan u programskom jeziku **Java (verzija 17)**, u skladu sa principima objektno-orijentisanog programiranja (OOP).

---

## 1. Korisnici sistema i Autentifikacija

Sistem podržava tri tipa korisnika:
*   **Administrator**:
    *   Puni CRUD pristup svim entitetima u sistemu.
    *   Registracija novih zaposlenih (Agenti i Administratori).
    *   Uvid u sve zaposlene, prihode i rashode agencije za izabrani period.
    *   Konfigurisanje parametara sistema (npr. podrazumevano trajanje najma, cenovnici).
*   **Agent**:
    *   Registracija novih klijenata i odobravanje zahteva za obnovu pretplate.
    *   Rad sa rezervacijama: odobravanje, odbijanje, evidentiranje preuzimanja (izdavanje) i vraćanja vozila.
    *   Uvid u raspoloživost vozila, dnevna izdavanja, rezervacije, i trenutni status vozila (slobodna/izdata).
*   **Klijent**:
    *   Kreiranje naloga isključivo posredstvom Agenta (koristi se email kao korisničko ime).
    *   Pregled dostupnih modela vozila za izabrane datume.
    *   Kreiranje zahteva za rezervaciju (nakon uplate/obnove pretplate).
    *   Pregled sopstvenih rezervacija, njihovih statusa i potrošenih iznosa (uključujući dodatne usluge i kazne).
    *   Mogućnost otkazivanja rezervacije (uz penal: zabrana rezervisanja u narednih 24h).

### Zajednički i specifični podaci o korisnicima
*   **Svi korisnici**: ime, prezime, pol, datum rođenja, telefon, adresa, korisničko ime (e-mail za klijente) i lozinka.
*   **Klijent**: datum izdavanja vozačke dozvole, opciono posebna kategorija (`STUDENT`, `PENZIONER`, `FIRMA` - donosi popust).
    *   *Ograničenje*: Klijent ne može rezervisati vozilo ako ima vozačku dozvolu kraće od 2 godine.
*   **Zaposleni (Admin i Agent)**: nivo stručne spreme, godine radnog staža, plata.
    *   *Obračun plate*: `osnova * (koeficijent_stručne_spreme + 0.004 * godine_staza)`.

---

## 2. Poslovna pravila i Funkcionisanje sistema

### Rezervacije i Izdavanje
1.  **Pregled dostupnosti**: Klijent proverava dostupnost po *modelu* vozila. Ako u traženom periodu postoji bar jedan fizički primerak tog modela koji je slobodan, klijent može kreirati rezervaciju.
2.  **Kreiranje rezervacije**: Rezervaciju klijent može kreirati za sutrašnji datum ili bilo koji datum u budućnosti.
3.  **Statusi rezervacije**: `NA ČEKANJU`, `POTVRĐENA`, `ODBIJENA`, `OTKAZANA`.
4.  **Proces odobravanja**:
    *   Inicijalni status je `NA ČEKANJU`.
    *   Agent potvrđuje ili odbija rezervaciju.
    *   Prilikom potvrđivanja, sistem proverava da li postoji slobodan primerak tog modela u traženom periodu. Ako nema slobodnih primeraka, sistem prikazuje grešku agentu.
    *   Rezervacije koje ne budu potvrđene do datuma početka se automatski odbijaju (ističu).
5.  **Preuzimanje (Izdavanje)**:
    *   Kada klijent dođe, agent pokreće izdavanje.
    *   Agent bira konkretan slobodan primerak (sa registarskom oznakom), dodeljuje ga rezervaciji, upisuje početnu kilometražu i postavlja status vozila na `IZDATO`.
    *   Vozilo mora biti u statusu `DOSTUPNO` pre izdavanja.
6.  **Vraćanje**:
    *   Agent evidentira povratak vozila, upisuje krajnju kilometražu i menja status vozila na `DOSTUPNO`.
    *   Ako klijent zakasni sa vraćanjem, naplaćuje se kazna definisana cenovnikom.
7.  **Otkazivanje i penalizacija**:
    *   Klijent može otkazati rezervaciju (status postaje `OTKAZANA`).
    *   U slučaju otkazivanja ili nepojavljivanja na preuzimanju (što takođe rezultira statusom `OTKAZANA`), klijent dobija zabranu rezervacije drugih vozila u narednih 24 sata.
    *   Ako agent odbije rezervaciju (`ODBIJENA`), klijent nema zabranu i može odmah pokušati novu rezervaciju.

### Dodatne usluge
Klijent (prilikom rezervacije) ili Agent (prilikom izdavanja, na usmeni zahtev klijenta) mogu dodati sledeće usluge:
*   **GPS navigacija, dečije sedište, dodatno osiguranje**: Naplaćuju se jednokratno po najmu prema važećem cenovniku.
*   **Produženo korišćenje**: Naplaćuje se po danu ukoliko najam traje duže od podrazumevanog broja dana (podrazumevano 3 dana, admin može menjati). *Primer: Za najam od 5 dana, plaća se produženo korišćenje za 2 dana.*

### Cenovnik
*   Definiše ga Administrator.
*   Sadrži: cenu godišnje pretplate, popuste za kategorije klijenata, dnevnu cenu najma po kategoriji vozila, cene dodatnih usluga i iznose kazni za kašnjenje.
*   Svaka stavka ima definisan **datum početka i kraja važenja**.
*   **Pravilo cene**: Cena se zaključava u trenutku kreiranja rezervacije prema tada važećem cenovniku. Kasnije promene cenovnika ne utiču na već napravljene rezervacije. Dodatne usluge dodate na izdavanju računaju se po cenovniku važećem u trenutku izdavanja.

### Pretplate
*   Svaki klijent mora imati aktivnu pretplatu da bi rezervisao vozilo.
*   Zahtev za obnovu podnosi klijent, a odobrava agent.
*   *Ograničenje*: Ako je klijent više od 5 puta zadržao vozilo duže od roka za vraćanje, zahtev za obnovu pretplate se **automatski odbija**.

---

## 3. Tehnički zahtevi i Entiteti

### Čuvanje podataka
*   Podaci se čuvaju u **tekstualnom (ljudski čitljivom) formatu**, na primer u `.csv` fajlovima.
*   Predefinisane vrednosti (statusi, kategorije, nivoi spreme) moraju biti implementirani kroz **Java Enumeracije (enum)**.

### Osnovni entiteti (smernice):
1.  `Zaposleni` (Administrator, Agent)
2.  `Klijent`
3.  `Pretplata`
4.  `Rezervacija vozila`
5.  `Izdavanje vozila`
6.  `Kategorija vozila` (`ECONOMY`, `STANDARD`, `FAMILY`, `LUXURY`)
7.  `Model vozila`
8.  `Vozilo` (fizički primerak sa registracijom i statusom)
9.  `Dodatna usluga`
10. `Cenovnik`

---

## 4. Ocenjivanje i Nivoi implementacije

Maksimalan broj bodova je **30**. Bodovi se dodeljuju na osnovu nivoa implementacije:

### Nivo 1: Do 12 bodova (Ocena 6)
*   **Konzolni korisnički interfejs**.
*   Podržane sve osnovne funkcionalnosti i poslovna pravila.
*   Za čuvanje podataka dozvoljena je i serijalizacija objekata.
*   **Izveštaji (za izabrani opseg datuma)**:
    1.  *Izdavanja*: Broj izdatih vozila po svakom agentu.
    2.  *Rezervacije*: Broj potvrđenih, odbijenih i otkazanih rezervacija.
    3.  *Modeli vozila*: Prikaz modela i kategorije, uz ukupan broj iznajmljivanja i rezervacija tog modela.
    4.  *Prihodi i rashodi*: Uplaćene pretplate, najmovi, dodatne usluge, naplaćene kazne (prihod) naspram plata zaposlenih (rashod).

### Nivo 2: Do 20 bodova
*   Sve funkcionalnosti iz Nivoa 1, ali realizovane kroz **grafički korisnički interfejs (Java Swing)**.

### Nivo 3: Preko 20 bodova (Maksimalno 30)
*   **Java Swing GUI** interfejs.
*   **Jedinični testovi (Unit Tests)**: Pokrivenost za sve *menadžer klase* (biznis logiku/kontrolere), osim za čiste entitete i GUI.
*   **Napredno filtriranje**: Pretraga dostupnih vozila po nazivu modela, proizvođaču, kategoriji i raspoloživom opsegu datuma.
*   **Grafički prikazi (Charts)** - pomoću Swing kompatibilne biblioteke (npr. XChart):
    1.  *Prihodi po kategoriji klijenta*: Prikaz prihoda (pretplate, najmovi, usluge, kazne) za prethodnih 12 meseci po kategorijama i ukupno.
    2.  *Opterećenje agenata*: Broj obrađenih rezervacija po agentu u poslednjih 30 dana.
    3.  *Statusi rezervacija*: Kružni dijagram (pie chart) svih rezervacija kreiranih u poslednjih 30 dana po statusu.

---

## 5. Važne Napomene i Pravila

*   **Samostalna izrada**: Svaki student samostalno izrađuje svoje rešenje. Ukoliko postoje dva ili više sličnih rešenja (plagijat), svi uključeni studenti dobijaju **0 poena** i zabranu predavanja projekta.
*   **Tehnologije**: Rešenje se razvija u **Javi 17**, a grafički korisnički interfejs (GUI) isključivo upotrebom **Java Swing** biblioteke.
*   **Dodatne biblioteke**: Dozvoljeno je korišćenje eksternih biblioteka i tehnologija koje nisu deo zvaničnog kursa, ali isključivo uz **prethodnu dozvolu predmetnog asistenta ili profesora**.
*   **Bodovanje**:
    *   Maksimalan broj bodova je **30**.
    *   Broj bodova zavisi od demonstracije rešenja, odgovora na pitanja asistenata, testiranja funkcionalnih zahteva i samog kvaliteta koda.
    *   Maksimalno **12 bodova** za konzolnu aplikaciju.
    *   Maksimalno **20 bodova** za GUI aplikaciju bez jediničnih testova.
    *   Preko **20 bodova** zahteva i GUI i jedinične testove.
*   **Čuvanje podataka**: Podaci se čuvaju u **tekstualnom (ljudski čitljivom) formatu** (npr. `.csv` fajlovi). Za konzolnu verziju (do 12 bodova) se može koristiti i serijalizacija objekata.
*   **Struktura koda**: Za podatke sa predefinisanim skupom vrednosti moraju se koristiti **Java enumeracije (enum)**.
*   **Predaja**: Obaveštenja o projektu i predaja rešenja vrše se preko **Canvas** platforme. Za sve nejasnoće konsultovati se sa predmetnim asistentom.


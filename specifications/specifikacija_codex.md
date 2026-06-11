# Specifikacija Projekta - Codex verzija

Izvor: `Projektni zadatak OOP1 2026.pdf`

PDF metapodaci iz dokumenta:
- Predmet: Objektno orijentisano programiranje 1
- Naziv: Specifikacija projektnog zadatka 2025/2026
- Poslednja izmena: 23.05.2026.
- Tekst izvučen alatom `pdftotext` iz paketa `poppler`

Ovaj dokument je radna Codex verzija specifikacije. Služi kao precizna kontrolna lista tokom mentorstva i code review-a. Ako postoji neslaganje između ovog dokumenta i zvaničnog PDF-a, zvanični PDF ima prednost.

---

## 1. Cilj projekta

Potrebno je implementirati informacioni sistem za rad rent-a-car agencije u programskom jeziku Java.

Sistem može biti realizovan kao:
- konzolna aplikacija
- GUI aplikacija korišćenjem Java Swing biblioteke

Student samostalno osmišljava i implementira arhitekturu projekta u skladu sa OOP principima.

---

## 2. Tipovi korisnika

Sistem vodi evidenciju o tri tipa korisnika:
- Administrator
- Agent
- Klijent

Sva tri tipa korisnika mogu da se prijave i odjave iz sistema, ali imaju različita prava pristupa.

### Administrator

Administrator:
- ima pristup svim entitetima
- može da izvrši CRUD operacije nad svim entitetima
- može da registruje agente
- može da registruje administratore
- ima uvid u sve zaposlene
- ima uvid u prihode i rashode za izabrani period
- podešava podrazumevano trajanje najma vozila

Podrazumevano trajanje najma vozila je 3 dana.

### Agent

Agent:
- potvrđuje ili odbija rezervacije
- ima uvid u raspoloživost vozila
- vidi kada su vozila slobodna ili zauzeta
- vrši izdavanje vozila
- prima vraćena vozila
- registruje nove klijente
- obrađuje obnovu pretplata klijenata
- vidi dnevna izdavanja i rezervacije
- vidi dostupna i izdata vozila

### Klijent

Klijent:
- registruje se u sistem preko agenta pri učlanjenju u agenciju
- koristi e-mail adresu kao korisničko ime
- može da pregleda dostupna vozila
- posle uplate pretplate može da napravi zahtev za rezervaciju
- vidi svoje rezervacije, statuse i potrošene iznose
- može da otkaže svoju rezervaciju

Samostalna registracija korisnika nije moguća.

---

## 3. Podaci o korisnicima

O svim korisnicima se evidentira:
- ime
- prezime
- pol
- datum rođenja
- telefon
- adresa
- korisničko ime
- lozinka

### Dodatni podaci o klijentu

O klijentu se dodatno evidentira:
- datum izdavanja vozačke dozvole
- opciona posebna kategorija klijenta

Klijent ne sme da rezerviše vozilo ako poseduje vozačku dozvolu kraće od 2 godine.

Posebne kategorije klijenata navedene u PDF-u:
- STUDENT
- PENZIONER
- FIRMA

Lista kategorija može se proširiti po potrebi. Ako klijent pripada odgovarajućoj kategoriji, može dobiti popust prilikom iznajmljivanja vozila.

### Dodatni podaci o zaposlenima

Zaposleni imaju dodatne podatke:
- nivo stručne spreme
- staž
- plata

Formula za platu:

```text
osnova * (koeficijent + 0.004 * godine_staza)
```

---

## 4. Vozila i modeli

Agencija raspolaže sa više fizičkih primeraka istog modela vozila.

Primer: agencija može imati pet primeraka modela `VW Golf 7`, svaki sa svojom registarskom oznakom.

Pravilo:
- klijent rezerviše model vozila
- agent prilikom izdavanja bira konkretan slobodan fizički primerak

Modeli vozila se grupišu u kategorije vozila.

Kategorije navedene u PDF-u:
- ECONOMY
- STANDARD
- FAMILY
- LUXURY

Lista se može modifikovati po potrebi.

---

## 5. Rezervacije

Klijent pregleda dostupnost vozila za željene datume.

Dostupnost se proverava po modelu vozila. Ako postoji bar jedan fizički primerak tog modela koji je dostupan u traženom periodu, klijent može da napravi zahtev za rezervaciju.

Rezervaciju je moguće napraviti:
- za sutrašnji datum
- za kasniji izabrani datum

Statusi rezervacije iz PDF-a:
- NA ČEKANJU
- POTVRĐENA
- ODBIJENA
- OTKAZANA

Inicijalni status rezervacije je `NA ČEKANJU`.

Agent zatim rezervaciju:
- potvrđuje, čime status postaje `POTVRĐENA`
- odbija, čime status postaje `ODBIJENA`

Prilikom potvrđivanja rezervacije, aplikacija mora proveriti da li postoji bar jedan primerak traženog modela vozila slobodan u periodu od datuma preuzimanja do datuma vraćanja.

Ako nema dostupnog vozila, agentu se prikazuje poruka greške.

Rezervacije koje nisu prihvaćene do datuma njihovog početka automatski se odbijaju kao istekle.

---

## 6. Izdavanje i vraćanje vozila

Kada klijent dođe da preuzme rezervisano vozilo, agent pokreće proces izdavanja.

Agent tada:
- bira slobodno vozilo rezervisanog modela
- dodeljuje konkretno vozilo rezervaciji
- evidentira kilometražu pri preuzimanju
- menja status vozila na `IZDATO`

Time se formalno završava izdavanje.

Kada klijent vraća vozilo, agent:
- evidentira kilometražu pri vraćanju
- menja status vozila na `DOSTUPNO`

Agent ne može izvršiti novo izdavanje primerka vozila dok status tog vozila nije `DOSTUPNO`.

---

## 7. Otkazivanje, nepojavljivanje i zabrana

Klijent može da otkaže svoju rezervaciju. Tada status postaje `OTKAZANA`.

Posledica otkazivanja:
- klijent ne može da rezerviše drugo vozilo naredna 24 sata

Ako agent odbije rezervaciju, status je `ODBIJENA` i klijent može odmah da pokuša rezervaciju drugog dostupnog vozila.

Ako se klijent ne pojavi da preuzme rezervisano vozilo:
- rezervacija dobija status `OTKAZANA`
- vozilo može biti rezervisano od strane drugog korisnika u tom terminu
- za klijenta važi zabrana nove rezervacije naredna 24 sata

---

## 8. Dodatne usluge

Dodatne usluge mogu se dodati:
- prilikom kreiranja rezervacije od strane klijenta
- prilikom izdavanja od strane agenta, ako klijent usmeno zatraži

Usluge navedene u PDF-u:
- GPS navigacija
- dečije sedište
- dodatno osiguranje
- produženo korišćenje

GPS navigacija, dečije sedište i dodatno osiguranje naplaćuju se jednokratno po izdavanju.

Produženo korišćenje se naplaćuje po danu. Koristi se kada klijent želi da koristi vozilo duže od predefinisanog broja dana.

Primer iz PDF-a:
- podrazumevano trajanje najma je 3 dana
- klijent želi vozilo 5 dana
- plaća produženo korišćenje za 2 dana

Ako klijent ne vrati vozilo u zadatom roku, plaća kaznu.

---

## 9. Cenovnik

Cenovnik definiše administrator.

Cenovnik mora obuhvatiti:
- cenu godišnje pretplate
- popuste za posebne kategorije klijenata
- cenu najma po danu za svaku kategoriju vozila
- cenu za svaku dodatnu uslugu
- iznose kazni za kašnjenje

Svaka cena ima:
- datum početka važenja
- datum kraja važenja

Prilikom izdavanja klijentu se obračunavaju:
- cena najma
- cena odabranih dodatnih usluga

Kazne za kašnjenje naplaćuju se pri vraćanju vozila.

Cena se računa u trenutku kreiranja rezervacije. Tada se obračunata cena upisuje u samu rezervaciju.

Ako se cenovnik promeni nakon rezervacije, promena ne utiče na već postojeće rezervacije.

Dodatne usluge dodate u trenutku izdavanja obračunavaju se po cenovniku koji važi u trenutku izdavanja.

---

## 10. Pretplate

Svaki klijent ima svoju pretplatu.

Ako je pretplata istekla, klijent ne može da rezerviše vozilo dok je ne obnovi.

Proces obnove:
- klijent podnosi zahtev
- agent odobrava zahtev

Ako je klijent više od 5 puta zadržao vozilo duže od roka za vraćanje, zahtev za produžetak pretplate automatski se odbija.

---

## 11. Tehnička specifikacija

Potrebno je razviti konzolni ili grafički korisnički interfejs.

Ako se radi GUI, mora se koristiti Java Swing.

Interfejs mora omogućiti:
- prijavu na sistem
- pregled podataka
- dodavanje podataka
- izmenu podataka
- brisanje podataka

Entiteti navedeni kao smernica:
- Zaposleni
- Klijent
- Pretplata
- Rezervacija vozila
- Izdavanje vozila
- Kategorija vozila
- Model vozila
- Vozilo, odnosno fizički primerak
- Dodatna usluga
- Cenovnik

Spisak entiteta je smernica. Dozvoljeno je kreiranje novih entiteta, kao i razdvajanje ili spajanje navedenih.

Za podatke koji imaju predefinisan skup mogućih vrednosti potrebno je koristiti enumeracije.

Podaci se čuvaju u tekstualnom, ljudski čitljivom formatu, na primer `.csv`.

Za konzolno rešenje do 12 bodova, PDF dozvoljava serijalizaciju objekata za čuvanje podataka.

---

## 12. Bodovanje

Maksimalan broj bodova je 30.

Broj bodova zavisi od:
- demonstracije rešenja
- odgovora na pitanja asistenata
- testiranja funkcionalnih zahteva
- kvaliteta rešenja

### Do 12 bodova

Aplikacija je izrađena u konzolnom interfejsu.

Mora podržati sve iz specifikacije zahteva, osim:
- analiza i prikaza chart-ova
- jediničnih testova

Za ocenu 6 može se koristiti serijalizacija objekata za čuvanje podataka.

Obavezni izveštaji:
- izdavanja: koliko je vozila svaki agent izdao za izabrani opseg datuma
- rezervacije: koliko je rezervacija potvrđeno, odbijeno i otkazano za izabrani opseg datuma
- modeli vozila: model, kategorija, ukupan broj iznajmljivanja i rezervacija tog modela za izabrani opseg datuma
- prihodi i rashodi: pretplate, najmovi, dodatne usluge, kazne i plate za izabrani opseg datuma

### Do 20 bodova

Sve kao za do 12 bodova, ali uz GUI korišćenjem Swing-a.

### Iznad 20 bodova

Sve kao za do 20 bodova, plus dodatne funkcionalnosti:
- jedinični testovi za sve menadžer klase
- napredno filtriranje prilikom rezervacije
- chart prikazi

Jedinični testovi nisu potrebni za čiste entitete i GUI.

Filtriranje mora omogućiti pretragu dostupnih vozila po:
- nazivu modela
- proizvođaču
- dostupnosti od-do datuma
- kategoriji vozila

Chart prikazi:
- prihodi za prethodnih 12 meseci po kategoriji klijenta i ukupan prihod
- opterećenje agenata po broju obrađenih rezervacija za prethodnih 30 dana
- statusi svih rezervacija kreiranih u prethodnih 30 dana

Za chart-ove se može koristiti jednostavna Swing kompatibilna biblioteka, na primer XChart, uz prethodnu dozvolu asistenta ili profesora ako biblioteka nije deo kursa.

---

## 13. Napomene iz PDF-a

- Svaki student samostalno izrađuje svoje rešenje.
- Rešenje razvijati u Javi 17.
- GUI mora biti urađen upotrebom Swing biblioteke.
- Za konzolni interfejs moguće je dobiti maksimalno 12 bodova.
- Za rešenje bez jediničnog testiranja moguće je dobiti maksimalno 20 bodova.
- Za bodove preko 20 neophodni su GUI i jedinično testiranje.
- Čuvanje podataka vršiti u tekstualnom, ljudski čitljivom formatu, npr. `.csv`.
- Za predefinisane vrednosti koristiti enumeracije.
- Slična rešenja više studenata dobijaju 0 poena i zabranu predavanja.
- Obaveštenja i predaja idu preko Canvas platforme.
- Dodatne biblioteke i tehnologije koje nisu deo kursa dozvoljene su uz prethodnu dozvolu asistenta ili profesora.
- Za nejasnoće konsultovati predmetnog asistenta.

---

## 14. Pitanja iz PDF-a

### Kako importovati klase iz `java.awt` paketa i zašto ne radi autocomplete?

PDF navodi da je to greška u Eclipse editoru. Rešenje:
- ručno pisati import linije
- ili u Eclipse-u otvoriti `Window > Preferences > Java > Appearance > Type Filters` i isključiti filter `java.awt*`

### Da li sme da se koristi WindowBuilder?

Da.

---

## 15. Codex kontrolne tačke za review

Pri svakom pregledu ideje ili koda proveravati:
- da li student samostalno predlaže rešenje pre komentara mentora
- da li model razlikuje fizičko vozilo od modela vozila
- da li rezervacija čuva model, a izdavanje konkretan primerak vozila
- da li su statusi, kategorije i drugi fiksni skupovi rešeni enum-ima gde je prikladno
- da li klijent sa dozvolom kraćom od 2 godine ne može rezervisati
- da li rezervacija može početi najranije sutra
- da li postoji validacija dostupnosti u traženom opsegu datuma
- da li nepotvrđene rezervacije istekom početka automatski postaju odbijene
- da li otkazivanje i nepojavljivanje uvode zabranu od 24 sata
- da li odbijanje od strane agenta ne uvodi zabranu
- da li se cena rezervacije zaključava u trenutku kreiranja rezervacije
- da li se dodatne usluge dodate pri izdavanju računaju po tada važećem cenovniku
- da li obnova pretplate automatski pada posle više od 5 kašnjenja
- da li su menadžer klase odvojene od čistih entiteta
- da li su CSV/perzistencija i poslovna logika razumno razdvojeni
- da li izveštaji računaju podatke za izabrani opseg datuma

# Metodologija Rada na Projektu

Ovaj dokument opisuje način saradnje između studenta i AI asistenta (Antigravity) tokom razvoja projekta. Cilj je da **student samostalno osmisli i implementira kompletno rešenje**, koristeći asistenta isključivo kao edukativni alat, mentora i kontrolora kvaliteta.

---

## 1. Ciljevi Projekta i Samostalni Rad
*   **Samostalna implementacija**: Kompletan kod u projektu (`src/` direktorijum) piše student. AI asistent neće generisati delove produkcionog koda, niti gotova rešenja, osim opštih primera i šablona za učenje.
*   **Učenje kroz diskusiju**: Svaku fazu projekta (dizajn klasa, perzistencija, GUI, testiranje) student najpre osmišljava samostalno, a potom je prezentuje asistentu na diskusiju.
*   **Pridržavanje OOP principa**: Poseban fokus je na enkapsulaciji, nasleđivanju, polimorfizmu, labavoj spregnutosti (loose coupling) i čistom kodu.

---

## 2. Uloga AI Asistenta (Antigravity)

AI asistent ima ulogu **mentora i tehničkog kritičara** sa sledećim zadacima:
1.  **Validacija arhitekture**: Pregledanje predloženih dijagrama klasa, rasporeda paketa i odnosa među entitetima. AI ukazuje na potencijalne probleme (npr. cirkularne zavisnosti, kršenje SRP - Single Responsibility Principle).
2.  **Code Review (Pregled koda)**: Analiza koda koji je student napisao. AI traži bagove, propuste u logici, neobrađene izuzetke (exceptions), i predlaže optimizacije.
3.  **Evaluacija ideja**: Odobravanje ili odbijanje koncepata koje student predloži (npr. kako rešiti popuste, kako validirati opseg datuma, kako strukturirati CSV parser).
4.  **Edukativna podrška**: Objašnjavanje Java specifičnosti, Swing biblioteke, korišćenje JUnit-a, ili integracija eksternih biblioteka (poput XChart).
5.  **Zabrana inicijative sledećeg koraka**: AI **nikada** ne predlaže sledeći korak niti samostalno bira šta treba raditi sledeće. Student ima potpunu kontrolu nad redosledom i tempom rada, a AI isključivo odgovara na predloge i ideje koje student inicira.
6.  **Stroga usaglašenost sa specifikacijom**: AI je u obavezama da detaljno proveri zvanični PDF sa zahtevima projekta za svaki predlog koji student iznese i da odmah upozori studenta ukoliko predloženi dizajn ili atributi odstupaju od onoga što se traži na fakultetu (kako bi se osigurala maksimalna ispravnost rešenja).



---

## 3. Faze i Koraci u Razvoju

Razvoj će se odvijati inkrementalno kroz sledeće korake:

```mermaid
graph TD
    A[Faza 1: Projektovanje Arhitekture] --> B[Faza 2: Model i Entiteti]
    B --> C[Faza 3: Menadžer Klase i CSV Logika]
    C --> D[Faza 4: Unit Testovi]
    D --> E[Faza 5: Grafički Interfejs Swing]
    E --> F[Faza 6: Izveštaji i Grafikoni]
    F --> G[Faza 7: Poliranje i Refaktorisanje]
```

### Detaljan plan faza:
*   **Faza 1: Projektovanje Arhitekture**
    *   *Student*: Predlaže strukturu klasa, veze (npr. asocijacije, kompozicije) i raspored paketa.
    *   *AI*: Odobrava dizajn ili ukazuje na OOP greške.
*   **Faza 2: Model i Entiteti**
    *   *Student*: Piše osnovne klase (Klijent, Zaposleni, Vozilo, itd.) i enumeracije.
    *   *AI*: Vrši pregled napisanih entiteta.
*   **Faza 3: Menadžeri i CSV Perzistencija**
    *   *Student*: Implementira logiku za učitavanje/čuvanje podataka u CSV fajlove i upravljanje entitetima (Managers).
    *   *AI*: Validira parsiranje i rukovanje fajlovima.
*   **Faza 4: Unit Testovi (Nivo 3)**
    *   *Student*: Piše testove za menadžer klase koristeći JUnit.
    *   *AI*: Analizira pokrivenost i predlaže granične slučajeve (edge cases) za testiranje.
*   **Faza 5: Swing GUI**
    *   *Student*: Kreira forme za prijavu i CRUD operacije nad entitetima.
    *   *AI*: Pomaže oko dizajna Swing komponenti i upravljanja događajima (Event Handling).
*   **Faza 6: Izveštaji i Grafikoni**
    *   *Student*: Implementira poslovne izveštaje i crta grafikone (npr. XChart).
    *   *AI*: Pomaže kod matematičke logike za izveštaje i vizuelnog doterivanja grafikona.

---

## 4. Kako započeti razgovor o ideji?

Kada student želi da predloži ideju ili započne sledeći korak, format poruke treba da bude:
1.  **Cilj**: Šta se tačno želi postići u tom koraku.
2.  **Predlog rešenja**: Opis ideje, pseudokod ili struktura klasa koju je student zamislio.
3.  **Pitanja za AI**: Specifične dileme oko kojih student želi mišljenje mentora.

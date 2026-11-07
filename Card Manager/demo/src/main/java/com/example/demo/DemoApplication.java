package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
		KarticaManager manager = ctx.getBean(KarticaManager.class);

		System.out.println("\n========== TEST KARTICA ==========");

		for (Kartica k : manager.dohvatiSve()) {
			manager.obrisiKarticu(k.getIdBroj());
		}

		System.out.println("\n--- 1. Izdavanje kartica ---");
		Kartica ana = manager.izdajKarticu("Ana Horvat", "IT",
				LocalDate.of(2025, 3, 1), LocalDate.of(2027, 3, 1), Kartica.Status.AKTIVNA);
		Kartica marko = manager.izdajKarticu("Marko Kovač", "Računovodstvo",
				LocalDate.of(2023, 5, 10), LocalDate.of(2025, 5, 10), Kartica.Status.AKTIVNA);
		Kartica iva = manager.izdajKarticu("Iva Babić", "Kadrovska",
				LocalDate.of(2025, 9, 15), LocalDate.of(2027, 9, 15), Kartica.Status.BLOKIRANA);
		Kartica ante = manager.izdajKarticu("Ante Anić", "IT",
				LocalDate.of(2024, 1, 20), LocalDate.of(2027, 1, 20), Kartica.Status.AKTIVNA);
		ispisi(manager.dohvatiSve());

		System.out.println("\n--- 2. Kontrolna znamenka ---");
		System.out.println("ID " + ana.getIdBroj() + " valjan: " + IdBroj.jeValjan(ana.getIdBroj()));
		int pokvaren = ana.getIdBroj() + 1;
		System.out.println("ID " + pokvaren + " valjan: " + IdBroj.jeValjan(pokvaren));
		try {
			manager.dodajKarticu(new Kartica("Lažna", pokvaren, "IT",
					LocalDate.now(), LocalDate.now().plusYears(1), Kartica.Status.AKTIVNA));
			System.out.println("GREŠKA: krivi ID je prošao!");
		} catch (IllegalArgumentException e) {
			System.out.println("OK, uhvaćena greška: " + e.getMessage());
		}

		System.out.println("\n--- 3. Duplikat ID-a ---");
		try {
			manager.dodajKarticu(new Kartica("Duplikat", ana.getIdBroj(), "IT",
					LocalDate.now(), LocalDate.now().plusYears(1), Kartica.Status.AKTIVNA));
			System.out.println("GREŠKA: duplikat je prošao!");
		} catch (IllegalArgumentException e) {
			System.out.println("OK, uhvaćena greška: " + e.getMessage());
		}

		System.out.println("\n--- 4. Aktivna / istekla / blokirana ---");
		for (Kartica k : manager.dohvatiSve()) {
			int id = k.getIdBroj();
			System.out.println(k.getIme() + " | aktivna: " + manager.jeAktivna(id)
					+ " | istekla: " + manager.jeIstekla(id)
					+ " | blokirana: " + manager.jeBlokirana(id));
		}

		System.out.println("\n--- 5. Pretraga ---");
		System.out.println("Pretraga 'ana':");
		ispisi(manager.pretrazi("ana"));
		System.out.println("Pretraga 'it' (odjel):");
		ispisi(manager.pretrazi("it"));
		System.out.println("Pretraga po ID-u " + iva.getIdBroj() + ":");
		ispisi(manager.pretrazi(String.valueOf(iva.getIdBroj())));

		System.out.println("\n--- 6. Filtriranje ---");
		System.out.println("Ime sadrži 'an':");
		ispisi(manager.filtriraj("an", null, null, null));
		System.out.println("Godina izdavanja 2025:");
		ispisi(manager.filtriraj(null, 2025, null, null));
		System.out.println("Godina isteka 2027:");
		ispisi(manager.filtriraj(null, null, 2027, null));
		System.out.println("Status BLOKIRANA:");
		ispisi(manager.filtriraj(null, null, null, Kartica.Status.BLOKIRANA));
		System.out.println("Kombinirano: ime 'a', izdana 2025, istječe 2027, AKTIVNA:");
		ispisi(manager.filtriraj("a", 2025, 2027, Kartica.Status.AKTIVNA));

		System.out.println("\n--- 7. Uređivanje ---");
		manager.urediKarticu(iva.getIdBroj(), "Iva Babić-Perić", "Pravna služba",
				LocalDate.of(2025, 9, 15), LocalDate.of(2027, 9, 15), Kartica.Status.AKTIVNA);
		manager.urediKarticu(marko.getIdBroj(), "Marko Kovač", "Računovodstvo",
				LocalDate.of(2023, 5, 10), LocalDate.now().plusMonths(6), Kartica.Status.AKTIVNA);
		ispisi(manager.dohvatiSve());
		System.out.println("Iva aktivna: " + manager.jeAktivna(iva.getIdBroj()));
		System.out.println("Marko aktivan: " + manager.jeAktivna(marko.getIdBroj()));

		try {
			manager.urediKarticu(999, "Nitko", "Nigdje",
					LocalDate.now(), LocalDate.now().plusYears(1), Kartica.Status.AKTIVNA);
			System.out.println("GREŠKA: uređivanje nepostojeće je prošlo!");
		} catch (IllegalArgumentException e) {
			System.out.println("OK, uhvaćena greška: " + e.getMessage());
		}

		System.out.println("\n--- 8. Brisanje ---");
		System.out.println("Brisanje Ante: " + manager.obrisiKarticu(ante.getIdBroj()));
		System.out.println("Brisanje Ante opet: " + manager.obrisiKarticu(ante.getIdBroj()));
		ispisi(manager.dohvatiSve());

		System.out.println("\n========== KRAJ TESTA ==========\n");
	}

	private static void ispisi(List<Kartica> lista) {
		if (lista.isEmpty()) {
			System.out.println("  (nema rezultata)");
		}
		for (Kartica k : lista) {
			System.out.println("  ID " + k.getIdBroj()
					+ " | " + k.getIme()
					+ " | " + k.getOdjel()
					+ " | izdana: " + k.getDatumIzdavanja()
					+ " | istječe: " + k.getDatumIsteka()
					+ " | " + k.getStatus());
		}
	}
}
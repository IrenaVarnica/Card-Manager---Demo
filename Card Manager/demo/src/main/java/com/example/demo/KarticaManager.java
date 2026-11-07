package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class KarticaManager {

    private final KarticaRepository kartice;

    public KarticaManager(KarticaRepository kartice) {
        this.kartice = kartice;
    }

    public Kartica izdajKarticu(String ime, String odjel,
                                LocalDate izdavanje, LocalDate istek,
                                Kartica.Status status) {
        int id;
        do {
            id = IdBroj.generiraj();
        } while (kartice.existsById(id));

        Kartica k = new Kartica(ime, id, odjel, izdavanje, istek, status);
        kartice.save(k);
        return k;
    }

    public void dodajKarticu(Kartica kartica) {
        if (!IdBroj.jeValjan(kartica.getIdBroj())) {
            throw new IllegalArgumentException(
                    "ID broj nije valjan (kriva kontrolna znamenka)."
            );
        }
        if (kartice.existsById(kartica.getIdBroj())) {
            throw new IllegalArgumentException(
                    "Kartica s tim ID-em već postoji."
            );
        }
        kartice.save(kartica);
    }

    public void urediKarticu(int idBroj, String ime, String odjel,
                             LocalDate izdavanje, LocalDate istek,
                             Kartica.Status status) {
        Kartica k = dohvati(idBroj);
        k.setIme(ime);
        k.setOdjel(odjel);
        k.setDatumIzdavanja(izdavanje);
        k.setDatumIsteka(istek);
        k.setStatus(status);
        kartice.save(k);
    }

    public boolean obrisiKarticu(int idBroj) {
        if (!kartice.existsById(idBroj)) {
            return false;
        }
        kartice.deleteById(idBroj);
        return true;
    }

    public List<Kartica> dohvatiSve() {
        return kartice.findAll();
    }

    public List<Kartica> pretrazi(String tekst) {
        if (tekst == null || tekst.isBlank()) {
            return dohvatiSve();
        }
        String t = tekst.toLowerCase().trim();

        return kartice.findAll().stream()
                .filter(k -> k.getIme().toLowerCase().contains(t)
                        || k.getOdjel().toLowerCase().contains(t)
                        || String.valueOf(k.getIdBroj()).contains(t))
                .toList();
    }

    public List<Kartica> filtriraj(String ime,
                                   Integer godinaIzdavanja,
                                   Integer godinaIsteka,
                                   Kartica.Status status) {
        return kartice.findAll().stream()
                .filter(k -> ime == null || ime.isBlank()
                        || k.getIme().toLowerCase().contains(ime.toLowerCase().trim()))
                .filter(k -> godinaIzdavanja == null
                        || (k.getDatumIzdavanja() != null
                        && k.getDatumIzdavanja().getYear() == godinaIzdavanja))
                .filter(k -> godinaIsteka == null
                        || (k.getDatumIsteka() != null
                        && k.getDatumIsteka().getYear() == godinaIsteka))
                .filter(k -> status == null || k.getStatus() == status)
                .toList();
    }

    public Kartica dohvatiKarticu(int idBroj) {
        return dohvati(idBroj);
    }

    public boolean jeBlokirana(int idBroj) {
        return dohvati(idBroj).getStatus() == Kartica.Status.BLOKIRANA;
    }

    public boolean jeIstekla(int idBroj) {
        return dohvati(idBroj).getDatumIsteka().isBefore(LocalDate.now());
    }

    public boolean jeAktivna(int idBroj) {
        return !jeBlokirana(idBroj) && !jeIstekla(idBroj);
    }

    private Kartica pronadiPoId(int idBroj) {
        return kartice.findById(idBroj).orElse(null);
    }

    private Kartica dohvati(int idBroj) {
        Kartica k = pronadiPoId(idBroj);
        if (k == null) {
            throw new IllegalArgumentException("Kartica ne postoji.");
        }
        return k;
    }
}
package com.example.demo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kartice")
public class KarticaController {

    private final KarticaManager manager;

    public KarticaController(KarticaManager manager) {
        this.manager = manager;
    }

    public record KarticaZahtjev(String ime, String odjel,
                                 LocalDate datumIzdavanja, LocalDate datumIsteka,
                                 Kartica.Status status) {
    }

    @PostMapping
    public ResponseEntity<Kartica> izdaj(@RequestBody KarticaZahtjev z) {
        Kartica k = manager.izdajKarticu(z.ime(), z.odjel(),
                z.datumIzdavanja(), z.datumIsteka(), z.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(k);
    }

    @GetMapping
    public List<Kartica> filtriraj(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) Integer godinaIzdavanja,
            @RequestParam(required = false) Integer godinaIsteka,
            @RequestParam(required = false) Kartica.Status status) {
        return manager.filtriraj(ime, godinaIzdavanja, godinaIsteka, status);
    }

    @GetMapping("/pretraga")
    public List<Kartica> pretrazi(@RequestParam String q) {
        return manager.pretrazi(q);
    }

    @GetMapping("/{id}")
    public Kartica dohvati(@PathVariable int id) {
        return manager.dohvatiKarticu(id);
    }

    @PutMapping("/{id}")
    public Kartica uredi(@PathVariable int id, @RequestBody KarticaZahtjev z) {
        manager.urediKarticu(id, z.ime(), z.odjel(),
                z.datumIzdavanja(), z.datumIsteka(), z.status());
        return manager.dohvatiKarticu(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> obrisi(@PathVariable int id) {
        if (manager.obrisiKarticu(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/valjanost")
    public Map<String, Object> valjanost(@PathVariable int id) {
        return Map.of(
                "idBroj", id,
                "idValjan", IdBroj.jeValjan(id),
                "aktivna", manager.jeAktivna(id),
                "istekla", manager.jeIstekla(id),
                "blokirana", manager.jeBlokirana(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> pogreska(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("greska", e.getMessage()));
    }
}
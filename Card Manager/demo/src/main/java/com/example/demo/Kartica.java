package com.example.demo;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
public class Kartica {

    public enum Status {
        AKTIVNA, BLOKIRANA
    }

    private String ime;

    @Id
    private int idBroj;

    private String odjel;
    private LocalDate datumIzdavanja;
    private LocalDate datumIsteka;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Kartica() {
    }

    public Kartica(String ime, int idBroj, String odjel,
                   LocalDate datumIzdavanja, LocalDate datumIsteka,
                   Status status) {
        this.ime = ime;
        this.idBroj = idBroj;
        this.odjel = odjel;
        this.datumIzdavanja = datumIzdavanja;
        this.datumIsteka = datumIsteka;
        this.status = status;
    }

    public String getIme() {
        return ime;
    }

    public int getIdBroj() {
        return idBroj;
    }

    public String getOdjel() {
        return odjel;
    }

    public LocalDate getDatumIzdavanja() {
        return datumIzdavanja;
    }

    public LocalDate getDatumIsteka() {
        return datumIsteka;
    }

    public Status getStatus() {
        return status;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setOdjel(String odjel) {
        this.odjel = odjel;
    }

    public void setDatumIzdavanja(LocalDate d) {
        this.datumIzdavanja = d;
    }

    public void setDatumIsteka(LocalDate d) {
        this.datumIsteka = d;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
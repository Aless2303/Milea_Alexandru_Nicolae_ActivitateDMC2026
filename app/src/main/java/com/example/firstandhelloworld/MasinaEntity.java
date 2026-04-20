package com.example.firstandhelloworld;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Lab 8: Entitate ROOM — reprezinta o tabela in baza de date SQLite
// @Entity spune ROOM ca aceasta clasa corespunde unei tabele
// tableName = "masini" — numele tabelei in baza de date
@Entity(tableName = "masini")
public class MasinaEntity {

    // @PrimaryKey = cheia primara a tabelei (unica pentru fiecare rand)
    // autoGenerate = true — ROOM genereaza automat valori crescatoare (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Atribut de tip String — marca masinii (ex: "BMW", "Audi")
    private String marca;

    // Atribut de tip int — anul de fabricatie (ex: 2024)
    private int anFabricatie;

    // Atribut de tip double — viteza maxima a masinii (ex: 250.5)
    private double vitezaMaxima;

    // Atribut de tip String — culoarea masinii (ex: "ROSU", "NEGRU")
    private String culoare;

    // Atribut de tip boolean — daca masina este electrica sau nu
    private boolean esteElectrica;

    // Constructor cu toate atributele (fara id — acesta e generat automat)
    public MasinaEntity(String marca, int anFabricatie, double vitezaMaxima, String culoare, boolean esteElectrica) {
        this.marca = marca;
        this.anFabricatie = anFabricatie;
        this.vitezaMaxima = vitezaMaxima;
        this.culoare = culoare;
        this.esteElectrica = esteElectrica;
    }

    // Getter si setter pentru id — ROOM are nevoie de ele pentru a citi/scrie id-ul
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter si setter pentru marca
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    // Getter si setter pentru anFabricatie
    public int getAnFabricatie() {
        return anFabricatie;
    }

    public void setAnFabricatie(int anFabricatie) {
        this.anFabricatie = anFabricatie;
    }

    // Getter si setter pentru vitezaMaxima
    public double getVitezaMaxima() {
        return vitezaMaxima;
    }

    public void setVitezaMaxima(double vitezaMaxima) {
        this.vitezaMaxima = vitezaMaxima;
    }

    // Getter si setter pentru culoare
    public String getCuloare() {
        return culoare;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    // Getter si setter pentru esteElectrica
    public boolean isEsteElectrica() {
        return esteElectrica;
    }

    public void setEsteElectrica(boolean esteElectrica) {
        this.esteElectrica = esteElectrica;
    }

    // toString() — folosit de ArrayAdapter pentru afisarea in ListView
    // Formateaza toate atributele intr-un text lizibil
    @Override
    public String toString() {
        return marca + " | An: " + anFabricatie +
                " | " + culoare +
                " | Electric: " + (esteElectrica ? "Da" : "Nu") +
                " | Viteza: " + vitezaMaxima;
    }
}

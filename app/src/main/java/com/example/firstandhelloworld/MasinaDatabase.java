package com.example.firstandhelloworld;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Lab 8: @Database — marcheaza aceasta clasa ca baza de date ROOM
// entities = {MasinaEntity.class} — lista tabelelor din baza de date (avem una singura)
// version = 1 — versiunea bazei de date (se incrementeaza la schimbari de schema)
// exportSchema = false — nu exportam schema intr-un fisier JSON
@Database(entities = {MasinaEntity.class}, version = 1, exportSchema = false)
public abstract class MasinaDatabase extends RoomDatabase {

    // Metoda abstracta care returneaza DAO-ul — ROOM genereaza implementarea automat
    // Prin acest DAO accesam toate metodele de lucru cu baza de date
    public abstract MasinaDao masinaDao();

    // Instanta unica (Singleton) a bazei de date — evitam crearea multipla
    private static MasinaDatabase instance;

    // Metoda statica care returneaza instanta bazei de date
    // synchronized = un singur thread poate accesa metoda simultan (thread-safe)
    // Daca instanta nu exista, o cream cu Room.databaseBuilder()
    public static synchronized MasinaDatabase getInstance(Context context) {
        if (instance == null) {
            // Room.databaseBuilder() creeaza baza de date
            // context.getApplicationContext() — folosim contextul aplicatiei (nu al activitatii)
            // MasinaDatabase.class — tipul bazei de date
            // "masini_database" — numele fisierului bazei de date pe disk
            // .fallbackToDestructiveMigration() — daca versiunea se schimba, sterge si recreaza
            // .build() — construieste efectiv baza de date
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MasinaDatabase.class,
                    "masini_database"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }



}

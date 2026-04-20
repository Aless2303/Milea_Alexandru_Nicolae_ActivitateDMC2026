package com.example.firstandhelloworld;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// Lab 8: @Dao = Data Access Object — interfata care defineste metodele de acces la baza de date
// ROOM genereaza automat implementarea acestor metode pe baza adnotarilor SQL
@Dao
public interface MasinaDao {

    // Lab 8, Cerinta 1: Inserare in baza de date a unui obiect MasinaEntity
    // @Insert — ROOM genereaza automat INSERT INTO masini VALUES(...)
    // Primeste un obiect MasinaEntity si il salveaza ca rand nou in tabela
    @Insert
    void inserareMasina(MasinaEntity masina);

    // Lab 8, Cerinta 2: Selectia tuturor inregistrarilor din tabela
    // @Query cu SELECT * — returneaza toate randurile din tabela "masini"
    // Rezultatul e o lista de obiecte MasinaEntity
    @Query("SELECT * FROM masini")
    List<MasinaEntity> selectToateMasinile();

    // Lab 8, Cerinta 3: Selectia obiectului care are marca egala cu o valoare primita ca parametru
    // :marca — parametru legat de argumentul metodei (ROOM il substituie automat)
    // WHERE marca = :marca — filtreaza doar randurile cu marca exacta
    @Query("SELECT * FROM masini WHERE marca = :marca")
    List<MasinaEntity> selectDupaMarca(String marca);

    // Lab 8, Cerinta 4: Selectia obiectelor care au anFabricatie intr-un interval [anMin, anMax]
    // :anMin si :anMax — parametrii legati de argumentele metodei
    // BETWEEN — operator SQL care verifica daca valoarea e in interval (inclusiv capetele)
    @Query("SELECT * FROM masini WHERE anFabricatie BETWEEN :anMin AND :anMax")
    List<MasinaEntity> selectDupaIntervalAn(int anMin, int anMax);

    // Lab 8, Cerinta 5: Stergerea inregistrarilor care au vitezaMaxima mai mare decat un parametru
    // DELETE FROM — sterge randurile care indeplinesc conditia
    // WHERE vitezaMaxima > :viteza — doar cele cu viteza peste pragul dat
    @Query("DELETE FROM masini WHERE vitezaMaxima > :viteza")
    void stergeDupaViteza(double viteza);

    // Lab 8, Cerinta 6: Cresterea cu o unitate a anFabricatie pentru inregistrarile a caror marca
    // incepe cu o litera primita ca parametru
    // UPDATE — modifica randurile existente
    // SET anFabricatie = anFabricatie + 1 — creste anul cu 1
    // WHERE marca LIKE :litera || '%' — marca incepe cu litera data
    // || '%' — concatenare SQL: litera + orice caractere dupa ea
    @Query("UPDATE masini SET anFabricatie = anFabricatie + 1 WHERE marca LIKE :litera || '%'")
    void cresteAnFabricatieDupaLitera(String litera);
}

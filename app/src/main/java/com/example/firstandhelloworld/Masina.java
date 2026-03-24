package com.example.firstandhelloworld;

import android.os.Parcel;
import android.os.Parcelable;

// Importam clasa Date din Java — reprezinta o data calendaristica (an, luna, zi, ora etc.)
import java.util.Date;

import androidx.annotation.NonNull;

public class Masina implements Parcelable {
    private String marca;
    private boolean esteElectrica;
    private int anFabricatie;
    private CuloareMasina culoare;
    private double vitezaMaxima;
    // Cerinta 2: atribut de tip Date — stocheaza data fabricatiei masinii
    private Date dataFabricatiei;

    // Constructor: primeste toate atributele, inclusiv dataFabricatiei (Date)
    public Masina(boolean esteElectrica, String marca, int anFabricatie, CuloareMasina culoare, double vitezaMaxima, Date dataFabricatiei) {
        this.esteElectrica = esteElectrica;
        this.marca = marca;
        this.anFabricatie = anFabricatie;
        this.culoare = culoare;
        this.vitezaMaxima = vitezaMaxima;
        // Salvam data fabricatiei in campul privat
        this.dataFabricatiei = dataFabricatiei;
    }

    protected Masina(Parcel in) {
        marca = in.readString();
        esteElectrica = in.readByte() != 0;
        anFabricatie = in.readInt();
        culoare = CuloareMasina.valueOf(in.readString());
        vitezaMaxima = in.readDouble();
        // Citim data din Parcel: Date se salveaza ca long (milisecunde de la 1 ian 1970)
        // readLong() citeste numarul, iar new Date(long) il transforma inapoi in obiect Date
        long tmpDate = in.readLong();
        dataFabricatiei = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<Masina> CREATOR = new Creator<Masina>() {
        @Override
        public Masina createFromParcel(Parcel in) {
            return new Masina(in);
        }

        @Override
        public Masina[] newArray(int size) {
            return new Masina[size];
        }
    };

    public String getMarca() {
        return marca;
    }

    public boolean isEsteElectrica() {
        return esteElectrica;
    }

    public int getAnFabricatie() {
        return anFabricatie;
    }

    public CuloareMasina getCuloare() {
        return culoare;
    }

    public double getVitezaMaxima() {
        return vitezaMaxima;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setEsteElectrica(boolean esteElectrica) {
        this.esteElectrica = esteElectrica;
    }

    public void setAnFabricatie(int anFabricatie) {
        this.anFabricatie = anFabricatie;
    }

    public void setCuloare(CuloareMasina culoare) {
        this.culoare = culoare;
    }

    public void setVitezaMaxima(double vitezaMaxima) {
        this.vitezaMaxima = vitezaMaxima;
    }

    // Getter pentru data fabricatiei — returneaza obiectul Date
    public Date getDataFabricatiei() {
        return dataFabricatiei;
    }

    // Setter pentru data fabricatiei — permite modificarea datei
    public void setDataFabricatiei(Date dataFabricatiei) {
        this.dataFabricatiei = dataFabricatiei;
    }

    // Cerinta 5: toString() — metoda apelata automat de ArrayAdapter cand afiseaza obiectul in ListView
    // Returneaza un String cu toate atributele, care apare ca text in fiecare rand din lista
    @Override
    public String toString() {
        return marca + " | " + anFabricatie +
                " | " + culoare +
                " | Electric: " + (esteElectrica ? "Da" : "Nu") +
                " | Viteza: " + vitezaMaxima +
                " | Data: " + (dataFabricatiei != null ? dataFabricatiei.toString() : "N/A");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(marca);
        dest.writeByte((byte) (esteElectrica ? 1 : 0));
        dest.writeInt(anFabricatie);
        dest.writeString(culoare.name());
        dest.writeDouble(vitezaMaxima);
        // Scriem data in Parcel ca long (milisecunde). Daca e null, scriem -1.
        // getTime() transforma Date-ul in numar long pe care Parcel-ul stie sa-l stocheze
        dest.writeLong(dataFabricatiei != null ? dataFabricatiei.getTime() : -1);
    }
}

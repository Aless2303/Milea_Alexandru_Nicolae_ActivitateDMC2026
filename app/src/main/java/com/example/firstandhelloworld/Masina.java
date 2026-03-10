package com.example.firstandhelloworld;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Masina implements Parcelable {
    private String marca;
    private boolean esteElectrica;
    private int anFabricatie;
    private CuloareMasina culoare;
    private double vitezaMaxima;

    public Masina(boolean esteElectrica, String marca, int anFabricatie, CuloareMasina culoare, double vitezaMaxima) {
        this.esteElectrica = esteElectrica;
        this.marca = marca;
        this.anFabricatie = anFabricatie;
        this.culoare = culoare;
        this.vitezaMaxima = vitezaMaxima;
    }

    protected Masina(Parcel in) {
        marca = in.readString();
        esteElectrica = in.readByte() != 0;
        anFabricatie = in.readInt();
        culoare = CuloareMasina.valueOf(in.readString());
        vitezaMaxima = in.readDouble();
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
    }
}

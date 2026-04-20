package com.example.firstandhelloworld;

import android.graphics.Bitmap;

// Lab 9: Clasa care contine datele pentru un element din ListView-ul cu imagini
// Fiecare obiect ImageItem are:
// - imageUrl: link-ul catre imaginea de pe internet (pentru descarcare cu Executors)
// - descriere: textul scurt afisat sub imagine in ListView
// - webUrl: link-ul catre site-ul care se deschide in WebView la click
// - bitmap: imaginea descarcata din internet (se seteaza dupa descarcare pe background thread)
public class ImageItem {

    // URL-ul imaginii de pe internet — de aici se descarca imaginea cu Executors
    private String imageUrl;

    // Text scurt de descriere — afisat in ListView langa imagine
    private String descriere;

    // URL-ul paginii web — se deschide in WebView cand utilizatorul da click pe item
    private String webUrl;

    // Imaginea descarcata — initial null, se seteaza dupa descarcare pe thread separat
    // Bitmap = reprezentarea imaginii in memorie (pixeli)
    private Bitmap bitmap;

    // Constructor cu cele 3 campuri principale (bitmap se seteaza ulterior)
    public ImageItem(String imageUrl, String descriere, String webUrl) {
        this.imageUrl = imageUrl;
        this.descriere = descriere;
        this.webUrl = webUrl;
        this.bitmap = null; // initial nu avem imaginea descarcata
    }

    // Getter pentru URL-ul imaginii
    public String getImageUrl() {
        return imageUrl;
    }

    // Getter pentru descriere
    public String getDescriere() {
        return descriere;
    }

    // Getter pentru URL-ul paginii web
    public String getWebUrl() {
        return webUrl;
    }

    // Getter pentru bitmap (imaginea descarcata)
    public Bitmap getBitmap() {
        return bitmap;
    }

    // Setter pentru bitmap — se apeleaza dupa ce imaginea a fost descarcata
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

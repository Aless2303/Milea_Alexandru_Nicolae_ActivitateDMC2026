package com.example.firstandhelloworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

// Lab 9: CustomAdapter pentru ListView-ul cu imagini.
// Extinde ArrayAdapter<ImageItem> si foloseste layout-ul item_image.xml
// Fiecare rand afiseaza imaginea descarcata din internet (Bitmap) + descrierea
public class ImageItemAdapter extends ArrayAdapter<ImageItem> {

    // Lista de obiecte ImageItem — fiecare contine imageUrl, descriere, webUrl, bitmap
    private List<ImageItem> items;

    // Constructor — primeste contextul si lista de ImageItem
    public ImageItemAdapter(Context context, List<ImageItem> items) {
        super(context, R.layout.item_image, items);
        this.items = items;
    }

    // Lab 9: getView() — metoda apelata pentru fiecare rand din ListView
    // Creeaza sau reutilizeaza un View si il populeaza cu datele obiectului ImageItem
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Daca convertView e null, inflam layout-ul item_image.xml
        // convertView = randul reciclat de ListView (poate fi refolosit)
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
        }

        // Luam obiectul ImageItem de la pozitia curenta
        ImageItem item = items.get(position);

        // Gasim elementele din layout-ul item_image.xml
        ImageView ivImage = convertView.findViewById(R.id.ivItemImage);
        TextView tvDescriere = convertView.findViewById(R.id.tvItemDescriere);

        // Setam descrierea textului
        tvDescriere.setText(item.getDescriere());

        // Lab 9: Daca bitmap-ul a fost descarcat (nu e null), il afisam in ImageView
        // Daca nu (inca se descarca), setam un fundal gri ca placeholder
        if (item.getBitmap() != null) {
            ivImage.setImageBitmap(item.getBitmap());
        } else {
            // Imaginea nu s-a descarcat inca — afisam placeholder gri
            ivImage.setImageBitmap(null);
            ivImage.setBackgroundColor(0xFFEEEEEE);
        }

        return convertView;
    }
}

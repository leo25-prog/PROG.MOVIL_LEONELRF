package com.example.recyclerviewcardview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<ListElement> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        elements =  new ArrayList<>();
        elements.add(new ListElement("#775447", "Leo", "Mexico", "Activo"));
        elements.add(new ListElement("#607d8b", "Emmanuel", "Morelia", "Activo"));
        elements.add(new ListElement("#03a9f4", "Daniel", "Acambaro", "Cancelado"));
        elements.add(new ListElement("#f44336", "Andrea", "Yuriria", "Inactivo"));
        elements.add(new ListElement("#009688", "Sheik", "Puruandiro", "Activo"));

        ListAdapter listAdapter = new ListAdapter(elements, this);
        RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
}
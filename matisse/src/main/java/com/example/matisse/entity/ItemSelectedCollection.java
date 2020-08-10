package com.example.matisse.entity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectedCollection {
    public static String DATA_KEY = "item_list";
    private List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }


    public int indexOf(Item item) {
        return items.indexOf(item);
    }

    public void remove(Item item) {
        items.remove(item);
    }



    public int getSize() {
        return items.size();
    }

    public void renew(List<Item> list) {
        items.clear();
        items.addAll(list);
    }


    public Bundle getItems() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATA_KEY, new ArrayList<>(items));
        return bundle;
    }
}

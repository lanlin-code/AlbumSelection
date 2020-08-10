package com.example.matisse.entity;

public class SelectionSpec {
    public boolean countable;
    public int maxSelection;
    public int spanCount;
    public float thumbnailScale = 1.0f;
    private static SelectionSpec selectionSpec;


    public void reset() {
        countable = false;
        maxSelection = 0;
        spanCount = 0;
        thumbnailScale = 1.0f;
    }

    public static SelectionSpec newInstance() {
        if (selectionSpec == null) selectionSpec = new SelectionSpec();
        return selectionSpec;
    }
}

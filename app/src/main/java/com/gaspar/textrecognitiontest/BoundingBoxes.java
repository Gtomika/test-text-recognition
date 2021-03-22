package com.gaspar.textrecognitiontest;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class BoundingBoxes {

    public List<Rect> boxes;

    public BoundingBoxes() {
        boxes = new ArrayList<>();
    }

    public void add(Rect r) {
        boxes.add(r);
    }
}

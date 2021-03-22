package com.gaspar.textrecognitiontest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    private FrameLayout root;

    private ImageView testDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root_layout);
        testDisplay = findViewById(R.id.testDisplay);
    }

    public void takeScreenshot(View view) {
        testDisplay.setImageBitmap(null);
        //screenshot
        Bitmap bitmap = Bitmap.createBitmap(root.getWidth(),
                root.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        root.draw(canvas);

        testDisplay.setImageBitmap(bitmap);

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Log.d("Test", "Screenshot taken.");
        TextRecognizer recognizer = TextRecognition.getClient();
        recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                Log.d("Test", "Screenshot processed. " + text.getTextBlocks().size());
                for (Text.TextBlock block : text.getTextBlocks()) {
                    for (Text.Line line : block.getLines()) {
                        for (Text.Element element : line.getElements()) {
                            Log.d("Test", "Word found: " + element.getText());
                            if ("hello".equalsIgnoreCase(element.getText())) {
                                Rect box = element.getBoundingBox();
                                assert (box != null);
                                int left = box.left;
                                int top = box.top;
                                int right = box.right;
                                int bottom = box.bottom;

                                int[] location = new int[2];
                                root.getLocationOnScreen(location);

                                final View highlighter = LayoutInflater.from(MainActivity.this)
                                        .inflate(R.layout.highligher, root, false);

                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(right - left,
                                        bottom - top);
                                params.setMargins(left - location[0],
                                        top - location[1],
                                        right - location[0],
                                        bottom - location[1]);
                                root.addView(highlighter, params);
                            }
                        }
                    }
                }
            }
        });
    }
}

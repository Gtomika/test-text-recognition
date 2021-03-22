package com.gaspar.textrecognitiontest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    private FrameLayout root;

    private List<View> highlighers;

    //private ImageView testDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root_layout);
        //testDisplay = findViewById(R.id.testDisplay);
        EventBus.getDefault().register(this);
        highlighers = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Bitmap bitmap) {
       testDisplay.setImageBitmap(bitmap);
    }
     */

    //this will get called when the service sends the bounding boxes
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BoundingBoxes boxes) {
        for(View v: highlighers) { //remove old highlighters
            root.removeView(v);
        }
        for(Rect box: boxes.boxes) {
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
            highlighers.add(highlighter); //save view to be able to remove later
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(ScreenCaptureService.getStartIntent(this, resultCode, data));
            }
        }
    }

    public void startProjection(View view) {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    public void stopProjection(View view) {
        startService(ScreenCaptureService.getStopIntent(this));
        for(View v: highlighers) { //remove highlighters
            root.removeView(v);
        }
    }


    /* THIS WAS THE OLD WAY OF TAKING AND PROCESSING THE SCREENSHOT
     public void takeScreenshot(View view) {
        testDisplay.setImageBitmap(null);
        //screenshot
        View decorView = getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(decorView.getWidth(),
                decorView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        decorView.draw(canvas);

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
     */
}

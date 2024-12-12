package com.smartangler.smartangler.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.smartangler.smartangler.BuildConfig;
import com.smartangler.smartangler.R;

import java.util.concurrent.Executors;

public class FishRecognitionActivity extends AppCompatActivity {

    private ImageView imageViewFish;
    private TextView textViewRecognition;
    GenerativeModel gm = new GenerativeModel(
            "gemini-1.5-flash",
            BuildConfig.apiKey
    );
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fish_recognition);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewFish = findViewById(R.id.imageViewFish);
        textViewRecognition = findViewById(R.id.textViewRecognition);

        textViewRecognition.setMovementMethod(new ScrollingMovementMethod());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Bitmap imageBitmap = null;

            if (extras.containsKey("imageBitmap")) {
                imageBitmap = (Bitmap) extras.get("imageBitmap");
            } else if (extras.containsKey("imageByteArray")) {
                byte[] byteArray = extras.getByteArray("imageByteArray");
                if (byteArray != null) {
                    imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
            }

            if (imageBitmap != null) {
                imageViewFish.setImageBitmap(imageBitmap);
                recognizeFish(imageBitmap);
            } else {
                textViewRecognition.setText(R.string.no_image_received_for_recognition);
            }
        }
    }


    private void recognizeFish(Bitmap imageBitmap) {
        String prompt = "Please analyze this fish image and provide: Species identification (common and scientific name), Key physical characteristics, Typical size and weight range, Natural habitat and geographical distribution, Any distinctive features that help with identification. Please provide the information in plain text without any formatting or special characters for emphasis.";

        Content content = new Content.Builder()
                .addText(prompt)
                .addImage(imageBitmap)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String recognitionResult = result.getText();
                runOnUiThread(() -> updateRecognitionText(recognitionResult));
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> updateRecognitionText("Error on recognition: " + t.getMessage()));
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void updateRecognitionText(String recognitionResult) {
        textViewRecognition.setText(recognitionResult);
    }
}

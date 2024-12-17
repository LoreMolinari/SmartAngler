package com.smartangler.smartangler.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class AskAIActivity extends AppCompatActivity {
    private TextView askAITextView;
    GenerativeModel gm = new GenerativeModel(
            "gemini-1.5-flash",
            BuildConfig.apiKey
    );
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_ai);

        askAITextView = findViewById(R.id.ask_ai_text);

        String prompt = getIntent().getStringExtra("prompt");
        if (prompt == null) {
            askAITextView.setText(R.string.llm_error);
            return;
        }

        Log.d("AI suggestions", prompt);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String recognitionResult = result.getText();
                runOnUiThread(() -> updateSuggestionText(recognitionResult));
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> updateSuggestionText("LLM error: " + t.getMessage()));
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void updateSuggestionText(String suggestion) {
        askAITextView.setText(suggestion);
    }

}

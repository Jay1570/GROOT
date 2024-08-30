package com.example.groot;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LanguageBarChartView extends LinearLayout {

    public LanguageBarChartView(Context context) {
        super(context);
        init();
    }

    public LanguageBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    public void setLanguagesData(Map<String, Float> languagesData) {
        removeAllViews();
        float totalPercentage = 0;

        for (Float percentage : languagesData.values()) {
            totalPercentage += percentage;
        }

        if (totalPercentage == 0) {
            totalPercentage = 1;
        }

        for (Map.Entry<String, Float> entry : languagesData.entrySet()) {
            String language = entry.getKey();
            float percentage = entry.getValue();

            // Create a view for the bar
            View languageBar = new View(getContext());
            int color = getColorForLanguage("."+language);
            languageBar.setBackgroundColor(color);

            // Calculate bar width based on percentage
            LayoutParams params = new LayoutParams(
                    0, LayoutParams.MATCH_PARENT, percentage / totalPercentage
            );

            addView(languageBar, params);
        }
    }

    private int getColorForLanguage(String language) {
        int colorId = ContextCompat.getColor(getContext(), R.color.default_color);
        Log.d("LanguageBarChart","Language :- "+language);
        try {
            String json = loadJsonFromAsset();
            if (json == null) {
                return colorId;
            }
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(language)) {
                JSONArray values = jsonObject.getJSONArray(language);
                String color = values.optString(1);
                Log.d("LanguageBarChart","Color :-"+ color);
                colorId = (color == null || color.isEmpty()) ? colorId : Color.parseColor(color);
            }
        } catch (Exception e) {
            Log.e("LanguageBarChart",e.getMessage());
            return colorId;
        }
        return colorId;
    }

    private String loadJsonFromAsset() {
        try (InputStream is = getContext().getAssets().open("languages.json")) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e("LanguageBarChart",ex.getMessage());
            return null;
        }
    }
}

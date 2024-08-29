package com.example.groot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

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
            int color = getColorForLanguage(language);
            languageBar.setBackgroundColor(color);

            // Calculate bar width based on percentage
            LayoutParams params = new LayoutParams(
                    0, LayoutParams.MATCH_PARENT, percentage / totalPercentage
            );

            addView(languageBar, params);
        }
    }

    private int getColorForLanguage(String language) {
        String colorName = language.toLowerCase().replace(" ", "_") + "_color";
        int colorId = getResources().getIdentifier(colorName, "color", getContext().getPackageName());
        if (colorId == 0) {
            colorId = R.color.default_color;
        }
        return getResources().getColor(colorId, null);
    }
}

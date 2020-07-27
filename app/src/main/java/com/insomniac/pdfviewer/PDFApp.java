package com.insomniac.pdfviewer;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.io.IOException;
import java.util.HashMap;

public class PDFApp extends Application {

    public static PDFApp pdfApp;
    private final HashMap<String, Typeface> TYPEFACES = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        pdfApp = this;
        intFonts();
    }

    public Typeface getFont(String key) {
        return TYPEFACES.get(key);
    }

    private void intFonts() {
        AssetManager assetManager = getAssets();
        TYPEFACES.clear();
        String[] fonts = new String[0];
        try {
            fonts = assetManager.list("");
            for (String font : fonts) {
                if (font.endsWith(".ttf")) {
                    TYPEFACES.put(font, Typeface.createFromAsset(assetManager, font));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.ambe.imagetoimage.ui.translate;

import android.graphics.Bitmap;

/**
 * Created by AMBE on 19/12/2018 at 11:44 AM.
 */
public interface ITranslateView {
    void onTranslateSuccess(Bitmap bitmap);

    void onTranslateFail(String mess);
}

package com.example.ambe.imagetoimage.ui.translate;

import android.graphics.Bitmap;

/**
 * Created by AMBE on 19/12/2018 at 11:46 AM.
 */
public interface ITranslatePres {
    void onTranslateSuccess(Bitmap bitmap);

    void onTranslateFail(String mess);
}

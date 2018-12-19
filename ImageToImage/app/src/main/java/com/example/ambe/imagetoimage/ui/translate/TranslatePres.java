package com.example.ambe.imagetoimage.ui.translate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.example.ambe.imagetoimage.ui.select.SelectActivity;

/**
 * Created by AMBE on 19/12/2018 at 11:46 AM.
 */
public class TranslatePres implements ITranslatePres {
    private ITranslateView iTranslateView;

    public TranslatePres(ITranslateView iTranslateView) {
        this.iTranslateView = iTranslateView;
    }

    @Override
    public void onTranslateSuccess(Bitmap bitmap) {
        iTranslateView.onTranslate(bitmap);

    }

    @Override
    public void onTranslateFail(String mess) {

        iTranslateView.onTranslateFail(mess);

    }
}

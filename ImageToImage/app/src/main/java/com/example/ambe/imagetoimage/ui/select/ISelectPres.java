package com.example.ambe.imagetoimage.ui.select;

import android.media.Image;

import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 9:37 AM.
 */
public interface ISelectPres {
    void showListImageSuccess(ArrayList<MyImage> arrayList);


    void showListImageFail(String msg);
}

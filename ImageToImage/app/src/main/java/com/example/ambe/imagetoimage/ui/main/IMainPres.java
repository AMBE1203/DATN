package com.example.ambe.imagetoimage.ui.main;

import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 9:02 AM.
 */
public interface IMainPres {

    void showListMyImageSuccess(ArrayList<MyImage> arrayList);
    void showListMyImageFail(String msg);

}

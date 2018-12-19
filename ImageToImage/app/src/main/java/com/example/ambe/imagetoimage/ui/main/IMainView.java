package com.example.ambe.imagetoimage.ui.main;

import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/11/2018.
 */
public interface IMainView {

    void onShowListMyImage(ArrayList<MyImage> arrayList);

    void onShowListMyImageFail(String mess);

    void onSelectImage(int pos, ArrayList<MyImage> arrayList);

    void onDeleteImage(int pos, String path, boolean selected);
}

package com.example.ambe.imagetoimage.ui.select;

import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 9:32 AM.
 */
public interface ISelectView {
    void onShowListImage(ArrayList<MyImage> arrayList);

    void onShowListImageFail(String mess);

    void onSelectImage(String path);

    void onDeleteImage(String path);
}

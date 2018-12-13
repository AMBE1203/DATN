package com.example.ambe.imagetoimage.ui.main;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.ambe.imagetoimage.ui.main.IMainPres;
import com.example.ambe.imagetoimage.ui.main.IMainView;
import com.example.ambe.imagetoimage.models.MyImage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 9:03 AM.
 */
public class MainPres implements IMainPres {

    private IMainView iMainView;

    public MainPres(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    public void getAllMyImage(Context context) {
        ArrayList<MyImage> arrayList = new ArrayList<>();
        arrayList.clear();
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media._ID};
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String selection = MediaStore.Images.Media.DATA + " like ? ";
        String[] arg = new String[]{"%tensorflow%"};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, arg, orderBy + " DESC");
        try {

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {

                    MyImage myImage = new MyImage();
                    int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                    int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    myImage.setPath(cursor.getString(dataColumnIndex));
                    myImage.setName(cursor.getString(titleColumnIndex));
                    myImage.setId(cursor.getString(idColumnIndex));
                    File file = new File(myImage.getPath());
                    if (file.exists()) {
                        arrayList.add(myImage);
                    }
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (arrayList.size() == 0) {
            showListMyImageFail("No Image");
        } else {

            showListMyImageSuccess(arrayList);
        }

    }

    @Override
    public void showListMyImageSuccess(ArrayList<MyImage> arrayList) {

        iMainView.onShowListMyImage(arrayList);

    }

    @Override
    public void showListMyImageFail(String msg) {
        iMainView.onShowListMyImageFail(msg);

    }
}

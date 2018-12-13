package com.example.ambe.imagetoimage.ui.select;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.provider.MediaStore;

import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 9:38 AM.
 */
public class SelectPres implements ISelectPres {

    private ISelectView iSelectView;

    public SelectPres(ISelectView iSelectView) {
        this.iSelectView = iSelectView;
    }

    public void getAllImageFromExternal(Context context) {
        ArrayList<MyImage> arrayList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media._ID};//lay tat ca cac cot cua images
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date
        String selection = MediaStore.Images.Media.DATA + " like ? ";
        String[] arg = new String[]{"%Camera%"};

        Cursor videoCusor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, arg, orderBy + " DESC");
        try {
            if (videoCusor != null && videoCusor.getCount() != 0) {
                videoCusor.moveToFirst();
                do {

                    MyImage myImage = new MyImage();

                    int dataColumnIndex = videoCusor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    int titleColumnIndex = videoCusor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                    int idColumnIndex = videoCusor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    myImage.setPath(videoCusor.getString(dataColumnIndex));
                    myImage.setName(videoCusor.getString(titleColumnIndex));
                    myImage.setId(videoCusor.getString(idColumnIndex));

                    arrayList.add(myImage);


                } while (videoCusor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arrayList.size() == 0) {
            showListImageFail("No image");

        } else {
            showListImageSuccess(arrayList);
        }


    }


    @Override
    public void showListImageSuccess(ArrayList<MyImage> arrayList) {
        iSelectView.onShowListImage(arrayList);

    }

    @Override
    public void showListImageFail(String msg) {

        iSelectView.onShowListImageFail(msg);

    }
}

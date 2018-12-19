package com.example.ambe.imagetoimage.ui.translate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ambe.imagetoimage.R;
import com.example.ambe.imagetoimage.ui.select.SelectActivity;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener, ITranslateView {

    private ImageView imgTranslate;
    private ImageView imgBack;
    private Button btnSave;
    private RelativeLayout rllMonet;
    private RelativeLayout rllVanghoh;
    private RelativeLayout rllUkiyoe;
    private String pathImage;
    private String style = "";
    private String MODEL_FILE = "file:///android_asset/";
    private TranslatePres translatePres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        addControls();
        addEvents();
    }

    private void addControls() {
        imgTranslate = findViewById(R.id.img_translate);
        imgBack = findViewById(R.id.img_back_translate);
        btnSave = findViewById(R.id.btn_save);
        rllMonet = findViewById(R.id.rll_monet);
        rllVanghoh = findViewById(R.id.rll_vanghoh);
        rllUkiyoe = findViewById(R.id.rll_ukiyoe);
        Intent intent = getIntent();
        pathImage = intent.getStringExtra(SelectActivity.EXTRA_IMAGE_PATH);
        Glide.with(getApplicationContext()).load("file://" + pathImage).centerCrop().into(imgTranslate);

    }

    private void addEvents() {
        imgBack.setOnClickListener(this);
        rllMonet.setOnClickListener(this);
        rllUkiyoe.setOnClickListener(this);
        rllVanghoh.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_translate:
                onBackPressed();
                break;
            case R.id.rll_monet:
                chooseStyle(rllMonet, rllVanghoh, rllUkiyoe, "monet2photo.pb");
                break;
            case R.id.rll_vanghoh:
                chooseStyle(rllVanghoh, rllMonet, rllUkiyoe, "vangoh2photo.pb");
                break;
            case R.id.rll_ukiyoe:
                chooseStyle(rllUkiyoe, rllVanghoh, rllMonet, "ukiyoe2photo.pb");
                break;
            case R.id.btn_save:

                traslate();


                break;
        }

    }

    private void traslate() {
        if (style.equals("")) {
            Toast.makeText(this, "Please select a style", Toast.LENGTH_SHORT).show();
        } else {
            translatePres = new TranslatePres(this, TranslateActivity.this);
            translatePres.translate(MODEL_FILE + style, pathImage);
        }
    }

    private void chooseStyle(RelativeLayout rl1, RelativeLayout rl2, RelativeLayout rl3, String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rl1.setBackground(getApplication().getResources().getDrawable(R.drawable.bg_boder));
            rl2.setBackground(null);
            rl3.setBackground(null);
            style = str;
        }
    }

    @Override
    public void onTranslate(Bitmap bitmap) {
        imgTranslate.setImageBitmap(bitmap);
    }

    @Override
    public void onTranslateFail(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();

    }
}

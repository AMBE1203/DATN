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

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgTranslate;
    private ImageView imgBack;
    private Button btnSave;
    private RelativeLayout rllMonet;
    private RelativeLayout rllVanghoh;
    private RelativeLayout rllUkiyoe;
    private String pathImage;
    private String style = "";
    private int[] intValues;
    private float[] floatValues;
    private TensorFlowInferenceInterface inferenceInterface;
    private String MODEL_FILE = "file:///android_asset/";
    private static final String INPUT_NODE = "inputA";
    private static final String OUTPUT_NODE = "a2b_generator/output_image";
    private FileInputStream is = null;


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
                Intent intent = new Intent(TranslateActivity.this, SelectActivity.class);
                startActivity(intent);
                break;
            case R.id.rll_monet:
                chooseStyle(rllMonet, rllVanghoh, rllUkiyoe);
                style = "monet2photo.pb";
                break;
            case R.id.rll_vanghoh:
                chooseStyle(rllVanghoh, rllMonet, rllUkiyoe);
                style = "vangoh2photo.pb";
                break;
            case R.id.rll_ukiyoe:
                chooseStyle(rllUkiyoe, rllVanghoh, rllMonet);
                style = "ukiyoe2photo.pb";
                break;
            case R.id.btn_save:
                translate();
                break;
        }

    }

    private void translate() {
        if (style.equals("")) {
            Toast.makeText(this, "Please select a style", Toast.LENGTH_SHORT).show();
        } else {
            long currentTime = System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");

            initTensorFlowAndLoadModel(MODEL_FILE + style);
            try {
                File file = new File(pathImage);
                is = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Bitmap bitmap1 = stylizeImage(bitmap);
                imgTranslate.setImageBitmap(bitmap1);
                saveBitmap(bitmap1, (simpleDateFormat.format(currentTime) + " Image_to_Image " + ".png"));

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void initTensorFlowAndLoadModel(String model) {
        intValues = new int[480 * 480];
        floatValues = new float[480 * 480 * 3];
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), model);
    }

    //  Image saving
    public void saveBitmap(final Bitmap bitmap, final String filename) {
//        String unique = Long.toString(System.currentTimeMillis());
        final String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tensorflow";
        final File myDir = new File(root);
//        final String myDir = root;
        final File file = new File(myDir, filename);
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{file.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });


        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (final Exception ignored) {
        }
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }

        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    private Bitmap stylizeImage(Bitmap bitmap) {
        Bitmap scaledBitmap = scaleBitmap(bitmap, 480, 480); // desiredSize
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = ((val >> 16) & 0xFF) / (127.5f) - 1f; //red

            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / (127.5f) - 1f; //green

            floatValues[i * 3 + 2] = (val & 0xFF) / (127.5f) - 1f; //blue
        }

        // Copy the input data into TensorFlow.
        inferenceInterface.feed(INPUT_NODE, floatValues, 1, 480, 480, 3);
        // Run the inference call.
        inferenceInterface.run(new String[]{OUTPUT_NODE});
        // Copy the output Tensor back into the output array.
        inferenceInterface.fetch(OUTPUT_NODE, floatValues);

        for (int i = 0; i < intValues.length; ++i) {
            intValues[i] =
                    0xFF000000
                            | (((int) ((floatValues[i * 3] + 1f) * 127.5f)) << 16) //red
                            | (((int) ((floatValues[i * 3 + 1] + 1f) * 127.5f)) << 8) //green
                            | ((int) ((floatValues[i * 3 + 2] + 1f) * 127.5f)); //blue
        }
        scaledBitmap.setPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());
        return scaledBitmap;
    }

    private void chooseStyle(RelativeLayout rl1, RelativeLayout rl2, RelativeLayout rl3) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rl1.setBackground(getApplication().getResources().getDrawable(R.drawable.bg_boder));
            rl2.setBackground(null);
            rl3.setBackground(null);
        }
    }
}

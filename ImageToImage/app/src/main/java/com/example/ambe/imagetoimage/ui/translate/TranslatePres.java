package com.example.ambe.imagetoimage.ui.translate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

/**
 * Created by AMBE on 19/12/2018 at 11:46 AM.
 */
public class TranslatePres implements ITranslatePres {
    private ITranslateView iTranslateView;
    private Context context;
    private int[] intValues;
    private float[] floatValues;
    private TensorFlowInferenceInterface inferenceInterface;
    private static final String INPUT_NODE = "inputA";
    private static final String OUTPUT_NODE = "a2b_generator/output_image";
    private FileInputStream is = null;

    public TranslatePres(ITranslateView iTranslateView, Context context) {
        this.iTranslateView = iTranslateView;
        this.context = context;
    }

    @Override
    public void onTranslateSuccess(Bitmap bitmap) {
        iTranslateView.onTranslateSuccess(bitmap);

    }

    @Override
    public void onTranslateFail(String mess) {

        iTranslateView.onTranslateFail(mess);

    }

    public void translate(String style, String path) {
        MyAsyntask myAsyntask = new MyAsyntask();
        myAsyntask.execute(new String[]{style, path});

    }

    public class MyAsyntask extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog progressBar;

        public MyAsyntask() {
            progressBar = new ProgressDialog(context);
            progressBar.setMessage("Loading ...");
            progressBar.setCancelable(false);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            String model = strings[0];
            String path = strings[1];
            try {
                return initTensorFlowAndLoadModel(model, path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                long currentTime = System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
                progressBar.dismiss();
                onTranslateSuccess(bitmap);
                saveBitmap(bitmap, (simpleDateFormat.format(currentTime) + " Image_to_Image " + ".png"));
            } else {
                onTranslateFail("Error initializing TensorFlow!");
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private Bitmap initTensorFlowAndLoadModel(String model, String pathImage) throws FileNotFoundException {
        intValues = new int[480 * 480];
        floatValues = new float[480 * 480 * 3];
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), model);


        File file = new File(pathImage);
        is = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        Bitmap bitmap1 = stylizeImage(bitmap);
        return bitmap1;

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
                context,
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
}

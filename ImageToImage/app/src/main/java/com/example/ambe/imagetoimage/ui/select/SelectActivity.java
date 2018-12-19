package com.example.ambe.imagetoimage.ui.select;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ambe.imagetoimage.BuildConfig;
import com.example.ambe.imagetoimage.R;
import com.example.ambe.imagetoimage.models.MyImage;
import com.example.ambe.imagetoimage.ui.main.MainActivity;
import com.example.ambe.imagetoimage.ui.translate.TranslateActivity;
import com.example.ambe.imagetoimage.utils.Constans;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SelectActivity extends AppCompatActivity implements ISelectView, View.OnClickListener {
    public static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
    private ViewGroup viewGroup;
    private int CAMERA_PIC_REQUEST = 1203;
    private ImageView imgBack;
    private ImageView imgRecoder;
    private RecyclerView rcvListImage;
    private ImageSelectAdapter adapter;
    private SelectPres selectPre;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        addControls();
        addEvents();
    }

    private void addControls() {
        imgBack = findViewById(R.id.img_back_selected);
        imgRecoder = findViewById(R.id.img_camera);
        rcvListImage = findViewById(R.id.rcv_list_my_image);
        selectPre = new SelectPres(this);
        selectPre.getAllImageFromExternal(this);
        GridLayoutManager grd = new GridLayoutManager(getApplicationContext(), 3);
        grd.setOrientation(LinearLayoutManager.VERTICAL);
        rcvListImage.setLayoutManager(grd);
    }

    private void addEvents() {
        imgBack.setOnClickListener(this);
        imgRecoder.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        selectPre = new SelectPres(this);
        selectPre.getAllImageFromExternal(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            MediaScannerConnection.scanFile(
                    getApplicationContext(),
                    new String[]{photoFile.getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            startTranslate(uri);

                        }
                    });


        } else {
            Toast.makeText(SelectActivity.this, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show();
        }

    }

    private void startTranslate(Uri uri) {
        Intent intent = new Intent(this, TranslateActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, Constans.getPath(this, uri));
        startActivity(intent);
    }

    private File photoFile;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_selected:
//                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
                break;
            case R.id.img_camera:
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        photoFile = createImageFile();
                        if (photoFile != null) {
                            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(pictureIntent, CAMERA_PIC_REQUEST);

                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
                break;
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM + "/Camera/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    @Override
    public void onShowListImage(ArrayList<MyImage> arrayList) {
        adapter = new ImageSelectAdapter(getApplicationContext(), arrayList, this);
        rcvListImage.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onShowListImageFail(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onSelectImage(String path) {
        Intent intent = new Intent(SelectActivity.this, TranslateActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, path);
        startActivity(intent);
    }

    @Override
    public void onDeleteImage(final String path) {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        viewGroup = dialog.findViewById(R.id.vgr_dialog_delete);
        LinearLayout lnlOk = dialog.findViewById(R.id.lnl_btn_dialog_ok);
        LinearLayout lnlCancel = dialog.findViewById(R.id.lnl_btn_dialog_cancel);
        lnlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        lnlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = Constans.deleteFile(getApplicationContext(), path);

                if (result == 0) {
                    Toast.makeText(SelectActivity.this, "Could not delete", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(SelectActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    selectPre.getAllImageFromExternal(SelectActivity.this);
                }
                dialog.dismiss();
            }
        });

        Constans.animateDialog(viewGroup);

        dialog.show();

    }
}

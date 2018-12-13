package com.example.ambe.imagetoimage.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ambe.imagetoimage.R;
import com.example.ambe.imagetoimage.models.MyImage;
import com.example.ambe.imagetoimage.ui.select.SelectActivity;
import com.example.ambe.imagetoimage.utils.Constans;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMainView, View.OnClickListener {

    private static final int REQUEST_PERMISSIONS = 100;
    private RelativeLayout rllTranslate;
    private RelativeLayout rllCollection;
    private RelativeLayout rllCsMain;
    private RelativeLayout rllMyStudio;
    private ImageView imgBack;
    private RecyclerView rcvMain;
    private MyImageAdapter adapter;
    private ViewGroup viewGroup;
    private MainPres mainPres;
    private ArrayList<MyImage> arrDelete;
    private ArrayList<String> arrPathDelete;
    private ImageView imgDelete;
    private ImageView imgShare;
    private TextView txtImageToImage;
    private ArrayList<MyImage> arrMyImage;
    private int checkPermission = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iCheckPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission += 1;
        if ((checkPermission == 3 && !checkWriteExternalPermission() || checkPermission == 3 && !checkCameraPermission())) {
            finish();
        } else if (checkPermission == 3 && checkWriteExternalPermission() && checkCameraPermission()) {
            addControls();
            addEvents();
        }
    }

    private void iCheckPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSIONS);
            } else {
                addControls();
                addEvents();
            }
        } else {
            addControls();
            addEvents();
        }

    }

    private void addEvents() {
        rllTranslate.setOnClickListener(this);
        rllCollection.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        imgShare.setOnClickListener(this);
    }

    private void addControls() {
        rllTranslate = findViewById(R.id.rll_translate);
        rllCollection = findViewById(R.id.rll_collection);
        rllCsMain = findViewById(R.id.csl_main);
        rllMyStudio = findViewById(R.id.rll_my_studio);
        imgBack = findViewById(R.id.img_back_collection);
        rcvMain = findViewById(R.id.rcv_my_colection);
        txtImageToImage = findViewById(R.id.txt_image_to_image);
        imgDelete = findViewById(R.id.img_delete_photo_main);
        imgShare = findViewById(R.id.img_share_photo_main);
        arrDelete = new ArrayList<>();
        arrPathDelete = new ArrayList<>();
        arrMyImage = new ArrayList<>();
        txtImageToImage.getPaint().setShader(new LinearGradient(0f, 0f, txtImageToImage.getLineHeight(),

                0f, getResources().getColor(R.color.colorStart), getResources().getColor(R.color.colorEnd), Shader.TileMode.REPEAT));
        mainPres = new MainPres(this);
        GridLayoutManager grd = new GridLayoutManager(getApplicationContext(), 3);
        grd.setOrientation(LinearLayoutManager.VERTICAL);
        rcvMain.setLayoutManager(grd);
    }

    private Boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;

    }

    private Boolean checkCameraPermission() {
        String permission = Manifest.permission.CAMERA;
        int res = checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                addControls();
                addEvents();
            } else {
                Toast.makeText(this, "Please grant permission to the app!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rll_translate:
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.rll_collection:
                mainPres.getAllMyImage(MainActivity.this);
                rllCsMain.setVisibility(View.GONE);
                rllMyStudio.setVisibility(View.VISIBLE);
                break;
            case R.id.img_back_collection:
                arrPathDelete.clear();
                rllCsMain.setVisibility(View.VISIBLE);
                rllMyStudio.setVisibility(View.GONE);
                break;
            case R.id.img_delete_photo_main:
                if (arrPathDelete.size() > 0) {
                    showDialogDeleteVideo(arrPathDelete);
                } else {
                    arrPathDelete.clear();
                    Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.img_share_photo_main:
                shareImage();
                break;

        }

    }

    private void shareImage() {
        if (arrPathDelete.size() > 1) {
            Toast.makeText(this, "Please slect one image", Toast.LENGTH_SHORT).show();

        } else if (arrPathDelete.size() == 0) {
            arrPathDelete.clear();
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        } else {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File file = new File(arrPathDelete.get(0));
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "Share image File"));
        }
    }

    private void showDialogDeleteVideo(final ArrayList<String> arrPathDelete) {

        final Dialog dialog = new Dialog(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                for (String str : arrPathDelete) {
                    File file = new File(str);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                arrMyImage.clear();
                arrPathDelete.clear();
                mainPres.getAllMyImage(MainActivity.this);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        Constans.animateDialog(viewGroup);
        dialog.show();

    }

    @Override
    public void onShowListMyImage(ArrayList<MyImage> arrayList) {
        arrDelete = arrayList;
        arrMyImage = arrayList;
        adapter = new MyImageAdapter(arrMyImage, getApplicationContext(), this);
        adapter.notifyDataSetChanged();
        rcvMain.setAdapter(adapter);

    }

    @Override
    public void onShowListMyImageFail(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSelectImage(String path) {

    }

    @Override
    public void onDeleteImage(int pos, String path, boolean selected) {
        if (selected) {
            arrPathDelete.add(path);
        } else {
            arrPathDelete.remove(path);
        }

    }
}

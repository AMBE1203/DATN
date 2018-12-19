package com.example.ambe.imagetoimage.ui.preview;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ambe.imagetoimage.R;
import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private SlidingImageAdapter adapter;
    private ArrayList<MyImage> arrayList;
    private ImageView imgBack;
    private TextView txtName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        addControls();
        addEvents();
    }

    private void addControls() {

        viewPager = findViewById(R.id.view_pager);
        imgBack = findViewById(R.id.img_back_preview);
        txtName = findViewById(R.id.txt_name_preview);
        arrayList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            arrayList = intent.getParcelableArrayListExtra("LIST");
            adapter = new SlidingImageAdapter(getApplicationContext(), arrayList);
            viewPager.setAdapter(adapter);

            int posSelected = intent.getIntExtra("PATH", 0);
            viewPager.setCurrentItem(posSelected);
            txtName.setText(arrayList.get(posSelected).getName());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    txtName.setText(arrayList.get(i).getName());

                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }


    }

    private void addEvents() {

        imgBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_preview:
                onBackPressed();
                break;
        }
    }
}

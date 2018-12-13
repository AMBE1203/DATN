package com.example.ambe.imagetoimage.ui.main;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ambe.imagetoimage.R;
import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 8:41 AM.
 */
public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.MyHolder> {

    private ArrayList<MyImage> arrayList;
    private Context context;
    private LayoutInflater inflater;
    private IMainView iMainView;

    public MyImageAdapter(ArrayList<MyImage> arrayList, Context context, IMainView iMainView) {
        this.arrayList = arrayList;
        this.context = context;
        this.iMainView = iMainView;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_my_image, viewGroup, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        MyImage myVideo = arrayList.get(i);

        Glide.with(context).load("file://" + myVideo.getPath()).centerCrop().into(myHolder.imgView);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.custom_anim);
        myHolder.imgView.startAnimation(animation);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView imgView;

        public MyHolder(final View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.img_custom);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iMainView.onSelectImage(arrayList.get(getAdapterPosition()).getPath());

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //                notifyDataSetChanged();
                    if (arrayList.get(getAdapterPosition()).isSelected()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            itemView.setBackground(null);
                        }
                        arrayList.get(getAdapterPosition()).setSelected(false);

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_boder));
                        }
                        arrayList.get(getAdapterPosition()).setSelected(true);


                    }

                    iMainView.onDeleteImage(getAdapterPosition(), arrayList.get(getAdapterPosition()).getPath(), arrayList.get(getAdapterPosition()).isSelected());

                    return true;
                }
            });
        }
    }

}

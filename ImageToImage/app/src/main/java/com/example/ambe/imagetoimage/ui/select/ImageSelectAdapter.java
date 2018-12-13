package com.example.ambe.imagetoimage.ui.select;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ambe.imagetoimage.R;
import com.example.ambe.imagetoimage.models.MyImage;

import java.util.ArrayList;

/**
 * Created by AMBE on 12/12/2018 at 9:43 AM.
 */
public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.ImageHolder> {

    private Context context;
    private ArrayList<MyImage> arrayList;
    private LayoutInflater inflater;
    private ISelectView i;

    public ImageSelectAdapter(Context context, ArrayList<MyImage> arrayList, ISelectView i) {
        this.context = context;
        this.arrayList = arrayList;
        this.i = i;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_my_image, viewGroup, false);
        ImageHolder holder = new ImageHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder imageHolder, int i) {
        MyImage myVideo = arrayList.get(i);
        Glide.with(context).load("file://" + myVideo.getPath()).centerCrop().into(imageHolder.imgImage);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.custom_anim);
        imageHolder.imgImage.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        private ImageView imgImage;

        public ImageHolder(View itemView) {
            super(itemView);

            imgImage = itemView.findViewById(R.id.img_custom);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        i.onSelectImage(arrayList.get(getAdapterPosition()).getPath());

                    } catch (NumberFormatException ex) { // handle your exception

                    }


                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    i.onDeleteImage(arrayList.get(getAdapterPosition()).getId());
                    arrayList.remove(getAdapterPosition());
                    return true;
                }
            });


        }
    }
}

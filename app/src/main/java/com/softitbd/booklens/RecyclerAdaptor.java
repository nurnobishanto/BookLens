package com.softitbd.booklens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerAdaptor extends RecyclerView.Adapter<RecyclerAdaptor.ViewHolder>{
    private ArrayList<ImageModel> list;

    public RecyclerAdaptor(ArrayList<ImageModel> list) {
        this.list = list;

    }
    @NonNull
    @Override
    public RecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.custom_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdaptor.ViewHolder holder, int position) {

        ImageModel imageModel = list.get(position);
        if(imageModel.getIs_bitmap()){
            holder.imageView.setImageBitmap(imageModel.getBitmap());
        }else {
            holder.imageView.setImageURI(imageModel.getUri());
        }
        holder.textView.setText(imageModel.getTitle());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageModel.getIs_bitmap()){
                    MainActivity.img.setImageBitmap(imageModel.getBitmap());
                }else {
                    MainActivity.img.setImageURI(imageModel.getUri());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textView=itemView.findViewById(R.id.textView);
        }
    }
}

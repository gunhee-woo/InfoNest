package com.dlog.info_nest.ui.palette.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorRcylAdapter extends RecyclerView.Adapter<ColorRcylAdapter.ViewHolder> {
    ArrayList<String> colorList;
    private ImageView[] figures;
    private boolean[] selected;
    public ColorRcylAdapter(ArrayList<String> colorList, ImageView[] figures){
        this.colorList = colorList;
        this.figures = figures;
        this.selected = new boolean[colorList.size()];
        selected[0] = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_rycl_item, parent, false);;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String color = colorList.get(position);
        final boolean isSelected = selected[position];
        holder.imgView.setBackgroundColor(Color.parseColor(color));
        if(isSelected){
            holder.imgCheck.setVisibility(View.VISIBLE);
        }else holder.imgCheck.setVisibility(View.INVISIBLE);
        holder.imgView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.imgCheck.setVisibility(View.VISIBLE);
                Arrays.fill(selected, false);
                selected[position] = true;
                for (int i = 0 ; i < figures.length ; i ++){
                    figures[i].setBackgroundColor(Color.parseColor(color));
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView;
        ImageView imgCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.img_rcyl_item);
            imgCheck = itemView.findViewById(R.id.img_check);

        }
    }
    //현재 선택된 색상을 반환.
    public String getSelectedColor(){
        int idx = 0;
        for(int i = 0 ; i < selected.length ; i++){
            if(selected[i]){
                idx = i;
                break;
            }
        }
        return colorList.get(idx);
    }

    /**
     * colorId로 색상을 지정.
     * @param colorId 색깔 Id값
     */
    public void setColor(String colorId){
        int idx = colorList.indexOf(colorId);
        Arrays.fill(selected, false);
        selected[idx] = true;
        for (int i = 0 ; i < figures.length ; i ++){
            figures[i].setBackgroundColor(Color.parseColor(colorId));
        }
        notifyDataSetChanged();
    }
}

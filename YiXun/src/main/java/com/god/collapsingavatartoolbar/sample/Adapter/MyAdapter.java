package com.god.collapsingavatartoolbar.sample.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sloydev.collapsingavatartoolbar.sample.R;
import java.util.List;


/**
 * Created by zd on 2016/3/4.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    private List<String>mData;
    private List<Integer>mImages;
    /*
    * 设置回调接口，实现点击与长按
    * */
    public interface OnItemClickListener{
        void OnItemClick(View view,int position);
        void OnItemLongClick(View view,int position);
    }

    public OnItemClickListener mOnItemClickListener;
    public void SetOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }

    public MyAdapter(List<String> Data,List<Integer> Images) {
        super();
        this.mData=Data;
        this.mImages=Images;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.card_item,
                parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        holder.tv.setText(mData.get(position));
        holder.iv.setImageResource(mImages.get(position));

        if(mOnItemClickListener!=null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(holder.itemView, position);
                }

            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.OnItemLongClick(holder.itemView,position);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv;
        public ImageView iv;

        public MyHolder(View View) {
            super(View);
            tv = (TextView) View.findViewById(R.id.id_tv);
            iv= (ImageView) View.findViewById(R.id.id_iv);

        }
    }
}
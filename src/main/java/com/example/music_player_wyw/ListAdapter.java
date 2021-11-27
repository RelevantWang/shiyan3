package com.example.music_player_wyw;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class ViewHolder{
    public ImageView itemIcon;
    public TextView itemMusicName;
    public TextView itemMusicSinger;
    public int defaultTextColor;

    View itemView;

    public ViewHolder(View itemView) {
        if (itemView == null){
            throw new IllegalArgumentException("item View can not be null!");
        }
        this.itemView = itemView;
        itemIcon = itemView.findViewById(R.id.rand_icon);
        itemMusicName = itemView.findViewById(R.id.item_music_name);
        itemMusicSinger = itemView.findViewById(R.id.item_music_singer);

        defaultTextColor = itemMusicName.getCurrentTextColor();
    }
}



public class ListAdapter extends BaseAdapter {
    private List<MusicInfo> musicList;
    private LayoutInflater layoutInflater;
    private Context context;
    private int currentPos = -1;
    private ViewHolder holder = null;

    public ListAdapter(Context context,List<MusicInfo> musicList) {
        this.musicList = musicList;
        this.context = context;

        layoutInflater = LayoutInflater.from(context);
    }

    public void setFocusItemPos(int pos){
        currentPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position).getMusic_title();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int index){
        musicList.remove(index);
    }

    public void refreshDataSet(){
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_layout,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        //如果是正在播放的音乐 就改变图片、字体颜色
        if (position == currentPos){
            holder.itemIcon.setImageBitmap(BitmapFactory.decodeResource(
                    context.getResources(),R.drawable.arrow));
            holder.itemMusicName.setTextColor(Color.RED);
            holder.itemMusicSinger.setTextColor(Color.RED);
        }
        //否则使用默认图片、字体颜色
        else{
            holder.itemIcon.setImageBitmap(BitmapFactory.decodeResource(
                    context.getResources(),R.drawable.music));
            holder.itemMusicName.setTextColor(holder.defaultTextColor);
            holder.itemMusicSinger.setTextColor(holder.defaultTextColor);
        }
        holder.itemMusicName.setText(musicList.get(position).getMusic_title());
        holder.itemMusicSinger.setText(musicList.get(position).getMusic_artist());
        return convertView;
    }
}

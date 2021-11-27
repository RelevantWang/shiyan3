package com.example.music_player_wyw;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {
    /*
     * 用于获取本地Music目录下的所有音乐信息，并封装成List后返回
     * 需要一个Context对象
     * */
    public static List<MusicInfo> ResolveMusicToList(Context context){
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME+"";
        List<MusicInfo> musicList = new ArrayList<MusicInfo>();

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,selection,null,sortOrder);

        if (cursor != null){
            for (cursor.moveToFirst(); cursor.isAfterLast() != true; cursor.moveToNext()){
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setMusic_title(cursor.getString(0));
                musicInfo.setMusic_artist(cursor.getString(1));
                musicInfo.setMusic_name(cursor.getString(2));
                musicInfo.setMusic_path(cursor.getString(3));
                musicInfo.setMusic_duration(Integer.parseInt(cursor.getString(4)));

                musicList.add(musicInfo);
            }
        }
        return musicList;
    }
}

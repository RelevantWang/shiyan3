package com.example.music_player_wyw;

public interface MyBinderInterface {
    //暂停
    void Pause();
    //恢复
    void Resume();
    //播放
    void Play();
    //播放下一首
    void PlayNext();
    //播放上一首
    void PlayPrev();
    //释放
    void Release();
    //是否正在播
    boolean isPlaying();
    //获取时长
    int getDuration();
    //当前位置
    int getCurrentPosition();
    //拖动位置
    void seekTo(int length);
    //获取当前索引
    int getCurrentIndex();
    //设置当前索引
    void setCurrentIndex(int currentIdx);
}


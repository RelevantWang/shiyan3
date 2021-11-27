package com.example.music_player_wyw;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<MusicInfo> musicList = new ArrayList<MusicInfo>();
    private ListAdapter mListAdapter;
    private ListView mListView;
    //记录长按的列表项坐标
    private int currentSel;
    //按钮
    private ImageView btnPrevious;
    private ImageView btnNext;
    private ImageView btnPlay;
    //文本
    private TextView listTitle;
    private TextView playingName;
    //进度条
    private SeekBar musicSeekBar;
    //自定义Binder对象 用于调用服务中的方法
    private MyBinderInterface myBinder;
    //自定义服务连接对象
    private MyServiceConnection conn;
    //是否正在播放
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            if (myBinder != null){
                try {
                    if (myBinder.isPlaying()){
                        int duration = myBinder.getDuration();
                        int currentPos = myBinder.getCurrentPosition();
                        musicSeekBar.setMax(duration);
                        musicSeekBar.setProgress(currentPos);

                        int prg_sec = currentPos/1000;
                        int max_sec = duration/1000;
                        if (prg_sec == max_sec){
                            myBinder.PlayNext();
                            updateState();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            handler.post(updateThread);
        }
    };

    //定义服务连接
    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyBinderInterface)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    //更新播放状态
    private void updateState() {
        int index = myBinder.getCurrentIndex();
        mListAdapter.setFocusItemPos(index);
        String currentMusicName = musicList.get(index).getMusic_title();
        playingName.setText(currentMusicName);
        btnPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));
        isPlaying = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //通过工具类MusicUtils获取音乐信息列表
        musicList = MusicUtils.ResolveMusicToList(getApplicationContext());
        //获取视图
        initView();
        //设置列表标题
        String title = getResources().getString(R.string.title_string).toString();
        title += "（总数："+ musicList.size() + "）";
        listTitle.setText(title);
        //为mListView注册上下文菜单
        registerForContextMenu(mListView);
        conn = new MyServiceConnection();
        //绑定服务
        bindService(new Intent(this,MusicService.class),conn, Context.BIND_AUTO_CREATE);

        handler.post(updateThread);
    }
    //初始化视图
    private void initView(){
        listTitle = (TextView)findViewById(R.id.music_list_title);
        playingName = (TextView)findViewById(R.id.music_name);
        musicSeekBar = (SeekBar)findViewById(R.id.music_seek_bar);

        btnPrevious = (ImageView)findViewById(R.id.btn_previous);
        btnNext = (ImageView)findViewById(R.id.btn_next);
        btnPlay = (ImageView)findViewById(R.id.btn_play);

        mListView = (ListView)findViewById(R.id.music_list);
        mListAdapter = new ListAdapter(MainActivity.this,musicList);
        mListView.setAdapter(mListAdapter);

        setListener();
    }
    //设置监听事件
    private void setListener(){
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.showContextMenu();
                currentSel = position;
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myBinder.setCurrentIndex(position);
                myBinder.seekTo(0);
                myBinder.Play();
                mListAdapter.setFocusItemPos(position);
                updateState();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBinder.PlayPrev();
                updateState();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBinder.PlayNext();
                updateState();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying == true){
                    btnPlay.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(),R.drawable.play));
                    isPlaying = false;
                    myBinder.Pause();
                    return;
                }
                if (isPlaying == false){
                    if (myBinder.getCurrentIndex() == -1){
                        myBinder.setCurrentIndex(0);
                        mListAdapter.setFocusItemPos(0);
                        myBinder.Play();
                        updateState();
                    }
                    btnPlay.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(),R.drawable.pause));
                    isPlaying = true;
                    myBinder.Resume();

                }
            }
        });

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myBinder != null){
                    try {
                        myBinder.seekTo(seekBar.getProgress());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0,0,0,R.string.menu_detail);
        menu.add(0,1,1,R.string.menu_play);
        super.onCreateContextMenu(menu,view,menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("文件名：" + musicList.get(currentSel).getMusic_name() + "\n");
                msgBuilder.append("路  径：" + musicList.get(currentSel).getMusic_path() + "\n");
                msgBuilder.append("时  长：" + musicList.get(currentSel).getMusic_duration()/1000 + " s\n");
                String title = "文件详情";
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.note)
                        .setTitle(title)
                        .setMessage(msgBuilder.toString())
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }}).create().show();
                break;
            case 1:
                //不处于播放状态 或者 选择的歌曲和正在播放的歌曲不是同一首 则更新状态且播放
                if (isPlaying == false || currentSel != myBinder.getCurrentIndex()){
                    myBinder.setCurrentIndex(currentSel);
                    updateState();
                    myBinder.Play();
                }
                //提示选择的歌曲已经在播放了
                else{
                    Toast.makeText(MainActivity.this,R.string.str_playing,Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retValue = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return retValue;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_about){
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append("音乐播放器\n");
            msgBuilder.append("作者：王亚伟\n");
            msgBuilder.append("2019013006 软工1902");
            String title = "关于";
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.drawable.note)
                    .setTitle(title)
                    .setMessage(msgBuilder.toString())
                    .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }}).create().show();
        }
        if (item.getItemId() == R.id.item_exit){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String title = "提示";
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.note)
                .setTitle(title)
                .setMessage("确定要退出吗?")
                .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myBinder.Release();
                        finish();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }
}

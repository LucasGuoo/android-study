package com.example.draw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity {
    private Switch aSwitch;

    private VideoView videoView;
    private MediaController mediaController;

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private ImageView imageViewPlay;
    private SeekBar seekBarProgress;
    private TextView textViewDuration, textViewProgress;
    public Handler handler;
    private int playStatus;
    /*
    0-停止中（没有播放）
    1-暂停中
    2-播放中
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio);

        // 获取控件
        videoView = findViewById(R.id.videoView);
        surfaceView = findViewById(R.id.surfaceView);
        imageViewPlay = findViewById(R.id.imageViewVideoPlay);
        aSwitch = findViewById(R.id.switch1);
        seekBarProgress = findViewById(R.id.seekBarVideoProgress);
        textViewDuration = findViewById(R.id.textViewVideoDuration);
        textViewProgress = findViewById(R.id.textViewVideoProgress);

        // 创建Handler对象
        handler = new Handler(Looper.getMainLooper()){
            // 重写handleMessage，处理消息
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) { // 定期更新进度条
                    //获取时长duration和当前进度progress
                    int duration = mediaPlayer.getDuration();
                    int progress = mediaPlayer.getCurrentPosition();
                    //对进度条进行设置
                    seekBarProgress.setMax(duration);
                    seekBarProgress.setProgress(progress);
                    textViewDuration.setText(changeFormat(duration));
                    textViewProgress.setText(changeFormat(progress));
                    handler.sendEmptyMessageDelayed(1, 100);
                }
            }
        };

        // 利用VideoView播放视频
        initVideoView();

        // 利用MediaPlayer + SurfaceView播放视频
        mediaPlayer = new MediaPlayer(); // 创建mediaPlayer对象
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
            }
        });
        initSurfaceView();

        // 初始界面：默认不使用VideoView
        startPlay();

        // 事件监听
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    videoView.setVisibility(View.VISIBLE);
                    surfaceView.setVisibility(View.GONE);
                    imageViewPlay.setVisibility(View.GONE);
                    seekBarProgress.setVisibility(View.GONE);
                    textViewDuration.setVisibility(View.GONE);
                    textViewProgress.setVisibility(View.GONE);
                    videoView.start(); // 开始播放，也可以注释掉选择默认不播放
                } else {
                    videoView.setVisibility(View.GONE);
                    surfaceView.setVisibility(View.VISIBLE);
                    imageViewPlay.setVisibility(View.VISIBLE);
                    seekBarProgress.setVisibility(View.VISIBLE);
                    textViewDuration.setVisibility(View.VISIBLE);
                    textViewProgress.setVisibility(View.VISIBLE);
                    startPlay(); // 开始播放
                }
            }
        });
        // 事件监听
        imageViewPlay.setOnClickListener(v -> {
            if (playStatus == 0) { // 还没有播放，停止中
                startPlay();
            } else if (playStatus == 1) { // 暂停中
                resumePlay();
            } else { // 播放中
                pausePlay();
            }
        });
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // 滑动条进度变化时调用
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            //开始拖动滑动条时调用（按下鼠标时）
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //结束拖动滑动条时调用（松开鼠标时）
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //根据拖动的进度改变音乐播放进度
                int progress = seekBar.getProgress(); // 获取seekBar的进度
                seekTo(progress); // 调整播放进度
            }
        });
    }

    private void initVideoView() {
        mediaController = new MediaController(this); // 创建mediaController对象
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/video1");
        videoView.setVideoURI(uri); // 设置视频文件
        videoView.setMediaController(mediaController); // 将mediaController和videoView相关联
        mediaController.setAnchorView(videoView); // 设置mediaController的参考视图，用于确定其显示位置（显示在其底部）
    }

    private void initMediaPlayer() {
        try {
            // 重置mediaPlayer对象
            mediaPlayer.reset();
            // 加载视频文件
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/video2"); // 获取URI
            mediaPlayer.setDataSource(VideoActivity.this, uri);
            // 做好准备
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initSurfaceView() {
        surfaceHolder = surfaceView.getHolder(); // 获取surfaceHolder
        surfaceHolder.addCallback(new SurfaceHolder.Callback() { // 添加callback
            // SurfaceView 被创建时调用
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                // 将画面输出到SurfaceView的SurfaceHolder中
                mediaPlayer.setDisplay(surfaceHolder);
            }

            // SurfaceView 的大小或格式发生变化时调用
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            // SurfaceView 被销毁时调用
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        stopPlay();
                    }
                }
            }
        });
    }

    private void startPlay() {
        initMediaPlayer();

        mediaPlayer.start();
        imageViewPlay.setImageResource(R.drawable.baseline_pause_24);
        playStatus = 2;
        handler.sendEmptyMessageAtTime(1, 100);
    }

    private void resumePlay() {
        mediaPlayer.start();
        imageViewPlay.setImageResource(R.drawable.baseline_pause_24);
        playStatus = 2;
        handler.sendEmptyMessageAtTime(1, 100);
    }

    private void pausePlay() {
        mediaPlayer.pause();
        imageViewPlay.setImageResource(R.drawable.baseline_play_arrow_24);
        playStatus = 1;
        handler.removeMessages(1);
    }

    private void stopPlay() {
        mediaPlayer.stop();
        imageViewPlay.setImageResource(R.drawable.baseline_play_arrow_24);
        playStatus = 0;
        handler.removeMessages(1);
    }

    private void seekTo(int progress) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(progress);
        }
    }

    // ms -> xxm: xxs
    private String changeFormat(int millionSeconds) {
        int minutes = millionSeconds / 1000 / 60;
        int seconds = millionSeconds / 1000 % 60;
        String m = String.format("%02d", minutes); // 两位数，用0填充
        String s = String.format("%02d", seconds);
        return m + ":" + s;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放mediaPlayer所占资源
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopPlay();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
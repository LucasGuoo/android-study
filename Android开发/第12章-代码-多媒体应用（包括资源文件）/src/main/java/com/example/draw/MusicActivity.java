package com.example.draw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    private TextView textViewTitle, textViewProgress, textViewDuration;
    private ImageView imageViewPicture, imageViewPrevious, imageViewNext, imageViewRewind, imageViewForward, imageViewPlay;
    private SeekBar seekBarProgress;
    private ObjectAnimator animator;
    public Handler handler;
    private ArrayList<Song> songs;
    private int curSongIndex;
    private Song curSong;
    private int playStatus;
    private MediaPlayer mediaPlayer;
    /*
    0-停止中（没有播放）
    1-暂停中
    2-播放中
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // 获取控件
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewProgress = findViewById(R.id.textViewProgress);
        textViewDuration = findViewById(R.id.textViewDuration);
        imageViewPicture = findViewById(R.id.imageViewPicture);
        imageViewPrevious = findViewById(R.id.imageViewPrevious);
        imageViewNext = findViewById(R.id.imageViewNext);
        imageViewRewind = findViewById(R.id.imageViewPauseVideo);
        imageViewForward = findViewById(R.id.imageViewForward);
        imageViewPlay = findViewById(R.id.imageViewPlay);
        seekBarProgress = findViewById(R.id.seekBarProgress);

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

        // 创建属性动画，旋转效果
        animator = ObjectAnimator.ofFloat(imageViewPicture, "rotation", 0f, 360.0f);
        animator.setDuration(10000); // 动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator()); // 匀速
        animator.setRepeatCount(Animation.INFINITE); // -1表示设置动画无限循环

        // 获取歌曲资源，生成播放列表
        songs = geneSongs();
        curSongIndex = 0;
        curSong = songs.get(curSongIndex);

        // 初始化MediaPlayer对象
        mediaPlayer = new MediaPlayer();
        // 事件监听
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
                playNextSong();
            }
        });
        initMediaPlayer();

        // 初始化界面
        textViewTitle.setText(curSong.getName());
        imageViewPicture.setImageResource(curSong.getPictureId());
        textViewProgress.setText(changeFormat(0));
        textViewDuration.setText(changeFormat(mediaPlayer.getDuration()));

        // 事件监听
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

        imageViewPlay.setOnClickListener(v -> {
            if (playStatus == 0) { // 还没有播放，停止中
                startPlay();
            } else if (playStatus == 1) { // 暂停中
                resumePlay();
            } else { // 播放中
                pausePlay();
            }
        });

        imageViewPrevious.setOnClickListener(v -> {
            playPreviousSong();
        });

        imageViewNext.setOnClickListener(v -> {
            playNextSong();
        });

        imageViewRewind.setOnClickListener(v -> {
            rewind();
        });

        imageViewForward.setOnClickListener(v -> {
            forward();
        });
    }

    private void initMediaPlayer() {
        // 方式一：重新创建（需要重新写事件监听）
        // mediaPlayer = MediaPlayer.create(this, curSong.getSongId());
        // 事件监听
        // mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        //     @Override
        //     public void onCompletion(MediaPlayer mp) {
        //         stopPlay();
        //         playNextSong();
        //     }
        // });

        // 方式二：修改数据源 （更节省内存）
        // 重置音乐
        mediaPlayer.reset();
        try {
            //加载音乐
            AssetFileDescriptor afd = getResources().openRawResourceFd(curSong.getSongId());
            mediaPlayer.setDataSource(afd);
            afd.close();
            // 做好准备
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startPlay() {
        // 初始化MediaPlayer对象
        initMediaPlayer();

        mediaPlayer.start();
        animator.start();
        imageViewPlay.setImageResource(R.drawable.baseline_pause_24);
        playStatus = 2;
        handler.sendEmptyMessageAtTime(1, 100);
    }

    private void resumePlay() {
        mediaPlayer.start();
        animator.resume();
        imageViewPlay.setImageResource(R.drawable.baseline_pause_24);
        playStatus = 2;
        handler.sendEmptyMessageAtTime(1, 100);
    }

    private void pausePlay() {
        mediaPlayer.pause();
        animator.pause();
        imageViewPlay.setImageResource(R.drawable.baseline_play_arrow_24);
        playStatus = 1;
        handler.removeMessages(1);
    }

    private void stopPlay() {
        mediaPlayer.stop();
        animator.end();
        imageViewPlay.setImageResource(R.drawable.baseline_play_arrow_24);
        playStatus = 0;
        handler.removeMessages(1);
    }

    private void rewind() {
        if (mediaPlayer.isPlaying()) {
            int progress = mediaPlayer.getCurrentPosition(); // 当前播放进度
            mediaPlayer.seekTo(Math.max(0, progress - 5000)); // 回退5s
        } else {
            Toast.makeText(this, "请先播放音乐", Toast.LENGTH_SHORT).show();
        }
    }

    private void forward() {
        if (mediaPlayer.isPlaying()) {
            int duration = mediaPlayer.getDuration(); // 歌曲总时长
            int progress = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(Math.min(duration, progress + 5000)); // 前进5s
        } else {
            Toast.makeText(this, "请先播放音乐", Toast.LENGTH_SHORT).show();
        }
    }

    private void seekTo(int progress) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(progress);
        }
    }

    private void playNextSong() {
        if (curSongIndex + 1 >= songs.size()) {
            Toast.makeText(this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
        } else {
            if (mediaPlayer.isPlaying()) {
                stopPlay();
            }
            curSongIndex++;
            curSong = songs.get(curSongIndex);
            textViewTitle.setText(curSong.getName());
            imageViewPicture.setImageResource(curSong.getPictureId());
            startPlay(); // 再播放下一首
        }
    }

    private void playPreviousSong() {
        if (curSongIndex - 1 < 0) {
            Toast.makeText(this, "已经是第一首了", Toast.LENGTH_SHORT).show();
        } else {
            if (mediaPlayer.isPlaying()) {
                stopPlay();
            }
            curSongIndex--;
            curSong = songs.get(curSongIndex);
            textViewTitle.setText(curSong.getName());
            imageViewPicture.setImageResource(curSong.getPictureId());
            startPlay();
        }
    }

    ArrayList<Song> geneSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        Song song1 = new Song("爱人错过", R.drawable.song1, R.raw.song1);
        Song song2 = new Song("梅香如故", R.drawable.song2, R.raw.song2);
        Song song3 = new Song("小美满", R.drawable.song3, R.raw.song3);
        songs.add(song1);
        songs.add(song2);
        songs.add(song3);
        return songs;
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
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopPlay();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
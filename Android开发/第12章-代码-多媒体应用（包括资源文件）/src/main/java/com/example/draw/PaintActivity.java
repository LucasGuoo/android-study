package com.example.draw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class PaintActivity extends AppCompatActivity {
    DrawBoard drawBoard;
    Button buttonSave, buttonClear;
    TextView textViewPaint;
    RadioGroup radioGroupColor;
    SeekBar seekBar;
    Handler handler;

    private static final int SUCCESS = 1;
    private static final int FAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        // 获取控件
        drawBoard = findViewById(R.id.myDrawBoard);
        buttonSave = findViewById(R.id.buttonSave);
        buttonClear = findViewById(R.id.buttonClear);
        textViewPaint = findViewById(R.id.textViewPaint);
        radioGroupColor = findViewById(R.id.radioGroupColor);
        seekBar = findViewById(R.id.seekBar);

        // 创建Handle对象
        handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS:
                        Toast.makeText(PaintActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                        break;
                    case FAIL:
                        Toast.makeText(PaintActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        // 事件监听

        // 设置画笔颜色
        radioGroupColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setPaintColor(checkedId);
            }
        });
        // 设置画笔粗细
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setStrokeWidth(progress);
                textViewPaint.setText(String.format("画笔粗细：%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // 保存图片
        buttonSave.setOnClickListener(v -> {
            checkPermission();
        });
        // 清空画布
        buttonClear.setOnClickListener(v ->
                drawBoard.clear());
    }

    private void setPaintColor(int checkedId) {
        if (checkedId == R.id.radioButtonRed) {
            drawBoard.setPaintColor(getColor(R.color.red));
        } else if (checkedId == R.id.radioButtonBlue) {
            drawBoard.setPaintColor(getColor(R.color.blue));
        } else if (checkedId == R.id.radioButtonPurple) {
            drawBoard.setPaintColor(getColor(R.color.purple));
        } else if (checkedId == R.id.radioButtonBlack) {
            drawBoard.setPaintColor(getColor(R.color.black));
        }
    }

    private void setStrokeWidth(int width) {
        drawBoard.setStrokeWidth(width);
    }

    private void savePhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = drawBoard.getBitmap();
                // 保存图片到相册
                // 方法一
                // String result = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
                // handler.sendEmptyMessage(result == null ? SUCCESS : FAIL);
                // 方法二
                ContentValues values = new ContentValues();
                // 插入空的内容，获得一个插入后的URI
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    handler.sendEmptyMessage(FAIL);
                } else {
                    try {
                        // 获取指定URI的输出流
                        OutputStream os = getContentResolver().openOutputStream(uri);
                        // 将图片压缩到指定输出流
                        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
                            handler.sendEmptyMessage(SUCCESS);
                        } else {
                            handler.sendEmptyMessage(FAIL);
                        }
                        os.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    // 动态授权
    private void checkPermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, 1);
        } else {
            savePhoto();
        }
    }

    // 重写回调方法，处理授权结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                savePhoto();
            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 回收资源
        Bitmap bitmap = drawBoard.getBitmap();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
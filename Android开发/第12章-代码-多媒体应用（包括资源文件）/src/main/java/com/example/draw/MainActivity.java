package com.example.draw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button buttonDraw, buttonAnimation, buttonMusic, buttonVideo;
    ImageView imageView;
    Bitmap bitmap;
    int width; // imageView's width
    int height; // imageView's height

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取控件
        buttonDraw = findViewById(R.id.buttonDraw);
        buttonAnimation = findViewById(R.id.buttonAnimation);
        buttonMusic = findViewById(R.id.buttonMusic);
        buttonVideo = findViewById(R.id.buttonVideo);
        imageView = findViewById(R.id.imageView);

        // 事件监听
        buttonDraw.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaintActivity.class);
            startActivity(intent);
        });
        buttonAnimation.setOnClickListener(v -> {
            Intent intent = new Intent(this, AnimationActivity.class);
            startActivity(intent);
        });
        buttonMusic.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicActivity.class);
            startActivity(intent);
        });
        buttonVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoActivity.class);
            startActivity(intent);
        });
    }

    // 获取到窗口焦点时回调
    /*
    因为在onCreate()中视图还没建成，在onCreate()中获取imageView.getWidth()会返回0
    所以需要在视图建成后，例如在onWindowFocusChanged()获取imageView.getWidth()，才能得到正确的值
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            width = imageView.getWidth(); // 返回的是px（像素数），统一用px
            height = imageView.getHeight();
            bitmap = drawMyPicture();
            // bitmap = drawMyRobot();
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap drawMyPicture(){
        // 画笔
        Paint paint = new Paint();
        paint.setARGB(255, 103, 80, 164); // 画笔颜色
        paint.setStyle(Paint.Style.STROKE);  // 画笔风格
        paint.setStrokeWidth(10); // 画笔粗细

        // 创建Canvas对象
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // 设置画布背景颜色
        canvas.drawColor(Color.WHITE);

        // 画圆
        canvas.drawCircle(200, 200, 100, paint); // 单位均为 px
        // 画矩形
        canvas.drawRect(100, 350, 300, 500, paint);
        // 画扇形
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(new RectF(100, 550, 300, 700), 0, 235, true, paint);
        // 绘制圆角矩形
        canvas.drawRoundRect(new RectF(100,750,300,900),20,20, paint);
        // 画线条
        canvas.drawLine(100, 950, 300, 950, paint);
        // 添加文本
        paint.setColor(Color.BLACK);
        paint.setTextSize(50); // 单位为px
        canvas.drawText("绘制文本", 100, 1100, paint);

        paint.setARGB(255, 197, 165, 204);
        paint.setStyle(Paint.Style.STROKE);
        //移动坐标系
        canvas.translate(width / 2 , 0);
        // 绘制路径
        Path path = new Path();
        path.moveTo(0, 100);
        path.lineTo(200, 200);
        path.lineTo(100, 300);
        path.lineTo(220, 350);
        path.lineTo(150, 400);
        canvas.drawPath(path, paint);

        //移动坐标系
        canvas.translate(0 , height / 2);
        // 路径效果
        paint.setColor(Color.GREEN);
        CornerPathEffect effect = new CornerPathEffect(20);
        paint.setPathEffect(effect);
        canvas.drawPath(path, paint);

        return bitmap;
    }

    private Bitmap drawMyRobot() {
        Paint paint = new Paint();  // 创建一个画笔
        paint.setAntiAlias(true);   // 抗锯齿,使它更加圆滑
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 绘制机器人
        canvas.scale(2.5f, 2.5f); // 缩放坐标系
        // 绘制机器人的头
        paint.setColor(0xFFA4C739);
        RectF rectF = new RectF(10,10,100,100); //定义外轮廓矩形
        rectF.offset(90,20);
        canvas.drawArc(rectF,-10,-160,false, paint);  //绘制弧

        // 绘制眼睛
        paint.setColor(0xFFFFFFFF); //设置画笔为白色
        canvas.drawCircle(165,53,4,paint); //绘制圆
        canvas.drawCircle(125,53,4,paint);

        //绘制天线
        paint.setColor(0xFFA4C739);  //设置画笔颜色
        paint.setStrokeWidth(2);  // 设置画笔的宽度
        canvas.drawLine(110,15,125,35,paint); //绘制线
        canvas.drawLine(180,15,165,35,paint); //绘制线

        // 绘制身体
        canvas.drawRect(100,75,190,150,paint);  //绘制矩形
        RectF rectF_body = new RectF(100,140,190,160);
        canvas.drawRoundRect(rectF_body,10,10,paint);  //绘制圆角矩形

        // 绘制胳膊
        RectF rectF_arm = new RectF(75,75,95,140);
        canvas.drawRoundRect(rectF_arm,10,10,paint);  //绘制圆角矩形
        rectF_arm.offset(120,0);
        canvas.drawRoundRect(rectF_arm,10,10,paint);  //绘制圆角矩形

        //绘制腿
        RectF rectF_leg = new RectF(115,150,135,200);
        canvas.drawRoundRect(rectF_leg,10,10,paint);  //绘制圆角矩形
        rectF_leg.offset(40,0);
        canvas.drawRoundRect(rectF_leg,10,10,paint);  //绘制圆角矩形

        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
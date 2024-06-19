package com.example.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrawBoard extends View {
    private Paint paint; // 画笔
    private Canvas canvas; // 画布
    private Bitmap bitmap; // 用来保存绘画
    private int lastX;
    private int lastY;
    int width, height;

    // 在Java代码中直接创建View对象时使用
    public DrawBoard(Context context) {
        super(context);
        init();
    }

    // 在XML布局文件中使用，AttributeSet attrs包含了该View的属性
    public DrawBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化画笔样式
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);   // 设置画笔宽度
        paint.setAntiAlias(true); // 抗锯齿
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.STROKE); // 描边
        paint.setStrokeJoin(Paint.Join.ROUND); //结合处为圆角
        paint.setStrokeCap(Paint.Cap.ROUND); // 设置转弯处为圆角
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // 注意区分this.canvas和canvas
        if (bitmap == null) {
            width = getWidth();
            height = getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 绘画保存到bitmap
            this.canvas = new Canvas(bitmap);
            this.canvas.drawColor(Color.WHITE); // 默认是透明的
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 获取鼠标按下时的坐标
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取鼠标移动后的坐标
                //在开始和结束之间画一条直线
                canvas.drawLine(lastX, lastY, x, y, paint);
                // 实时更新开始坐标
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate(); // 更新绘图，调用onDraw
        return true;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    public void setStrokeWidth(int width) {
        paint.setStrokeWidth(width);
    }

    public void clear() {
        if (bitmap != null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            canvas.drawColor(Color.WHITE);
            invalidate();
        }
    }
}

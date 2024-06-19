package com.example.draw;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class AnimationActivity extends AppCompatActivity {
    Button buttonFrameAnimation, buttonTranslate, buttonScale, buttonRotate, buttonAlpha;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        // 获取控件
        buttonFrameAnimation = findViewById(R.id.buttonFrameAnimation);
        buttonTranslate = findViewById(R.id.buttonTranslate);
        buttonScale = findViewById(R.id.buttonScale);
        buttonRotate = findViewById(R.id.buttonRotate);
        buttonAlpha = findViewById(R.id.buttonAlpha);
        imageView = findViewById(R.id.imageViewAnimation);

        // 事件监听
        buttonFrameAnimation.setOnClickListener(v -> {
            setFrameByFrameAnimation();
        });
        buttonTranslate.setOnClickListener(v -> {
            setTranslateAnimation();
        });
        buttonScale.setOnClickListener(v -> {
            setScaleAnimation();
        });
        buttonRotate.setOnClickListener(v -> {
            setRotateAnimation();
        });
        buttonAlpha.setOnClickListener(v -> {
            setAlphaAnimation();
        });
    }

    private void setFrameByFrameAnimation() {
        // 通过XML代码创建逐帧动画
        imageView.setImageResource(R.drawable.frame_animation);
        AnimationDrawable animation = (AnimationDrawable) imageView.getDrawable();
        animation.start();

        // // 通过Java代码创建逐帧动画（AnimationDrawable）
        // AnimationDrawable animationDrawable = new AnimationDrawable();
        // animationDrawable.addFrame(getDrawable(R.drawable.song1), 300);
        // animationDrawable.addFrame(getDrawable(R.drawable.song2), 300);
        // animationDrawable.addFrame(getDrawable(R.drawable.song3), 300);
        // imageView.setImageDrawable(animationDrawable);
        // animationDrawable.start();
    }

    private void setTranslateAnimation() {
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        // 加载动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_animation);
        // 启动动画
        imageView.startAnimation(animation);
    }

    private void setScaleAnimation() {
        imageView.setImageResource(R.mipmap.ic_launcher_round);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_animation);
        imageView.startAnimation(animation);
    }

    private void setRotateAnimation() {
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        // // 通过XML代码创建补间动画
        // Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        // imageView.startAnimation(animation);

        // // 通过Java代码创建补间动画（RotateAnimation）
        // RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // 创建旋转效果的补间动画
        // animation.setDuration(1000); // 设置持续时间
        // animation.setInterpolator(new LinearInterpolator()); // 匀速
        // animation.setRepeatCount(1); // 重复1次，共2次
        // imageView.startAnimation(animation); // 启动动画

        // // 通过XML文件创建属性动画
        // Animator animator = AnimatorInflater.loadAnimator(this, R.animator.rotate_animator);
        // animator.setTarget(imageView);
        // animator.start();

        // 通过Java文件创建属性动画（ObjectAnimator）
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f); // 创建 ObjectAnimator 对象
        animator.setDuration(1000); // // 设置动画持续时间
        animator.setInterpolator(new LinearInterpolator()); // 匀速
        animator.setRepeatCount(1); // -1 表示设置动画无限循环
        animator.start(); // 启动动画
    }

    private void setAlphaAnimation() {
        imageView.setImageResource(R.mipmap.ic_launcher_round);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_animation);
        imageView.startAnimation(animation);
    }
}
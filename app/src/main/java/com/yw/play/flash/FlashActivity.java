package com.yw.play.flash;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yw.play.R;
import com.yw.play.main.MainActivity;
import com.yw.play.utils.WindowUtils;

public class FlashActivity extends AppCompatActivity {
    private ImageView img_logo;
    private TextView tv_name,tv_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        initView();
        playAnim();

    }

    public void initView(){
        img_logo = (ImageView)findViewById(R.id.img_logo);
        tv_auth = (TextView)findViewById(R.id.tv_auth);
        tv_name = (TextView)findViewById(R.id.tv_name);
    }

    public void playAnim(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_logo,"translationX",-400,img_logo.getX());
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(tv_name,"translationX"
                ,WindowUtils.getInstance(getWindowManager()).getWindowWidth(),tv_name.getX());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator,objectAnimator1);
        animatorSet.setDuration(500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                tv_auth.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(tv_auth,"alpha",0f,1f);
                ObjectAnimator objectAnimator3 = ObjectAnimator.ofObject(tv_name,"color"
                        ,new ArgbEvaluator(), Color.parseColor("#1b1b1b"),Color.parseColor("#ffffff"));
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.playTogether(objectAnimator2,objectAnimator3);
                animatorSet1.setDuration(1000);
                animatorSet1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Intent intent = new Intent(FlashActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animatorSet1.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

}

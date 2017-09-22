package com.yw.play.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yw.play.R;
import com.yw.play.adapter.MyFragmentPagerAdapter;
import com.yw.play.base.BaseFragment;
import com.yw.play.data.MusicInfo;
import com.yw.play.main.music.MusicFragment;
import com.yw.play.main.video.VideoFragment;
import com.yw.play.musicfragment.MusicListConstract;
import com.yw.play.musicfragment.MusicMoreConstract;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MusicListConstract,MusicMoreConstract{
    public static final int PERMISSION = 1;//权限获取标记

    private ViewPager viewpager;
    private TabLayout tablayout;
    private MyFragmentPagerAdapter adapter;//viewpager适配器
    private List<BaseFragment> fragmentList = new ArrayList<>();//存放viewpager的fragment
    private List<String> stringList = new ArrayList<>();// 存放tablayout显示的字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        }
        initView();
    }

    /**
     * 初始化UI
     */
    public void initView(){
        viewpager = (ViewPager)findViewById(R.id.viewpager);
        tablayout = (TabLayout)findViewById(R.id.tablayout);

        fragmentList.add(new VideoFragment());
        fragmentList.add(new MusicFragment());

        stringList.add("视频");
        stringList.add("音乐");

        //设置viewpager的适配器
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,stringList);
        viewpager.setAdapter(adapter);
        //设置tablayout绑定viewpager
        tablayout.setupWithViewPager(viewpager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (requestCode == PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragmentList.get(0).refreshInfo();
                    fragmentList.get(1).refreshInfo();
                } else {
                    Toast.makeText(this, "请给予读写的权限", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onBackPressed() {
        if (fragmentList.get(1).isShowList()){
            hideView();
            return;
        }
        moveTaskToBack(true);
    }

    @Override
    public List<MusicInfo> getMusicList() {
        return ((MusicListConstract)fragmentList.get(1)).getMusicList();
    }

    @Override
    public void hideView() {
        ((MusicListConstract)fragmentList.get(1)).hideView();
    }

    @Override
    public void playMusic(int position) {
        ((MusicListConstract)fragmentList.get(1)).playMusic(position);
    }

    @Override
    public void setPlayType(int type) {
        ((MusicListConstract)fragmentList.get(1)).setPlayType(type);
    }

    @Override
    public int getPlayType() {
        return ((MusicListConstract)fragmentList.get(1)).getPlayType();
    }

    @Override
    public void checkDetail(String Past) {
        ((MusicMoreConstract)fragmentList.get(1)).checkDetail(Past);
    }

    @Override
    public void setNext(String Path) {
        ((MusicMoreConstract)fragmentList.get(1)).setNext(Path);
    }

    @Override
    public void showView(String path) {
        ((MusicMoreConstract)fragmentList.get(1)).showView(path);
    }
}

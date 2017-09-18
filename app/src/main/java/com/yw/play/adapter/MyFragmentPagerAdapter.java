package com.yw.play.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yw.play.base.BaseFragment;

import java.util.List;

/**
 * Created by Administrator on 2017/9/10.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
    private List<BaseFragment> fragmentList;
    private List<String> stringList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList, List<String> stringList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.stringList = stringList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }
}

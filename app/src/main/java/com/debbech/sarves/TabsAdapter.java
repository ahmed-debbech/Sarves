package com.debbech.sarves;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabsAdapter extends FragmentStateAdapter {

    int totalTabs;

    public TabsAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle life) {
        super(fm, life);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new RecentFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new FavoriteFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

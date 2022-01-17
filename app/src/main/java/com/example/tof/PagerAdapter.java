package com.example.tof;

//import a
/*import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;*/

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    int numberOfTabs;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm, BEHAVIOR_SET_USER_VISIBLE_HINT);
        this.numberOfTabs = NumOfTabs;
    }
    private StatisticsFragment statisticsFragment;
    private SettingsFragment settingsFragment;
    private OperationsFragment operationsFragment;
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                statisticsFragment = new StatisticsFragment();
                return statisticsFragment;
            case 1:
                settingsFragment = new SettingsFragment();
                return settingsFragment;
            case 2:
                operationsFragment = new OperationsFragment();
                return operationsFragment;
            default:
                return null;
        }
    }

    public StatisticsFragment getStatisticsFragment() {
        return statisticsFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    public OperationsFragment getOperationsFragment() {
        return operationsFragment;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

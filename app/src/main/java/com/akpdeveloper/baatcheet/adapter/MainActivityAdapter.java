package com.akpdeveloper.baatcheet.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.akpdeveloper.baatcheet.fragments.ChatFragment;
import com.akpdeveloper.baatcheet.fragments.GroupFragment;
import com.akpdeveloper.baatcheet.fragments.PeopleFragment;
import com.akpdeveloper.baatcheet.fragments.StatusFragment;

public class MainActivityAdapter extends FragmentStateAdapter {

    public MainActivityAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0 : return new ChatFragment();
//            case 1 : return new GroupFragment();
            case 1 : return new PeopleFragment();
//            default: return new StatusFragment();
            default: return new PeopleFragment();
        }
    }

    @Override
    public int getItemCount() {return 2;}
}

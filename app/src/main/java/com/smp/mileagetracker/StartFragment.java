package com.smp.mileagetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Steve on 1/9/15.
 */
public class StartFragment extends Fragment implements OnClickListener
{
    @InjectView(R.id.button_start)
    Button mStartButton;


    private static final String ARG_SECTION_NUMBER = "section_number";

    public static StartFragment newInstance(int sectionNumber)
    {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StartFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.inject(this, rootView);
        mStartButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_start:
                startTrackActivity();
                break;
        }
    }

    private void startTrackActivity()
    {
        Intent intent = new Intent(this.getActivity(), TrackActivity.class);
        startActivity(intent);
    }
}

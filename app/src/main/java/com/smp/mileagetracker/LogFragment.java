package com.smp.mileagetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by Steve on 1/9/15.
 */
public class LogFragment extends Fragment
{

    @InjectView(R.id.list_log)
    RecyclerView mRecyclerView;

    private LogAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static LogFragment newInstance(int sectionNumber)
    {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LogFragment()
    {
    }
    public void onEvent(NewLogItemEvent event)
    {
        Toast.makeText(getActivity(), getActivity()
                .getResources().getString(R.string.new_item), Toast.LENGTH_LONG).show();
        List<TrackInfoComplete> list = Utils.getTrackingList(getActivity());
        mAdapter.updateList(list);
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.inject(this, rootView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        mAdapter = new LogAdapter(Utils.getTrackingList(getActivity()), getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
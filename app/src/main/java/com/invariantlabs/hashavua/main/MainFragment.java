package com.invariantlabs.hashavua.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.base.BaseFragment;
import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.view.DividerItemDecoration;
import com.invariantlabs.hashavua.view.HashavuaFeedItemAnimator;

import java.util.List;

import butterknife.Bind;

public class MainFragment extends BaseFragment {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private HashavuaFeedAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HashavuaFeedAdapter(getContext(), (HashavuaFeedAdapter.HashavuaFeedCallback) getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new HashavuaFeedItemAnimator(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), null, false, true));
    }

    public void setData(List<HashavuaEntry> data) {
        adapter.setEntries(data);
    }
}

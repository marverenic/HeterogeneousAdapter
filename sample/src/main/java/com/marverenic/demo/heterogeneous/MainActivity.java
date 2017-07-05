package com.marverenic.demo.heterogeneous;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.marverenic.adapter.DragDropAdapter;
import com.marverenic.demo.heterogeneous.section.DynamicEntrySection;
import com.marverenic.demo.heterogeneous.section.DynamicHeaderSection;
import com.marverenic.demo.heterogeneous.section.VarietyTypeAndDraggableSection;
import com.marverenic.demo.heterogeneous.section.EntrySection;
import com.marverenic.demo.heterogeneous.section.HeaderSection;
import com.marverenic.demo.heterogeneous.section.TextSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DragDropAdapter mAdapter;

    private List<String> mDynamicEntries;
    private int mDynamicCount;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mAdapter = new DragDropAdapter();

        mDynamicEntries = new ArrayList<>(Arrays.asList("Entry 1", "Entry 2"));
        mDynamicCount = mDynamicEntries.size();

        mAdapter.addSection(new TextSection(getString(R.string.message_intro)));
        mAdapter.addSection(new HeaderSection("Static entries"));
        mAdapter.addSection(new EntrySection(Arrays.asList(
                "Static Entry 1", "Static Entry 2", "Static Entry 3", "Static Entry 4")));

        mAdapter.addSection(new DynamicHeaderSection("Dynamic entries", mDynamicEntries));
        mAdapter.addSection(new DynamicEntrySection(mDynamicEntries));

        mAdapter.addSection(new HeaderSection("Variety type and draggable section"));
        mAdapter.setDragSection(new VarietyTypeAndDraggableSection(new ArrayList<>(Arrays.asList(
                "Red", "Blue", "Red", "Blue"))));


        mAdapter.addSection(new HeaderSection("Footer"));
        mAdapter.addSection(new TextSection(getString(R.string.message_footer)));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.attach(mRecyclerView);

        findViewById(R.id.add_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mDynamicCount++;
        mDynamicEntries.add("Entry " + mDynamicCount);

        mAdapter.notifyDataSetChanged();
    }
}

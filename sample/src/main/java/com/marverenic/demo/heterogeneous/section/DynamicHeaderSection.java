package com.marverenic.demo.heterogeneous.section;

import com.marverenic.adapter.HeterogeneousAdapter;

import java.util.List;

public class DynamicHeaderSection extends HeaderSection {

    private List<?> mDependency;

    public DynamicHeaderSection(String data, List<?> dependency) {
        super(data);
        mDependency = dependency;
    }

    @Override
    public boolean showSection(HeterogeneousAdapter adapter) {
        return !mDependency.isEmpty();
    }
}

package com.marverenic.demo.heterogeneous.section;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marverenic.adapter.EnhancedViewHolder;
import com.marverenic.adapter.HeterogeneousAdapter;
import com.marverenic.demo.heterogeneous.R;

import java.util.List;

public class EntrySection extends HeterogeneousAdapter.ListSection<String> {

    public EntrySection(@NonNull List<String> data) {
        super(data);
    }

    @Override
    public EnhancedViewHolder<String> createViewHolder(HeterogeneousAdapter adapter,
                                                       ViewGroup parent) {
        return ViewHolder.create(parent);
    }

    private static class ViewHolder extends EnhancedViewHolder<String> {

        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.itemText);
        }

        public static ViewHolder create(ViewGroup parent) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_entry, parent, false));
        }

        @Override
        public void onUpdate(String item, int position) {
            mTextView.setText(item);
        }
    }

}

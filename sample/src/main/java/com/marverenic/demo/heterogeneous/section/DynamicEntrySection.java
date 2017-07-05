package com.marverenic.demo.heterogeneous.section;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marverenic.adapter.EnhancedViewHolder;
import com.marverenic.adapter.HeterogeneousAdapter;
import com.marverenic.demo.heterogeneous.R;

import java.util.List;

public class DynamicEntrySection extends HeterogeneousAdapter.ListSection<String> {

    public DynamicEntrySection(@NonNull List<String> data) {
        super(data);
    }

    @Override
    public EnhancedViewHolder<String> createViewHolder(HeterogeneousAdapter adapter,
                                                       ViewGroup parent, int itemType) {
        return ViewHolder.create(adapter, getData(), parent);
    }

    private static class ViewHolder extends EnhancedViewHolder<String>
            implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private HeterogeneousAdapter mAdapter;
        private List<String> mData;
        private int mIndex;

        private TextView mTextView;

        public ViewHolder(HeterogeneousAdapter adapter, List<String> data, View itemView) {
            super(itemView);
            mAdapter = adapter;
            mData = data;

            mTextView = (TextView) itemView.findViewById(R.id.itemText);
            itemView.findViewById(R.id.overflowButton).setOnClickListener(this);
        }

        public static ViewHolder create(HeterogeneousAdapter adapter, List<String> data,
                                        ViewGroup parent) {
            return new ViewHolder(adapter, data,
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_dynamic_entry, parent, false));
        }

        @Override
        public void onUpdate(String item, int position) {
            mTextView.setText(item);
            mIndex = position;
        }

        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(itemView.getContext(), v, Gravity.END);
            menu.getMenuInflater().inflate(R.menu.menu_entry, menu.getMenu());
            menu.setOnMenuItemClickListener(this);
            menu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.menu_action_delete) {
                mData.remove(mIndex);
                mAdapter.notifyDataSetChanged();
                return true;
            }
            return false;
        }
    }

}

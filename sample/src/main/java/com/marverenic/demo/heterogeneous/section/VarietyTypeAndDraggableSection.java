package com.marverenic.demo.heterogeneous.section;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marverenic.adapter.DragDropAdapter;
import com.marverenic.adapter.EnhancedViewHolder;
import com.marverenic.adapter.HeterogeneousAdapter;
import com.marverenic.demo.heterogeneous.R;

import java.util.List;

/**
 * Created by Khang NT on 7/5/17.
 * Email: khang.neon.1997@gmail.com
 */

public class VarietyTypeAndDraggableSection extends DragDropAdapter.ListDragSection<String> {

    private static final int TYPE_RED = 1;
    private static final int TYPE_BLUE = 2;

    public VarietyTypeAndDraggableSection(@NonNull List<String> data) {
        super(data);
    }

    @Override
    public int getItemType(int position) {
        return "red".equalsIgnoreCase(get(position)) ? TYPE_RED : TYPE_BLUE;
    }

    @Override
    public EnhancedViewHolder<String> createViewHolder(HeterogeneousAdapter adapter, ViewGroup parent, int itemType) {
        int color;
        if (itemType == TYPE_RED) {
            color = Color.RED;
        } else if (itemType == TYPE_BLUE) {
            color = Color.BLUE;
        } else {
            throw new IllegalArgumentException("Invalid item type: " + itemType);
        }
        return ViewHolder.create(parent, color);
    }

    @Override
    public int getDragHandleId() {
        return R.id.dragView;
    }

    @Override
    protected void onDrop(int from, int to) {
        // does nothing
    }

    private static class ViewHolder extends EnhancedViewHolder<String> {

        private TextView mTextView;

        public ViewHolder(View itemView, int color) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.itemText);
            mTextView.setTextColor(color);
        }

        public static ViewHolder create(ViewGroup parent, int color) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_entry_draggable, parent, false), color);
        }

        @Override
        public void onUpdate(String item, int position) {
            mTextView.setText(item);
        }
    }
}

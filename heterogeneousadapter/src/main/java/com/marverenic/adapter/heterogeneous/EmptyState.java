package com.marverenic.adapter.heterogeneous;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Generates Views that represents empty states in a {@link RecyclerView}
 * @see HeterogeneousAdapter#setEmptyState(EmptyState)
 */
public abstract class EmptyState {

    /**
     * Generates the view to be shown when the data set is empty
     * @param adapter The {@link RecyclerView.Adapter} that is requesting this view
     * @param parent The ViewGroup that this view will be attached to
     * @return A view representing that there is no data in this RecyclerView
     */
    public abstract View onCreateView(RecyclerView.Adapter<EnhancedViewHolder> adapter,
                                      ViewGroup parent);

    /**
     * Called when the View generated by {@link #onCreateView(RecyclerView.Adapter, ViewGroup)} is
     * about to be used by the adapter this EmptyState is attached to. Implementations of this
     * method should update the information in their empty state here (unless the info is
     * guaranteed to never change)
     * @param emptyStateView The view to update
     */
    protected abstract void onUpdate(View emptyStateView);

    /**
     * Wraps Views created by {@link #onCreateView(RecyclerView.Adapter, ViewGroup)} in an
     * {@link EmptyState.EmptyViewHolder} so that they can be used in a RecyclerView
     * @param adapter The adapter requesting a ViewHolder
     * @param parent The ViewGroup that this ViewHolder will be attached to
     * @return A ViewHolder used to indicate that the data set is empty
     */
    protected final EmptyViewHolder onCreateViewHolder(
            RecyclerView.Adapter<EnhancedViewHolder> adapter, ViewGroup parent) {
        return new EmptyViewHolder(onCreateView(adapter, parent), this);
    }

    /**
     * Implementation of {@link EnhancedViewHolder} used to wrap an empty state view to be used with
     * a {@link RecyclerView}
     */
    static final class EmptyViewHolder extends EnhancedViewHolder<Void> {

        private EmptyState mState;

        /**
         * Instances are only created in
         * {@link EmptyState#onCreateViewHolder(RecyclerView.Adapter, ViewGroup)}
         * @param itemView The View of the empty state
         * @param state The EmptyState that created this ViewHolder which will later be used as a
         *              callback to update this ViewHolder
         */
        private EmptyViewHolder(View itemView, EmptyState state) {
            super(itemView);
            mState = state;
        }

        @Override
        public void onUpdate(Void item, int sectionPosition) {
            mState.onUpdate(itemView);
        }

    }
}
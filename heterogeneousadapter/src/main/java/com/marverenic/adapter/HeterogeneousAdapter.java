package com.marverenic.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of {@link android.support.v7.widget.RecyclerView.Adapter} designed for data sets
 * that have different kinds of data that may be grouped into sections. {@link Section Sections}
 * behave similarly to a standard {@link android.support.v7.widget.RecyclerView.Adapter}, with the
 * exception that they may only have one type of view. Sections may be implemented similarly to
 * extending {@link android.support.v7.widget.RecyclerView.Adapter}, or existing implementations
 * including {@link ListSection} {@link SingletonSection} may be used instead.
 * <p>
 * To populate this adapter, use {@link #addSection(Section)}. All data lookup and ViewHolder
 * instantiation is handled by Sections. Sections appear one after another in the order that they
 * are added, and may be positioned relative to other sections using
 * {@link #addSection(Section, int)}.
 */
public class HeterogeneousAdapter extends RecyclerView.Adapter<EnhancedViewHolder> {

    private static final int NO_ID = (int) RecyclerView.NO_ID;

    private static final long EMPTY_STATE_ID = -2;

    public static final int ITEM_TYPE_UNDEFINED = Integer.MIN_VALUE;
    public static final int MIN_ITEM_TYPE_VALUE = 0;
    public static final int MAX_ITEM_TYPE_VALUE = 99;

    private static final int MULTIPLIER = MAX_ITEM_TYPE_VALUE + 1;

    /**
     * Used in {@link #getItemViewType(int)} to denote that the empty state should be shown
     */
    private static final int EMPTY_TYPE = -2;

    private List<Section> mSections;
    private SparseArray<Section> mSectionTypeMap;
    private EmptyState mEmptyState;
    private boolean mShareItemType;

    /**
     * The number of times {@link #addSection(Section)} and {@link #addSection(Section, int)} have
     * been called. This value is used to generate item IDs used with
     * {@link RecyclerView.Adapter#getItemViewType(int)}
     */
    private int mSectionBindingCount;

    /**
     * A reused Coordinate to avoid GC overhead when calling
     * {@link #lookupCoordinates(int, Coordinate))
     */
    private Coordinate mCoordinate;

    /**
     * Sets up a new HeterogeneousAdapter with no children and disable <code>shareItemType</code>
     * by default.
     */
    public HeterogeneousAdapter() {
        this(false);
    }

    /**
     * Sets up a new HeterogeneousAdapter with no children
     *
     * @param shareItemType set true if a {@link Section} can reuse <code>ViewHolder</code> with the same <code>itemType</code>
     *                      returned from another section.
     */
    public HeterogeneousAdapter(boolean shareItemType) {
        mSections = new ArrayList<>();
        mSectionTypeMap = new SparseArray<>();
        mCoordinate = new Coordinate();
        mSectionBindingCount = 0;
        mShareItemType = shareItemType;
    }

    /**
     * @return The number of Sections currently attached to this Adapter
     */
    public int getSectionCount() {
        return mSections.size();
    }

    private int getNextSectionId() {
        return ++mSectionBindingCount;
    }

    /**
     * @return Return true if a {@link Section} can reuse <code>ViewHolder</code> with the same <code>itemType</code>
     * returned from another section. This value is immutable.
     */
    public boolean isShareItemType() {
        return mShareItemType;
    }

    /**
     * Adds a {@link HeterogeneousAdapter.Section} to the bottom of this Adapter
     *
     * @param section the Section to add
     * @return this Adapter, for chain building
     */
    public HeterogeneousAdapter addSection(@NonNull Section section) {
        return addSection(section, getSectionCount());
    }

    /**
     * Adds a {@link HeterogeneousAdapter.Section} to a specified index in this Adapter
     *
     * @param section the Section to add
     * @param index   the index to add this Section at
     * @return this Adapter, for chain building
     */
    public HeterogeneousAdapter addSection(@NonNull Section section, int index) {
        section.setSectionId(getNextSectionId());
        mSections.add(index, section);
        notifyDataSetChanged();
        return this;
    }

    /**
     * Removes a section in a specified index
     *
     * @param index the index to remove
     */
    public void removeSection(int index) {
        Section removed = mSections.remove(index);
        for (int i = 0; i < mSectionTypeMap.size(); i++) {
            int key = mSectionTypeMap.keyAt(i);
            if (removed == mSectionTypeMap.get(key)) {
                mSectionTypeMap.remove(key);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Sets the {@link EmptyState} to be displayed when there are no views to otherwise be displayed
     * in this Adapter. This may occur either because no data has been loaded (and all Sections are
     * empty), or because no Sections have been added.
     *
     * @param emptyState The empty state to show when there are no items to be shown in this list
     *                   {@code null} to disable showing an empty state when the Adapter is empty,
     *                   and to keep it completely blank.
     */
    public void setEmptyState(@Nullable EmptyState emptyState) {
        mEmptyState = emptyState;
    }

    /**
     * Converts a position in the entire data set to a Coordinate in the section list. This method
     * returns two values into the provided {@code Coordinate} so that it can be reused to save GC
     * overhead.
     *
     * @param position   The position in the entire data set to lookup a coordinate of
     * @param coordinate {@code Coordinate} object to put the result into
     */
    protected final void lookupCoordinates(int position, Coordinate coordinate) {
        int runningTotal = 0;
        for (int i = 0; i < mSections.size(); i++) {
            int sectionTotal = mSections.get(i).getItemCount(this);
            if (position < runningTotal + sectionTotal) {
                coordinate.setSection(i);
                coordinate.setItemIndex(position - runningTotal);
                return;
            }
            runningTotal += sectionTotal;
        }
        coordinate.clear();
    }

    /**
     * Calculates the number of views contained in sections proceeding a given section
     *
     * @return The number of views in this list that are above the first view in the given section
     */
    protected int getLeadingViewCount(Section section) {
        int count = 0;
        for (Section _section : mSections) {
            if (_section == section) {
                break;
            }
            count += _section.getItemCount(this);
        }
        return count;
    }

    private int zipItemType(int sectionId, int itemType) {
        return sectionId * MULTIPLIER + itemType;
    }

    private int unzipItemType(int sectionId, int zippedNumber) {
        return zippedNumber - sectionId * MULTIPLIER;
    }

    @Override
    public int getItemViewType(int position) {
        if (getDataSize() == 0) {
            return EMPTY_TYPE;
        }

        lookupCoordinates(position, mCoordinate);
        int sectionIndex = mCoordinate.getSection();
        Section section = mSections.get(sectionIndex);

        int givenItemType = section.getItemType(mCoordinate.getItemIndex());

        if (isShareItemType()) {
            mSectionTypeMap.put(givenItemType, section);
            return givenItemType;
        } else {
            // check if given itemType is valid
            if ((givenItemType != ITEM_TYPE_UNDEFINED)
                    && (givenItemType < MIN_ITEM_TYPE_VALUE || givenItemType > MAX_ITEM_TYPE_VALUE)) {
                String error = String.format(Locale.US, "Invalid itemType at position %d of section %d: " +
                        "itemType must be between %d and %d", position, sectionIndex,
                        MIN_ITEM_TYPE_VALUE, MAX_ITEM_TYPE_VALUE);
                throw new IllegalStateException(error);
            }
            int itemType = zipItemType(section.getSectionId(), givenItemType);
            mSectionTypeMap.put(itemType, section);
            return itemType;
        }
    }

    @Override
    public long getItemId(int position) {
        if (getDataSize() == 0 && mEmptyState != null) {
            return EMPTY_STATE_ID;
        }

        lookupCoordinates(position, mCoordinate);
        int section = mCoordinate.getSection();
        int item = mCoordinate.getItemIndex();

        int givenId = mSections.get(section).getId(item);
        int sectionId = mSections.get(section).getSectionId();

        if (givenId == NO_ID) {
            return RecyclerView.NO_ID;
        } else {
            return (long) sectionId << 32 | givenId;
        }
    }

    protected Section getSectionByViewType(int viewType) {
        return mSectionTypeMap.get(viewType);
    }

    @Override
    public EnhancedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_TYPE) {
            return mEmptyState.createViewHolder(this, parent);
        }
        Section section = mSectionTypeMap.get(viewType);
        if (!isShareItemType()) {
            // get real itemType
            int itemType = unzipItemType(section.getSectionId(), viewType);
            return section.createViewHolder(this, parent, itemType);
        } else {
            return section.createViewHolder(this, parent, viewType);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(EnhancedViewHolder holder, int position) {
        if (holder instanceof EmptyState.EmptyViewHolder) {
            ((EmptyState.EmptyViewHolder) holder).onUpdate(null, position);
        } else {
            lookupCoordinates(position, mCoordinate);
            int section = mCoordinate.getSection();
            int item = mCoordinate.getItemIndex();
            holder.onUpdate(mSections.get(section).get(item), item);
        }
    }

    /**
     * Gets the number of data elements in all attached sections. The value returned from this
     * method does not necessarily correspond to the value returned by {@link #getItemCount()}.
     * For example, if each attached section has no data and an {@link EmptyState} is attached, this
     * method will return {@code 0}, but {@link #getItemCount()} will return {@code 1}.
     *
     * @return The number of visible data entries in all sections
     */
    protected int getDataSize() {
        int sum = 0;
        for (Section s : mSections) {
            sum += s.getItemCount(this);
        }
        return sum;
    }

    @Override
    public final int getItemCount() {
        int count = getDataSize();

        if (count == 0 && mEmptyState != null) {
            return 1;
        } else {
            return count;
        }
    }

    /**
     * Gets the index of a section in this adapter. Equality is checked using the implementation of
     * {@link Object#equals(Object)}, which defaults to checking references
     *
     * @param section The section to lookup an index for
     * @return The index of this section relative to other sections in the adapter, or {@code -1}
     * if it wasn't found in this adapter.
     */
    public int getSectionIndex(Section<?> section) {
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.get(i).equals(section)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the section at a given index
     *
     * @param index The index to get the attached section of
     * @return The section at the specified index
     */
    public Section getSection(int index) {
        return mSections.get(index);
    }

    /**
     * Holds a group of sequential items if the same type to be displayed in a
     * {@link HeterogeneousAdapter}. Sections act as {@link RecyclerView.Adapter}s with the
     * condition that they may only have one type of ItemView
     *
     * @param <Type> The type of data that this Section holds
     * @see HeterogeneousAdapter.ListSection
     * @see HeterogeneousAdapter.SingletonSection
     */
    public static abstract class Section<Type> {
        private int mSectionId;

        /**
         * Creates a ViewHolder for the {@link HeterogeneousAdapter} this Section is attached to
         *
         * @param adapter the Adapter requesting a new ViewHolder
         * @param parent  the ViewGroup that this ViewHolder will be placed into
         * @return A valid ViewHolder that may be used for items in this Section
         * @see RecyclerView.Adapter#createViewHolder(ViewGroup, int)
         */
        public abstract EnhancedViewHolder<Type> createViewHolder(HeterogeneousAdapter adapter,
                                                                  ViewGroup parent, int itemType);

        /**
         * Get the ID of an item in the data set
         *
         * @param position The index in the data set that an ID has been requested for
         * @return The ID of this item or {@link RecyclerView#NO_ID}
         * @see RecyclerView.Adapter#getItemId(int)
         */
        public int getId(int position) {
            return NO_ID;
        }

        /**
         * Override this method to hide this Section if its visibility is dependent on another
         * external condition. The default implementation always shows this section.
         *
         * @param adapter The adapter this Section is attached to
         * @return true if this section should be shown, false if it should be hidden
         */
        public boolean showSection(HeterogeneousAdapter adapter) {
            return true;
        }

        /**
         * Gets the number of visible items held by this section. This value handles hiding this
         * section with {@link #showSection(HeterogeneousAdapter)}, and will return either
         * {@link #getItemCount()} or {@code 0}.
         *
         * @param adapter The adapter that this section is attached to
         * @return The number of visible items that are held by this section
         */
        public final int getSize(HeterogeneousAdapter adapter) {
            return showSection(adapter) ? getItemCount(adapter) : 0;
        }

        /**
         * Gets the number of items held by this section
         *
         * @return The number of items in this section's backing data set
         */
        public abstract int getItemCount(HeterogeneousAdapter adapter);

        /**
         * Returns an item in the data set used to populate a ViewHolder
         *
         * @param position The index of the item to return
         * @return The item at the specified index in this Section's data set
         */
        public abstract Type get(int position);

        /**
         * Get the item type at <code>position</code> used to create ViewHolder. This method works like
         * {@link RecyclerView.Adapter#getItemViewType(int)}.
         * <p>
         * The return value must be {@link #ITEM_TYPE_UNDEFINED} or between range
         * {@link #MIN_ITEM_TYPE_VALUE} and {@link #MAX_ITEM_TYPE_VALUE} (inclusive).
         * </p>
         * Default implementation return {@link #ITEM_TYPE_UNDEFINED}.
         *
         * @param position The index of item in this section.
         * @return The item type.
         */
        public int getItemType(int position) {
            return ITEM_TYPE_UNDEFINED;
        }

        /**
         * Used internally by {@link HeterogeneousAdapter} to set a unique ID for this section.
         * This Id is used for calculating unique itemType to return in function
         * {@link RecyclerView.Adapter#getItemViewType(int)}.
         *
         * @param sectionId Unique section ID.
         */
        private void setSectionId(int sectionId) {
            mSectionId = sectionId;
        }

        /**
         * @return the unique ID of this section.
         */
        private int getSectionId() {
            return mSectionId;
        }
    }

    /**
     * An extension of {@link HeterogeneousAdapter.Section} that always has exactly one item in
     * the set
     *
     * @param <Type> The class of the item that this Section shows. You may use {@link Void}
     *               if this Section has no data
     */
    public static abstract class SingletonSection<Type> extends Section<Type> {

        private Type mData;

        /**
         * @param data The item to show in this Section
         */
        public SingletonSection(Type data) {
            mData = data;
        }

        /**
         * Replace the current data item. Callers are responsible for calling
         * {@link RecyclerView.Adapter#notifyDataSetChanged()} or an equivalent method
         *
         * @param data The new data item to show in this Section
         */
        public void setData(Type data) {
            mData = data;
        }

        @Override
        public final int getItemCount(HeterogeneousAdapter adapter) {
            return showSection(adapter) ? 1 : 0;
        }

        @Override
        public final Type get(int position) {
            return mData;
        }

    }

    /**
     * An extension of {@link HeterogeneousAdapter.Section} used to show a list of items of the
     * same type
     *
     * @param <Type> The class of the data that this Section shows.
     */
    public static abstract class ListSection<Type> extends Section<Type> {

        private List<Type> mData;

        /**
         * @param data The data to populate this Section with
         */
        public ListSection(@NonNull List<Type> data) {
            mData = data;
        }

        /**
         * @return the backing data set
         */
        public List<Type> getData() {
            return mData;
        }

        /**
         * Replace the active data set. Callers are responsible for calling
         * {@link RecyclerView.Adapter#notifyDataSetChanged()}
         *
         * @param mData The new data set to back this Section
         */
        public void setData(@NonNull List<Type> mData) {
            this.mData = mData;
        }

        @Override
        public final int getItemCount(HeterogeneousAdapter adapter) {
            return showSection(adapter) ? mData.size() : 0;
        }

        @Override
        public final Type get(int position) {
            return mData.get(position);
        }
    }
}

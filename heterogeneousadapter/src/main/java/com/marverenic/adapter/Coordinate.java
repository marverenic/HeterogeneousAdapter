package com.marverenic.adapter;

/**
 * An ordered pair of integers used by {@link HeterogeneousAdapter} to lookup the index of data
 * items within sections.
 *
 * @see HeterogeneousAdapter#lookupCoordinates(int, Coordinate)
 */
public final class Coordinate {

    public static final int UNKNOWN_POSITION = -1;

    private int mSection;
    private int mItemIndex;

    public Coordinate() {
        clear();
    }

    public Coordinate(int section, int sectionIndex) {
        setSection(section);
        setItemIndex(sectionIndex);
    }

    /**
     * Resets this coordinate's values to the default {@link #UNKNOWN_POSITION}
     */
    public void clear() {
        setSection(UNKNOWN_POSITION);
        setItemIndex(UNKNOWN_POSITION);
    }

    /**
     * @param section The section index to set this coordinate to point to
     */
    public void setSection(int section) {
        mSection = section;
    }

    /**
     * @param itemIndex The index within a section's data to set this coordinate to point to
     */
    public void setItemIndex(int itemIndex) {
        mItemIndex = itemIndex;
    }

    /**
     * @return The section that this coordinate points to
     */
    public int getSection() {
        return mSection;
    }

    /**
     * @return The index within a section's data that this coordinate points to
     */
    public int getItemIndex() {
        return mItemIndex;
    }

}
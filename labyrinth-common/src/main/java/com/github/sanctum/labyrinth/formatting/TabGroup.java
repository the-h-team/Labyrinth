package com.github.sanctum.labyrinth.formatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An interface delegating tab list header and footer display information.
 */
public interface TabGroup {

	/**
	 * Get the special key for this tab info group.
	 *
	 * @return This groups special key.
	 */
	String getKey();

	/**
	 * Check if this tab info group is currently active.
	 *
	 * @return true if this tab info group is ready for use.
	 */
	boolean isActive();

	/**
	 * Check if this tab group can re-wind its own indexing for smooth looping.
	 *
	 * @return true if this tab group is windable.
	 */
	boolean isWindable();

	/**
	 * Set the activity status of this tab group.
	 *
	 * @param active the status of the tab group.
	 */
	void setActive(boolean active);

	/**
	 * Get the tab information header at the specified index.
	 *
	 * @param index The index to retrieve.
	 * @return A tab information object.
	 */
	TabInfo getHeader(int index);

	/**
	 * Get the current tab information header index.
	 *
	 * @return the current header index.
	 */
	int getCurrentHeaderIndex();

	/**
	 * Get the tab information footer at the specified index.
	 *
	 * @param index The index to retrieve.
	 * @return A tab information object.
	 */
	TabInfo getFooter(int index);

	/**
	 * Get the current tab information footer index.
	 *
	 * @return the current footer index.
	 */
	int getCurrentFooterIndex();

	/**
	 * Set whether this tab group can rewind its own indexing or not.
	 *
	 * @param windable whether to allow winding or not.
	 */
	void setWindable(boolean windable);

	/**
	 * Adjust the current index for a given tab list position to the next display in order.
	 *
	 * @param side The tab list position to update.
	 */
	void nextDisplayIndex(@MagicConstant(valuesFromClass = TabInfo.class) int side);

	/**
	 * Create a fresh tab group from the provided header and footer.
	 *
	 * @param key The special key to use
	 * @param header The header context
	 * @param footer The footer context
	 * @return A fresh TabGroup object containing the provided source material.
	 */
	static TabGroup fromInfo(String key, TabInfo[] header, TabInfo[] footer) {
		return new TabGroup() {

			private final List<TabInfo> headerlist = Collections.synchronizedList(new LinkedList<>());
			private final List<TabInfo> footerlist = Collections.synchronizedList(new LinkedList<>());
			private int headerPos;
			private int footerPos;
			private boolean isWindable = true;
			private boolean isActive = true;
			private boolean headerGoingBackwards;
			private boolean footerGoingBackwards;

			{
				headerlist.addAll(Arrays.asList(header));
				footerlist.addAll(Arrays.asList(footer));
			}

			@Override
			public String getKey() {
				return key;
			}

			@Override
			public boolean isActive() {
				return isActive;
			}

			@Override
			public boolean isWindable() {
				return isWindable;
			}

			@Override
			public void setActive(boolean active) {
				this.isActive = active;
			}

			@Override
			public TabInfo getHeader(int index) {
				return Collections.unmodifiableList(headerlist).get(index);
			}

			@Override
			public int getCurrentHeaderIndex() {
				return Math.max(0, headerPos);
			}

			@Override
			public TabInfo getFooter(int index) {
				return Collections.unmodifiableList(footerlist).get(index);
			}

			@Override
			public int getCurrentFooterIndex() {
				return Math.max(footerPos, 0);
			}

			@Override
			public void setWindable(boolean windable) {
				this.isWindable = windable;
			}

			@Override
			public void nextDisplayIndex(int side) {
				switch (side) {
					case 0:
						if (headerPos + 1 >= headerlist.size()) {
							if (isWindable) {
								headerGoingBackwards = true;
								headerPos = headerPos - 1;
							} else {
								headerPos = 0;
							}
						} else {
							if (headerGoingBackwards) {
								if (headerPos == 0) {
									headerGoingBackwards = false;
									headerPos = headerPos + 1;
								} else {
									headerPos = headerPos - 1;
								}
							} else {
								headerPos = headerPos + 1;
							}
						}
						break;
					case 1:
						if (footerPos + 1 >= footerlist.size()) {
							if (isWindable) {
								footerGoingBackwards = true;
								footerPos = footerPos - 1;
							} else {
								footerPos = 0;
							}
						} else {
							if (footerGoingBackwards) {
								if (footerPos == 0) {
									footerGoingBackwards = false;
									footerPos = footerPos + 1;
								} else {
									footerPos = footerPos - 1;
								}
							} else {
								footerPos = footerPos + 1;
							}
						}
						break;
				}
			}
		};
	}

}

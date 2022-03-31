package com.github.sanctum.labyrinth.formatting;

import com.github.sanctum.labyrinth.data.container.LabyrinthCollection;
import com.github.sanctum.labyrinth.data.container.LabyrinthList;
import java.util.Arrays;

public interface ScoreboardGroup {

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
	ScoreboardBuilder getBuilder(int index);

	/**
	 * Get the current tab information header index.
	 *
	 * @return the current header index.
	 */
	int getIndex();

	/**
	 * Set whether this tab group can rewind its own indexing or not.
	 *
	 * @param windable whether to allow winding or not.
	 */
	void setWindable(boolean windable);

	/**
	 * Adjust the current index for a given scoreboard group position to the next display in order.
	 */
	void next();

	/**
	 * Create a fresh tab group from the provided header and footer.
	 *
	 * @param key The special key to use
	 * @return A fresh TabGroup object containing the provided source material.
	 */
	static ScoreboardGroup fromInfo(String key, ScoreboardBuilder... builders) {
		return new ScoreboardGroup() {

			private final LabyrinthCollection<ScoreboardBuilder> headerlist = new LabyrinthList<>();
			private int headerPos;
			private boolean isWindable = true;
			private boolean isActive = true;
			private boolean headerGoingBackwards;

			{
				headerlist.addAll(Arrays.asList(builders));
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
			public ScoreboardBuilder getBuilder(int index) {
				return headerlist.get(index);
			}

			@Override
			public int getIndex() {
				return Math.max(0, headerPos);
			}

			@Override
			public void setWindable(boolean windable) {
				this.isWindable = windable;
			}

			@Override
			public void next() {
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
			}
		};
	}

}

package com.github.sanctum.labyrinth.formatting;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.intellij.lang.annotations.MagicConstant;

public interface TabGroup {

	String getKey();

	boolean isActive();

	boolean isWindable();

	void setActive(boolean active);

	TabInfo getHeader(int index);

	int getCurrentHeaderIndex();

	TabInfo getFooter(int index);

	int getCurrentFooterIndex();

	void setWindable(boolean windable);

	void nextDisplay(@MagicConstant(valuesFromClass = TabInfo.class) int side);

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
				return headerPos;
			}

			@Override
			public TabInfo getFooter(int index) {
				return Collections.unmodifiableList(footerlist).get(index);
			}

			@Override
			public int getCurrentFooterIndex() {
				return footerPos;
			}

			@Override
			public void setWindable(boolean windable) {
				this.isWindable = windable;
			}

			@Override
			public void nextDisplay(int side) {
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

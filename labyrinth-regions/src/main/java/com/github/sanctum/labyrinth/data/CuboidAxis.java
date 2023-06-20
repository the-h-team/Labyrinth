package com.github.sanctum.labyrinth.data;

public class CuboidAxis {

	private final int xMin;
	private final int xMax;
	private final int yMin;
	private final int yMax;
	private final int zMin;
	private final int zMax;
	private final int height;
	private final int zWidth;
	private final int xWidth;
	private final int totalSize;

	public CuboidAxis(int x1, int x2, int y1, int y2, int z1, int z2) {
		this.xMin = Math.min(x1, x2);
		this.xMax = Math.max(x1, x2);
		this.yMin = Math.min(y1, y2);
		this.yMax = Math.max(y1, y2);
		this.zMin = Math.min(z1, z2);
		this.zMax = Math.max(z1, z2);
		this.height = this.yMax - this.yMin + 1;
		this.xWidth = this.xMax - this.xMin + 1;
		this.zWidth = this.zMax - this.zMin + 1;
		this.totalSize = height * xWidth * zWidth;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public int getHeight() {
		return height;
	}

	public int getxWidth() {
		return xWidth;
	}

	public int getzWidth() {
		return zWidth;
	}

	public int getxMax() {
		return xMax;
	}

	public int getxMin() {
		return xMin;
	}

	public int getyMax() {
		return yMax;
	}

	public int getyMin() {
		return yMin;
	}

	public int getzMax() {
		return zMax;
	}

	public int getzMin() {
		return zMin;
	}

}

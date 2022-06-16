package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.data.JsonAdapter;
import com.github.sanctum.labyrinth.interfacing.JsonIntermediate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import org.intellij.lang.annotations.MagicConstant;

public class ProgressBar implements JsonIntermediate {

	public static final int NO_PERCENTAGE = 0;
	public static final int PERCENT_ON_LEFT = 1;
	public static final int PERCENT_ON_RIGHT = 2;
	public static final int PERCENT_IN_MIDDLE = 3;

	private int position;
	private int progress;
	private int maxProgress = 150;
	private int maxBars = 33;
	private char symbol = '|';
	private String emptyColor = "&7";
	private String fullColor = "&a";
	private String prefix;
	private String suffix;

	public static ProgressBar fromJson(@Json String json) throws IllegalArgumentException {
		return fromJson(JsonIntermediate.toJsonObject(json));
	}

	public static ProgressBar fromJson(JsonObject object) throws IllegalArgumentException {
		if (object.get(ProgressBar.class.getName()) != null) {
			JsonObject o = object.get(ProgressBar.class.getName()).getAsJsonObject();
			Map<String, Object> map = JsonIntermediate.convertToMap(o);
			String prefix = (String) map.get("prefix");
			String suffix = (String) map.get("suffix");
			String ecolor = (String) map.get("empty-color");
			String fcolor = (String) map.get("full-color");
			String symbol = (String) map.get("symbol");
			int position = (int) map.get("position");
			int progress = (int) map.get("progress");
			int maxprog = (int) map.get("max-progress");
			int bars = (int) map.get("bars");
			return new ProgressBar().setPrefix(prefix).setSuffix(suffix).setSymbol(symbol.charAt(0)).setEmptyColor(ecolor).setFullColor(fcolor).setPercentPosition(position).setProgress(progress).setGoal(maxprog).setBars(bars);
		}
		throw new IllegalArgumentException("Object not related to progress bar.");
	}

	@Override
	public String toString() {
		int currentProgressBarIndex = (int) Math.ceil(((double) maxBars / maxProgress) * progress);
		String formattedPercent = String.format("%5.1f %% ", (100 * currentProgressBarIndex) / (double) maxBars);
		int percentStartIndex = (maxBars - formattedPercent.length()) / 2;

		StringBuilder sb = new StringBuilder();
		if (this.prefix != null) {
			sb.append(this.prefix);
		}
		if (position == 1) {
			sb.append(formattedPercent);
		}
		for (int progressBarIndex = 0; progressBarIndex < maxBars - 1; progressBarIndex++) {
			if (progressBarIndex <= percentStartIndex - 1
					|| progressBarIndex >= percentStartIndex + formattedPercent.length()) {
				if (currentProgressBarIndex <= progressBarIndex) {
					if (this.emptyColor != null) {
						sb.append(this.emptyColor).append(this.symbol);
					}
				} else {
					if (this.fullColor != null) {
						sb.append(this.fullColor).append(this.symbol);
					}
				}
			}
			if (position == 3) {
				if (progressBarIndex == percentStartIndex) {
					sb.append("&r").append(formattedPercent.contains("100.0 %") ? " 100.0% " : formattedPercent);
				}
			}
		}
		if (position == 2) {
			sb.append(formattedPercent);
		}
		if (this.suffix != null) {
			sb.append(this.suffix);
		}
		return sb.toString();
	}

	public ProgressBar setProgress(int progress) {
		this.progress = progress;
		return this;
	}

	public ProgressBar setGoal(int progress) {
		this.maxProgress = progress;
		return this;
	}

	public ProgressBar setBars(int count) {
		if (count < 9 || count % 2 == 0) {
			maxBars = 33;
		} else {
			maxBars = count;
		}
		return this;
	}

	public ProgressBar setSymbol(char symbol) {
		this.symbol = symbol;
		return this;
	}

	public ProgressBar setEmptyColor(String color) {
		this.emptyColor = color;
		return this;
	}
	public ProgressBar setFullColor(String color) {
		this.fullColor = color;
		return this;
	}

	public ProgressBar setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public ProgressBar setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public ProgressBar setPercentPosition(@MagicConstant(valuesFromClass = ProgressBar.class) int position) {
		this.position = position;
		return this;
	}

	public double getPercentage() {
		return Math.round(progress * 100.0 / maxProgress * 100.0) / 100.0;
	}

	@Json(key = "prefix")
	String getPrefix() {
		return prefix;
	}

	@Json(key = "suffix")
	String getSuffix() {
		return suffix;
	}

	@Json(key = "progress")
	int getProgress() {
		return progress;
	}

	@Json(key = "max-progress")
	int getMaxProgress() {
		return maxProgress;
	}

	@Json(key = "bars")
	int getMaxBars() {
		return maxBars;
	}

	@Json(key = "position")
	int getPosition() {
		return position;
	}

	@Json(key = "symbol")
	String getSymbol() {
		return String.valueOf(symbol);
	}

	@Json(key = "empty-color")
	String getEmptyColor() {
		return emptyColor;
	}

	@Json(key = "full-color")
	String getFullColor() {
		return fullColor;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject o = new JsonObject();
		o.add(getClass().getName(), JsonIntermediate.toJsonObject(this));
		return o;
	}

	/**
	 * Method usage here is restricted.
	 * @apiNote A non-collective object cannot be converted to an array.
	 *
	 * @return null
	 */
	@Override
	@Deprecated
	public JsonArray toJsonArray() {
		return null;
	}
}

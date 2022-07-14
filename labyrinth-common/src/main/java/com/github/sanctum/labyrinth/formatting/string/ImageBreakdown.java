package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.panther.annotation.Note;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.bukkit.ChatColor;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

/**
 * An object dedicated to converting imagery to pixel art. Improved upon by Hempfest allowing support for RGB.
 *
 * @author bobacadodl
 * @author Hempfest
 */
public abstract class ImageBreakdown {

	@Note("All default game colors using awt from java.")
	public static final Color[] VANILLA_COLORS = {
			new Color(0, 0, 0),
			new Color(0, 0, 170),
			new Color(0, 170, 0),
			new Color(0, 170, 170),
			new Color(170, 0, 0),
			new Color(170, 0, 170),
			new Color(255, 170, 0),
			new Color(170, 170, 170),
			new Color(85, 85, 85),
			new Color(85, 85, 255),
			new Color(85, 255, 85),
			new Color(85, 255, 255),
			new Color(255, 85, 85),
			new Color(255, 85, 255),
			new Color(255, 255, 85),
			new Color(255, 255, 255),
	};

	@Note("The max vanilla chat width using box characters.")
	public static final int VANILLA_MAX_WIDTH = 35;

	private final String[] lines;

	public ImageBreakdown(@NotNull ImageBreakdown breakdown) {
		this.lines = Arrays.copyOf(breakdown.lines, breakdown.lines.length);
	}

	public ImageBreakdown(BufferedImage image, int height, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar, @Note("Null to keep translucency") Color transparency) {
		boolean legacy = LabyrinthProvider.getInstance().isLegacy();
		double ratio = (double) image.getHeight() / image.getWidth();
		BufferedImage resized = resize(image, (int) (height / ratio), height);
		Color[][] colors = new Color[resized.getWidth()][resized.getHeight()];
		for (int x = 0; x < resized.getWidth(); x++) {
			for (int y = 0; y < resized.getHeight(); y++) {
				int rgb = resized.getRGB(x, y);
				if (rgb >> 24 == 0x00) {
					colors[x][y] = transparency;
				} else {
					colors[x][y] = new Color(rgb, true);
				}
			}
		}
		String[] lines = new String[colors[0].length];
		for (int y = 0; y < colors[0].length; y++) {
			StringBuilder line = new StringBuilder();
			for (Color[] chatColors : colors) {
				Color color = chatColors[y];
				line.append((color != null) ? (legacy ? DefaultColor.fromAwt(color).toString() : ColoredString.fromAwt(color)) + imgChar : ' ');
			}
			lines[y] = line.toString() + ChatColor.RESET;
		}
		this.lines = lines;
	}

	public ImageBreakdown(BufferedImage image, int height, int width, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar, @Note("Null to keep translucency") Color transparency) {
		boolean legacy = LabyrinthProvider.getInstance().isLegacy();
		BufferedImage resized = resize(image, width, height);
		Color[][] colors = new Color[resized.getWidth()][resized.getHeight()];
		for (int x = 0; x < resized.getWidth(); x++) {
			for (int y = 0; y < resized.getHeight(); y++) {
				int rgb = resized.getRGB(x, y);
				if (rgb >> 24 == 0x00) {
					colors[x][y] = transparency;
				} else {
					colors[x][y] = new Color(rgb, true);
				}
			}
		}
		String[] lines = new String[colors[0].length];
		for (int y = 0; y < colors[0].length; y++) {
			StringBuilder line = new StringBuilder();
			for (Color[] chatColors : colors) {
				Color color = chatColors[y];
				line.append((color != null) ? (legacy ? DefaultColor.fromAwt(color).toString() : ColoredString.fromAwt(color)) + imgChar : ' ');
			}
			lines[y] = line.toString() + ChatColor.RESET;
		}
		this.lines = lines;
	}

	public ImageBreakdown(String url, int height, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar, @Note("Null to keep translucency") Color transparency) {
		boolean legacy = LabyrinthProvider.getInstance().isLegacy();
		Color[][] chatColors = new Color[0][];
		try {
			BufferedImage image = ImageIO.read(new URL(url));
			double ratio = (double) image.getHeight() / image.getWidth();
			BufferedImage resized = resize(image, (int) (height / ratio), height);
			Color[][] colors = new Color[resized.getWidth()][resized.getHeight()];
			for (int x = 0; x < resized.getWidth(); x++) {
				for (int y = 0; y < resized.getHeight(); y++) {
					int rgb = resized.getRGB(x, y);
					if (rgb >> 24 == 0x00) {
						colors[x][y] = transparency;
					} else {
						colors[x][y] = new Color(rgb, true);
					}
				}
			}
			chatColors = colors;
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] lines = new String[chatColors[0].length];
		for (int y = 0; y < chatColors[0].length; y++) {
			StringBuilder line = new StringBuilder();
			for (Color[] colors : chatColors) {
				Color color = colors[y];
				line.append((color != null) ? (legacy ? DefaultColor.fromAwt(color).toString() : ColoredString.fromAwt(color)) + imgChar : ' ');
			}
			lines[y] = line.toString() + ChatColor.RESET;
		}
		this.lines = lines;
	}

	public ImageBreakdown(String url, int height, int width, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar, @Note("Null to keep translucency") Color transparency) {
		boolean legacy = LabyrinthProvider.getInstance().isLegacy();
		Color[][] chatColors = new Color[0][];
		try {
			BufferedImage image = ImageIO.read(new URL(url));
			BufferedImage resized = resize(image, width, height);
			Color[][] colors = new Color[resized.getWidth()][resized.getHeight()];
			for (int x = 0; x < resized.getWidth(); x++) {
				for (int y = 0; y < resized.getHeight(); y++) {
					int rgb = resized.getRGB(x, y);
					if (rgb >> 24 == 0x00) {
						colors[x][y] = transparency;
					} else {
						colors[x][y] = new Color(rgb, true);
					}
				}
			}
			chatColors = colors;
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] lines = new String[chatColors[0].length];
		for (int y = 0; y < chatColors[0].length; y++) {
			StringBuilder line = new StringBuilder();
			for (Color[] colors : chatColors) {
				Color color = colors[y];
				line.append((color != null) ? (legacy ? DefaultColor.fromAwt(color).toString() : ColoredString.fromAwt(color)) + imgChar : ' ');
			}
			lines[y] = line.toString() + ChatColor.RESET;
		}
		this.lines = lines;
	}

	public ImageBreakdown(BufferedImage image, int height, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar) {
		this(image, height, imgChar, null);
	}

	public ImageBreakdown(BufferedImage image, int height, int width, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar) {
		this(image, height, width, imgChar, null);
	}

	public ImageBreakdown(String url, int height, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar) {
		this(url, height, imgChar, null);
	}

	public ImageBreakdown(String url, int height, int width, @MagicConstant(valuesFromClass = BlockChar.class) char imgChar) {
		this(url, height, width, imgChar, null);
	}

	public static BufferedImage resize(BufferedImage bufferedImage, int width, int height) {
		AffineTransform af = new AffineTransform();
		af.scale(
				width / (double) bufferedImage.getWidth(),
				height / (double) bufferedImage.getHeight());

		AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return operation.filter(bufferedImage, null);
	}

	public ImageBreakdown append(String... text) {
		for (int y = 0; y < lines.length; y++) {
			if (text.length > y) {
				lines[y] += " " + text[y];
			}
		}
		return this;
	}

	public String[] read() {
		return lines;
	}

}
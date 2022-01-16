package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.Message;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.ImageIO;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class MessageBreakdown extends ImageBreakdown implements Iterable<Message> {

	final Message[] messages;

	public MessageBreakdown(BufferedImage image, int height, int width, char imgChar, Color transparency) {
		super(image, height, width, imgChar, transparency);
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
		Message[] lines = new Message[colors[0].length];
		for (int y = 0; y < colors[0].length; y++) {
			FancyMessage message = new FancyMessage();
			for (Color[] chatColors : colors) {
				Color color = chatColors[y];
				if (color != null) {
					if (legacy) {
						message.then(imgChar).color(DefaultColor.fromAwt(color));
					} else {
						message.then(imgChar).color(org.bukkit.Color.fromRGB(color.getRGB()));
					}
				} else {
					message.then(' ');
				}
			}
			lines[y] = message;
		}
		this.messages = lines;
	}

	public MessageBreakdown(String url, int height, int width, char imgChar, Color transparency) {
		super(url, height, width, imgChar, transparency);
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
		Message[] lines = new Message[chatColors[0].length];
		for (int y = 0; y < chatColors[0].length; y++) {
			FancyMessage message = new FancyMessage();
			for (Color[] colors : chatColors) {
				Color color = colors[y];
				if (color != null) {
					if (legacy) {
						message.then(imgChar).color(DefaultColor.fromAwt(color));
					} else {
						message.then(imgChar).color(org.bukkit.Color.fromRGB(color.getRGB()));
					}
				} else {
					message.then(' ');
				}
			}
			lines[y] = message;
		}
		this.messages = lines;
	}

	public MessageBreakdown(@NotNull ImageBreakdown breakdown) {
		super(breakdown);
		if (breakdown instanceof MessageBreakdown) {
			messages = ((MessageBreakdown) breakdown).messages;
		} else messages = null;
	}

	public MessageBreakdown(BufferedImage image, int height, char imgChar, Color transparency) {
		super(image, height, imgChar, transparency);
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
		Message[] lines = new Message[colors[0].length];
		for (int y = 0; y < colors[0].length; y++) {
			FancyMessage message = new FancyMessage();
			for (Color[] chatColors : colors) {
				Color color = chatColors[y];
				if (color != null) {
					if (legacy) {
						message.then(imgChar).color(DefaultColor.fromAwt(color));
					} else {
						message.then(imgChar).color(org.bukkit.Color.fromRGB(color.getRGB()));
					}
				} else {
					message.then(' ');
				}
			}
			lines[y] = message;
		}
		this.messages = lines;
	}

	public MessageBreakdown(String url, int height, char imgChar, Color transparency) {
		super(url, height, imgChar, transparency);
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
		Message[] lines = new Message[chatColors[0].length];
		for (int y = 0; y < chatColors[0].length; y++) {
			FancyMessage message = new FancyMessage();
			for (Color[] colors : chatColors) {
				Color color = colors[y];
				if (color != null) {
					if (legacy) {
						message.then(imgChar).color(DefaultColor.fromAwt(color));
					} else {
						message.then(imgChar).color(org.bukkit.Color.fromRGB(color.getRGB()));
					}
				} else {
					message.then(' ');
				}
			}
			lines[y] = message;
		}
		this.messages = lines;
	}

	public MessageBreakdown(BufferedImage image, int height, char imgChar) {
		this(image, height, imgChar, Color.WHITE);
	}

	public MessageBreakdown(BufferedImage image, int height, int width, char imgChar) {
		this(image, height, width, imgChar, Color.WHITE);
	}

	public MessageBreakdown(String url, int height, char imgChar) {
		this(url, height, imgChar, Color.WHITE);
	}

	public MessageBreakdown(String url, int height, int width, char imgChar) {
		this(url, height, width, imgChar, Color.WHITE);
	}

	public Message[] get() {
		return messages;
	}

	@NotNull
	@Override
	public Iterator<Message> iterator() {
		return Arrays.asList(messages).iterator();
	}
}

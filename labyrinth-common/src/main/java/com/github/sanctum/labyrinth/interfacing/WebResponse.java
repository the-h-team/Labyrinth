package com.github.sanctum.labyrinth.interfacing;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface WebResponse {

	@NotNull String get();

	static WebResponse download(ResourceCheck check, String output, String file, String type) {
		try {
			URL url = new URL("https://github.com/" + check.getAuthor() + "/" + check.getResource() + "/releases/download/" + check.getLatest() + "/" + check.getResource() + ".jar");
			if (new File("downloads/" + output + "/" + file + "." + type).exists()) return () -> "Latest version already downloaded.";
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			File f = new File(LabyrinthProvider.getInstance().getPluginInstance().getDataFolder(), "downloads/" + output + "/" + file + "." + type);
			File parent = f.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) return () -> "Unable to access output location.";
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			return () -> "SUCCESS";
		} catch (IOException ex) {
			try {
				File f = new File("log.txt");
				PrintWriter myWriter = new PrintWriter(f);
				for (StackTraceElement trace : ex.getStackTrace()) {
					Date now = new Date();
					myWriter.println("[" + now.toLocaleString() + "] " + trace.toString());
				}
				myWriter.close();
			} catch (FileNotFoundException e) {
				return () -> "Unable to locate target file from url & log failure.";
			}
			return () -> "Resource " + check.getResource() + " doesn't have a release available.";
		}
	}

}

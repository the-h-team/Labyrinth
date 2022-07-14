package com.github.sanctum.labyrinth.interfacing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SpigotUpdate implements ResourceCheck {

	private static final long serialVersionUID = -1189891322392928733L;
	final String project;
	final String author;
	String recent;
	String latest;
	final int id;

	public SpigotUpdate(String project, String author, int id) {
		this.project = project;
		this.author = author;
		this.id = id;

		File test = new File(project);

		if (test.exists()) {
			if (test.isDirectory()) {
				for (File f : test.listFiles()) {
					if (f.getName().endsWith(".txt")) {
						recent = f.getName().replace(".txt", "");
						break;
					}
				}
			}
		}

	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getResource() {
		return project;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getCurrent() {
		return recent;
	}

	@Override
	public String getLatest() {
		return latest;
	}

	@Override
	public void run() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id);
			this.latest = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream())).readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

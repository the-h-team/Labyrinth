package com.github.sanctum.labyrinth.library;

public final class MailerPrefix {

	private final Mailer parent;
	private String b;
	private String m;
	private String e;

	MailerPrefix(Mailer parent) {
		this.parent = parent;
	}

	public MailerPrefix start(String b) {
		this.b = b;
		return this;
	}

	public MailerPrefix middle(String e) {
		this.e = e;
		return this;
	}

	public MailerPrefix end(String m) {
		this.m = m;
		return this;
	}

	public String start() {
		return this.b;
	}

	public String middle() {
		return this.m;
	}

	public String end() {
		return this.e;
	}

	public String join() {
		String joined = "";
		if (start() != null) {
			joined = start();
		}
		if (middle() != null) {
			joined = joined + middle();
		}
		if (end() != null) {
			joined = joined + end();
		}
		return joined;
	}

	public boolean isEmpty() {
		return (start() != null && start().isEmpty()) && (middle() != null && middle().isEmpty()) && (end() != null && end().isEmpty());
	}

	public Mailer finish() {
		return this.parent;
	}

}

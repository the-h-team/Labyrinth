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

	public MailerPrefix middle(String m) {
		this.m = m;
		return this;
	}

	public MailerPrefix end(String e) {
		this.e = e;
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
		if (start() != null && middle() != null && end() != null) {
			return start() + middle() + end();
		}
		if (start() != null && middle() != null) {
			return start() + middle();
		}
		if (start() != null) {
			return start();
		}
		return "";
	}

	public boolean isEmpty() {
		return start() == null && middle() == null && end() == null;
	}

	public Mailer finish() {
		return this.parent;
	}

}

package com.github.sanctum.labyrinth.common;

public final class MailablePrefix {

	private final MessageComposer parent;
	private String b;
	private String m;
	private String e;

	MailablePrefix(MessageComposer parent) {
		this.parent = parent;
	}

	public MailablePrefix start(String b) {
		this.b = b;
		return this;
	}

	public MailablePrefix middle(String m) {
		this.m = m;
		return this;
	}

	public MailablePrefix end(String e) {
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

	public MessageComposer finish() {
		return this.parent;
	}

}

package com.github.sanctum.labyrinth.placeholders;

import com.github.sanctum.labyrinth.annotation.Note;

public interface Placeholder {

	Placeholder ANGLE_BRACKETS = new Placeholder() {
		@Override
		public char start() {
			return '<';
		}

		@Override
		public char end() {
			return '>';
		}
	};
	Placeholder CURLEY_BRACKETS = new Placeholder() {
		@Override
		public char start() {
			return '{';
		}

		@Override
		public char end() {
			return '}';
		}
	};
	Placeholder PERCENT = new Placeholder() {
		@Override
		public char start() {
			return '%';
		}

		@Override
		public char end() {
			return '%';
		}
	};

	char start();

	default @Note("This might be empty!") CharSequence parameters() {
		return "";
	}

	char end();

	default boolean isSame(Placeholder placeholder) {
		return start() == placeholder.start() && end() == placeholder.end() && parameters().equals(placeholder.parameters());
	}


}

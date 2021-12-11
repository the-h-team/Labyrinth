package com.github.sanctum.labyrinth.data;

public final class LinkedLabyrinthSet<E> extends LabyrinthSet<E> {

	public E getFirst() {
		if (head == null) return null;
		return head.data;
	}

	public E getLast() {
		if (tail == null) return null;
		return tail.data;
	}

}

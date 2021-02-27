package com.github.sanctum.labyrinth.task;


import com.github.sanctum.labyrinth.library.Applicable;

public class Schedule {

	public static Synchronous sync(Applicable applicable) {
		return new Synchronous(applicable);
	}

	public static Asynchronous async(Applicable applicable) {
		return new Asynchronous(applicable);
	}

}

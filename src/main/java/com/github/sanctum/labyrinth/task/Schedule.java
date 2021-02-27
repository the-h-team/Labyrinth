package com.github.sanctum.labyrinth.task;


import com.github.sanctum.labyrinth.library.Applicable;
import org.bukkit.entity.Player;

public class Schedule {

	/**
	 * A basic builder for a synchronous specific task you wish to complete.
	 *
	 * <p>Execution Results: [run, wait, repeat, repeatReal, waitReal]
	 *
	 * Pre-conditions: [{@link Synchronous#cancelEmpty()},
	 * {@link Synchronous#cancelAfter(Player)},
	 * {@link Synchronous#cancelAfter(int)}]</p>
	 *
	 * @param applicable The applicable data to execute within the task via void or lambda reference.
	 * @return A synchronous task builder.
	 */
	public static Synchronous sync(Applicable applicable) {
		return new Synchronous(applicable);
	}

	/**
	 * A basic builder for an asynchronous specific task you wish to complete.
	 *
	 * <p>Execution Results: [run, wait, repeat]
	 *
	 * Pre-conditions: [{@link Asynchronous#cancelAfter(Player)},
	 * {@link Asynchronous#cancelAfter(int)},
	 * {@link Asynchronous#cancelEmpty()}]</p>
	 *
	 * @param applicable The applicable data to execute within the task via void or lambda reference.
	 * @return A synchronous task builder.
	 */
	public static Asynchronous async(Applicable applicable) {
		return new Asynchronous(applicable);
	}

}

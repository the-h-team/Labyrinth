package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.library.TypeFlag;

public interface Counter<N extends Number> extends Service {

	void add();

	void subtract();

	N get();

	static <N extends Number> Counter<N> newInstance() {
		return new Counter<N>() {

			Number i;
			boolean integer = false;

			{
				TypeFlag<N> flag = TypeFlag.get();
				Class<N> type = flag.getType();
				if (type == Integer.class) {
					i = 0L;
					integer = true;
				} else i = 0.0D;
			}

			@Override
			public void add() {
				if (integer) {
					i = (i.longValue() + 1L);
				} else i = (i.doubleValue() + 1D);
			}

			@Override
			public void subtract() {
				if (integer) {
					i = (i.longValue() - 1L);
				} else i = (i.doubleValue() - 1D);
			}

			@Override
			public N get() {
				return (N) i;
			}
		};
	}

	static <N extends Number> Counter<N> newInstance(N influence) {
		return new Counter<N>() {

			Number i;
			final Number inf = influence;
			boolean integer = false;

			{
				TypeFlag<N> flag = TypeFlag.get();
				Class<N> type = flag.getType();
				if (type == Integer.class) {
					i = 0L;
					integer = true;
				} else i = 0.0D;
			}

			@Override
			public void add() {
				if (integer) {
					i = (i.longValue() + inf.longValue());
				} else i = (i.doubleValue() + inf.doubleValue());
			}

			@Override
			public void subtract() {
				if (integer) {
					i = (i.longValue() - inf.longValue());
				} else i = (i.doubleValue() - inf.doubleValue());
			}

			@Override
			public N get() {
				return (N) i;
			}
		};
	}

}

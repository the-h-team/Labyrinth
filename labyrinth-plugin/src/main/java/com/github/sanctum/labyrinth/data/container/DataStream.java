package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.HUID;
import java.io.Serializable;

@Deprecated
public abstract class DataStream implements Serializable {

	private static final long serialVersionUID = -5132123038980309392L;

	public abstract HUID getId();

	public abstract String value();

	public abstract String value(int index);

	public abstract String getMetaId();

}

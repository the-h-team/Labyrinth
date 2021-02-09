package com.github.sanctum.labyrinth.data.container;

import com.github.sanctum.labyrinth.library.HUID;
import java.io.Serializable;

public abstract class DataStream implements Serializable {

	public abstract HUID getId();

	public abstract String value();

	public abstract String value(int index);

	public abstract String getMetaId();

}

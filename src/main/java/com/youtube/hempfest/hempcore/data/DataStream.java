package com.youtube.hempfest.hempcore.data;

import com.youtube.hempfest.hempcore.library.HUID;
import java.io.Serializable;

public abstract class DataStream implements Serializable {

	public abstract HUID getId();

	public abstract String value();

	public abstract String value(int index);

	public abstract String getMetaId();

}

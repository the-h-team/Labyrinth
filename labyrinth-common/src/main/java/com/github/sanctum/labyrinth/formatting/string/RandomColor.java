package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.data.service.Constant;
import java.util.Random;
import net.md_5.bungee.api.ChatColor;

public class RandomColor extends Number {

	private static final long serialVersionUID = -7633896621271251363L;
	private final String color;
	private final Number parent;

	public RandomColor() {
		int i = new Random().nextInt(10);
		this.color = "&" + i;
		this.parent = i;
	}

	@Override
	public String toString() {
		return color;
	}

	public ChatColor toColor() {
		return Constant.values(ChatColor.class).stream().filter(chatColorConstant -> chatColorConstant.getValue().toString().equals(color)).map(Constant::getValue).findFirst().get();
	}


	@Override
	public int intValue() {
		return parent.intValue();
	}

	@Override
	public long longValue() {
		return parent.longValue();
	}

	@Override
	public float floatValue() {
		return parent.floatValue();
	}

	@Override
	public double doubleValue() {
		return parent.doubleValue();
	}
}

package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.library.RandomObject;
import org.jetbrains.annotations.NotNull;

public abstract class SpecialID implements CharSequence {

	@Override
	public abstract String toString();

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj instanceof SpecialID ? ((SpecialID)obj).toString().equals(toString()) : obj.toString().equals(toString());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private int length;
		private Character[] options = new Character[]{
				'A', 'a', 'B', 'b', 'C', 'c', 'D', 'd', 'E', 'e',
				'F', 'f', 'G', 'g', 'H', 'h', 'I', 'i', 'J', 'j',
				'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o',
				'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T', 't',
				'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y',
				'Z', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9'};

		Builder() {
		}

		public Builder setLength(int length) {
			this.length = length;
			return this;
		}

		public Builder setOptions(Character... chars) {
			if (chars != null && chars.length > 0) {
				this.options = chars;
			}
			return this;
		}

		public SpecialID build(@NotNull Object o) {
			return new SpecialID() {

				private final String id;

				{
					final int code = o.hashCode();
					final String[] hash = String.valueOf(code).replace("-", "").split("");
					final StringBuilder builder = new StringBuilder();
					final RandomObject<Character> letter = new RandomObject<>(options);
					while (builder.length() != length) {
						if (builder.length() > hash.length) {
							builder.append(letter.get(length - builder.length() + code));
						} else {
							char chara = letter.get(Integer.parseInt(hash[Math.max(0, builder.length() - 1)]));
							builder.append(chara);
						}
					}
					this.id = builder.toString();
				}

				@Override
				public String toString() {
					return id;
				}

				@Override
				public int length() {
					return id.length();
				}

				@Override
				public char charAt(int index) {
					return id.charAt(index);
				}

				@NotNull
				@Override
				public CharSequence subSequence(int start, int end) {
					return id.subSequence(start, end);
				}
			};
		}

	}

}

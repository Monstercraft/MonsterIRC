package org.monstercraft.irc.util;

	public enum Colors {
		BLACK("&0"),
		DARK_BLUE("&1"),
		GREEN("&2"),
		TURQUOISE("&3"),
		RED("&4"),
		PURPLE("&5"),
		ORANGE("&6"),
		GREY("&7"),
		BROWN("&8"),
		LIGHT_BLUE("&9"),
		LIGHT_GREEN("&a"),
		CYAN("&b"),
		LIGHT_RED("&c"),
		PINK("&d"),
		YELLOW("&e"),
		WHITE("&f");
		
		final String code;

		private Colors(final String code) {
			this.code = code;
		}

		public String getColorCode() {
			return code;
		}
	}
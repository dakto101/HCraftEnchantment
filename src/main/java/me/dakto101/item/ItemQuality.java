package me.dakto101.item;

public enum ItemQuality {
	COMMON("§fThường", 1),
	UNCOMMON("§2Khá", 2), 
	RARE("§bHiếm", 3),
	ELITE("§cƯu", 4),
	MYSTERY("§dThần bí", 5),
	LEGENDARY("§eHuyền thoại", 6),
	RANDOM("§6Ngẫu nhiên", 7);
	
	private String name;
	private int level;

	ItemQuality(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return this.name;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public static ItemQuality getItemQuality(int level) {
		for (ItemQuality q : ItemQuality.values()) {
			if (q.getLevel() == level) return q;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getName() + ", " + getLevel();
	}
}

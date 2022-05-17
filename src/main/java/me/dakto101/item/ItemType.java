package me.dakto101.item;

public enum ItemType {

	ADVANCED_ENCHANTED_BOOK("§e§lSách phù phép cao cấp"),
	RANDOM_ENCHANTED_BOOK("§a§lSách phù phép ngẫu nhiên"),
	AMULET("§c§lBùa hộ mệnh"),
	LUCKY_DUST("§6§lBụi may mắn"),
	MAGIC_GEM("§b§lĐá ma pháp"),
	RECIPE_BOOK("§e§lBí kíp"),
	SKILL_BOOK("§6§lSách kỹ năng")
	;

	private String name;

	ItemType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

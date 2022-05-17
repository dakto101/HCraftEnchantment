package me.dakto101.gui;

public class GUIManager {

	public static void registerMenuGUI() {
		EnchantmentListGUI.register();
		MemberMenuGUI.register();
		AdminMenuGUI.register();
		ItemListGUI.register();
		EnchantmentCheckGUI.register();
		PlayerSkillGUI.register();
		PlayerSkillDetailGUI.register();
		SkillListGUI.register();
	}
	
}

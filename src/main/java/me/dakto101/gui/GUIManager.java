package me.dakto101.gui;

import org.json.simple.ItemList;

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

	public static void unregisterMenuGUI() {
		EnchantmentListGUI.unregister();
		MemberMenuGUI.unregister();
		AdminMenuGUI.unregister();
		ItemListGUI.unregister();
		EnchantmentCheckGUI.unregister();
		PlayerSkillGUI.unregister();
		PlayerSkillDetailGUI.unregister();
		SkillListGUI.unregister();
	}
	
}

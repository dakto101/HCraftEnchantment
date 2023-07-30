package me.dakto101;

import me.dakto101.listener.*;
import org.bukkit.plugin.java.JavaPlugin;

import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.api.SkillAPI;
import me.dakto101.cmd.CommandManager;
import me.dakto101.enchantment.armor.BaoHiem;
import me.dakto101.enchantment.armor.BenBi;
import me.dakto101.enchantment.armor.CheChan;
import me.dakto101.enchantment.armor.KhangDoc;
import me.dakto101.enchantment.armor.KhangLua;
import me.dakto101.enchantment.armor.KhangPhep;
import me.dakto101.enchantment.armor.PhanCong;
import me.dakto101.enchantment.armor.ThieuDot;
import me.dakto101.enchantment.armor.TruyenLua;
import me.dakto101.enchantment.armor.TuHuy;
import me.dakto101.enchantment.melee.CanBang;
import me.dakto101.enchantment.melee.ChiMang;
import me.dakto101.enchantment.melee.ChiTu;
import me.dakto101.enchantment.melee.CuongHuyet;
import me.dakto101.enchantment.melee.HatTung;
import me.dakto101.enchantment.melee.HutMau;
import me.dakto101.enchantment.melee.KyBinh;
import me.dakto101.enchantment.melee.PhuDau;
import me.dakto101.enchantment.melee.SacNet;
import me.dakto101.enchantment.melee.TieuDiet;
import me.dakto101.enchantment.melee.TuChien;
import me.dakto101.enchantment.melee.VinhQuang;
import me.dakto101.enchantment.melee.XuyenPha;
import me.dakto101.enchantment.potion.Doc;
import me.dakto101.enchantment.potion.HoiPhuc;
import me.dakto101.enchantment.potion.KhangCu;
import me.dakto101.enchantment.potion.KhoHeo;
import me.dakto101.enchantment.potion.LamCham;
import me.dakto101.enchantment.potion.Mu;
import me.dakto101.enchantment.potion.SucManh;
import me.dakto101.enchantment.potion.SuyYeu;
import me.dakto101.enchantment.potion.TocDo;
import me.dakto101.enchantment.ranged.ChuanXac;
import me.dakto101.enchantment.ranged.DanHoi;
import me.dakto101.enchantment.ranged.LongVu;
import me.dakto101.enchantment.ranged.ThuocNo;
import me.dakto101.enchantment.ranged.TichDien;
import me.dakto101.enchantment.ranged.XuyenGiap;
import me.dakto101.gui.GUIManager;
import me.dakto101.skill.TestSkill;
import me.dakto101.skill.archery.BangTien;
import me.dakto101.skill.archery.CuNhayBungNo;
import me.dakto101.skill.archery.KichDoc;
import me.dakto101.skill.archery.MuiTenBac;
import me.dakto101.skill.archery.PhaGiap;
import me.dakto101.skill.archery.PhaoCoi;
import me.dakto101.skill.archery.TanXaTien;
import me.dakto101.skill.magic.BomHenGio;
import me.dakto101.skill.magic.CauLua;
import me.dakto101.skill.magic.ChoSan;
import me.dakto101.skill.magic.CuongPhong;
import me.dakto101.skill.magic.LucHapDan;
import me.dakto101.skill.magic.TriLieu;
import me.dakto101.skill.magic.TrungPhat;
import me.dakto101.skill.swordsmanship.BatGiu;
import me.dakto101.skill.swordsmanship.CanQuet;
import me.dakto101.skill.swordsmanship.ChienMa;
import me.dakto101.skill.swordsmanship.GiaoChien;
import me.dakto101.skill.swordsmanship.Luot;
import me.dakto101.skill.swordsmanship.TuBao;
import me.dakto101.skill.swordsmanship.XoayKiem;
import me.dakto101.skill.unarmed.BatTu;
import me.dakto101.skill.unarmed.DamMoc;
import me.dakto101.skill.unarmed.DiaChan;
import me.dakto101.skill.unarmed.DiemHuyet;
import me.dakto101.skill.unarmed.LaChan;
import me.dakto101.skill.unarmed.VoAnhCuoc;

public class HCraftEnchantment extends JavaPlugin {

	public static HCraftEnchantment plugin;
    
    public void onEnable() {
    	plugin = this;
    	
    	
    	ListenerManager.registerEvents(
    		new CustomEnchantmentListener(), 
    		new GUIListener(),
    		new ItemListener(), 
    		new SkillListener(),
    		new SetNoDamageTickListener(),
			new AdminListener()
    	);
    	CommandManager.registerCommand();
    	CustomEnchantmentAPI.registerEnchantments(
    		//Test enchantment
    		//new TestEnchantment(),
    		//Melee enchantment
    		new ChiMang(), new HutMau(), new CuongHuyet(), new PhuDau(), new HatTung(),
    		new SacNet(), new ChiTu(), new CanBang(), new TuChien(), new TieuDiet(),
    		new VinhQuang(), new KyBinh(), new XuyenPha(),
    		//Ranged enchantment
    		new LongVu(), new DanHoi(), new ChuanXac(),
    		new TichDien(), new ThuocNo(), new me.dakto101.enchantment.ranged.HuyetCung(), new XuyenGiap(),
    		//Potion enchantment
    		new Doc(), new KhoHeo(), new LamCham(), new SuyYeu(), new Mu(), 
    		new TocDo(), new SucManh(), new KhangCu(), new HoiPhuc(),
    		//Armor enchantment
    		new PhanCong(), new BenBi(), new KhangLua(), new KhangPhep(), new KhangDoc(),
    		new BaoHiem(), new ThieuDot(), new TruyenLua(), new TuHuy(), new CheChan()
    		//Active enchantment
    	);
    	SkillAPI.registerEnchantments(
    		//Other skill
    		new TestSkill(), 
    		//Archery skill
    		new PhaGiap(), new TanXaTien(), new CuNhayBungNo(), new PhaoCoi(), new me.dakto101.skill.archery.HuyetCung(),
    		new KichDoc(), new BangTien(), new MuiTenBac(),
    		//Magic skill
    		new LucHapDan(), new TriLieu(), new TrungPhat(), new CauLua(), new CuongPhong(),
    		new ChoSan(), new BomHenGio(),
    		//Swordsmanship skill
    		new GiaoChien(), new Luot(), new TuBao(), new CanQuet(), new BatGiu(), new XoayKiem(),
    		new ChienMa(),
    		//Unarmed skill
    		new LaChan(), new BatTu(), new DiaChan(), new DamMoc(), new VoAnhCuoc(),
    		new DiemHuyet()
    	);
    	GUIManager.registerMenuGUI();
    	
    	// MySQL.setup();
    	
    }
    
    public void onDisable() {
		SkillAPI.unregisterEnchantments();
		CustomEnchantmentAPI.unregisterEnchantments();
		GUIManager.unregisterMenuGUI();
    	// Unregister all
    }
    
}

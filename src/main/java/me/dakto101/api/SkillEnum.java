package me.dakto101.api;

import java.util.ArrayList;
import java.util.List;

public enum SkillEnum {
	
	TEST_SKILL("Test test", -1),
	/**
	 * ARCHERY: ID = 1-99
	 */
	PHA_GIAP("Phá giáp", 1),
	TAN_XA_TIEN("Tán xạ tiễn", 2),
	CU_NHAY_BUNG_NO("Cú nhảy bùng nổ", 3),
	PHAO_COI("Pháo cối", 4),
	HUYET_CUNG("Huyết cung", 5),
	KICH_DOC("Kịch độc", 6),
	BANG_TIEN("Băng tiễn", 7),
	MUI_TEN_BAC("Mũi tên bạc", 8),
	/**
	 * MAGIC: ID = 100-199
	 */
	LUC_HAP_DAN("Lực hấp dẫn", 100),
	TRI_LIEU("Trị liệu", 101),
	TRUNG_PHAT("Trừng phạt", 102),
	CAU_LUA("Cầu lửa", 103),
	CUONG_PHONG("Cuồng phong", 104),
	CHO_SAN("Chó săn", 105),
	BOM_HEN_GIO("Bom hẹn giờ", 106),
	/**
	 * SWORDSMANSHIP: ID = 200-299
	 */
	GIAO_CHIEN("Giao chiến", 200),
	LUOT("Lướt", 201),
	TU_BAO("Tụ bão", 202),
	CAN_QUET("Càn quét", 203),
	BAT_GIU("Bắt giữ", 204),
	XOAY_KIEM("Xoay kiếm", 205),
	CHIEN_MA("Chiến mã", 206),
	/**
	 * UNARMED: ID = 300-399
	 */
	BAT_TU("Bất tử", 300),
	LA_CHAN("Lá chắn", 301),
	DIA_CHAN("Địa chấn", 302), 
	DAM_MOC("Đấm móc", 303),
	VO_ANH_CUOC("Vô ảnh cước", 304),
	DIEM_HUYET("Điểm huyệt", 305)
	;
	
	private String name;
	private int id;

    /**
     * An enum set name and id for a specific skill.
     */
	SkillEnum(final String name, final int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	/**
	 * 
	 * @param list of SkillType
	 * @return SkillType toString
	 */
	public static String toString(final List<SkillEnum> list) {
		String s = "";
		for (SkillEnum type : list) {
			s += type.getName();
			s += ", ";
		}
		s = s.substring(0, s.length() - 2);
		return s;
	}
	/**
	 * 
	 * @param s string formatted by toString(List<SkillType>)
	 * @see SkillEnum#toString(List)
	 * @return list of SkillType
	 */
	public static List<SkillEnum> toList(final String s) {
		List<SkillEnum> result = new ArrayList<SkillEnum>();
		for (SkillEnum type : SkillEnum.values()) {
			if (s.contains(type.getName())) result.add(type);
		}
		return result;
	}
}

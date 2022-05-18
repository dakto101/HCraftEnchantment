package me.dakto101.permission;

public enum HPermission {

	COMMAND_ADMIN("hcraftenchantment.admin"),
	COMMAND_MEMBER("hcraftenchantment.member");

	private String name;

	/**
	 * An enum set name and id for a specific permission.
	 *
	 * @param name permission name
	 */
	HPermission(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}

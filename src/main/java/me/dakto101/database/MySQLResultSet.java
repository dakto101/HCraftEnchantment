package me.dakto101.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

public class MySQLResultSet {
	
	private Connection con;
	private ResultSet rs;
	
	public MySQLResultSet(Connection con, ResultSet rs) {
		this.con = con;
		this.rs = rs;
	}
	
	public Connection getConnection() {
		return this.con;
	}
	
	public ResultSet getResultSet() {
		return this.rs;
	}
	
	public void closeConnection() {
		try {
			this.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() {
		try {
			//Close connection
			if ((con != null) && (!con.isClosed())) {
				con.close();
				Bukkit.getConsoleSender().sendMessage("§c<HCraftEnchantment - MySQL> Plugin chua dong ket noi database sau khi truy van!! (" + this.getClass().getName() + ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

package me.dakto101.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class MySQL {
	
	public static final String DBNAME_HCRAFT_ENCHANTMENT = "hcraft_enchantment";
	public static final String TABLENAME_PLAYER_SKILL = "player_skill";
	public static final String TABLENAME_PLAYER_CHOSEN_SKILL = "player_chosen_skill";
	public static final String TABLENAME_PLAYER = "player";
	
	public static void setup() {
		Bukkit.getConsoleSender().sendMessage("<HCraftEnchantment - MySQL>: Creating database if not exists...");
		String createDatabase = "CREATE DATABASE IF NOT EXISTS " + DBNAME_HCRAFT_ENCHANTMENT + ";";
		String createPlayerSkillTable = "CREATE TABLE IF NOT EXISTS " + TABLENAME_PLAYER_SKILL + "("
				+ "id INT(11) NOT NULL AUTO_INCREMENT, "
				+ "player_uuid VARCHAR(255) NOT NULL, "
				+ "skill_id INT NOT NULL, "
				+ "xp INT, "
				+ "require_xp INT, "
				+ "level INT, "
				+ ""
				+ "CONSTRAINT pk_id PRIMARY KEY(id)"
				+ ") ENGINE = INNODB;";
		String createPlayerChosenSkillTable = "CREATE TABLE IF NOT EXISTS " + TABLENAME_PLAYER_CHOSEN_SKILL + "("
				+ "id INT(11) NOT NULL AUTO_INCREMENT, "
				+ "player_uuid VARCHAR(255) NOT NULL UNIQUE, "
				+ "skill_id INT NOT NULL, "
				+ ""
				+ "CONSTRAINT pk_id PRIMARY KEY(id)"
				+ ") ENGINE = INNODB;";
		String createPlayerTable = "CREATE TABLE IF NOT EXISTS " + TABLENAME_PLAYER + "("
				+ "id INT(11) NOT NULL AUTO_INCREMENT, "
				+ "player_uuid VARCHAR(255) NOT NULL UNIQUE, "
				+ "player_name VARCHAR(255) NOT NULL, "
				+ ""
				+ "CONSTRAINT pk_id PRIMARY KEY(id)"
				+ ") ENGINE = INNODB;";
		sendQuery(createDatabase, "" , false);
		sendQuery(createPlayerSkillTable, DBNAME_HCRAFT_ENCHANTMENT, false);
		sendQuery(createPlayerChosenSkillTable, DBNAME_HCRAFT_ENCHANTMENT, false);
		sendQuery(createPlayerTable, DBNAME_HCRAFT_ENCHANTMENT, false);
	}
	
	public static synchronized MySQLResultSet sendSelectQuery(String query, String databaseName, boolean notifyConsole) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false","dakto101",""); 
            Statement stmt = con.createStatement();
            
            ResultSet rs;
            rs = stmt.executeQuery(query);
            
            if (notifyConsole == true) {
            	Bukkit.getServer().getConsoleSender().sendMessage("<HCraftEnchantment - MySQL>: " + query);
            }
            
            MySQLResultSet sqlrs = new MySQLResultSet(con, rs);
            
            return sqlrs;
            
        } catch (SQLException e) {
            System.err.println("(#HCraftEnchantment#me.dakto101.database.MySQL): SQLException! " + e.getMessage());
        } catch (Exception e) {
        	System.err.println("(#HCraftEnchantment#me.dakto101.database.MySQL): Exception! " + e.getMessage());
        }
		return null;
	}
	
	/* Send a query to database.
	 * 
	 */
	public static synchronized int sendQuery(String query, String databaseName, boolean notifyConsole) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false","dakto101",""); 
            Statement stmt = con.createStatement();
            if (notifyConsole == true) Bukkit.getServer().getLogger().info("<HCraftEnchantment - MySQL>: " + query);
            
            int rs;
            rs = stmt.executeUpdate(query);
            stmt.execute(query);
            con.close();
            return rs;
        } catch (SQLException e) {
            System.err.println("(#HCraftEnchantment#me.dakto101.database.MySQL): SQLException! " + e.getMessage());
        } catch (Exception e) {
        	System.err.println("(#HCraftEnchantment#me.dakto101.database.MySQL): Exception! " + e.getMessage());
        }
		return 0;
	}
	
	public static synchronized int sendUpdate(String query, String databaseName, boolean notifyConsole) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false","dakto101",""); 
            Statement stmt = con.createStatement();
            
            int rs;
            rs = stmt.executeUpdate(query);
            stmt.executeUpdate(query);
            
            if (notifyConsole == true) Bukkit.getServer().getConsoleSender().sendMessage("<HCraftEnchantment - MySQL>: " + query);

            con.close();
            return rs;
        } catch (SQLException e) {
            System.err.println("(#HCraftEnchantment#me.dakto101.database.MySQL): SQLException! " + e.getMessage());
        } catch (Exception e) {
        	System.err.println("(#HCraftEnchantment#me.dakto101.database.MySQL): Exception! " + e.getMessage());
        }
		return 0;
	}
	
}

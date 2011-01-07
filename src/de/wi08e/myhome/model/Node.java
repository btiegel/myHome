package de.wi08e.myhome.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.wi08e.myhome.database.Database;

/**
 * This is a class to identify nodes (=external components). 
 * It is identified by a type, a manufacture and an id.
 * 
 * Examples for category: "enocean", "ip-camera", "cellphone", "email", ...
 * Exampled for manufacture: "generic", "simulator", "userdefined", "thermokon", "0x001", ...
 *  
 * If there is no known manufacture "generic" is used instead.
 * 
 * Type and manufacturer are lower case strings and therefore automatically converted.
 * 
 * Type, a manufacture and an id are not allowed to contain the character ":", because this is used as
 * delimiter for the descriptor
 * 
 * category, manufacture and id should me no longer than 50 chars.
 * 
 * @author Marek
 *
 */
public class Node {
	
	private int databaseId = -1;
	
	private Map<String, String> status = Collections.synchronizedMap(new HashMap<String, String>());
	
	private String category;
	private String manufacturer;
	private String hardwareId;
	
	private String type = "unknown";
	
	/**
	 * Returns the type in lower case letters 
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * Returns the manufacturer in lower case letters 
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	
	/**
	 * Returns the id 
	 */
	public String getHardwareId() {
		return hardwareId;
	}
	
	/**
	 * initiates the object from type, manufacturer and id
	 * @param category Category
	 * @param manufacturer Manufacturer
	 * @param hardwareId Id
	 * @throws IllegalArgumentException Is thrown when ":" is used in the parameters or any parameter is left blank
	 */
	public Node(String category, String manufacturer, String hardwareId) {
		super();
		
		if (category.contains(":") || 
				manufacturer.contains(":") || 
				hardwareId.contains(":") || 
				category.length() == 0 ||
				manufacturer.length() == 0 ||
				hardwareId.length() == 0)
			throw new IllegalArgumentException();
		
		this.category = category.toLowerCase();
		this.manufacturer = manufacturer.toLowerCase();
		this.hardwareId = hardwareId;
	}
	
	/**
	 * initiates the object from descriptor
	 * @param descriptor Descriptor, as generated by toString()
	 * @throws IllegalArgumentException Is thrown descriptor is not valid
	 */
	/* NOT USED AT THE MOMENT
	public Node(String descriptor) {
		super();
		
		String[] elements = descriptor.split(":");
		if (elements.length != 3 ||
			elements[0].length() == 0 ||
			elements[1].length() == 0 ||
			elements[2].length() == 0) 
			throw new IllegalArgumentException();
		
		
		
		this.type = elements[1].toLowerCase();
		this.manufacturer = elements[2].toLowerCase();
		this.id = elements[3];
	}
	*/
	
	/**
	 * @throws SQLException 
	 * Create node from ResultSet. 
	 * @throws  
	 */
	public Node(ResultSet resultSet) throws SQLException  {
		this(resultSet.getString("category"), resultSet.getString("manufacturer"), resultSet.getString("hardware_id"));
		setType(resultSet.getString("type"));
		setDatabaseId(resultSet.getInt("id"));
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type == null)
			this.type = "";
		else
			this.type = type.toLowerCase();
	}

	public Map<String, String> getStatus() {
		return status;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public synchronized void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	/**
	 * Returns the descriptor for this Node as a single string
	 */
	public String toString() {
		return category+':'+manufacturer+':'+hardwareId;
	}

	public boolean equals(Node node) {
		return (node.toString().contentEquals(toString()));
	}
	
	public void loadStatus(Database database) {
		Statement getStatus;
		try {
			getStatus = database.getConnection().createStatement();
			if (getStatus.execute("SELECT `key`, value FROM node_status WHERE node_id="+String.valueOf(databaseId)+";")) {
				ResultSet rs2 = getStatus.getResultSet();
				while (rs2.next()) 
					getStatus().put(rs2.getString("key"), rs2.getString("value"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

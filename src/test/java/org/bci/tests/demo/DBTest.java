package org.bci.tests.demo;

import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import org.bci.utilities.DBManager;

public class DBTest {
	
	
	@Test
	public void verifyInventoryStatusInDB() {
	    String sku = "WMS-SKU-1001";
	    
	    // Query SSMS database to check stock status
	    List<Map<String, Object>> dbResult = DBManager.executeQuery(
	        "SELECT Quantity, Status FROM Inventory WHERE SKU = ?", sku);
	    
	    org.testng.Assert.assertFalse(dbResult.isEmpty(), "SKU not found in database!");
	    
	    int quantity = (int) dbResult.get(0).get("Quantity");
	    assertTrue(quantity > 0, "Inventory quantity is zero or negative in DB!");
	}

}

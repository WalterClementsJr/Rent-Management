/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.database;

import java.sql.ResultSet;
import main.model.Customer;
import main.model.Room;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author walker
 */
public class DatabaseHandlerTest {
    
    public DatabaseHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class DatabaseHandler.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        DatabaseHandler expResult = null;
        DatabaseHandler result = DatabaseHandler.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeQuery method, of class DatabaseHandler.
     */
    @Ignore
    public void testExecuteQuery() {
        System.out.println("executeQuery");
        String query = "";
        DatabaseHandler instance = new DatabaseHandler();
        ResultSet expResult = null;
        ResultSet result = instance.execQuery(query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of executeUpdate method, of class DatabaseHandler.
     */
    @Ignore
    public void testExecuteUpdate() {
        System.out.println("executeUpdate");
        String query = "";
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.execUpdate(query);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isCMNDExist method, of class DatabaseHandler.
     */
    @Ignore
    public void testIsCMNDExist() {
        System.out.println("isCMNDExist");
        String cmnd = "";
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.isCMNDExist(cmnd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of insertNewCustomer method, of class DatabaseHandler.
     */
    @Ignore
    public void testInsertNewCustomer() {
        System.out.println("insertNewCustomer");
        Customer customer = null;
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.insertNewCustomer(customer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateCustomer method, of class DatabaseHandler.
     */
    @Ignore
    public void testUpdateCustomer() {
        System.out.println("updateCustomer");
        Customer customer = null;
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.updateCustomer(customer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteCustomer method, of class DatabaseHandler.
     */
    @Ignore
    public void testDeleteCustomer() {
        System.out.println("deleteCustomer");
        Customer customer = null;
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.deleteCustomer(customer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of insertNewRoom method, of class DatabaseHandler.
     */
    @Ignore
    public void testInsertNewRoom() {
        System.out.println("insertNewRoom");
        Room room = null;
        int type = 0;
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.insertNewRoom(room, type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateRoom method, of class DatabaseHandler.
     */
    @Ignore
    public void testUpdateRoom() {
        System.out.println("updateRoom");
        Room room = null;
        int type = 0;
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.updateRoom(room, type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteRoom method, of class DatabaseHandler.
     */
    @Ignore
    public void testDeleteRoom() {
        System.out.println("deleteRoom");
        Room room = null;
        DatabaseHandler instance = new DatabaseHandler();
        boolean expResult = false;
        boolean result = instance.deleteRoom(room);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createConnection method, of class DatabaseHandler.
     */
    @Test
    public void testCreateConnection() {
        System.out.println("createConnection");
        DatabaseHandler instance = new DatabaseHandler();
        instance.createConnection();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

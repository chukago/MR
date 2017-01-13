package org.tco.railroad.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

public class SQLiteDatabase {

	private String _dbName = null;
	private Connection _con = null;
	private Semaphore _transactionSem = new Semaphore(1);
	private boolean _transactionMode = false;
	
	public SQLiteDatabase(String dbName) {
		
		try {
			
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e) {
			
			e.printStackTrace();
			return;
		}
		
		if (dbName == null || dbName.isEmpty()) {
			
			return;
		}
		
		_dbName = dbName;
	}
	
	public synchronized boolean transaction() {
		
		try {
			
			_transactionSem.acquire();
			_transactionMode = true;
		}
		catch (InterruptedException e) {
		
			_transactionSem.release();
			_transactionMode = false;
			
			e.printStackTrace();
			return false;
		}
		
		try {
			
			openConnection();
			_con.setAutoCommit(false);
		}
		catch (SQLException e) {
			
			_transactionSem.release();
			_transactionMode = false;
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public synchronized boolean commit() {
		
		try {
			
			_con.commit();
		}
		catch (SQLException e) {
			
			System.out.println("Cannot rollback transaction");
			e.printStackTrace();
			return false;
		}
		finally {
			
			_transactionSem.release();
			_transactionMode = false;
			closeConnection();
		}
		
		return true;
	}
	
	public synchronized boolean rollback() {
		
		try {
			
			_con.rollback();
		}
		catch (SQLException e) {
			
			System.out.println("Cannot rollback transaction");
			e.printStackTrace();
			return false;
		}
		finally {
			
			_transactionSem.release();
			_transactionMode = false;
			closeConnection();
		}
		
		return true;
	}
	
	private synchronized boolean openConnection() {
		
		try {
			
			if (_con != null && !_con.isClosed()) {
			
				if (_transactionMode) {
					
					return true;
				}
				
				System.out.println("Connection is already opened");
				return false;
			}
			
			if (_dbName == null || _dbName.isEmpty()) {
				
				System.out.println("Database name is null");
				return false;
			}
			
			_con = DriverManager.getConnection("jdbc:sqlite:" + _dbName);
			
		}
		catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private synchronized boolean closeConnection() {
		
		try {
			
			if (_transactionMode) {
				
				return true;
			}
			
			if (_con.isClosed()) {
				
				System.out.println("Connection is already closed");
				return true;
			}
			
			_con.close();
		}
		catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private Statement createStatement() {
		
		Statement s;
		
		try {
			
			if (_con.isClosed()) {
				
				System.out.println("Connection is closed");
				return null;
			}
			
			s = _con.createStatement();
			s.setQueryTimeout(30);
		}
		catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
		
		return s;
	}
	
	private synchronized boolean execute(String queryString) {
		
		if (!openConnection()) {
			
			System.out.println("Cannot open connection");
			return false;
		}
		
		Statement s = createStatement();
		
		if (s == null) {
			
			System.out.println("Cannot create statement");
			return false;
		}
		
		try {
			
			s.execute(queryString);
			s.closeOnCompletion();
		}
		catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		finally {
			
			closeConnection();
		}
		
		return true;
	}
	
	private synchronized SQLiteResultSet executeQuery(String queryString) {
		
		if (!openConnection()) {
			
			System.out.println("Cannot open connection");
			return null;
		}
		
		Statement s = createStatement();
		
		if (s == null) {
			
			System.out.println("Cannot create statement");
			return null;
		}
		
		SQLiteResultSet result = null;
		
		try {
			
			s.execute(queryString);
			
			result = new SQLiteResultSet(s.getResultSet());
			
			s.closeOnCompletion();
		}
		catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
		finally {
			
			closeConnection();
		}
		
		return result;
	}
	
	public boolean execute(SQLiteInsertQuery q) {
		
		return execute(q.getQueryString());
	}
	
	public boolean execute(SQLiteCreateTableQuery q) {
		
		return execute(q.getQueryString());
	}
	
	public SQLiteResultSet execute(SQLiteSelectQuery q) {
		
		return executeQuery(q.getQueryString());
	}
	
	public boolean execute(SQLiteUpdateQuery q) {
		
		return execute(q.getQueryString());
	}
	
	public boolean execute(SQLiteDeleteQuery q) {
		
		return execute(q.getQueryString());
	}
}

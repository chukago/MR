package org.tco.railroad.sqlite;

public class SQLiteProperty<T> {
	
	public String name;
	public T value;
	
	public SQLiteProperty(String name, T value) {
		
		this.name = name;
		this.value = value;
	}
}

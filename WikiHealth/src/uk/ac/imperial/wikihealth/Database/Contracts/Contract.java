package uk.ac.imperial.wikihealth.Database.Contracts;

public interface Contract {

	public String getTableName();
	public String getDBCreate();
	public String getNullColumn();
	public String[] getProjection();
	public boolean getPermission();
	public int getPeriod();
}
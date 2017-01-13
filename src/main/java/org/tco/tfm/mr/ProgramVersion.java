package org.tco.tfm.mr;

public class ProgramVersion {
	
	private static final int major_version		=	1;
	private static final int minor_version		=	0;
	private static final int release_version	=	0;
	
	public static int getMajorVersion()
	{
		return major_version;
	}
	
	public static int getMinorVersion()
	{
		return minor_version;
	}
	
	public static int getReleaseVersion()
	{
		return release_version;
	}
	
	public static String getVersionString()
	{
		
		return Integer.toString(major_version) + "." +
		Integer.toString(minor_version) + "." +
		Integer.toString(release_version);
		
	}

}

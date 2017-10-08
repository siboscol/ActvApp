package it.unive.android.actvapp.database;

import java.util.ArrayList;

public class Fermate {
	String stopId;
	String stopDesc = null;
	String aliasLine = null;
	String firstStop = null;
	String lastStop = null;
	String aliasRoute = null;
	String passageTime = null;
	String runid = null;
	
	
	//Bisogna creare una classe per ogni tabella presente nel database e sistemare tutti i suoi campi
	
	
	//creo costruttore vuoto
	public Fermate (){}
	
	public Fermate (String i){
		this.stopId = i;
	}

	//definizione del costruttore
	public Fermate ( String id, String desc,String linee, String lastStopName, String aliasRoute, String passageTime)
	{
		this.stopId=id;
		this.stopDesc=desc;
		this.aliasLine=linee;
		this.firstStop = lastStopName;
		this.aliasRoute= aliasRoute;
		this.passageTime=passageTime;
	}
	
	public Fermate ( String id, String desc,String linee, String aliasRoute, String runid)
	{
		this.stopId=id;
		this.stopDesc=desc;
		this.aliasLine=linee;
		this.aliasRoute= aliasRoute;
		this.runid = runid;
	}
	
	public Fermate ( String id,String linee, String aliasRoute, String passageTime)
	{
		this.stopId=id;
		this.aliasLine=linee;
		this.aliasRoute= aliasRoute;
		this.passageTime=passageTime;
	}

	//get stopId
	public String getStopId ()
	{
		return this.stopId;
	}
	//set stopId
	public void setStopId (String id)
	{
		this.stopId=id;
	}
	//get aliasLine
	public String getStopDesc ()
	{
		return this.stopDesc;
	}
	//set aliasLine
	public void setStopDesc (String desc)
	{
		this.stopDesc=desc;
	}
	
	//get passageTime
	public String getPassageTime ()
	{
		return this.passageTime;
	}
	//set passageTime
	public void setPassageTime (String passageTime)
	{
		this.passageTime=passageTime;
	}
	
	//getAliasRoute
	public String getAliasRoute ()
	{
		return this.aliasRoute;
	}
	//set aliasRoutes
	public void setAliasRoute (String aliasRoute)
	{
		this.aliasRoute=aliasRoute;
	}
	//getAliasLine
	public String getAliasLine ()
	{
		return this.aliasLine;
	}
	//set aliasLIne
	public void setAliasLine (String aliasLine)		
	{
			this.aliasLine=aliasLine;
	}
	
	//get lastStopName
	public String getlastStopName()
	{
		return this.firstStop;
	}
	//set lastStopName
	public void setlastStopName(String last)
	{
		this.firstStop=last;
	}
	//get runId
	public String getRunId ()
	{
		return this.runid;
	}
	//set runId
	public void setRunId (String runid)
	{
		this.runid=runid;
	}
	
	
}

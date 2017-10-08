package it.unive.android.actvapp.database;

public class Linea {
	
	String linesId;
	String route;
	int icon;

	
	//creo costruttore vuoto
	public Linea (){}

	//definizione del costruttore
	public Linea ( String id, String desc )
	{
		this.linesId=id;
		this.route=desc;
	}

	//get lineId
	public String getLinesId ()
	{
		return this.linesId;
	}
	//set lineId
	public void setLinesId (String id)
	{
		this.linesId = id;
	}
	//get route
	public String getRoute ()
	{
		return this.route;
	}
	
	//get icon
	public int getIcon ()
	{
		return this.icon;
	}
	//set icon
	public void setIcon (int icon)
	{
		this.icon = icon;
	}
	//set route
	public void setRoute (String desc)
	{
		this.route=desc;
	}
	
	@Override
	public boolean equals(Object obj) {
	    // TODO Auto-generated method stub
	    if(obj instanceof Linea)
	    {
	        Linea temp = (Linea) obj;
	        if( this.linesId.equals(temp.linesId) )
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {
	    // TODO Auto-generated method stub

	    return (this.linesId.hashCode() + this.route.hashCode() );        
	}
}

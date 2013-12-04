package ch.unibe.sport.core;

import ch.unibe.sport.utils.bulker.BulkArray;

public class Unisport {

	@BulkArray
	private Sport[] sports;
	
	public void setSports(Sport[] sports){
		this.sports = sports;
	}
	
	public Sport[] getSports(){
		return this.sports;
	}	
}

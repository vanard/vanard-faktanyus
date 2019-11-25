package com.vanard.faktanyus.models.apixuweather;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse{

	@SerializedName("current")
	private Current current;

	@SerializedName("location")
	private Location location;

	public void setCurrent(Current current){
		this.current = current;
	}

	public Current getCurrent(){
		return current;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public Location getLocation(){
		return location;
	}

	@Override
 	public String toString(){
		return 
			"WeatherResponse{" + 
			"current = '" + current + '\'' + 
			",location = '" + location + '\'' + 
			"}";
		}
}
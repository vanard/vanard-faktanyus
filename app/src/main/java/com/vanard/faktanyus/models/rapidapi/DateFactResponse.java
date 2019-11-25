package com.vanard.faktanyus.models.rapidapi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DateFactResponse implements Parcelable {

	@SerializedName("number")
	private int number;

	@SerializedName("found")
	private boolean found;

	@SerializedName("year")
	private int year;

	@SerializedName("text")
	private String text;

	@SerializedName("type")
	private String type;

	protected DateFactResponse(Parcel in) {
		number = in.readInt();
		found = in.readByte() != 0;
		year = in.readInt();
		text = in.readString();
		type = in.readString();
	}

	public static final Creator<DateFactResponse> CREATOR = new Creator<DateFactResponse>() {
		@Override
		public DateFactResponse createFromParcel(Parcel in) {
			return new DateFactResponse(in);
		}

		@Override
		public DateFactResponse[] newArray(int size) {
			return new DateFactResponse[size];
		}
	};

	public void setNumber(int number){
		this.number = number;
	}

	public int getNumber(){
		return number;
	}

	public void setFound(boolean found){
		this.found = found;
	}

	public boolean isFound(){
		return found;
	}

	public void setYear(int year){
		this.year = year;
	}

	public int getYear(){
		return year;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"DateFactResponse{" + 
			"number = '" + number + '\'' + 
			",found = '" + found + '\'' + 
			",year = '" + year + '\'' + 
			",text = '" + text + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(number);
		dest.writeByte((byte) (found ? 1 : 0));
		dest.writeInt(year);
		dest.writeString(text);
		dest.writeString(type);
	}
}
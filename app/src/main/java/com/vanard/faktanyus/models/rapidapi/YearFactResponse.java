package com.vanard.faktanyus.models.rapidapi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class YearFactResponse implements Parcelable {

	@SerializedName("date")
	private String date;

	@SerializedName("number")
	private int number;

	@SerializedName("found")
	private boolean found;

	@SerializedName("text")
	private String text;

	@SerializedName("type")
	private String type;

	protected YearFactResponse(Parcel in) {
		date = in.readString();
		number = in.readInt();
		found = in.readByte() != 0;
		text = in.readString();
		type = in.readString();
	}

	public static final Creator<YearFactResponse> CREATOR = new Creator<YearFactResponse>() {
		@Override
		public YearFactResponse createFromParcel(Parcel in) {
			return new YearFactResponse(in);
		}

		@Override
		public YearFactResponse[] newArray(int size) {
			return new YearFactResponse[size];
		}
	};

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

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
			"YearFactResponse{" + 
			"date = '" + date + '\'' + 
			",number = '" + number + '\'' + 
			",found = '" + found + '\'' + 
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
		dest.writeString(date);
		dest.writeInt(number);
		dest.writeByte((byte) (found ? 1 : 0));
		dest.writeString(text);
		dest.writeString(type);
	}
}
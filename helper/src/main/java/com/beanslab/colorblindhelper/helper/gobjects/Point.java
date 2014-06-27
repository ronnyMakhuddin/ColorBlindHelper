package com.beanslab.colorblindhelper.helper.gobjects;
import com.google.gson.annotations.SerializedName;

public class Point {

	@SerializedName("x")
	public double x;

	@SerializedName("y")
	public double y;
	
	@SerializedName("z")
	public double z;

	@SerializedName("label")
	public String label;
	
	
}

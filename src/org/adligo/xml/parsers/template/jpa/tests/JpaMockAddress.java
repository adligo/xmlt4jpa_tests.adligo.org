package org.adligo.xml.parsers.template.jpa.tests;

import java.util.Date;

public class JpaMockAddress {
	private Integer tid;
	private Integer version ;
	private String country;
	private String sub;
	private String city;
	private Integer zip;
	private String address;
	private String unit;
	private Date edited;
	private Integer edited_by;
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getZip() {
		return zip;
	}
	public void setZip(Integer zip) {
		this.zip = zip;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Date getEdited() {
		return edited;
	}
	public void setEdited(Date edited) {
		this.edited = edited;
	}
	public Integer getEdited_by() {
		return edited_by;
	}
	public void setEdited_by(Integer edited_by) {
		this.edited_by = edited_by;
	}
}

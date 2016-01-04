package com.scicrop.se.commons.dataobjects;

public class EntryFileProperty {

	private String name = null;
	private String md5Checksum = null;
	private String uuid = null;
	private long size = -1l;

	
	
	public EntryFileProperty(String name, String md5Checksum, String uuid,
			long size) {
		super();
		this.name = name;
		this.md5Checksum = md5Checksum;
		this.uuid = uuid;
		this.size = size;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMd5Checksum() {
		return md5Checksum;
	}
	public void setMd5Checksum(String md5Checksum) {
		this.md5Checksum = md5Checksum;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	
	
}

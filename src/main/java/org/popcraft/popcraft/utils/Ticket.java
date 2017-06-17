package org.popcraft.popcraft.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.Gson;

public class Ticket implements Comparable<Ticket> {

    private int id;
    private long creation_date, resolved_date;
    private String owner, text, location, assignee, comment;

    public Ticket(String owner, String text, String location) {
	this.id = TicketManager.issueID();
	this.creation_date = System.currentTimeMillis();
	this.resolved_date = 0;
	this.owner = owner;
	this.text = text;
	this.location = location;
	this.assignee = null;
	this.comment = null;
    }

    public Ticket(int id, long creation_date, long resolved_date, String owner, String text, String location,
	    String assignee, String comment) {
	this.id = id;
	this.creation_date = creation_date;
	this.resolved_date = resolved_date;
	this.owner = owner;
	this.text = text;
	this.location = location;
	this.assignee = assignee;
	this.comment = comment;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public long getCreation_date() {
	return creation_date;
    }

    public void setCreation_date(long creation_date) {
	this.creation_date = creation_date;
    }

    public long getResolved_date() {
	return resolved_date;
    }

    public void setResolved_date(long resolved_date) {
	this.resolved_date = resolved_date;
    }

    public String getOwner() {
	return owner;
    }

    public void setOwner(String owner) {
	this.owner = owner;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public String getAssignee() {
	return assignee;
    }

    public void setAssignee(String assignee) {
	this.assignee = assignee;
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public boolean isOpen() {
	return this.resolved_date == 0;
    }

    public Location decodeLocation() {
	Matcher m = Pattern.compile("[a-zA-Z_0-9-.]+").matcher(this.location);
	String[] coordinates = new String[4];
	for (int i = 0; m.find(); i++)
	    coordinates[i] = m.group();
	return new Location(Bukkit.getWorld(coordinates[0]), Double.parseDouble(coordinates[1]),
		Double.parseDouble(coordinates[2]), Double.parseDouble(coordinates[3]));
    }

    public String preview(int length) {
	return this.text.length() < length ? this.text : this.text.substring(0, length) + "...";
    }

    @Override
    public String toString() {
	return (new Gson()).toJson(this);
    }

    @Override
    public int compareTo(Ticket other) {
	return this.id - other.id;
    }

}

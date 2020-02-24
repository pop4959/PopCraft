package org.popcraft.popcraft.data;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ticket implements Comparable<Ticket> {

    private String uuid;
    private long creationDate, resolvedDate;
    private String owner, text, location, assignee;
    private List<String> comments;

    public Ticket(String owner, String text, String location) {
        this.uuid = UUID.randomUUID().toString();
        this.creationDate = System.currentTimeMillis();
        this.resolvedDate = 0;
        this.owner = owner;
        this.text = text;
        this.location = location;
        this.assignee = null;
        this.comments = new ArrayList<>();
    }

    public Ticket(String uuid, long creation_date, long resolved_date, String owner, String text, String location,
                  String assignee, List<String> comments) {
        this.uuid = uuid;
        this.creationDate = creation_date;
        this.resolvedDate = resolved_date;
        this.owner = owner;
        this.text = text;
        this.location = location;
        this.assignee = assignee;
        this.comments = comments;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(long resolvedDate) {
        this.resolvedDate = resolvedDate;
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

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public boolean isOpen() {
        return this.resolvedDate == 0;
    }

    public String getShortId() {
        return this.uuid.substring(0, 4);
    }

    public String getLocationFormatted() {
        String[] location = this.location.split(",");
        return String.format("%s at %s, %s, %s", location[0], location[1], location[2], location[3]);
    }

    public Location decodeLocation() {
        String[] locationData = this.location.split(",");
        return new Location(Bukkit.getWorld(locationData[0]), Integer.parseInt(locationData[1]),
                Integer.parseInt(locationData[2]), Integer.parseInt(locationData[3]));
    }

    public String preview(int length) {
        return this.text.length() <= length ? this.text : this.text.substring(0, length) + "...";
    }

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }

    @Override
    public int compareTo(Ticket other) {
        return Long.compare(this.creationDate, other.creationDate);
    }

}

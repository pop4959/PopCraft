package org.popcraft.popcraft.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCraft;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.md_5.bungee.api.ChatColor;

public class TicketManager {

    private static String TICKET_FILE = PopCraft.getPlugin().getDataFolder().toString() + File.separatorChar
	    + "tickets.json",
	    TICKET_ARCHIVE_FILE = PopCraft.getPlugin().getDataFolder().toString() + File.separatorChar + "tickets.dat";
    private static HashMap<Integer, Ticket> tickets = new HashMap<Integer, Ticket>();
    private static int next_id = 0, LIST_LENGTH = 8;

    public TicketManager() {
	readTickets();
    }

    public static int issueID() {
	return next_id++;
    }

    public Ticket getTicket(int id) {
	return tickets.get(id);
    }

    public int ticketCount(boolean open) {
	int count = 0;
	for (Ticket t : tickets.values())
	    if (open && t.isOpen() || !open && !t.isOpen())
		count++;
	return count;
    }

    public int createTicket(Player owner, String text) {
	Location l = owner.getLocation();
	Ticket t = new Ticket(owner.getName(), text,
		l.getWorld().getName() + " @ [" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "]");
	tickets.put(t.getId(), t);
	writeTickets();
	return t.getId();
    }

    public void claim(int id, Player player) {
	Ticket t = tickets.get(id);
	if (t == null)
	    throw new NumberFormatException("Invalid ticket ID");
	t.setAssignee(player.getName());
	tickets.put(t.getId(), t);
	writeTickets();
    }

    public boolean unclaim(int id, Player player) {
	Ticket t = tickets.get(id);
	if (t == null)
	    throw new NumberFormatException("Invalid ticket ID");
	else if (t.getAssignee() != null && t.getAssignee().equals(player.getName())) {
	    t.setAssignee(null);
	    tickets.put(t.getId(), t);
	    writeTickets();
	    return true;
	}
	return false;
    }

    public boolean open(int id) {
	Ticket t = tickets.get(id);
	if (t == null)
	    throw new NumberFormatException("Invalid ticket ID");
	else if (!t.isOpen()) {
	    t.setResolved_date(0);
	    writeTickets();
	    return true;
	}
	return false;
    }

    public boolean close(int id, Player player) {
	Ticket t = tickets.get(id);
	if (t == null)
	    throw new NumberFormatException("Invalid ticket ID");
	else if (t.getAssignee() != null) {
	    t.setResolved_date(System.currentTimeMillis());
	    writeTickets();
	    return true;
	}
	return false;
    }

    public boolean comment(int id, Player player, String comment) {
	Ticket t = tickets.get(id);
	if (t == null)
	    throw new NumberFormatException("Invalid ticket ID");
	else if (t.getAssignee() != null && t.getAssignee().equals(player.getName())) {
	    t.setComment(comment);
	    writeTickets();
	    return true;
	}
	return false;
    }

    public void view(int id, Player player) {
	Ticket t = tickets.get(id);
	if (t == null)
	    throw new NumberFormatException("Invalid ticket ID");
	Message.normal(player, "===== Ticket #" + ChatColor.RED + t.getId() + ChatColor.GOLD + " =====");
	Message.normal(player, "Reporter: " + ChatColor.WHITE + t.getOwner());
	if (t.getAssignee() != null)
	    Message.normal(player, "Assignee: " + ChatColor.WHITE + t.getAssignee());
	Message.normal(player, "Created: " + ChatColor.WHITE
		+ (new SimpleDateFormat("h:mm a, MMMM dd, yyyy")).format(new Date(t.getCreation_date())));
	if (!t.isOpen())
	    Message.normal(player, "Resolved: " + ChatColor.WHITE
		    + (new SimpleDateFormat("h:mm a, MMMM dd, yyyy")).format(new Date(t.getResolved_date())));
	Message.normal(player, "Description: " + ChatColor.WHITE + t.getText());
	Message.normal(player, "Location: " + ChatColor.WHITE + t.getLocation());
	if (t.getComment() != null)
	    Message.normal(player, "Comment: " + ChatColor.WHITE + t.getComment());
    }

    public void list(int page, Player player, boolean open) {
	ArrayList<Ticket> listing = new ArrayList<Ticket>();
	for (Ticket t : tickets.values()) {
	    if (player.hasPermission("popcraft.ticket.mod") || t.getOwner().equals(player.getName()))
		if (open && t.isOpen()) {
		    listing.add(t);
		} else if (!open && !t.isOpen()) {
		    listing.add(t);
		}
	}
	int pages = (int) Math.ceil((double) listing.size() / LIST_LENGTH);
	if (page < 1 || page > pages)
	    throw new NumberFormatException("Invalid page number");
	Collections.sort(listing);
	Message.normal(player,
		ChatColor.YELLOW + "----- " + ChatColor.GOLD + "Tickets" + ChatColor.YELLOW + " -- " + ChatColor.GOLD
			+ "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + pages
			+ ChatColor.YELLOW + " -----");
	for (int i = page * LIST_LENGTH - LIST_LENGTH; i < page * LIST_LENGTH && i < listing.size(); i++) {
	    Ticket t = listing.get(i);
	    String assigneeText = t.getAssignee() != null ? "Assigned to " + ChatColor.RED + t.getAssignee()
		    : "Unassigned",
		    commentText = t.getComment() != null && player.hasPermission("popcraft.ticket.mod")
			    ? ChatColor.GOLD + " *" : "";
	    Message.normal(player, "Ticket #" + ChatColor.RED + t.getId() + ChatColor.YELLOW + " - " + ChatColor.GOLD
		    + assigneeText + ChatColor.YELLOW + " - " + ChatColor.WHITE + "\""
		    + t.preview(12 + (28 - assigneeText.length()) + (2 - commentText.length())) + "\"" + commentText);
	}
	if (page != pages)
	    Message.normal(player, "Type " + ChatColor.RED + "/ticket " + (open ? "list " : "archive ") + (page + 1)
		    + ChatColor.GOLD + " to read the next page.");
    }

    public boolean purge(int days) {
	try {
	    File archive = new File(TICKET_ARCHIVE_FILE);
	    if (!archive.exists()) {
		archive.createNewFile();
	    }
	    BufferedWriter bw = new BufferedWriter(new FileWriter(archive.getAbsoluteFile(), true));
	    ArrayList<Integer> toRemove = new ArrayList<Integer>();
	    for (Ticket t : tickets.values()) {
		if (!t.isOpen() && t.getResolved_date() < System.currentTimeMillis() - Duration.ofSeconds(days)
			.toMillis()/* Duration.ofDays(days).toMillis() */) {
		    toRemove.add(t.getId());
		}
	    }
	    for (Integer i : toRemove) {
		bw.append(tickets.remove(i).toString());
		bw.newLine();
	    }
	    bw.close();
	    writeTickets();
	    return true;
	} catch (IOException e) {
	    return false;
	}
    }

    public boolean delete(int id) {
	return this.delete(id, id);
    }

    public boolean delete(int start, int end) {
	try {
	    File archive = new File(TICKET_ARCHIVE_FILE);
	    if (!archive.exists()) {
		archive.createNewFile();
	    }
	    BufferedWriter bw = new BufferedWriter(new FileWriter(archive.getAbsoluteFile(), true));
	    for (int i = start; i <= end; i++) {
		Ticket removed = tickets.remove(i);
		if (removed != null) {
		    bw.append(removed.toString());
		    bw.newLine();
		}
	    }
	    bw.close();
	    writeTickets();
	    return true;
	} catch (IOException e) {
	    return false;
	}
    }

    public void writeTickets() {
	try {
	    JsonWriter jw = new JsonWriter(new FileWriter(TICKET_FILE));
	    jw.setIndent("  ");
	    jw.beginArray();
	    jw.beginObject();
	    jw.name("next_id").value(next_id);
	    jw.endObject();
	    for (Ticket t : tickets.values()) {
		jw.beginObject();
		jw.name("id").value(t.getId());
		jw.name("creation_date").value(t.getCreation_date());
		jw.name("resolved_date").value(t.getResolved_date());
		jw.name("owner").value(t.getOwner());
		jw.name("text").value(t.getText());
		jw.name("location").value(t.getLocation());
		jw.name("assignee").value(t.getAssignee());
		jw.name("comment").value(t.getComment());
		jw.endObject();
	    }
	    jw.endArray();
	    jw.close();
	} catch (IOException e) {
	    return;
	}
    }

    public void readTickets() {
	try {
	    File f = new File(TICKET_FILE);
	    if (!f.exists()) {
		f.createNewFile();
		return;
	    }
	    JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(f)));
	    reader.beginArray();
	    while (reader.hasNext()) {
		boolean is_next_id = false;
		int id = 0;
		long creation_date = 0, resolved_date = 0;
		String owner = null, text = null, location = null, assignee = null, comment = null;
		reader.beginObject();
		while (reader.hasNext()) {
		    String name = reader.nextName();
		    if (name.equals("next_id")) {
			next_id = reader.nextInt();
			is_next_id = true;
			break;
		    } else if (name.equals("id")) {
			id = reader.nextInt();
		    } else if (name.equals("creation_date")) {
			creation_date = reader.nextLong();
		    } else if (name.equals("resolved_date")) {
			resolved_date = reader.nextLong();
		    } else if (name.equals("owner") && reader.peek() != JsonToken.NULL) {
			owner = reader.nextString();
		    } else if (name.equals("text") && reader.peek() != JsonToken.NULL) {
			text = reader.nextString();
		    } else if (name.equals("location") && reader.peek() != JsonToken.NULL) {
			location = reader.nextString();
		    } else if (name.equals("assignee") && reader.peek() != JsonToken.NULL) {
			assignee = reader.nextString();
		    } else if (name.equals("comment") && reader.peek() != JsonToken.NULL) {
			comment = reader.nextString();
		    } else {
			reader.skipValue();
		    }
		}
		reader.endObject();
		if (!is_next_id)
		    tickets.put(id,
			    new Ticket(id, creation_date, resolved_date, owner, text, location, assignee, comment));
	    }
	    reader.endArray();
	    reader.close();
	} catch (IOException e) {
	    return;
	}
    }

}

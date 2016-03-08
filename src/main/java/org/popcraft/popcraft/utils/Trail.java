package org.popcraft.popcraft.utils;

import org.bukkit.Location;

public class Trail {

    public static Location randomizeLocation(Location location, double xz, double y, boolean yNeg) {
	if (yNeg)
	    return location.add((sign() * Math.random() * xz), (sign() * Math.random() * y),
		    (sign() * Math.random() * xz));
	else
	    return location.add((sign() * Math.random() * xz), (Math.random() * y), (sign() * Math.random() * xz));
    }

    public static Location randomizeLocation(Location location, double xz) {
	return location.add((sign() * Math.random() * xz), 0, (sign() * Math.random() * xz));
    }

    public static Location moveUp(Location location, double y) {
	return location.add(0, y, 0);
    }

    private static double sign() {
	if (Math.random() < 0.5)
	    return -1;
	else
	    return 1;
    }

}

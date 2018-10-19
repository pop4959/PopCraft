package org.popcraft.popcraft.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MCAPI {

    public static String getUUID(String name) {
        try {
            URL url = new URL("https://us.mc-api.net/v3/uuid/" + name);
            InputStream in = url.openConnection().getInputStream();
            String contents = IOUtils.toString(in);
            in.close();
            return StringUtils.substringBetween(contents, "\"full_uuid\":\"", "\"");
        } catch (IOException e) {
            return null;
        }
    }

    public static String getName(String UUID) {
        try {
            URL url = new URL("https://us.mc-api.net/v3/name/" + UUID);
            InputStream in = url.openConnection().getInputStream();
            String contents = IOUtils.toString(in);
            in.close();
            return StringUtils.substringBetween(contents, "\"name\":\"", "\"");
        } catch (IOException e) {
            return null;
        }
    }

    public static String[] getHistory(String name) {
        try {
            URL url = new URL("https://us.mc-api.net/v3/history/" + getUUID(name));
            InputStream in = url.openConnection().getInputStream();
            String contents = IOUtils.toString(in);
            in.close();
            int count = StringUtils.countMatches(contents, "\"name\":\"");
            String[] names = new String[count];
            Pattern p = Pattern.compile("\"name\":\"[a-zA-Z0-9_]+\"");
            Matcher m = p.matcher(contents);
            int i = 0;
            while (m.find()) {
                names[i] = StringUtils.substringBetween(m.group(), "\"name\":\"", "\"");
                i++;
            }
            ArrayUtils.reverse(names);
            return names;
        } catch (IOException e) {
            return null;
        }
    }

}

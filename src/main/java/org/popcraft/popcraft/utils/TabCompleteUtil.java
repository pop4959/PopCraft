package org.popcraft.popcraft.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteUtil {

    public static List<String> startsWithLastArg(Collection<String> completions, String[] args) {
        return completions.stream().filter(s -> s.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }

}

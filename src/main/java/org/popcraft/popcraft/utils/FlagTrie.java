package org.popcraft.popcraft.utils;

import java.util.HashMap;
import java.util.Map;

public class FlagTrie {

    private Map<Character, FlagTrie> trieMap = new HashMap<>();
    private int maxText = 0;
    private boolean isFlag;

    public void addFlag(final String flag) {
        if (flag.length() > this.maxText) {
            this.maxText = flag.length();
        }
        this.insertFlag(flag.toLowerCase());
    }

    private void insertFlag(final String flag) {
        if (flag.length() == 0) {
            this.isFlag = true;
        } else {
            final Character letter = flag.charAt(0);
            FlagTrie trie = this.trieMap.get(letter);
            if (trie == null) {
                trie = new FlagTrie();
                this.trieMap.put(letter, trie);
            }
            trie.insertFlag(flag.substring(1));
        }
    }

    public boolean containsFlag(final String text) {
        final String lowerText = text.toLowerCase();
        final StringBuilder builder = new StringBuilder(this.maxText);

        Boolean status = null;

        for (int i = 0; i < lowerText.length() && !Boolean.TRUE.equals(status); i++) {
            builder.append(lowerText.charAt(i));
            do {
                status = this.containsCharacters(builder, 0);
                if (Boolean.FALSE.equals(status)) {
                    builder.deleteCharAt(0);
                }
            } while (builder.length() > 0 && Boolean.FALSE.equals(status));
        }

        return Boolean.TRUE.equals(status);
    }

    /**
     * Checks to see if a word is contained within the trie. This method will return true if the word is the trie,
     * false if it isn't, and null if there's a prefix match. For example if the trie contained the word captain
     * <p>
     * Then:
     * <ul>
     * <li>captain = true</li>
     * <li>crew = false</li>
     * <li>cap = null</li>
     * </ul>
     *
     * @param builder
     * @param index
     * @return true if the word is the trie, false if it isn't, and null if there's a prefix match
     */
    private Boolean containsCharacters(final StringBuilder builder, final int index) {
        if (builder.length() == index) {
            return this.isFlag ? Boolean.TRUE : null;
        }
        final FlagTrie trie = this.trieMap.get(builder.charAt(index));
        return trie == null ? Boolean.FALSE : trie.containsCharacters(builder, index + 1);
    }

}

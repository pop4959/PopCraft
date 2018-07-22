package org.popcraft.popcraft.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlagTrieTest {

    private FlagTrie trie;

    @Before
    public void setUp() {
        this.trie = new FlagTrie();
        this.trie.addFlag("dog");
        this.trie.addFlag("dot");
        this.trie.addFlag("cat");
        this.trie.addFlag("cot");
        this.trie.addFlag("doggo");
        this.trie.addFlag("dogs");
        this.trie.addFlag("pop");
    }

    @Test
    public void testExactMatchFlags() {
        assertTrue(this.trie.containsFlag("dog"));
        assertTrue(this.trie.containsFlag("dot"));
        assertTrue(this.trie.containsFlag("cat"));
        assertTrue(this.trie.containsFlag("cot"));
        assertTrue(this.trie.containsFlag("doggo"));
        assertTrue(this.trie.containsFlag("dogs"));
        assertTrue(this.trie.containsFlag("pop"));
    }

    @Test
    public void testIncompleteMatch() {
        this.trie.addFlag("do");
        this.trie.addFlag("d");
        this.trie.addFlag("ca");
        this.trie.addFlag("co");
        this.trie.addFlag("dgg");
        this.trie.addFlag("do");
        this.trie.addFlag("p");
    }

    @Test
    public void testPrefixMatch() {
        assertTrue(this.trie.containsFlag("dogs"));
        assertTrue(this.trie.containsFlag("dotting"));
        assertTrue(this.trie.containsFlag("caterpie"));
        assertTrue(this.trie.containsFlag("cottage"));
    }

    @Test
    public void testSufixMatch() {
        assertTrue(this.trie.containsFlag("The white dog"));

        assertTrue(this.trie.containsFlag("Please pet the cat"));
        assertTrue(this.trie.containsFlag("Taste my apricot"));
    }

    @Test
    public void testMiddleMatch() {
        assertTrue(this.trie.containsFlag("Put the dot on the whiteboard"));
        assertTrue(this.trie.containsFlag("Do you have any popcorn I can have?"));
        assertTrue(this.trie.containsFlag("I would kill for some sodapop right about now"));
    }
}
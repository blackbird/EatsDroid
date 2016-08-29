package com.company.server;

/**
 * Created by brian on 4/20/16.
 */

import java.util.HashMap;

class TrieNode {
    char c;
    HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    boolean isLeaf;
    public JsonRequestHandlerInterface jsonRequestHandlerInterface;

    public TrieNode() {}

    public TrieNode(char c){
        this.c = c;
    }
}

package atrai.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;

/**
 * A trie data structure
 */
class TrieNode {
    /**
     * True if a word ends here
     */
    boolean isSet = false;

    /**
     * Associated ID of the trie node / word.
     */
    int ID;
    Int2ObjectRBTreeMap<TrieNode> children = null;

    /**
     * Continue down the trie along the character
     *
     * @param c the character
     * @return the next trie node or null
     */
    TrieNode get(int c) {
        TrieNode ret = null;
        if (this.children != null) {
            ret = this.children.get(c);
        }
        return ret;
    }

    /**
     * Continue down the trie along the character, or add if it doesn't exist
     *
     * @param c the character
     * @return the next, existing, trie node, or a new node
     */
    TrieNode getOrCreate(int c) {
        if (this.children == null) {
            this.children = new Int2ObjectRBTreeMap<>();
        }

        TrieNode ret = this.children.get(c);
        if (ret == null) {
            ret = new TrieNode();
            this.children.put(c, ret);
        }
        return ret;
    }
}


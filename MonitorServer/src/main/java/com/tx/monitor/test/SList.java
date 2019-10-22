package com.tx.monitor.test;

import java.util.Random;

/**
 * Created by Administrator on 2019/10/14.
 */
public class SList {

    static class ListNode {
        static class Level {
            ListNode next; // forward node
            int span; // the nodes count between this and forward node, include forward node
        }

        Object object; // payload
        double score; // node score

        ListNode previous; // backward node
        Level[] levels; // nodes to next

        private ListNode next(int level) {
            return levels[level].next;
        }
    }

    public static class ObjectWithScore {
        Object object;
        double score;
    }

    //===================================================================================
    private ListNode headNode; // head node of skip list
    private ListNode tailNode; // tail node of skip list

    private int maxLevel; // max level allowed
    private int currentLevel = 1; // current level
    private int length = 0; // nodes count

    //===================================================================================
    private int randomLevel() {
        Random random = new Random();
        int level = 1;

        // 25% probability to upgrade 1 level
        while ((random.nextInt(Integer.MAX_VALUE) & 0xFFFF)
                < 0.25f * 0xFFFF)
            level += 1;

        if (level > maxLevel)
            level = maxLevel;
        return level;
    }

    private int compare(Object o1, Object o2) {
        int h1 = o1.hashCode();
        int h2 = o2.hashCode();

        if (h1 < h2)
            return -1;
        if (h1 > h2)
            return 1;
        return 0;
    }

    /**
     * Create a new ListNode object
     * @param level level of the node
     * @param object payload
     * @param score node score
     * @return the new node
     */
    private ListNode createNode(int level, Object object, double score) {
        ListNode node = new ListNode();
        node.object = object;
        node.score = score;

        node.levels = new ListNode.Level[level];
        for (int i = 0; i < level; i++)
            node.levels[i] = new ListNode.Level();

        return node;
    }

    /**
     * Delete node
     * @param node to be deleted
     * @param update the affected nodes array of skip list
     */
    private void deleteNode(ListNode node, ListNode[] update) {
        // reconnect the forward nodes for each level
        for (int i = 0; i < currentLevel; i++) {
            if (update[i].levels[i].next == node) {
                update[i].levels[i].next = node.levels[i].next;
                update[i].levels[i].span += node.levels[i].span - 1;

            } else {
                update[i].levels[i].span -= 1;
            }
        }

        // reconnect the backward node
        if (node.levels[0].next != null) {
            node.levels[0].next.previous = node.previous;
        } else {
            tailNode = node.previous;
        }

        // update current level if needed
        while (currentLevel > 1
                && headNode.levels[currentLevel - 1].next == null) {
            currentLevel -= 1;
        }

        length -= 1;
    }

    private ListNode getNodeByRank(long rank) {
        // start from top level
        int level = currentLevel - 1;
        ListNode node = headNode;
        long tmpRank = 0;

        while (node != null
                && level >= 0) {

            if ((tmpRank + node.levels[level].span) > rank) {
                level -= 1;
                continue;
            }

            tmpRank += node.levels[level].span;
            if (tmpRank == rank)
                return node.levels[level].next;

            node = node.levels[level].next;
        }

        return null;
    }

    //===================================================================================
    public SList(int maxLevel) {
        this.maxLevel = maxLevel;

        headNode = createNode(maxLevel, null, 0);
    }

    public void insert(Object object, double score) {
        ListNode[] update = new ListNode[maxLevel]; // the updated nodes in each level
        int[] rank = new int[maxLevel]; // rank of nodes in each level
        int level;

        ListNode node = headNode;

        // scan from top node row by row
        // put the node which has the closest smaller score of each level to 'update' array
        for (int i = currentLevel - 1; i >= 0; i--) {
            if (i == currentLevel - 1) // the top row
                rank[i] = 0; // top level has rank 0
            else
                rank[i] = rank[i + 1]; // lower level rank starts from topper rank

            // scan the level to find the closest smaller score,
            // it will be the head node if no node matched
            while (node.levels[i].next != null
                    && (node.levels[i].next.score < score
                        || (node.levels[i].next.score == score
                            && compare(node.levels[i].next.object, object) < 0))) {
                // add the spans in current level, with topper level rank
                rank[i] += node.levels[i].span;
                node = node.levels[i].next;
            }

            update[i] = node;
        }

        // insert new node with random level
        level = randomLevel();

        // if the new level is bigger than current level
        if (level > currentLevel) {
            for (int i = currentLevel; i < level; i++) {
//                update[i] = headNode;
//                update[i].levels[i].span = length;
                headNode.levels[i].span = length;
                update[i] = headNode;
            }

            // update current max level
            currentLevel = level;
        }

        //
        node = createNode(level, object, score);
        for (int i = 0; i < level; i++) {
            // link the forward nodes
            node.levels[i].next = update[i].levels[i].next;
            update[i].levels[i].next = node;

            node.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = (rank[0] - rank[i]) + 1;
        }

        //
        for (int i = level; i < currentLevel; i++)
            update[i].levels[i].span++;

        //
        if (update[0] == headNode)
            node.previous = null;
        else
            node.previous = update[0];

        if (node.levels[0].next != null)
            node.levels[0].next.previous = node;
        else
            tailNode = node;

        length += 1;
    }

    public void delete(Object object, double score) {
        ListNode[] update = new ListNode[maxLevel];
        int[] rank = new int[maxLevel];

        ListNode node = headNode;

        for (int i = currentLevel - 1; i >= 0; i--) {
            if (i == currentLevel - 1)
                rank[i] = 0;
            else
                rank[i] = rank[i + 1];

            while (node.levels[i].next != null
                    && (node.levels[i].next.score < score
                        || (node.levels[i].next.score == score
                            && compare(node.levels[i].next.object, object) < 0))) {
                node = node.levels[i].next;
            }

            update[i] = node;
        }

        node = node.levels[0].next;

        if (node != null
                && node.score == score
                && node.object == object)
            deleteNode(node, update);
    }

    public long getRank(Object object, double score) {
        ListNode node = headNode;
        long rank = 0;

        for (int i = currentLevel - 1; i >= 0; i--) {
            while (node.levels[i].next != null
                    && (node.levels[i].next.score < score
                        || (node.levels[i].next.score == score
                            && compare(node.levels[i].next.object, object) <= 0))) {
                rank += node.levels[i].span;
                node = node.levels[i].next;
            }

            if (object == node.object)
                return rank;
        }

        return 0;
    }

    public ObjectWithScore[] getRankRange(long rank, int range) {

        ListNode node = getNodeByRank(rank);
        if (node == null)
            return null;

        ObjectWithScore[] result = new ObjectWithScore[range];
        for (int i = 0; i < range; i++) {
            if (node == null)
                continue;

            ObjectWithScore objectWithScore = new ObjectWithScore();
            objectWithScore.object = node.object;
            objectWithScore.score = node.score;

            result[i] = objectWithScore;

            //
            node = node.levels[0].next;
        }

        return result;
    }


    //===================================================================================
    public static void main(String[] args) {

        Random random = new Random();

        SList sList = new SList(16);
        int length = 1000000;

        String[] keys = new String[length];
        for (int n = 0; n < length; n++) {
            keys[n] = "" + n;
        }

        long start = System.currentTimeMillis();

        for (int n = 0; n < length; n++) {
            sList.insert(keys[n], n);
        }

        long end = System.currentTimeMillis();
        System.out.println("used: " + (end - start));

        start = System.currentTimeMillis();
        ObjectWithScore[] scores = sList.getRankRange(100000, 20);
        end = System.currentTimeMillis();
        System.out.println("used: " + (end - start));

        Object object = sList.getNodeByRank(10);
        object = sList.getNodeByRank(50);

        object = new Object();
        sList.insert(object, 30);

        long rank = sList.getRank(object, 30);

        sList.delete(object, 30);
        rank = sList.getRank(object, 30);

        rank = sList.getRank(keys[10], 10);
        rank = sList.getRank(object, 30);
        rank = sList.getRank(keys[30], 30);
        rank = sList.getRank(keys[55], 55);
        rank = sList.getRank(keys[11], 11);
        rank = sList.getRank(keys[99], 99);
        rank = sList.getRank(keys[0], 0);
        rank = sList.getRank(keys[80], 80);

//        sList.insert(new Object(), 1);
//        sList.insert(new Object(), 50);
//        sList.insert(new Object(), 30);
//        sList.insert(new Object(), 100);


    }
}

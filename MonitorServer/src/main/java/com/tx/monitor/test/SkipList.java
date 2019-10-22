package com.tx.monitor.test;

import java.util.Random;

/**
 * Created by Administrator on 2019/10/12.
 */
public class SkipList {
    //===================================================================================
    private class SkipListNode {
        class SkipListLevel {
            SkipListNode forwardNode; // forward node
            long span = 0; // the distance to forward node
        }
        Object object; // payload
        double score; // payload score
        SkipListNode backwardNode; // backward node

        SkipListLevel[] levels;

        //===============================================================================
        SkipListNode(int level) {
            levels = new SkipListLevel[level];
        }
    }

    SkipListNode headNode;
    SkipListNode tailNode;

    private int maxLevel;
    private int level = 1;
    private long length = 0; // nodes count

    //===================================================================================
    private void insertNode(SkipListNode frontNode,
                            SkipListNode backNode,
                            SkipListNode insertedNode) {
    }

    private SkipListNode createNode(int level, Object object, double score) {
        SkipListNode node = new SkipListNode(level);
        node.object = object;
        node.score = score;

        return node;
    }

    //===================================================================================
    public SkipList() {
        this(16);
    }

    public SkipList(int maxLevel) {
        this.maxLevel = maxLevel;
        length = 0;
        level = 1;

        headNode = createNode(maxLevel, null, 0);
        headNode.backwardNode = null;

        tailNode = null;
    }

    public void insert(Object object, double score) {
        SkipListNode[] update = new SkipListNode[maxLevel];
        SkipListNode x = headNode;
        long[] rank = new long[maxLevel];
        int i, tmpLevel;

        for (i = level - 1; i >= 0; i--) {
            rank[i] = i == level - 1 ? 0 : rank[i + 1];

            while (x.levels[i].forwardNode != null
                    && (x.levels[i].forwardNode.score < score
                        || (x.levels[i].forwardNode.score == score /*&& x.levels[i].forwardNode.object < object*/))

            ) {
                rank[i] += x.levels[i].span;
                x = x.levels[i].forwardNode;
            }

            update[i] = x;
        }

        //
        tmpLevel = randomLevel(0.25f, maxLevel);
        if (tmpLevel > level) {
            for (i = level; i < tmpLevel; i++) {
                rank[i] = 0;

                update[i] = headNode;
                update[i].levels[i].span = length;
            }

            level = tmpLevel;
        }

        x = createNode(tmpLevel, object, score);
        for (i = 0; i < tmpLevel; i++) {
            x.levels[i].forwardNode = update[i].levels[i].forwardNode;
            update[i].levels[i].forwardNode = x;
            /*
             header                update[i]     x    update[i]->forward
              |-----------|-----------|-----------|-----------|-----------|-----------|
                                      |<---update[i].span---->|
              |<-------rank[i]------->|
              |<-------------------rank[0]------------------->|

              更新update数组中span值和新插入元素span值, rank[0]存储的是x元素距离头部的距离, rank[i]存储的是update[i]距离头部的距离, 上面给出了示意图
              */
            x.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = (rank[0] - rank[i]) + 1;
        }

        //
        length += 1;
    }

    public void delete(Object object) {


    }

    public long rank(Object object) {
        return 0;
    }


    //===================================================================================
    public static int randomLevel(float probability, int maxLevel) {
        Random random = new Random();
        int level = 0;

//        while ((random.nextInt(Integer.MAX_VALUE) & 0xFFFF)
//                < probability * 0xFFFF)
//            level += 1;
        while (random.nextInt(Integer.MAX_VALUE)
                < probability * Integer.MAX_VALUE)
            level += 1;

        if (level > maxLevel)
            level = maxLevel;
        return level;
    }


    //===================================================================================
    public static void main(String[] args) {
//        Random random = new Random();
//        int nonZero = 0;
//
//        for (int n = 0; n < 1000000; n++) {
//            int level = randomLevel(0.2f, 32);
//            if (level != 0)
//                nonZero += 1;
//        }
//        System.out.println(nonZero);

        SkipList skipList = new SkipList(64);


    }
}

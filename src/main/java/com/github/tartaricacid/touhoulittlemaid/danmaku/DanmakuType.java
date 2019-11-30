package com.github.tartaricacid.touhoulittlemaid.danmaku;

public enum DanmakuType {
    // 点弹
    PELLET(0.6d),
    // 小玉
    BALL(0.8d),
    // 环玉
    ORBS(0.3d),
    // 中玉
    BIG_BALL(0.8d),
    // 大玉
    BUBBLE(1.2d),
    // 心弹
    HEART(0.4d),
    // 椭弹
    JELLYBEAN(0.4d),
    // 札弹
    AMULET(0.4d),
    // 星弹
    STAR(0.6d),
    // 大星弹
    BIG_STAR(1.2d),
    // 米弹
    PETAL(0.6d),
    // 刀弹
    KNIFE(0.6d);

    private double size;

    /**
     * 弹幕类型枚举
     *
     * @param size 弹幕渲染放大缩小倍数
     */
    DanmakuType(double size) {
        this.size = size;
    }

    public static DanmakuType getType(int index) {
        if (index < 0 || index >= values().length) {
            return PELLET;
        }
        return values()[index];
    }

    public double getSize() {
        return size;
    }

    public static int getSinglePlaneDanmakuSize() {
        return 6;
    }

    public static int getLength() {
        return values().length;
    }
}

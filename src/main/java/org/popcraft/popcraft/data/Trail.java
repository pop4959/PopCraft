package org.popcraft.popcraft.data;

import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;

public class Trail {

    private Object trail;
    private BlockData data;
    private Type type;
    private Style style;
    private int count, extra;
    private double offsetX, offsetY, offsetZ, shiftX, shiftY, shiftZ;

    public enum Type {
        EFFECT, PARTICLE, BLOCK, ITEM;
    }

    public enum Style {
        NORMAL, DOTS, RAIN, DUST, SPREAD;
    }

    public Trail(Object trail, BlockData data, Type type, Style style, int count, int extra, double offsetX,
                 double offsetY, double offsetZ, double shiftX, double shiftY, double shiftZ) {
        this.trail = trail;
        this.data = data;
        this.type = type;
        this.style = style;
        this.count = count;
        this.extra = extra;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.shiftZ = shiftZ;
    }

    public Trail(final Trail other) {
        this(other.trail, other.data, other.type, other.style, other.count, other.extra, other.offsetX, other.offsetY,
                other.offsetZ, other.shiftX, other.shiftY, other.shiftZ);
    }

    public Trail changeStyle(Style newStyle) {
        switch (newStyle) {
            case DOTS:
                return new Trail(this.trail, this.data, this.type, newStyle, 1, this.extra, 0, 0, 0, 0, 0, 0);
            case RAIN:
                return new Trail(this.trail, this.data, this.type, newStyle, this.count, this.extra, 0.5, 0, 0.5, 0, 4,
                        0);
            case DUST:
                if (this.getType() == Type.BLOCK)
                    return new Trail(Particle.FALLING_DUST, this.data, this.type, newStyle, 4, this.extra, 0.5, 0.5,
                            0.5, 0, 0, 0);
                else
                    return this;
            case SPREAD:
                return new Trail(this.trail, this.data, this.type, newStyle, this.count, this.extra, 0.5, 0.5, 0.5, 0,
                        0, 0);
            case NORMAL:
            default:
                return this;
        }
    }

    public Object getTrail() {
        return trail;
    }

    public Trail setTrail(Object trail) {
        this.trail = trail;
        return this;
    }

    public BlockData getData() {
        return data;
    }

    public Trail setData(BlockData data) {
        this.data = data;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Trail setType(Type type) {
        this.type = type;
        return this;
    }

    public Style getStyle() {
        return style;
    }

    public Trail setStyle(Style style) {
        this.style = style;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Trail setCount(int count) {
        this.count = count;
        return this;
    }

    public int getExtra() {
        return extra;
    }

    public Trail setExtra(int extra) {
        this.extra = extra;
        return this;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public Trail setOffsetX(double offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public Trail setOffsetY(double offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public Trail setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
        return this;
    }

    public double getShiftX() {
        return shiftX;
    }

    public Trail setShiftX(double shiftX) {
        this.shiftX = shiftX;
        return this;
    }

    public double getShiftY() {
        return shiftY;
    }

    public Trail setShiftY(double shiftY) {
        this.shiftY = shiftY;
        return this;
    }

    public double getShiftZ() {
        return shiftZ;
    }

    public Trail setShiftZ(double shiftZ) {
        this.shiftZ = shiftZ;
        return this;
    }

}

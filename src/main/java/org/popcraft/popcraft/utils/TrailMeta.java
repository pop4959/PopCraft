package org.popcraft.popcraft.utils;

import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

public class TrailMeta {

    private Object trail;
    private BlockData data;
    private TrailType type;
    private TrailStyle style;
    private int count, extra;
    private double offsetX, offsetY, offsetZ, shiftX, shiftY, shiftZ;

    public enum TrailType {
	EFFECT, PARTICLE, BLOCK, ITEM;
    }

    public enum TrailStyle {
	NORMAL, DOTS, RAIN, DUST, SPREAD;
    }

    public TrailMeta(Object trail, BlockData data, TrailType type, TrailStyle style, int count, int extra,
					 double offsetX, double offsetY, double offsetZ, double shiftX, double shiftY, double shiftZ) {
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

    public TrailMeta(final TrailMeta other) {
	this(other.trail, other.data, other.type, other.style, other.count, other.extra, other.offsetX, other.offsetY,
		other.offsetZ, other.shiftX, other.shiftY, other.shiftZ);
    }

    public TrailMeta changeStyle(TrailStyle newStyle) {
	switch (newStyle) {
	case DOTS:
	    return new TrailMeta(this.trail, this.data, this.type, newStyle, 1, this.extra, 0, 0, 0, 0, 0, 0);
	case RAIN:
	    return new TrailMeta(this.trail, this.data, this.type, newStyle, this.count, this.extra, 0.5, 0, 0.5, 0, 4,
		    0);
	case DUST:
	    if (this.getType() == TrailType.BLOCK)
		return new TrailMeta(Particle.FALLING_DUST, this.data, this.type, newStyle, 4, this.extra, 0.5, 0.5,
			0.5, 0, 0, 0);
	    else
		return this;
	case SPREAD:
	    return new TrailMeta(this.trail, this.data, this.type, newStyle, this.count, this.extra, 0.5, 0.5, 0.5, 0,
		    0, 0);
	case NORMAL:
	default:
	    return this;
	}
    }

    public Object getTrail() {
	return trail;
    }

    public TrailMeta setTrail(Object trail) {
	this.trail = trail;
	return this;
    }

    public BlockData getData() {
	return data;
    }

    public TrailMeta setData(BlockData data) {
	this.data = data;
	return this;
    }

    public TrailType getType() {
	return type;
    }

    public TrailMeta setType(TrailType type) {
	this.type = type;
	return this;
    }

    public TrailStyle getStyle() {
	return style;
    }

    public TrailMeta setStyle(TrailStyle style) {
	this.style = style;
	return this;
    }

    public int getCount() {
	return count;
    }

    public TrailMeta setCount(int count) {
	this.count = count;
	return this;
    }

    public int getExtra() {
	return extra;
    }

    public TrailMeta setExtra(int extra) {
	this.extra = extra;
	return this;
    }

    public double getOffsetX() {
	return offsetX;
    }

    public TrailMeta setOffsetX(double offsetX) {
	this.offsetX = offsetX;
	return this;
    }

    public double getOffsetY() {
	return offsetY;
    }

    public TrailMeta setOffsetY(double offsetY) {
	this.offsetY = offsetY;
	return this;
    }

    public double getOffsetZ() {
	return offsetZ;
    }

    public TrailMeta setOffsetZ(double offsetZ) {
	this.offsetZ = offsetZ;
	return this;
    }

    public double getShiftX() {
	return shiftX;
    }

    public TrailMeta setShiftX(double shiftX) {
	this.shiftX = shiftX;
	return this;
    }

    public double getShiftY() {
	return shiftY;
    }

    public TrailMeta setShiftY(double shiftY) {
	this.shiftY = shiftY;
	return this;
    }

    public double getShiftZ() {
	return shiftZ;
    }

    public TrailMeta setShiftZ(double shiftZ) {
	this.shiftZ = shiftZ;
	return this;
    }

}

package org.popcraft.popcraft.utils;

import org.bukkit.Particle;
import org.bukkit.material.MaterialData;

public class TrailMeta {

    private Object trail;
    private TrailType type;
    private MaterialData data = null;
    private TrailStyle style = TrailStyle.NORMAL;
    private int particleCount = 32, extraData = 0;
    private double offsetX = 0, offsetY = 0, offsetZ = 0, shiftX = 0, shiftY = 0, shiftZ = 0;

    public enum TrailType {
        EFFECT, PARTICLE, BLOCK, ITEM
    }

    public enum TrailStyle {
        NORMAL, DOTS, RAIN, DUST, SPREAD
    }

    private TrailMeta(final Object trail, final TrailType type) {
        this.trail = trail;
        this.type = type;
    }

    public TrailMeta(final TrailMeta other) {
        this(other.trail, other.type);
        this.data = other.data;
        this.style = other.style;
        this.particleCount = other.particleCount;
        this.extraData = other.extraData;
        this.offsetX = other.offsetX;
        this.offsetY = other.offsetY;
        this.offsetZ = other.offsetZ;
        this.shiftX = other.shiftX;
        this.shiftY = other.shiftY;
        this.shiftZ = other.shiftZ;
    }

    public static TrailMeta of(Object trail, TrailType type) {
        return new TrailMeta(trail, type);
    }

    public TrailMeta changeStyle(TrailStyle newStyle) {
        TrailMeta newMeta = this;
        switch (newStyle) {
            case DOTS:
                newMeta = newMeta.setStyle(TrailStyle.DOTS).setParticleCount(1).setOffset(0, 0, 0).setShift(0, 0, 0);
                break;
            case RAIN:
                newMeta = newMeta.setStyle(TrailStyle.RAIN).setOffset(0.5, 0, 0.5).setShiftY(4);
                break;
            case DUST:
                if (this.getType().equals(TrailType.BLOCK))
                    newMeta = newMeta.setStyle(TrailStyle.DUST).setTrail(Particle.FALLING_DUST).setParticleCount(4).setOffset(0.5, 0.5, 0.5).setShift(0, 0, 0);
                break;
            case SPREAD:
                newMeta = newMeta.setStyle(TrailStyle.SPREAD).setOffset(0.5, 0.5, 0.5).setShift(0, 0, 0);
                break;
            case NORMAL:
            default:
                break;
        }
        return newMeta;
    }

    public Object getTrail() {
        return trail;
    }

    public TrailMeta setTrail(Object trail) {
        this.trail = trail;
        return this;
    }

    public MaterialData getData() {
        return data;
    }

    public TrailType getType() {
        return type;
    }

    public TrailMeta setType(TrailType type) {
        this.type = type;
        return this;
    }

    public TrailMeta setData(MaterialData data) {
        this.data = data;
        return this;
    }

    public TrailStyle getStyle() {
        return style;
    }

    public TrailMeta setStyle(TrailStyle style) {
        this.style = style;
        return this;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public TrailMeta setParticleCount(int particleCount) {
        this.particleCount = particleCount;
        return this;
    }

    public int getExtraData() {
        return extraData;
    }

    public TrailMeta setExtraData(int extraData) {
        this.extraData = extraData;
        return this;
    }

    public TrailMeta setOffset(double offsetX, double offsetY, double offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
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

    public TrailMeta setShift(double shiftX, double shiftY, double shiftZ) {
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.shiftZ = shiftZ;
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

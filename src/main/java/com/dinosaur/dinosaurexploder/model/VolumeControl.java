package com.dinosaur.dinosaurexploder.model;

public class VolumeControl {

    private boolean muted = false;
    private double volume = 1.0;

    public VolumeControl() {}

    public VolumeControl(double volume, boolean muted) {
        this.volume = volume;
        this.muted = muted;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }
}

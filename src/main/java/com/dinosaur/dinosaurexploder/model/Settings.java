package com.dinosaur.dinosaurexploder.model;

public class Settings {

    private double volume;
    private boolean muted;
    private double volumeVFX;
    private boolean vfxMuted;
    private String language;

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

    public double getVolumeVFX() {
        return volumeVFX;
    }

    public void setVolumeVFX(double volumeVFX) {
        this.volumeVFX = volumeVFX;
    }

    public boolean isVfxMuted() {
        return vfxMuted;
    }

    public void setVfxMuted(boolean vfxMuted) {
        this.vfxMuted = vfxMuted;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

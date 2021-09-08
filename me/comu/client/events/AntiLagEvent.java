package me.comu.client.events;

import me.comu.api.event.Event;

public class AntiLagEvent extends Event {

    private boolean monsters, animals, tnt, blocks, spawner, piston, explosions, players, rain, redstone, signs;

    public boolean isSigns() {
        return signs;
    }

    public void setSigns(boolean signs) {
        this.signs = signs;
    }

    public boolean isRedstone() {
        return redstone;
    }

    public void setRedstone(boolean redstone) {
        this.redstone = redstone;
    }

    public boolean isBlocks() { return blocks; }

    public void setBlocks(boolean blocks) { this.blocks = blocks; }

    public boolean isRain() { return rain; }

    public void setRain(boolean rain) { this.rain = rain; }

    public boolean isPlayers() {
        return players;
    }

    public void setPlayers(boolean players) {
        this.players = players;
    }

    public boolean isMonsters() {
        return monsters;
    }

    public boolean isAnimals() {
        return animals;
    }

    public boolean isTnt() {
        return tnt;
    }

    public boolean isSpawner() {
        return spawner;
    }

    public boolean isPiston() {
        return piston;
    }

    public void setMonsters(boolean monsters) {
        this.monsters = monsters;
    }

    public void setAnimals(boolean animals) {
        this.animals = animals;
    }

    public void setTnt(boolean tnt) {
        this.tnt = tnt;
    }

    public void setSpawner(boolean spawner) {
        this.spawner = spawner;
    }

    public void setPiston(boolean piston) {
        this.piston = piston;
    }

    public boolean isExplosions() {
        return explosions;
    }

    public void setExplosions(boolean explosions) {
        this.explosions = explosions;
    }
}

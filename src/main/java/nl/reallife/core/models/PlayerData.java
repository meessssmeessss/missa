package nl.reallife.core.models;

import java.util.UUID;

/**
 * Houdt alle spelersgebonden data bij: banksaldo, fitheid, level, prefix
 * en de teller die gebruikt wordt om lopen om te zetten in fitheidspunten.
 */
public class PlayerData {

    private final UUID uuid;
    private double saldo;
    private int fitheid;
    private int level;
    private String prefix;

    // Interne tellers, niet opgeslagen op schijf (worden bij logout weggegooid)
    private double loopAfstandTeller = 0.0;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.saldo = 0.0;
        this.fitheid = 50;
        this.level = 1;
        this.prefix = "Burger";
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void addSaldo(double bedrag) {
        this.saldo += bedrag;
    }

    public boolean magAfschrijven(double bedrag) {
        return this.saldo >= bedrag;
    }

    public int getFitheid() {
        return fitheid;
    }

    public void setFitheid(int fitheid, int max) {
        this.fitheid = Math.max(0, Math.min(max, fitheid));
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public double getLoopAfstandTeller() {
        return loopAfstandTeller;
    }

    public void addLoopAfstand(double afstand) {
        this.loopAfstandTeller += afstand;
    }

    public void resetLoopAfstandTeller() {
        this.loopAfstandTeller = 0.0;
    }
}

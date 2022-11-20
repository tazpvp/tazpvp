package net.tazpvp.tazpvp.utils.data;

import javax.annotation.Nonnull;

public enum QuantitativeData {
    COINS("coins", 1),
    LEVEL("level", 2),
    KILLS("kills", 3),
    DEATHS("deaths", 4),
    TOPKILLSTREAK("top_ks", 5),
    PRESTIGE("prestige", 6),
    REBIRTH("rebirth", 7),
    PLAYTIMEUNIX("playtime", 8),
    TALENTS("talents", 9);

    private final String columnName;
    private final int columnIndex;

    QuantitativeData(@Nonnull final String columnName, final int columnIndex) {
        this.columnName = columnName;
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }
}
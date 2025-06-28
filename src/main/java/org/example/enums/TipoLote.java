package org.example.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoLote {
    LOTEFIJO, PERIODOFIJO;

    @JsonCreator
    public static TipoLote fromString(String value) {
        if (value == null) return null;
        return TipoLote.valueOf(value.toUpperCase());
    }
}
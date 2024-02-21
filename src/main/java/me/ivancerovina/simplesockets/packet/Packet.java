package me.ivancerovina.simplesockets.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

public interface Packet {
    @JsonIgnore
    default String getType() {
        return this.getClass().getSimpleName();
    }
}
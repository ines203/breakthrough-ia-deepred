package com.kleblanc.breakthrough_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

// Un record génère automatiquement les champs en final private, un getter pour chaque champ,
// un constructeur public, une fonction toString, une fonction equals, et une fonction hashCode.
public record Move(Coordinate source, Coordinate target) implements iMove {
    @Override
    @JsonIgnore
    public String toString() {
        return "Source: { " + source.toString() + " }, target: { " + target.toString() + " }";
    }
}


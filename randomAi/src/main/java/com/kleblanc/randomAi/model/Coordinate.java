package com.kleblanc.randomAi.model;


// Un record génère automatiquement les champs en final private, un getter pour chaque champ,
// un constructeur public, une fonction toString, une fonction equals, et une fonction hashCode.
public record Coordinate(int x, int y) {}
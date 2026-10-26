package com.kleblanc.randomAi.model.message;

import com.kleblanc.randomAi.model.Move;

public record MoveResponseMessage(String aiId, Move move) {}
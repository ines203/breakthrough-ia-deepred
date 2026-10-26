package com.kleblanc.breakthrough_backend.model.message;

import com.kleblanc.breakthrough_backend.model.Move;

public record MoveResponseMessage(String aiId, Move move) {}

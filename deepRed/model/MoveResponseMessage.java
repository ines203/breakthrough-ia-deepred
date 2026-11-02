package com.kleblanc.LeCHONKai.model.message;

import com.kleblanc.LeCHONKai.model.Move;

public record MoveResponseMessage(String aiId, Move move) {}
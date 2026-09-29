package org.mythic_goose.amalgamation.api.tooltip_v1.colors;

public record TooltipSegment(String text, TooltipStyle style) {} // style == null -> plain text

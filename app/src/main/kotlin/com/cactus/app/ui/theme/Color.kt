package com.cactus.app.ui.theme

import androidx.compose.ui.graphics.Color

/* Cactus design tokens — "Warm Sand & Cactus Green" (PRD §5.1).
 * Names match the PRD color roles exactly so the mapping is unambiguous.
 * Dark mode is the default (PRD §5.2, §12.5). */

// Primary roles
val primary           = Color(0xFF6FCF87) // Cactus Green — main actions, active state
val onPrimary         = Color(0xFF00391B) // text on primary surfaces
val primaryContainer  = Color(0xFF1B5E2E) // cards, active list items

// Secondary / Tertiary
val secondary         = Color(0xFFF0B86C) // Desert Gold — accents, timestamps
val tertiary          = Color(0xFFD99CBE) // Desert Bloom — loops, notes features

// Surfaces (warm dark, never pure black)
val surface           = Color(0xFF1A1A14) // Warm Charcoal — APP background (PRD §5.1)
val surfaceContainer  = Color(0xFF25251E) // Elevated surfaces (cards, nav)
val background        = Color(0xFF121210) // Base background (deepest layer)

// Text / support
val onSurface         = Color(0xFFF5F3EC) // high-contrast text on warm dark
val onSurfaceVariant  = Color(0xFFA8A597) // secondary text, muted icons
val outline           = Color(0xFF3A3A30) // hairlines, dividers

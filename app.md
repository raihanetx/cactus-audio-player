# Cactus — PRD: Product Requirements Document & UI/UX Design System (v1.1)

> **Project:** Cactus — Learn English by Listening  
> **Platform:** Web Prototype (HTML/Tailwind/JS) representing Android Native (Kotlin + Jetpack Compose)  
> **Target Users:** Bengali-speaking English learners, busy adults  
> **Tagline:** "Learn English without studying. Just listen."

---

## Table of Contents

1. [Product Overview](#1-product-overview)
2. [Competitive Landscape](#2-competitive-landscape)
3. [User Personas](#3-user-personas)
4. [Design Philosophy & Principles](#4-design-philosophy--principles)
5. [Color System & Rationale](#5-color-system--rationale)
6. [Typography System](#6-typography-system)
7. [Layout & Navigation Architecture](#7-layout--navigation-architecture)
8. [Screen-by-Screen UX Design](#8-screen-by-screen-ux-design)
9. [Micro-Interactions & Motion](#9-micro-interactions--motion)
10. [Design System Components](#10-design-system-components)
11. [Accessibility](#11-accessibility)
12. [Why This Approach — Key Decisions](#12-why-this-approach--key-decisions)

---

## 1. Product Overview

### 1.1 The Core Idea
Cactus is a **passive language immersion** app. Users download English videos (movies, podcasts, TED talks, YouTube) to their phone. Cactus detects them, extracts audio, and plays them in the background — no screen time required.

### 1.2 Key Features
| Feature | Priority | Description |
|---------|----------|-------------|
| Video Detection | P0 | Auto-scan device for video files, extract audio |
| Background Playback | P0 | Play audio with screen off, minimal battery |
| Playlist Management | P0 | Organize videos into learning playlists |
| Loops (A-B Repeat) | P1 | Mark start/end timestamps, repeat infinitely |
| Subtitles | P1 | English text + Bangla translation + pronunciation guide |
| Timestamped Notes | P1 | Add notes at any moment during playback |
| Sleep Timer | P1 | Auto-stop after set duration |
| Playback Speed | P1 | 0.5x – 2.0x speed control |
| Offline Mode | P0 | Everything works without internet during playback |

### 1.3 What Cactus is NOT
- NOT a flashcard app
- NOT a grammar drill app
- NOT a gamified quiz app
- NOT a social network
- NOT a video player (audio-first, video is only a source)

---

## 2. Competitive Landscape

### 2.1 Direct Competitors (Researched)
| App | Approach | Gap / Opportunity |
|-----|----------|-------------------|
| **Aelano** | AI bilingual subtitles, shadowing, YouTube import | Requires active engagement; Cactus is passive-first |
| **Erla** | Comprehensible input, tap-to-reveal sentences, AI chat | Active listening mode; Cactus is background-first |
| **Lingofable** | AI-generated stories with audio narration | Content is generated, not user's own videos |
| **Aprendos** | Podcast-based with AI coaching | Structured lessons, not free-form listening |
| **Duolingo** | Gamified micro-lessons | Active study, high cognitive load, no passive mode |

### 2.2 Why Cactus Wins
1. **Zero-effort onboarding** — Download videos, that's it. No placement tests, no signup walls.
2. **True background immersion** — Every competitor requires screen-on active participation. Cactus works while you drive, walk, code.
3. **User's own content** — No curated lessons. Users bring content they actually enjoy.
4. **Offline-first by design** — Every competitor requires internet for AI features. Cactus works fully offline.
5. **Battery-conscious** — Audio-only playback with no screen rendering = minimal battery drain.

---

## 3. User Personas

### 3.1 Primary: Rahim (28, Dhaka)
- **English level:** B1 (intermediate)  
- **Pain point:** Too busy for classes. Wants to improve listening comprehension  
- **Needs:** Plays English content in background during commute/work. Puts phone in pocket.

### 3.2 Secondary: Nusrat (22, Student)
- **English level:** A2 (elementary)  
- **Pain point:** Understands written English but can't follow movies or fast speech  
- **Needs:** Wants subtitles + Bangla translation when she actively studies. Uses loops to drill difficult sentences.

### 3.3 Tertiary: Shakib (35, Business Owner)
- **English level:** A1 (beginner)  
- **Pain point:** Needs English for business but has zero time  
- **Needs:** Simple interface in Bangla. Just play and forget. No complex features.

---

## 4. Design Philosophy & Principles

### 4.1 Core Design Philosophy: Calm Technology
| Principle | Application in Cactus |
|-----------|----------------------|
| 1. Smallest possible attention | App is designed to be launched and forgotten. Primary interaction is "tap play, put phone away." |
| 2. Inform and create calm | No badging, no streaks, no notifications that pressure user. UI is still, quiet, serene. |
| 3. Use the periphery | Most features live behind intentional taps. The main screen is just a list and a play button. |
| 4. Communicate without speaking | Status is conveyed through subtle UI: a pulsing waveform shows playback. |
| 5. Minimum tech needed | No AI, no cloud sync, no accounts. |
| 6. Work when it fails | Fully offline. No network dependency during playback. |

### 4.2 Secondary Philosophy: Emotion-Driven Design (M3 Expressive)
- **Expressive shapes** — Soft, rounded corners (24dp+ radius) signal approachability.
- **Motion physics** — Spring-based animations for transitions feel organic and alive.
- **Dynamic color** — App adopts user's wallpaper palette, making it feel like *their* app.

### 4.3 Tertiary: Minimalist + Purposeful
- **Reduce cognitive load** — The app has exactly 4 main screens. No sub-navigation deeper than 2 levels.
- **Glanceable** — At a glance you see: what's playing, what's in your queue, how far along you are.
- **Progressive disclosure** — Advanced features (loops, notes, subtitles) are hidden behind bottom sheets until needed.

---

## 5. Color System & Rationale

### 5.1 Warm Sand & Cactus Green
| Token | Hex (Dark - Default) | Role |
|-------|-------------|------|
| Primary | `#6FCF87` (Cactus Green) | Main actions, active state |
| On Primary | `#00391B` | Text on primary surfaces |
| Primary Container | `#1B5E2E` | Cards, active list items |
| Secondary | `#F0B86C` (Desert Gold) | Accents, timestamps |
| Tertiary | `#D99CBE` (Desert Bloom) | Loops, notes features |
| Surface | `#1A1A14` (Warm Charcoal) | App background (not pure black) |
| Surface Container | `#25251E` | Elevated surfaces (cards, nav) |
| Background | `#121210` | Base background |

### 5.2 Why Dark Mode Default?
- **Primary use case:** Listening before sleep, during commute (low light).
- **Battery:** Dark mode reduces battery by 30-60% on OLED screens.
- **Warmth:** `#1A1A14` mimics a dimly lit room, perfect for sleeping/listening, avoiding the harshness of pure `#000000`.

---

## 6. Typography System

### 6.1 Font Selection: Inter + Noto Sans Bengali
| Usage | Font | Weight | Size |
|-------|------|--------|------|
| Headlines (English) | Inter | ExtraBold (800) | 32sp - 36sp |
| Body (English) | Inter | Regular (400) | 14sp - 16sp |
| Caption (English) | Inter | Medium (500) | 10sp - 12sp |
| Bangla UI labels | Noto Sans Bengali | Regular (400) | 14sp |
| Bangla translation | Noto Sans Bengali | Regular (400) | 14sp |
| Pronunciation | Inter | Light (300) | 12sp |

---

## 7. Layout & Navigation Architecture

### 7.1 Navigation Structure
```
Bottom Navigation (4 Tabs)
├── Library (Home) — All detected videos, search, sort
│   └── Video Detail → Player Screen
├── Playlists — User-created playlists
│   └── Playlist Detail → Player Screen
├── Now Playing — Full player with controls, progress, metadata
│   └── Loops/Subtitles/Notes (bottom sheet)
└── Settings — Theme, language, scan folders, about
```

### 7.2 Layout Philosophy
- **Full-Screen Responsive:** The prototype spans the entire viewport (mobile or desktop simulated mobile) without artificial device frames, maximizing content space.
- **Single-hand operation:** All primary actions within thumb reach (bottom-third of screen).
- **Bottom sheets for advanced features:** Loops, notes, subtitles slide up from bottom, never push navigation.
- **Persistent mini-player:** A slim bar at the bottom of Library/Playlists shows current track; tap to expand to full player.

---

## 8. Screen-by-Screen UX Design

### 8.1 Library (Home Screen)

**Purpose:** User opens app, sees all their content. Tap to play. That's it.

**Elements (top to bottom):**
1. **Status bar** — Transparent, white text/icons.
2. **App bar** — "Library" title (left, ExtraBold). On the right side:
   - **Notification Icon** (Bell): Sits to the left of the search icon. Features a subtle dot indicator for new alerts (e.g., "3 new videos detected").
   - **Search Icon** (Magnifying glass): Instant filter across titles.
3. **Sort chips** — Horizontal scrollable: Recent | Title | Duration | Language
4. **Content list** — Each item is a row:
   - Thumbnail (16:9 extracted from video, subtly blurred — emphasis on audio)
   - Title (English, bold, 2 lines max)
   - Metadata row: duration • date added
   - Play button (right side, primary color circle)
5. **Mini-player** — Fixed at bottom. Shows current track title and play/pause. Tap to expand.

**Key interactions:**
- Tap item → opens player screen
- Tap notification icon → opens notification history/details
- Swipe left → mark as "Finished"
- Swipe right → Add to "Up Next" queue

**Design Update Note:** 
*The Floating Action Button (FAB) for manual file addition has been intentionally removed from this screen. To maintain Calm Technology principles, file rescanning happens automatically in the background. Manual folder configuration is handled in Settings.*

### 8.2 Player Screen
**Purpose:** Clean, distraction-free, works in a pocket.

**Elements:**
1. **Blurred artwork background** — Dominant gradient extracted from video frame.
2. **Top bar** — Back button (left), "Playing From" context (center), More options (right).
3. **Artwork area** — Large square card. Tap to toggle between English and Bangla titles.
4. **Track info** — Title (ExtraBold), file name (monospace).
5. **Progress bar** — Thick (6dp), glowing thumb, shows elapsed / remaining time.
6. **Playback controls:**
   - Skip back 15s
   - Play / Pause (large, 80dp, white with pulsing glow)
   - Skip forward 15s
7. **Feature pills** — Loops, Subtitles, Notes (open bottom sheets).
8. **Bottom row** — Sleep timer, Speed (1.0x), Volume.

### 8.3 Loops (A-B Repeat) — Bottom Sheet
**Elements:**
- A-B marker timestamps (tap to set current position)
- Visual highlighted region on a mini progress bar
- Loop count selector: ∞ | 3x | 5x | 10x
- Saved loops list for current video

### 8.4 Subtitles — Bottom Sheet
**Elements:**
- Auto-scroll toggle
- Current subtitle card (English, Bangla, Pronunciation)
- Scrollable timeline of subtitle chunks
- Tap any subtitle → jump to timestamp

### 8.5 Notes — Bottom Sheet
**Elements:**
- Text input (auto-focuses, prepends timestamp)
- Saved notes list (chronological)
- Tap note → jump to timestamp

### 8.6 Settings Screen
**Elements (grouped):**
- **Media Sources** — Folder picker, "Rescan Now" button (replaces home FAB).
- **Playback** — Default speed, sleep timer, crossfade.
- **Appearance & Language** — Theme, EN/BN toggle, Dynamic Color.
- **About** — Version info.

---

## 9. Micro-Interactions & Motion

### 9.1 Animation Philosophy
Spring physics (M3 Expressive) for natural-feeling motion:

| Interaction | Animation |
|-------------|-----------|
| Tab switch | Cross-fade + 8px slide up |
| Bottom sheet open | Slides up with bounce (0.34, 1.2, 0.64, 1) |
| Play button press | Scales to 0.95, springs back to 1.0 |
| Notification badge | Subtle pulse if unread |
| Progress bar thumb | Glowing shadow, smooth width transition |

### 9.2 Ambient Playback Indicator
- Play button has a soft pulsing glow (green) when playing.
- Mini-player and list thumbnails show 3 animated waveform bars.

---

## 10. Design System Components

### 10.1 Component Inventory
| Component | Customization |
|-----------|---------------|
| App Bar | Transparent, text left-aligned, action icons right-aligned |
| Bottom Navigation | 4 items, labeled, active = primary color |
| Cards | 16dp-24dp rounded corners, surface-container background |
| Mini-player | Fixed bottom, 56dp height, frosted glass effect |
| Play Button | Circular, 80dp, white/green, ripple/pulse on play |
| Progress Bar | 6dp track, glowing thumb |
| Bottom Sheet | 32dp top radius, drag handle implicit |
| Chips | Pill-shaped, primary container for active |
| Notification Icon | Bell outline, secondary color dot for alerts |

*(Note: FAB / Floating Action Button has been removed from the design system inventory).*

---

## 11. Accessibility

### 11.1 Contrast Compliance
All color combinations meet **WCAG 2.2 AA** standard (4.5:1 for normal text). High-contrast white text on warm dark backgrounds ensures readability in low-light environments.

### 11.2 Touch Targets
- All interactive elements minimum **44x44dp**.
- Play button is 80dp for easy thumb reach.
- Notification and Search icons in the App Bar have 40dp touch targets.

---

## 12. Why This Approach — Key Decisions

### 12.1 Why No AI Features?
Users bring content; we don't process/analyze it. Privacy-first. Zero server costs, fully offline, no data collection, instant app launch.

### 12.2 Why Audio-Only When Source is Video?
- **Battery:** Screen-off audio playback uses 5-10% of video playback power.
- **Attention:** Video encourages watching; audio encourages doing other things.
- **Background:** Android's `MediaSession` + `ForegroundService` handles audio seamlessly.

### 12.3 Why Remove the FAB (Floating Action Button)?
Originally, a FAB was considered for manual file addition/rescanning. It was removed because:
1. **Calm Tech Principle:** A FAB demands visual attention and implies manual work is required frequently.
2. **Auto-Sufficiency:** The app automatically scans standard directories. User shouldn't have to press a button every time they open the app.
3. **Cleaner Aesthetic:** Removing the FAB allows the list view to use the full screen real estate without overlapping content, making the interface feel more serene and list-focused. Manual scanning is moved to Settings.

### 12.4 Why Add a Notification Icon to the Home Screen?
While we avoid intrusive push notifications, users still need to know when the app has detected new media in the background or when a download/scan is complete. A passive notification icon (Bell) in the top app bar, next to search, provides a quiet, non-intrusive way to check system status without leaving the home screen.

### 12.5 Why Dark Mode Default?
Primary use case is listening before sleep, during commute (low light). Dark mode reduces battery by 30-60% on OLED screens. Warm dark (`#1A1A14`) mimics a dimly lit room, perfect for sleeping/listening.

### 12.6 Why Minimal Text on Player Screen?
Player screen is the most-used screen; clutter here ruins the experience. Users should be able to control playback without looking at the screen. Large title only.
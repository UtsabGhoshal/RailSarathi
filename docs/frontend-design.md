# 🎨 Frontend Design Document Specification (DDS)
# RailSarathi – Railway Reservation & Journey Experience Platform

**Document Version:** 1.0  
**Status:** Approved Specification  
**Design Paradigm:** Modern Deep Slate & Dark Glassmorphism  
**Target Platform:** Modern Web (Responsive: Mobile, Tablet, Desktop)  
**Tech Stack:** React 19 / Vite • TypeScript • Tailwind CSS • Lucide Icons • Framer Motion  

---

## 1. Executive Summary & Design Vision

**RailSarathi** is designed to transform the often cluttered, outdated railway reservation user experience into a **high-performance, luxurious, intuitive, and modern journey management platform**. 

### Core Design Pillars
1. **Visual Sophistication:** Deep slate background with dark glassmorphism, frosted translucent cards, ambient glowing neon accents (electric cyan `#06b6d4` & violet/indigo `#6366f1`), and crisp typography.
2. **Seamless Frictionless Flows:** Zero-refresh searching, interactive floating modals for authentication (ensuring search and booking context is never lost), dynamic autocomplete station inputs, and live seat matrix toggling.
3. **Clarity & Information Hierarchy:** Immediate visual recognition of train timings, availability statuses (`AVAILABLE` in Emerald, `RAC` in Amber, `WAITLIST` in Crimson), fare breakdowns, and journey timeline graphics.
4. **Session & State Integrity:** Multi-tab and device isolation, persistent search filters, and smooth micro-animations on interactive elements.

---

## 2. Design System & Style Tokens

### 2.1 Color Palette

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           COLOR PALETTE                                 │
├───────────────────┬──────────────────────────┬──────────────────────────┤
│ Token             │ Hex Code / Tailwind      │ Purpose / Usage          │
├───────────────────┼──────────────────────────┼──────────────────────────┤
│ Background Primary│ #090d16 (slate-950 deep) │ Global page background   │
│ Surface Dark      │ #0f172a (slate-900)      │ Container surfaces       │
│ Glass Surface     │ rgba(30, 41, 59, 0.65)   │ Frosted cards & panels   │
│ Glass Border      │ rgba(255, 255, 255, 0.08)│ Subtle highlights        │
│ Primary Accent    │ #06b6d4 (cyan-500)       │ CTAs, active states, PNR │
│ Secondary Accent  │ #6366f1 (indigo-500)     │ Gradients, brand badges  │
│ Accent Glow       │ rgba(6, 182, 212, 0.25)  │ Ambient light effects    │
│ Success / Avail   │ #10b981 (emerald-500)    │ Available seats, confirm │
│ Warning / RAC     │ #f59e0b (amber-500)      │ RAC status, delay alerts │
│ Danger / WL       │ #ef4444 (rose-500)       │ Waitlist, cancellation   │
│ Text Primary      │ #f8fafc (slate-50)       │ High-emphasis headings   │
│ Text Muted        │ #94a3b8 (slate-400)      │ Subtitles, labels, hints │
└───────────────────┴──────────────────────────┴──────────────────────────┘
```

### 2.2 Typography System
* **Headings / Brand:** `Outfit`, sans-serif (Geometric, modern, bold personality).
* **Body & UI Elements:** `Inter`, sans-serif (High readability, neutral, legible at all sizes).
* **Data / Numbers / PNR / Codes:** `JetBrains Mono`, monospace (Tabular alignment, clear numbers).

| Level | Size | Weight | Usage |
| :--- | :--- | :--- | :--- |
| **Display 1** | 3.5rem (56px) | 800 (ExtraBold) | Hero Banner Headline |
| **H1** | 2.25rem (36px) | 700 (Bold) | Page Titles, Search Headers |
| **H2** | 1.5rem (24px) | 600 (SemiBold) | Train Names, Section Titles |
| **H3** | 1.25rem (20px) | 600 (SemiBold) | Card Headers, Modal Titles |
| **Body Large**| 1.125rem (18px)| 400 / 500 | Hero subtext, important info |
| **Body Base** | 1rem (16px) | 400 (Regular) | Primary text, form inputs |
| **Caption** | 0.875rem (14px)| 500 (Medium) | Badges, tags, timings |
| **Mono Code** | 0.875rem (14px)| 600 (SemiBold) | Train numbers, PNRs, Coach IDs |

### 2.3 Glassmorphism & Elevation System
* **Frosted Glass Cards:** `background: rgba(15, 23, 42, 0.70); backdrop-filter: blur(16px); border: 1px solid rgba(255, 255, 255, 0.08); box-shadow: 0 20px 40px -15px rgba(0, 0, 0, 0.5);`
* **Floating Header / Navbar:** `background: rgba(9, 13, 22, 0.80); backdrop-filter: blur(20px); border-bottom: 1px solid rgba(255, 255, 255, 0.06);`
* **Glow Effects:** Radial gradients layered in background pseudo-elements (`radial-gradient(circle at 50% -20%, rgba(6, 182, 212, 0.15), transparent 70%)`).

---

## 3. Information Architecture & Navigation

```
                                    +-----------------------+
                                    |     RailSarathi UI    |
                                    +-----------+-----------+
                                                |
          +-----------------------+-------------+-------------+-----------------------+
          |                       |                           |                       |
    [Landing Page]       [Search Results]             [Booking Flow]          [User Dashboard]
    • Hero Search        • Filter Sidebar             • Passenger Form        • Active Journeys
    • Quick Services     • Date Ribbon                • Berth Preference      • Past Bookings
    • Live Train Track   • Interactive Cards          • Fare Breakdown        • Cancel & Refund
    • Popular Routes     • Class/Quota Matrix         • Payment Simulator     • Profile Settings
```

### Route Map

| URL Path | View / Component | Access Level | Description |
| :--- | :--- | :--- | :--- |
| `/` | `HomePage` | Public | Hero search console, popular routes, quick PNR check |
| `/search` | `SearchResultsPage` | Public | Train listings, schedule timeline, class availability |
| `/train/:trainNumber` | `TrainDetailsPage` | Public | Live route schedule, intermediate stops & delays |
| `/booking/initiate` | `BookingCheckoutPage`| Authenticated | Passenger inputs, berth selector, price breakdown |
| `/booking/confirmation`| `TicketViewPage` | Authenticated | Generated Boarding Pass ticket with QR code |
| `/dashboard` | `UserDashboardPage` | Authenticated | Trip manager, profile, session statistics |
| `/dashboard/history` | `BookingHistoryPage`| Authenticated | Historical bookings, download PDF, cancel tickets |
| `/admin` | `AdminDashboardPage` | Admin Only | Train/station fleet management, booking metrics |

---

## 4. Detailed Component & UI Layout Specifications

### 4.1 Global Navigation Header (Glassmorphic Top Bar)

```text
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│  🚆 RailSarathi      [ Trains ]  [ PNR Status ]  [ Live Track ]        🔍 Search   [ Log In ] [ Sign Up ]  │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
```

* **Brand Logo:** Train glyph with glowing electric cyan dot + gradient brand typography `RailSarathi`.
* **Center Navigation:** Pill-shaped glass bar with active indicator animation (`Trains`, `PNR Status`, `Live Schedule`, `Fares`).
* **Right Actions:**
  * **When Unauthenticated:** `Log In` button (Subtle border) and `Sign Up` button (Vibrant gradient with glow).
  * **When Authenticated:** Avatar with User initials, Role Badge (`Passenger` / `Admin`), and Dropdown (`My Bookings`, `Active Tickets`, `Profile`, `Logout`).

---

### 4.2 Hero Section with Floating Search Console

The focal point of the home page. Designed with an ambient animated railway background and a high-converting search console.

```text
                                  EXPERIENCE SEAMLESS RAIL TRAVEL
                         India's Next-Generation Intelligent Railway Booking
  ┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
  │  [⇄ One Way / Round Trip]                 [General Quota ▾]              [All Classes ▾]         │
  │ ──────────────────────────────────────────────────────────────────────────────────────────────── │
  │  FROM                          ⇄                TO                           DEPARTURE DATE      │
  │  📍 [ HWH ] Howrah Jn                          📍 [ NDLS ] New Delhi        📅 [ 2026-10-15 ]    │
  │     Kolkata, West Bengal                          Delhi, NCT                    Today | Tomorrow │
  │ ──────────────────────────────────────────────────────────────────────────────────────────────── │
  │                                                               [ 🔍 Search Trains Express ➔ ]     │
  └──────────────────────────────────────────────────────────────────────────────────────────────────┘
```

* **Interactive Station Search Inputs:**
  * Auto-complete dropdown displaying Station Code in glowing mono font (`HWH`, `NDLS`, `CSMT`, `SBC`) with city and state.
  * Recent station history & popular hubs fast-select tags.
  * Central **Swap Button (`⇄`)** with 180° rotation micro-animation.
* **Quick Date Selector:** Integrated date picker with convenient `Today`, `Tomorrow`, and `Weekend` fast-pills.
* **Quota Selector:** `General`, `Tatkal`, `Ladies`, `Senior Citizen`, `Divyang`.

---

### 4.3 Interactive Train Search Results Page

```text
┌─────────────────────────┬────────────────────────────────────────────────────────────────────────┐
│ ⚙️ FILTER RESULTS       │ 📅 DATE RIBBON: [ < 14 Oct ] [ 15 Oct (Selected) ] [ 16 Oct > ]        │
│ ─────────────────────── │ ────────────────────────────────────────────────────────────────────── │
│ Travel Class            │ 🚆 22301 • VANDE BHARAT EXPRESS                       ⚡ Fastest Route │
│ ☑ 1A (First AC)         │ HWH (05:55) ───────────── 07h 30m ─────────────► NJP (13:25)           │
│ ☑ 2A (2 Tier)           │ Runs on: [M] [T] [W] [T] [F] [S] [•]               Live Route View ▾   │
│ ☑ 3A (3 Tier)           │ ┌──────────────┬──────────────┬──────────────┬──────────────┐          │
│ ☑ CC (Chair Car)        │ │ CC • ₹1,565  │ EC • ₹2,825  │ 3A • ₹1,240  │ 2A • ₹1,850  │          │
│ ─────────────────────── │ │ ✅ AVL - 42  │ ✅ AVL - 08  │ ⚠️ RAC - 14  │ ❌ WL - 22   │          │
│ Departure Time          │ └──────────────┴──────────────┴──────────────┴──────────────┘          │
│ 🔘 Morning (06:00-12:00)│                                               [ Book Selected Seat ➔ ] │
│ 🔘 Evening (18:00-24:00)│ ────────────────────────────────────────────────────────────────────── │
│ ─────────────────────── │ 🚆 12301 • RAJDHANI EXPRESS                            🍽️ Food Included│
│ Train Types             │ HWH (16:50) ───────────── 17h 05m ─────────────► NDLS (09:55)          │
│ ☑ Vande Bharat          │ Runs on: [M] [T] [W] [T] [F] [S] [S]               Live Route View ▾   │
│ ☑ Rajdhani              │ ...                                                                    │
└─────────────────────────┴────────────────────────────────────────────────────────────────────────┘
```

#### Train Result Card Features:
1. **Journey Timeline Graphic:** Clear visual line connecting origin time & station code to destination time & station code with total transit duration.
2. **Interactive Class Strip:** Clickable cards for each available class (`1A`, `2A`, `3A`, `3E`, `SL`, `CC`, `EC`).
   * Clicking a class updates the active booking selection instantly.
   * Real-time availability indicator:
     * **Green Glow:** `AVAILABLE - <Count>`
     * **Amber Tag:** `RAC - <Count>`
     * **Rose Tag:** `WAITLIST - <Count>`
3. **Expandable Route Accordion:** Shows all intermediate stops with arrival, halt duration, and departure times.

---

### 4.4 Dynamic Authentication Modals (Floating & Non-Intrusive)

Authentication happens via an animated frosted modal over the current screen, preventing search query resets.

```text
┌────────────────────────────────────────────────────────┐
│                        🚆 RailSarathi                  │
│                     [ Sign In ]   [ Register ]         │
│ ────────────────────────────────────────────────────── │
│  Welcome Back! Enter your credentials to continue.     │
│                                                        │
│  Username or Email Address                             │
│  [ utsab@railsarathi.com                             ] │
│                                                        │
│  Password                                              │
│  [ ••••••••••••••••                                👁 ] │
│                                                        │
│  🔒 Session-Protected Login • 24h Secure Access         │
│                                                        │
│  [                Sign In to Account                ]  │
│                                                        │
│  Don't have an account? Create one now                 │
└────────────────────────────────────────────────────────┘
```

* **Tab Toggle:** Smooth slide animation between **Sign In** and **Create Account**.
* **Register Form Validation Feedback:**
  * Password strength indicator bar with 8+ character counter.
  * Real-time 10-digit phone verification tag.
  * Inline validation warnings for duplicate email / username.
* **Session Storage:** Automatically stores `accessToken`, `sessionId`, and `user` payload upon success, updating application state reactively.

---

### 4.5 Digital Boarding Pass Ticket Component

A modern boarding-pass style ticket layout designed for mobile and desktop screens.

```text
┌────────────────────────────────────────────────────────────────────────────────────┐
│ 🚆 RAILSARATHI BOARDING PASS                     PNR: 842-1948291   STATUS: CNF   │
├──────────────────────────────────────────┬─────────────────────────────────────────┤
│ TRAIN                                    │ PASSENGERS                              │
│ 22301 / VANDE BHARAT EXP                 │ 1. Utsab Ghoshal (22, M) - C4 / 24 [WS] │
│                                          │ 2. Sourav Ganguly (28, M) - C4 / 25 [M] │
│ FROM              TO                     ├─────────────────────────────────────────┤
│ HOWRAH (HWH)  ➔  NEW JALPAIGURI (NJP)   │ FARE & PAYMENT                          │
│ Dep: 05:55 AM     Arr: 01:25 PM          │ Total: ₹3,130 • Paid via UPI            │
│ Date: 15 Oct 2026 Class: AC Chair (CC)   │ Session: 961048ad-687d                  │
├──────────────────────────────────────────┴─────────────────────────────────────────┤
│ [ QR Code: PNR Verification ]     [ 🖨️ Print Ticket ]    [ 📥 Download PDF ]       │
└────────────────────────────────────────────────────────────────────────────────────┘
```

---

### 4.6 Fast Refund & Ticket Cancellation Dialog

Interactive cancellation modal computing instant refunds dynamically based on the departure countdown.

```text
┌────────────────────────────────────────────────────────┐
│ ⚠️ Confirm Ticket Cancellation                         │
│ PNR: 842-1948291 • Train 22301 (Vande Bharat)          │
│ ────────────────────────────────────────────────────── │
│ Journey Departure: in 72 hours (Standard Cancellation) │
│                                                        │
│  Total Ticket Fare:                     ₹3,130.00      │
│  Deduction (Flat Class Charge):       - ₹  130.00      │
│ ────────────────────────────────────────────────────── │
│  Estimated Immediate Refund:           ₹3,000.00      │
│                                                        │
│  ⚡ Instant Seat Release & Background Refund Dispatch  │
│                                                        │
│  [ Keep My Ticket ]         [ 🔴 Confirm & Cancel ]    │
└────────────────────────────────────────────────────────┘
```

---

## 5. Frontend Architecture & State Management

```
frontend/src/
├── assets/                  # Icons, railway SVGs, train brand assets
├── components/
│   ├── common/              # Button, Input, Modal, Badge, Toast, GlassCard
│   ├── layout/              # Navbar, Footer, AppShell, Sidebar
│   ├── auth/                # LoginModal, RegisterModal, ProtectedRoute
│   ├── search/              # SearchWidget, StationAutocomplete, DatePicker
│   ├── trains/              # TrainCard, QuotaBadge, RouteTimeline, ClassStrip
│   ├── booking/             # PassengerForm, BerthVisualizer, FareSummary
│   └── ticket/              # BoardingPass, QRCodeView, PrintTicket
├── context/
│   ├── AuthContext.tsx      # User state, JWT token, Session ID, Login/Logout
│   ├── SearchContext.tsx    # Source, Dest, Date, Trains search results cache
│   └── ToastContext.tsx     # Global notification toast provider
├── hooks/
│   ├── useAuth.ts           # Auth hook for user session access
│   ├── useTrainSearch.ts    # Train search API query hook
│   └── useDebounce.ts       # Autocomplete search debounce
├── services/
│   ├── api.ts               # Axios instance with Bearer & Session interceptors
│   ├── authService.ts       # Register, login, profile API endpoints
│   └── trainService.ts      # Stations & trains search API endpoints
├── types/
│   ├── auth.types.ts        # User, AuthResponse, Session definitions
│   ├── train.types.ts       # Station, Train, Schedule, Seat availability types
│   └── booking.types.ts     # BookingRequest, Ticket, Refund types
├── App.tsx                  # Router configuration & Layout wrap
├── index.css                # Tailwind directives, custom glassmorphism styles
└── main.tsx                 # React DOM root
```

### HTTP Client Interceptor Configuration
* **Request Interceptor:** Automatically appends `Authorization: Bearer <accessToken>` and `X-Session-ID: <sessionId>` to all outgoing requests.
* **Response Interceptor:** Automatically intercepts `401 Unauthorized` responses and opens the Auth Modal smoothly without losing the user's current progress.

---

## 6. Micro-Interactions & Animation Guidelines

| Action | Animation Effect | Duration / Curve |
| :--- | :--- | :--- |
| **Card Hover** | Subtle `translateY(-4px)` with cyan border glow enhancement | 200ms `ease-out` |
| **Station Swap (`⇄`)** | 180° smooth rotational spin with icon color flash | 300ms `cubic-bezier(0.4, 0, 0.2, 1)` |
| **Modal Open** | Backdrop blur fade-in + Modal scale from `0.95` to `1.0` | 250ms spring |
| **Class Tab Select** | Cyan glow border expansion and fare total counter ticker | 150ms `ease-in-out` |
| **Loading Skeletons** | Shimmer wave pulse gradient across train card slots | Infinite 1.5s linear |

---

## 7. Responsiveness & Device Breakpoints

* **Mobile (< 640px):**
  * Search widget converts to a clean single-column stacked accordion.
  * Train cards collapse into compact cards with a horizontal scrolling class selector.
  * Bottom navigation bar for fast mobile navigation (`Search`, `Trips`, `Profile`).
* **Tablet (640px – 1024px):**
  * 2-column search widget layout with top navigation bar.
* **Desktop (1024px+):**
  * Full horizontal search console, filter sidebar alongside train listings, full expanded booking console.

---

## 8. Summary of Review & Next Implementation Phase

With this Design Document Specification approved, the implementation of the frontend will proceed directly into **Scaffolding the Vite + React + TypeScript + Tailwind CSS application** in [`RailSarathi/frontend/`](file:///c:/Users/UTSAB/Desktop/Travel/RailSarathi/frontend) and constructing the **Design System**, **Navbar**, **Hero Banner with Interactive Search Widget**, and **Session-Aware Auth Modals**.

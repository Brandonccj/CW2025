# TetrisJFX - Coursework Documentation

## GitHub Repository
```
https://github.com/Brandonccj/CW2025
```
**https://github.com/Brandonccj/CW2025**

---

## Table of Contents
- [Compilation Instructions](#compilation-instructions)
- [Implemented and Working Properly](#implemented-and-working-properly)
- [Implemented but Not Working Properly](#implemented-but-not-working-properly)
- [Features Not Implemented](#features-not-implemented)
- [New Java Classes](#new-java-classes)
- [Modified Java Classes](#modified-java-classes)
- [Unexpected Problems](#unexpected-problems)

---

## Compilation Instructions

### Prerequisites:
- **Java JDK 23 or higher**
- **JavaFX SDK 21.0.6 or higher**
- **Maven** (optional, if using Maven build)

### Steps to Compile and Run:

#### Option 1: Using IDE (IntelliJ IDEA / Eclipse)

1. **Clone the repository:**
```bash
   git clone https://github.com/[your-username]/TetrisJFX
   cd TetrisJFX
```

2. **Import the project:**
   - Open your IDE
   - Import as a Java/JavaFX project
   - Add JavaFX library to project dependencies

3. **Configure VM Options:**
```
   --module-path [path-to-javafx-sdk]/lib --add-modules javafx.controls,javafx.fxml,javafx.media
```

4. **Run the application:**
   - Run `Main.java` located in `src/com/comp2042/`

#### Option 2: Using Command Line

1. **Compile:**
```bash
   javac --module-path [path-to-javafx-sdk]/lib \
         --add-modules javafx.controls,javafx.fxml,javafx.media \
         -d out \
         src/com/comp2042/**/*.java
```

2. **Run:**
```bash
   java --module-path [path-to-javafx-sdk]/lib \
        --add-modules javafx.controls,javafx.fxml,javafx.media \
        -cp out \
        com.comp2042.Main
```

### Required Dependencies:
- JavaFX Controls
- JavaFX FXML
- JavaFX Media (for audio system)

---

## Implemented and Working Properly

### 🎮 Core Game Features

| Feature | Description | Status |
|---------|-------------|--------|
| **Two Game Modes** | Normal Mode (classic with levels) and Zen Mode (relaxed, no game over) | ✅ Working |
| **Complete Sound System** | Background music with playlists, sound effects, toggle controls | ✅ Working |
| **Hold Piece Mechanism** | Hold current piece with 'C' key, one swap per piece rule | ✅ Working |
| **Ghost Piece (Shadow)** | Semi-transparent preview showing landing position | ✅ Working |
| **Next Piece Preview** | Shows next 3 upcoming pieces with "bag" system | ✅ Working |
| **Level Progression** | Increases every 5 lines in Normal mode with speed changes | ✅ Working |
| **Pause/Resume System** | Full pause menu with Resume/Restart/Main Menu options | ✅ Working |
| **Game Over Screen** | Statistics display (time, score, high score, lines) | ✅ Working |
| **High Score Persistence** | Saves to file, loads on start, updates in real-time | ✅ Working |
| **Enhanced Input System** | Arrow keys + WASD support, space for hard drop | ✅ Working |
| **Score Animations** | Floating notifications with fade/slide effects | ✅ Working |
| **Main Menu** | Mode selection, instructions, exit, music controls | ✅ Working |
| **Theme System** | Purple theme (Normal), Cyan theme (Zen) with different colors | ✅ Working |
| **Wall Kick System** | Rotation attempts 7 positions when blocked | ✅ Working |
| **Timer Display** | Shows elapsed time in MM:SS format | ✅ Working |

### 🎵 Sound System Details

| Component | Description |
|-----------|-------------|
| **Background Music** | 6 tracks for Normal mode, 3 tracks for Zen mode |
| **Sound Effects** | Hard drop, line clear, level up, game over, hold piece, button clicks |
| **Music Controls** | Toggle with 'M' key, visual status indicator |
| **SFX Controls** | Toggle with 'K' key, visual status indicator |
| **Playlist Features** | Shuffle mode, automatic track progression, loop support |
| **State Management** | Music pauses on pause, changes on mode switch, game over sequence |

### ⌨️ Complete Control Scheme

| Key(s) | Action |
|--------|--------|
| **← → or A D** | Move piece left/right |
| **↑ or W** | Rotate piece |
| **↓ or S** | Soft drop (move down faster) |
| **SPACE** | Hard drop (instant drop with animation) |
| **C** | Hold piece |
| **P or ESC** | Pause/Resume |
| **N** | New game |
| **M** | Toggle music on/off |
| **K** | Toggle sound effects on/off |

### 🎨 Visual Features

| Feature | Implementation |
|---------|----------------|
| **Color Schemes** | Mode-specific colors (Purple for Normal, Cyan for Zen) |
| **Background Images** | Different backgrounds for each mode |
| **Ghost Piece** | Semi-transparent with white border, updates in real-time |
| **Rounded Corners** | Smooth rounded rectangles for bricks (6px radius) |
| **Glow Effects** | Score notifications and UI elements |
| **Animations** | Fade-in/fade-out, slide transitions for notifications |
| **Drop Shadow** | Game board has colored shadow matching theme |

---

## Implemented but Not Working Properly

**None** - All implemented features are functioning correctly.

---

## Features Not Implemented

| Feature | Reason for Omission |
|---------|---------------------|
| **Online Multiplayer** | Beyond coursework scope; requires server infrastructure and networking implementation |
| **Customizable Key Bindings** | Default WASD + Arrow keys cover most use cases; would require settings UI |
| **Multiple High Score Slots** | Simple high score tracking sufficient; would need database or complex file structure |
| **Custom Music Playlist** | Music tracks are embedded resources; would need file browser and audio format handling |
| **Combo System** | T-spin detection and combo tracking adds complexity beyond basic requirements |
| **Particle Effects** | Would require additional graphics libraries or custom particle system |
| **Achievements System** | Would need persistent achievement tracking and notification system |

---

## New Java Classes

### Control Package (`com.comp2042.game.control`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 1 | **MenuController.java** | Controls the main menu screen | Mode selection, instructions display, music initialization, navigation |

---

### Controller Package (`com.comp2042.game.controller`)

#### Animation (`com.comp2042.game.controller.animation`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 2 | **AnimationManager.java** | Manages all game timelines and animations | Game loop, instant drop animation, timer updates, level speed changes |

#### Audio (`com.comp2042.game.controller.audio`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 3 | **SoundManager.java** | Singleton managing all game audio | Music playback, sound effects, playlist management, volume control, shuffle mode |

#### Game (`com.comp2042.game.controller.game`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 4 | **BoardController.java** | Main board controller (replaces SimpleBoard) | Brick movement, collision detection, line clearing, hold mechanism, game mode support |
| 5 | **BrickRotator.java** | Manages brick rotation state | Current shape tracking, next shape calculation, extracted from original board |

#### Input (`com.comp2042.game.controller.input`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 6 | **InputHandler.java** | Centralizes all keyboard input handling | Key binding management, state-dependent input, global controls |

#### State (`com.comp2042.game.controller.state`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 7 | **GameStateController.java** | Manages game state transitions | Pause/resume, game over handling, new game initialization, overlay management |

#### UI (`com.comp2042.game.controller.ui`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 8 | **GameViewController.java** | Main coordinator for game view components | Delegates rendering, manages UI updates, binds properties |
| 9 | **BrickRenderer.java** | Renders bricks, shadows, and board | Brick drawing, ghost piece rendering, board updates |
| 10 | **GridRenderer.java** | Renders preview and hold grids | Next piece preview (3 pieces), held piece display |
| 11 | **ColorScheme.java** | Provides color schemes for game modes | Mode-specific brick colors and ghost colors |
| 12 | **GameSetupManager.java** | Handles game mode setup | Theme application, music playlist selection, UI configuration |

---

### Event Package (`com.comp2042.game.event`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 13 | **GameMode.java** | Enumeration for game modes | Values: `NORMAL`, `ZEN` |

---

### View Package (`com.comp2042.game.view`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 14 | **PauseMenu.java** | Pause menu overlay | Resume/Restart/Main Menu buttons, mode-specific styling |

---

### Utility Package (`com.comp2042.game.util`)

| # | Class | Purpose | Key Features |
|---|-------|---------|--------------|
| 15 | **HighScoreManager.java** | Manages high score persistence | Save/load high scores to/from file |

---

### Model - Brick Classes (Made Public)

| # | Classes | Purpose | Changes |
|---|---------|---------|---------|
| 16-22 | **IBrick, JBrick, LBrick, OBrick, SBrick, TBrick, ZBrick** | All 7 Tetris piece types | Changed from package-private to public for testability |

---

## Modified Java Classes

### 1. Main.java

| Aspect | Details |
|--------|---------|
| **File** | `Main.java` |
| **Original Location** | `src/com/comp2042/Main.java` |
| **New Location** | `src/com/comp2042/Main.java` |
| **Major Changes** | • **Changed:** Loads `mainMenu.fxml` instead of directly loading game<br>• **Removed:** Direct `GameController` instantiation<br>• **Added:** Menu flow - user selects mode first |
| **Rationale** | Proper game flow with menu system allowing players to choose game mode before starting |

---

### 2. GuiController.java

| Aspect | Details |
|--------|---------|
| **File** | `GuiController.java` |
| **Original Location** | `src/com/comp2042/GuiController.java` |
| **New Location** | `src/com/comp2042/game/control/GuiController.java` |
| **Major Changes** | • **Architectural Refactoring:** Transformed from monolithic controller to coordinator pattern<br>• **Delegation:** Now delegates to specialized controllers (GameViewController, AnimationManager, InputHandler, GameStateController)<br>• **Added:** Game mode support in initialization<br>• **Added:** Multiple overlay management (pause, game over)<br>• **Added:** Timer label binding and updates<br>• **Added:** Music/SFX status label updates<br>• **Removed:** Direct rendering code (delegated to renderers)<br>• **Removed:** Direct input handling (delegated to InputHandler) |
| **Rationale** | Separation of concerns - each controller now has a single, well-defined responsibility following SOLID principles |

---

### 3. GameController.java

| Aspect | Details |
|--------|---------|
| **File** | `GameController.java` |
| **Original Location** | `src/com/comp2042/GameController.java` |
| **New Location** | `src/com/comp2042/game/controller/game/GameController.java` |
| **Major Changes** | • **Added:** `GameMode` parameter in constructor<br>• **Added:** Level progression tracking (every 5 lines)<br>• **Added:** `currentLevel` field and `checkLevelUp()` method<br>• **Added:** Lines cleared tracking via `Score.linesClearedProperty()`<br>• **Added:** Zen mode special handling (board clear instead of game over)<br>• **Added:** Hold piece event handling (`onHoldEvent()`)<br>• **Added:** Sound effect on successful hold<br>• **Changed:** Uses `BoardController` instead of `SimpleBoard`<br>• **Enhanced:** `onDownEvent()` with game mode-specific logic |
| **Rationale** | Support multiple game modes with different rules and progression systems, providing varied gameplay experiences |

---

### 4. Board.java (Interface)

| Aspect | Details |
|--------|---------|
| **File** | `Board.java` |
| **Original Location** | `src/com/comp2042/Board.java` |
| **New Location** | `src/com/comp2042/game/model/board/Board.java` |
| **Major Changes** | • **Added:** `boolean holdBrick()` - Support hold mechanism<br>• **Added:** `int[][] getHeldBrickData()` - Get held piece for display<br>• **Added:** `GameMode getGameMode()` - Query current game mode<br>• **Added:** `void clearBoard()` - Clear board for Zen mode |
| **Rationale** | Extend board interface to support new gameplay features required for modern Tetris mechanics |

---

### 5. SimpleBoard.java → BoardController.java

| Aspect | Details |
|--------|---------|
| **File** | `SimpleBoard.java` → `BoardController.java` |
| **Original Location** | `src/com/comp2042/SimpleBoard.java` |
| **New Location** | `src/com/comp2042/game/controller/game/BoardController.java` |
| **Major Changes** | • **Renamed:** `SimpleBoard` → `BoardController` (better naming convention)<br>• **Added:** `GameMode gameMode` field<br>• **Added:** `Brick heldBrick` field for hold mechanism<br>• **Added:** `boolean hasSwapped` flag (one swap per piece rule)<br>• **Added:** `holdBrick()` implementation with swap logic<br>• **Added:** `getHeldBrickData()` for UI display<br>• **Added:** `clearBoard()` for Zen mode<br>• **Added:** `dropDistance()` calculation for ghost piece<br>• **Enhanced:** `getViewData()` now includes drop distance and held brick<br>• **Enhanced:** `rotateLeftBrick()` with wall kick system (7 positions)<br>• **Changed:** Board dimensions parameter to support different modes |
| **Rationale** | Support advanced Tetris features (hold, ghost, wall kicks) and multiple game modes with distinct behaviors |

---

### 6. Score.java

| Aspect | Details |
|--------|---------|
| **File** | `Score.java` |
| **Original Location** | `src/com/comp2042/Score.java` |
| **New Location** | `src/com/comp2042/game/model/board/Score.java` |
| **Major Changes** | • **Added:** `IntegerProperty linesCleared` field<br>• **Added:** `linesClearedProperty()` method for JavaFX binding<br>• **Added:** `addLines(int lines)` method<br>• **Added:** `getLinesCleared()` getter<br>• **Enhanced:** `reset()` now resets both score and lines |
| **Rationale** | Track lines cleared separately for level progression calculation and end-game statistics display |

---

### 7. ViewData.java

| Aspect | Details |
|--------|---------|
| **File** | `ViewData.java` |
| **Original Location** | `src/com/comp2042/ViewData.java` |
| **New Location** | `src/com/comp2042/game/view/ViewData.java` |
| **Major Changes** | • **Changed:** `int[][] nextBrickData` → `List<int[][]> nextBricksData` (multiple previews)<br>• **Added:** `int dropDistance` field for ghost piece positioning<br>• **Added:** `int[][] heldBrickData` field for hold display<br>• **Added:** `getDropDistance()` getter<br>• **Added:** `getHeldBrickData()` getter<br>• **Enhanced:** `getNextBricksData()` returns list with deep copies for safety |
| **Rationale** | Support displaying multiple next pieces (3 pieces preview), ghost piece shadow, and held piece in UI |

---

### 8. RandomBrickGenerator.java

| Aspect | Details |
|--------|---------|
| **File** | `RandomBrickGenerator.java` |
| **Original Location** | `src/com/comp2042/logic/bricks/RandomBrickGenerator.java` |
| **New Location** | `src/com/comp2042/game/model/brick/RandomBrickGenerator.java` |
| **Major Changes** | • **Changed:** Pure random selection → "Bag" system with shuffle<br>• **Added:** `fillBag()` method that shuffles all 7 pieces<br>• **Changed:** `nextBricks` now uses deque with shuffled bags<br>• **Added:** `getNextBricks(int count)` method for multi-piece preview<br>• **Improved:** Fair distribution - guarantees all 7 pieces appear before repeating any |
| **Rationale** | Modern Tetris standard (guideline compliant) - prevents frustrating long droughts of specific pieces, ensures fair gameplay |

---

### 9. BrickGenerator.java (Interface)

| Aspect | Details |
|--------|---------|
| **File** | `BrickGenerator.java` |
| **Original Location** | `src/com/comp2042/logic/bricks/BrickGenerator.java` |
| **New Location** | `src/com/comp2042/game/model/brick/BrickGenerator.java` |
| **Major Changes** | • **Changed:** `Brick getNextBrick()` → `List<Brick> getNextBricks(int count)`<br>• **Removed:** Single next brick method |
| **Rationale** | Support showing multiple upcoming pieces in preview grid (standard modern Tetris feature) |

---

### 10. GameOverPanel.java

| Aspect | Details |
|--------|---------|
| **File** | `GameOverPanel.java` |
| **Original Location** | `src/com/comp2042/GameOverPanel.java` |
| **New Location** | `src/com/comp2042/game/view/GameOverPanel.java` |
| **Major Changes** | • **Complete Redesign:** Changed from simple label to comprehensive statistics panel<br>• **Added:** Time display label (MM:SS format)<br>• **Added:** Score display label<br>• **Added:** High score display label (for Normal mode)<br>• **Added:** Lines cleared display label<br>• **Added:** Instruction label ("Press N for New Game")<br>• **Added:** Main menu button for navigation<br>• **Added:** `updateStats()` method to update all statistics<br>• **Added:** Styled background with rounded corners, border, and glow effect |
| **Rationale** | Provide comprehensive game over experience with full statistics and clear navigation options |

---

### 11. All Brick Classes (IBrick, JBrick, LBrick, OBrick, SBrick, TBrick, ZBrick)

| Aspect | Details |
|--------|---------|
| **Files** | `IBrick.java`, `JBrick.java`, `LBrick.java`, `OBrick.java`, `SBrick.java`, `TBrick.java`, `ZBrick.java` |
| **Original Location** | `src/com/comp2042/logic/bricks/` (package-private visibility) |
| **New Location** | `src/com/comp2042/game/model/brick/` (public visibility) |
| **Major Changes** | • **Changed:** Package-private (default) visibility → Public<br>• **Changed:** Package reorganization from `logic.bricks` to `model.brick` |
| **Rationale** | Better testability (can be accessed in test classes) and adherence to Java naming conventions |

---

### 12. Event Classes (EventType, EventSource, MoveEvent, DownData, ClearRow)

| Aspect | Details |
|--------|---------|
| **Files** | `EventType.java`, `EventSource.java`, `MoveEvent.java`, `DownData.java`, `ClearRow.java` |
| **Original Location** | `src/com/comp2042/` (root package) |
| **New Location** | `src/com/comp2042/game/event/` |
| **Major Changes** | • **Package Reorganization:** Moved to dedicated event package<br>• **No functional changes** to the classes themselves |
| **Rationale** | Better code organization and separation of concerns - all event-related classes grouped together |

---

### 13. MatrixOperations.java

| Aspect | Details |
|--------|---------|
| **File** | `MatrixOperations.java` |
| **Original Location** | `src/com/comp2042/MatrixOperations.java` |
| **New Location** | `src/com/comp2042/game/util/MatrixOperations.java` |
| **Major Changes** | • **Package Reorganization:** Moved to util package<br>• **No functional changes** to the utility methods |
| **Rationale** | Clear separation of utility code from business logic - follows standard Java package structure |

---

### 14. BrickRotator.java

| Aspect | Details |
|--------|---------|
| **File** | `BrickRotator.java` |
| **Original Location** | Logic was embedded within `SimpleBoard.java` |
| **New Location** | `src/com/comp2042/game/controller/game/BrickRotator.java` |
| **Major Changes** | • **Extracted:** Separated rotation logic from board class<br>• **Created:** New dedicated class for rotation state management<br>• **Maintains:** Current shape position and brick reference |
| **Rationale** | Single Responsibility Principle - separate rotation concerns from board game logic |

---

### 15. NotificationPanel.java

| Aspect | Details |
|--------|---------|
| **File** | `NotificationPanel.java` |
| **Original Location** | `src/com/comp2042/NotificationPanel.java` |
| **New Location** | `src/com/comp2042/game/view/NotificationPanel.java` |
| **Major Changes** | • **Package Reorganization:** Moved to view package<br>• **No functional changes** to animation logic |
| **Rationale** | Proper organization of view/UI components - all view classes grouped together |

---

### 16. NextShapeInfo.java

| Aspect | Details |
|--------|---------|
| **File** | `NextShapeInfo.java` |
| **Original Location** | `src/com/comp2042/NextShapeInfo.java` |
| **New Location** | `src/com/comp2042/game/view/NextShapeInfo.java` |
| **Major Changes** | • **Package Reorganization:** Moved to view package<br>• **No functional changes** to data structure |
| **Rationale** | This is view-related data (used for rendering), should be in view package |

---

## Unexpected Problems

### 1. JavaFX Media Player Race Conditions

| Problem | Solution | Outcome |
|---------|----------|---------|
| When rapidly toggling music on/off, `MediaPlayer` would sometimes throw `IllegalStateException` or fail to play/pause correctly | • Added proper null checks before all operations<br>• Implemented `pendingMusicTrack` string to track what music should play when unmuted<br>• Ensured `stopMusic()` properly disposes of player before creating new one<br>• Added state validation before operations | Stable music system with no crashes, smooth toggling |

---

### 2. Ghost Piece Performance Issues

| Problem | Solution | Outcome |
|---------|----------|---------|
| Calculating ghost piece drop distance every frame caused noticeable lag (5-10 FPS drop) with complex board states | • Optimized `dropDistance()` calculation algorithm<br>• Only recalculate when piece moves or rotates, not on every render frame<br>• Used early exit conditions in collision detection<br>• Cached result until next movement | Smooth 60 FPS gameplay even with full board |

---

### 3. Timeline Synchronization Conflicts

| Problem | Solution | Outcome |
|---------|----------|---------|
| Multiple timelines (game loop, timer, instant drop) would sometimes conflict, causing visual glitches like piece teleporting or double-rendering | • Centralized all timeline management in `AnimationManager` class<br>• Added `isDropping` boolean flag to prevent conflicting animations<br>• Implemented proper pause/resume for all timelines simultaneously<br>• Added synchronization locks where needed | No visual glitches, smooth animations |

---

### 4. FXML Loading and Scene Transitions

| Problem | Solution | Outcome |
|---------|----------|---------|
| Initially tried creating new `Stage` instances for menu/game transitions, causing window focus issues and music restarting unexpectedly | • Keep single `Scene` instance throughout application lifecycle<br>• Replace scene root instead of creating new scenes<br>• Properly dispose of resources when switching roots<br>• Maintain reference to stage from `Main.java` | Smooth transitions, no focus issues |

---

### 5. Wall Kick System Implementation

| Problem | Solution | Outcome |
|---------|----------|---------|
| Standard rotation (only testing original position) felt "stuck" near walls, frustrating players and not matching modern Tetris expectations | • Implemented wall kick system testing 7 positions: original, -1, +1, -2, +2, -3, +3<br>• Allows rotation to "push" piece away from walls horizontally<br>• Added to `BoardController.rotateLeftBrick()` method | Much smoother gameplay, rotation feels natural |

---

### 6. File Persistence Cross-Platform Issues

| Problem | Solution | Outcome |
|---------|----------|---------|
| `HighScoreManager` initially used absolute paths like `C:/Users/.../highscore.txt`, failing on different systems (Mac, Linux) | • Changed to relative path (`highscore.txt` in application directory)<br>• Added proper exception handling for file I/O operations<br>• Used `Files.readString()` and `Files.writeString()` for simplicity<br>• File created in current working directory | Works on Windows, Mac, and Linux |

---

### 7. Bag Randomizer Edge Cases

| Problem | Solution | Outcome |
|---------|----------|---------|
| Initial "bag" implementation could run out of pieces if game requested more pieces than available in deque | • Always fill bag when size drops below threshold (≤7 pieces)<br>• Double-fill initially (14 pieces) to ensure buffer<br>• Added size checks in `getNextBricks()` to prevent empty returns<br>• Used `Collections.shuffle()` for proper randomization | Never runs out of pieces, fair distribution |

---

### 8. Hold Mechanism Swap Exploit

| Problem | Solution | Outcome |
|---------|----------|---------|
| Players could swap pieces infinitely by repeatedly pressing 'C', effectively "choosing" their next piece | • Added `hasSwapped` boolean flag<br>• Only allow one swap per piece drop<br>• Reset flag when new piece is created<br>• Sound effect only plays on successful swap | Balanced hold mechanism, prevents exploitation |

---

### 9. Zen Mode Board Clear Timing

| Problem | Solution | Outcome |
|---------|----------|---------|
| When board filled in Zen mode, immediate clear caused visual confusion (pieces disappeared instantly without feedback) | • Added special "zen_clear" sound effect<br>• Display "BOARD CLEARED!" notification<br>• Maintained smooth transition without jarring screen flicker<br>• Player clearly understands what happened | Clear visual/audio feedback, good UX |

---

## Design Patterns Used

| Pattern | Implementation | Location | Purpose |
|---------|----------------|----------|---------|
| **MVC (Model-View-Controller)** | Clear separation: Model (Board, Score, Brick), View (UI panels, ViewData), Controller (game logic) | Throughout entire architecture | Separate concerns, maintainability |
| **Singleton** | `SoundManager.getInstance()` ensures single audio manager | `SoundManager.java` | Prevent multiple audio streams, centralized control |
| **Factory** | `RandomBrickGenerator` creates `Brick` instances | `RandomBrickGenerator.java` | Encapsulate brick creation logic |
| **Observer** | JavaFX Properties (`scoreProperty`, `linesClearedProperty`) | `Score.java`, `GuiController.java` | Automatic UI updates when data changes |
| **Strategy** | `ColorScheme` provides different strategies based on `GameMode` | `ColorScheme.java` | Different color behaviors per mode |
| **Delegation** | `GuiController` delegates to specialized controllers | `GuiController.java` | Reduce complexity, single responsibility |
| **State** | `GameStateController` manages game states (playing, paused, game over) | `GameStateController.java` | Clean state transitions |

---

## Testing Recommendations

### Manual Testing Checklist

| Category | Test Cases |
|----------|------------|
| **Game Modes** | • Test Normal mode with level progression<br>• Test Zen mode with board clear behavior<br>• Verify mode-specific colors and music |
| **Hold Mechanism** | • Hold piece with 'C' key<br>• Verify one swap per piece rule<br>• Test hold at spawn position<br>• Test hold when held piece would collide |
| **Audio System** | • Toggle music on/off during gameplay<br>• Toggle SFX on/off<br>• Verify music changes between modes<br>• Test all sound effects trigger correctly |
| **Pause/Resume** | • Pause during active gameplay<br>• Verify all timelines stop<br>• Test resume functionality<br>• Test restart from pause menu |
| **Ghost Piece** | • Verify shadow appears at correct position<br>• Test shadow updates with piece movement<br>• Verify shadow color matches theme |
| **Level Progression** | • Clear 5 lines and verify level up<br>• Test speed increase<br>• Verify level-up notification and sound |
| **High Score** | • Achieve new high score<br>• Restart game and verify persistence<br>• Test with multiple game sessions |
| **Wall Kicks** | • Rotate near left wall<br>• Rotate near right wall<br>• Rotate when blocked by other pieces<br>• Verify smooth rotation behavior |
| **Input System** | • Test all arrow keys<br>• Test all WASD keys<br>• Test space bar hard drop<br>• Test 'N' for new game<br>• Test 'P' for pause |

---

## Known Limitations

| Limitation | Description | Potential Impact |
|------------|-------------|------------------|
| **Single High Score Slot** | Only one high score saved per system | Players cannot track multiple scores or compare |
| **Fixed Key Bindings** | Cannot customize keyboard controls | Players with different preferences cannot adapt |
| **Desktop Only** | No mobile or web version | Limited platform accessibility |
| **Hardcoded Music** | Cannot add custom music tracks,Players stuck with provided tracks|
| **No Difficulty Options** | Starting speed and progression fixed | Cannot adjust for skill level |
| **Local High Score**| No online leaderboard | Cannot compare scores with other players |
| **English Only** | No internationalization/localization | Non-English speakers may struggle with instructions |

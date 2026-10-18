package FightingGame;

import javafx.scene.layout.Pane; // Kept for the placeholder `root` field (see note below).
import javafx.stage.Stage; // Used by the static adapter methods.

/**
 * Thin adapter kept for backward compatibility. (U3)
 * GameSession is the source of truth for all match logic and arena constants.
 * GameApp simply re-exports the constants and delegates the two static calls
 * that FightingGameApp makes, so FightingGameApp.java needs no changes.
 *
 * NOTE: `root` is kept as a no-op placeholder to avoid compile errors in any
 * remaining references that have not yet been cleaned up (Phase 5 / Q1 will
 * remove them). Character.constructor and ProjectileManager no longer use it.
 */
public class GameApp
{

    // Arena constants delegating to GameSession (U3: GameSession is the source of truth).
    public static final int    ARENA_WIDTH    = GameSession.ARENA_WIDTH;
    public static final int    ARENA_HEIGHT   = GameSession.ARENA_HEIGHT;
    public static final double BOUNDARY_TOP   = GameSession.BOUNDARY_TOP;
    public static final double BOUNDARY_BOTTOM = GameSession.BOUNDARY_BOTTOM;
    public static final double BOUNDARY_LEFT  = GameSession.BOUNDARY_LEFT;
    public static final double BOUNDARY_RIGHT = GameSession.BOUNDARY_RIGHT;

    // No-op placeholder; nothing in the codebase adds to it after Phase 3. (U3)
    static Pane root = new Pane();

    /**
     * Delegates to GameSession.setPrimaryStage. (U3)
     */
    public static void setPrimaryStage(Stage stage)
    {
        GameSession.setPrimaryStage(stage);
    }

    /**
     * Delegates to GameSession.startGameWithSpecs. (U2, U6)
     * Builds fresh Character instances from the specs and starts a new match.
     */
    public static void startGameWithSpecs(CharacterRoster.CharacterSpec spec1,
                                          CharacterRoster.CharacterSpec spec2,
                                          Runnable rematch, Runnable mainMenu)
    {
        GameSession.startGameWithSpecs(spec1, spec2, rematch, mainMenu);
    }

}

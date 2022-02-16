package ac.grim.grimac.utils.events;

import ac.grim.grimac.player.GrimPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GrimAlertEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GrimPlayer player;
    private final String verbose;
    private final String checkName;
    private final String violations;

    public GrimAlertEvent(GrimPlayer player, String verbose, String checkName, String violations) {
        super(true); // Async!
        this.player = player;
        this.verbose = verbose;
        this.checkName = checkName;
        this.violations = violations;
    }

    public GrimPlayer getPlayer() {
        return player;
    }

    public String getVerbose() {
        return verbose;
    }

    public String getCheckName() {
        return checkName;
    }

    public String getViolations() {
        return violations;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

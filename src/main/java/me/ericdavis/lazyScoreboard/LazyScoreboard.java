package me.ericdavis.lazyScoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class LazyScoreboard {

    private final Map<Player, Scoreboard> playerBoards = new HashMap<>();
    private final Map<Player, Objective> playerObjectives = new HashMap<>();
    private final Map<Player, LinkedHashMap<String, String>> playerStats = new HashMap<>();

    private final String title;

    public LazyScoreboard(String title) {
        this.title = ChatColor.GREEN + title;
    }

    public void setupScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("gameStats", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(board);
        playerBoards.put(player, board);
        playerObjectives.put(player, objective);
        playerStats.put(player, new LinkedHashMap<>());
    }

    /**
     * Sets a custom line on the scoreboard. The key is a unique identifier, and the value is the full line text.
     */
    public void setStat(Player player, String key, String lineText) {
        if (!playerStats.containsKey(player)) {
            setupScoreboard(player);
        }
        playerStats.get(player).put(key, lineText);
    }

    public void updateStats(Player player) {
        if (!playerBoards.containsKey(player)) {
            setupScoreboard(player);
        }

        Scoreboard board = playerBoards.get(player);
        Objective objective = playerObjectives.get(player);
        LinkedHashMap<String, String> stats = playerStats.get(player);

        // Clear existing entries
        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }

        // Re-add all custom lines
        int line = stats.size();
        for (String lineText : stats.values()) {
            objective.getScore(lineText).setScore(line--);
        }
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerBoards.remove(player);
        playerObjectives.remove(player);
        playerStats.remove(player);
    }
}

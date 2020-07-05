package com.derongan.deepestworld;

import com.derongan.deepestworld.config.WorldConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChunkFixedWindowInitializerTest {
    private ChunkFixedWindowInitializer chunkFixedWindowInitializer;

    @Before
    public void setUp() {
        WorldConfiguration worldConfiguration = new TestWorldConfiguration();
        SectionLayout sectionLayout = new LinearSectionLayout(worldConfiguration);
        CoordinateConversions coordinateConversions = new CoordinateConversions(sectionLayout);
        chunkFixedWindowInitializer = new ChunkFixedWindowInitializer(coordinateConversions, new TickCounter());
    }

    @Test
    public void calculateWindow_atOrigin() {
        Player player = mockPlayer(0, 0, 0);
        assertThat(chunkFixedWindowInitializer.calculateWindow(player).getYBottom()).isEqualTo(0);
    }

    @Test
    public void calculateWindow_atTop() {
        Player player = mockPlayer(0, 255, 0);
        assertThat(chunkFixedWindowInitializer.calculateWindow(player).getYBottom()).isEqualTo(0);
    }

    @Test
    public void calculateWindow_inSecondSection() {
        Player player = mockPlayer(67, 127, 5);
        assertThat(chunkFixedWindowInitializer.calculateWindow(player).getYBottom()).isEqualTo(256);
    }

    @Test
    public void calculateWindow_playerOutOfBounds_positive() {
        Player player = mockPlayer(0, 256, 0);
        assertThat(chunkFixedWindowInitializer.calculateWindow(player).getYBottom()).isEqualTo(256);
    }

    @Test
    public void calculateWindow_playerOutOfBounds_negative() {
        Player player = mockPlayer(0, -1, 0);
        assertThat(chunkFixedWindowInitializer.calculateWindow(player).getYBottom()).isEqualTo(0);
    }

    private static Player mockPlayer(double x, double y, double z) {
        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(new Location(null, x, y, z));
        return player;
    }
}
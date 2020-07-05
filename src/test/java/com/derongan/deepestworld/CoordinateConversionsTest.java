package com.derongan.deepestworld;

import com.derongan.deepestworld.config.WorldConfiguration;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class CoordinateConversionsTest {
    private static final ServerPosition SERVER_ORIGIN = ServerPosition.create(0, 0, 0);
    private static final DeeperPosition DEEPER_ORIGIN = DeeperPosition.create(0, 0, 0);
    private static final ServerPosition SERVER_ORIGIN_TOP = ServerPosition.create(0, 255, 0);
    private static final DeeperPosition DEEPER_ORIGIN_TOP = DeeperPosition.create(0, 255, 0);
    private static final ServerPosition SERVER_ORIGIN_SECTION_END = ServerPosition.create(63, 0, 63);
    private static final DeeperPosition DEEPER_ORIGIN_SECTION_END = DeeperPosition.create(63, 0, 63);
    private static final ServerPosition SERVER_SECOND_SECTION_BOT = ServerPosition.create(64, 0, 0);
    private static final DeeperPosition DEEPER_SECOND_SECTION_BOT = DeeperPosition.create(0, 256, 0);
    private static final ServerPosition SERVER_THIRD_SECTION_BOT = ServerPosition.create(127, 0, 0);
    private static final DeeperPosition DEEPER_THIRD_SECTION_BOT = DeeperPosition.create(63, 256, 0);
    private static final ClientPosition CLIENT_ORIGIN = ClientPosition.create(0, 0, 0);
    private static final ClientPosition CLIENT_ORIGIN_TOP = ClientPosition.create(0, 255, 0);
    private static final DeeperPosition DEEPER_HALFWAY = DeeperPosition.create(5, 383, 32);
    private static final ClientPosition CLIENT_HALFWAY_TOP = ClientPosition.create(0, 127, 0);
    private static final ClientPosition CLIENT_HALFWAY_MID = ClientPosition.create(5, 255, 32);
    private CoordinateConversions coordinateConversions;

    @Before
    public void setUp() {
        coordinateConversions = new CoordinateConversions(new LinearSectionLayout(new TestWorldConfiguration()));
    }

    @Test
    public void serverToDeeper_inOriginSection() {
        assertThat(coordinateConversions.convertToDeeper(SERVER_ORIGIN)).isEqualTo(DEEPER_ORIGIN);
        assertThat(coordinateConversions.convertToDeeper(SERVER_ORIGIN_TOP)).isEqualTo(DEEPER_ORIGIN_TOP);
        assertThat(coordinateConversions.convertToDeeper(SERVER_ORIGIN_SECTION_END)).isEqualTo(DEEPER_ORIGIN_SECTION_END);
    }

    @Test
    public void serverToDeeper_inOtherSection() {
        assertThat(coordinateConversions.convertToDeeper(SERVER_SECOND_SECTION_BOT)).isEqualTo(DEEPER_SECOND_SECTION_BOT);
        assertThat(coordinateConversions.convertToDeeper(SERVER_THIRD_SECTION_BOT)).isEqualTo(DEEPER_THIRD_SECTION_BOT);
    }

    @Test
    public void deeperToServer_inOriginSection() {
        assertThat(coordinateConversions.convertToServer(DEEPER_ORIGIN)).isEqualTo(SERVER_ORIGIN);
        assertThat(coordinateConversions.convertToServer(DEEPER_ORIGIN_TOP)).isEqualTo(SERVER_ORIGIN_TOP);
        assertThat(coordinateConversions.convertToServer(DEEPER_ORIGIN_SECTION_END)).isEqualTo(SERVER_ORIGIN_SECTION_END);
    }

    @Test
    public void deeperToServer_inOtherSection() {
        assertThat(coordinateConversions.convertToServer(DEEPER_SECOND_SECTION_BOT)).isEqualTo(SERVER_SECOND_SECTION_BOT);
        assertThat(coordinateConversions.convertToServer(DEEPER_THIRD_SECTION_BOT)).isEqualTo(SERVER_THIRD_SECTION_BOT);
    }

    @Test
    public void deeperToClient_chunkFixedWindow() {
        // Client window starting at the bottom of the first section.
        ClientWindow originWindow = ClientWindow.create(0);

        // Client window starting at the bottom of the second section.
        ClientWindow secondSectionWindow = ClientWindow.create(256);

        assertThat(coordinateConversions.convertToClient(DEEPER_ORIGIN, originWindow)).isEqualTo(CLIENT_ORIGIN);
        assertThat(coordinateConversions.convertToClient(DEEPER_ORIGIN_TOP, originWindow)).isEqualTo(CLIENT_ORIGIN_TOP);
        assertThat(coordinateConversions.convertToClient(DEEPER_SECOND_SECTION_BOT, secondSectionWindow)).isEqualTo(CLIENT_ORIGIN);
    }

    @Test
    public void deeperToClient_offsetWindow() {
        // Client window starting halfway up the first section.
        ClientWindow originHalfwayWindow = ClientWindow.create(128);

        assertThat(coordinateConversions.convertToClient(DEEPER_ORIGIN_TOP, originHalfwayWindow)).isEqualTo(CLIENT_HALFWAY_TOP);
        assertThat(coordinateConversions.convertToClient(DEEPER_HALFWAY, originHalfwayWindow)).isEqualTo(CLIENT_HALFWAY_MID);
    }

    @Test
    public void clientToDeeper_chunkFixedWindow() {
        // Client window starting at the bottom of the first section.
        ClientWindow originWindow = ClientWindow.create(0);

        // Client window starting at the bottom of the second section.
        ClientWindow secondSectionWindow = ClientWindow.create(256);

        assertThat(coordinateConversions.convertToDeeper(CLIENT_ORIGIN, originWindow)).isEqualTo(DEEPER_ORIGIN);
        assertThat(coordinateConversions.convertToDeeper(CLIENT_ORIGIN_TOP, originWindow)).isEqualTo(DEEPER_ORIGIN_TOP);
        assertThat(coordinateConversions.convertToDeeper(CLIENT_ORIGIN, secondSectionWindow)).isEqualTo(DEEPER_SECOND_SECTION_BOT);
    }

    @Test
    public void clientToDeeper_offsetWindow() {
        // Client window starting halfway up the first section.
        ClientWindow originHalfwayWindow = ClientWindow.create(128);

        assertThat(coordinateConversions.convertToDeeper(CLIENT_HALFWAY_TOP, originHalfwayWindow)).isEqualTo(DEEPER_ORIGIN_TOP);
        assertThat(coordinateConversions.convertToDeeper(CLIENT_HALFWAY_MID, originHalfwayWindow)).isEqualTo(DEEPER_HALFWAY);
    }
}
package com.derongan.deepestworld;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

public class ClientWindowTest {
    @Test
    public void create_notMultipleOf16() {
        IllegalArgumentException expected = assertThrows(IllegalArgumentException.class, () -> ClientWindow.create(17));

        assertThat(expected).hasMessageThat().contains("ClientWindow must be ChunkSegment aligned");
    }

    @Test
    public void create() {
        assertThat(ClientWindow.create(16).getYBottom()).isEqualTo(16);
    }
}
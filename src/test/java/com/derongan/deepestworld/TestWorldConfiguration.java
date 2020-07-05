package com.derongan.deepestworld;

import com.derongan.deepestworld.config.WorldConfiguration;

public class TestWorldConfiguration  implements WorldConfiguration {
    @Override
    public int sectionSizeInChunks() {
        return 4;
    }

}

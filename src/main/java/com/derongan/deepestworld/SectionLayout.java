package com.derongan.deepestworld;

import com.derongan.util.Pair;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

public interface SectionLayout {
    Section getSection(DeeperPosition deeperPosition);

    Section getSection(ServerPosition serverPosition);

    @AutoValue
    abstract class Section {
        public abstract Pair<ChunkCoord> getCoordinates();

        public abstract int getIndex();

        public static Builder builder() {
            return new AutoValue_SectionLayout_Section.Builder();
        }


        @AutoValue.Builder
        public abstract static class Builder {
            private ChunkCoord bottomLeft;
            private ChunkCoord topRight;

            abstract Builder setCoordinates(Pair<ChunkCoord> coordinate);

            abstract Builder setIndex(int index);

            public Builder setBottomLeft(ChunkCoord bottomLeft) {
                this.bottomLeft = bottomLeft;
                return this;
            }

            public Builder setTopRight(ChunkCoord topRight) {
                this.topRight = topRight;
                return this;
            }

            public abstract Section autoBuild();

            public Section build() {
                Preconditions.checkState(bottomLeft.chunkX() <= topRight.chunkX(), "Left value must <= right");
                Preconditions.checkState(bottomLeft.chunkZ() <= topRight.chunkZ(), "Bottom value must <= top");
                return setCoordinates(Pair.of(bottomLeft, topRight)).autoBuild();
            }
        }
    }
}

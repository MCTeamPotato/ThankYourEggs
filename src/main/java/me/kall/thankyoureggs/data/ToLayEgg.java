package me.kall.thankyoureggs.data;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ToLayEgg extends SavedData {
    private final Object2ObjectMap<ResourceLocation, Long2ObjectMap<LongSet>> positions = new Object2ObjectOpenHashMap<>();

    public static final String DATA_NAME = "tye_egg_positions";

    public static @NotNull ToLayEgg get(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ToLayEgg::load, ToLayEgg::new, DATA_NAME);
    }

    public void add(ResourceLocation dim, long chunk, long block) {
        positions.computeIfAbsent(dim, key -> new Long2ObjectOpenHashMap<>())
                .computeIfAbsent(chunk, key -> new LongOpenHashSet())
                .add(block);
        this.setDirty();
    }

    public void remove(ResourceLocation dim, long chunk, long block) {
        Long2ObjectMap<LongSet> chunks = positions.get(dim);
        if (chunks == null) return;
        LongSet blocks = chunks.get(chunk);
        if (blocks == null) return;
        if (blocks.isEmpty()) return;

        blocks.remove(block);
        if (blocks.isEmpty()) chunks.remove(chunk);
        if (chunks.isEmpty()) positions.remove(dim);

        this.setDirty();
    }

    public Object2ObjectMap<ResourceLocation, Long2ObjectMap<LongSet>> posMap() {
        return Object2ObjectMaps.unmodifiable(positions);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag dimList = new ListTag();

        for (Map.Entry<ResourceLocation, Long2ObjectMap<LongSet>> dimEntry : positions.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("dim", dimEntry.getKey().toString());
            ListTag chunkList = new ListTag();

            for (Long2ObjectMap.Entry<LongSet> chunkEntry : dimEntry.getValue().long2ObjectEntrySet()) {
                CompoundTag chunkTag = new CompoundTag();
                chunkTag.putLong("chunk", chunkEntry.getLongKey());
                ListTag blockList = new ListTag();
                for (long pos : chunkEntry.getValue()) {
                    blockList.add(LongTag.valueOf(pos));
                }
                chunkTag.put("blocks", blockList);
                chunkList.add(chunkTag);
            }

            dimTag.put("chunks", chunkList);
            dimList.add(dimTag);
        }

        tag.put("positions", dimList);
        return tag;
    }

    public static @NotNull ToLayEgg load(@NotNull CompoundTag tag) {
        ToLayEgg data = new ToLayEgg();
        ListTag dimList = tag.getList("positions", Tag.TAG_COMPOUND);

        for (int i = 0; i < dimList.size(); i++) {
            CompoundTag dimTag = dimList.getCompound(i);
            ResourceLocation dim = ResourceLocation.parse(dimTag.getString("dim"));
            Long2ObjectMap<LongSet> chunkMap = new Long2ObjectOpenHashMap<>();

            ListTag chunkList = dimTag.getList("chunks", Tag.TAG_COMPOUND);
            for (int j = 0; j < chunkList.size(); j++) {
                CompoundTag chunkTag = chunkList.getCompound(j);
                long chunk = chunkTag.getLong("chunk");
                LongSet blocks = new LongOpenHashSet();
                ListTag blockList = chunkTag.getList("blocks", Tag.TAG_LONG);
                for (Tag value : blockList) {
                    blocks.add(((LongTag) value).getAsLong());
                }
                chunkMap.put(chunk, blocks);
            }

            data.positions.put(dim, chunkMap);
        }

        return data;
    }
}

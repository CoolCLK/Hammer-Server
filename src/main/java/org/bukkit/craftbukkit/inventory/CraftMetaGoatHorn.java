package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.meta.GoatHornMeta;
import org.jetbrains.annotations.Nullable;

@DelegateDeserialization(CraftMetaItem.SerializableMeta.class)
public class CraftMetaGoatHorn extends CraftMetaItem implements GoatHornMeta {
    static final ItemMetaKey GOATHORN_INSTRUMENT = new ItemMetaKey("instrument");
    private Instrument instrument;

    CraftMetaGoatHorn(CraftMetaItem meta) {
        super(meta);

        if (meta instanceof CraftMetaGoatHorn) {
            CraftMetaGoatHorn craftMetaGoatHorn = (CraftMetaGoatHorn) meta;
            this.instrument = craftMetaGoatHorn.instrument;
        }
    }

    CraftMetaGoatHorn(NBTTagCompound tag) {
        super(tag);

        if (tag.contains(GOATHORN_INSTRUMENT.NBT)) {
            String string = tag.getString(GOATHORN_INSTRUMENT.NBT);
            this.instrument = Instrument.valueOf(NamespacedKey.minecraft(string).toString());
        }
    }

    CraftMetaGoatHorn(Map<String, Object> map) {
        super(map);

        String instrumentString = SerializableMeta.getString(map, GOATHORN_INSTRUMENT.BUKKIT, true);
        if (instrumentString != null) {
            this.instrument = Instrument.valueOf(instrumentString.toUpperCase());
        }

    }

    @Override
    void applyToItem(NBTTagCompound tag) {
        super.applyToItem(tag);

        tag.putString(GOATHORN_INSTRUMENT.NBT, instrument.getKey().toString());
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
            case GOAT_HORN:
                return true;
            default:
                return false;
        }
    }


    @Override
    boolean equalsCommon(CraftMetaItem that) {
        return super.equalsCommon(that);
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta);
    }


    @Override
    boolean isEmpty() {
        return super.isEmpty() && isInstrumentEmpty();
    }

    boolean isInstrumentEmpty() {
        return instrument == null;
    }


    @Override
    int applyHash() {
        final int orginal;
        int hash = orginal = super.applyHash();

        if (hasInstrument()) {
            hash = 61 * hash + instrument.ordinal();
        }

        return orginal != hash ? CraftMetaGoatHorn.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaGoatHorn clone() {
        CraftMetaGoatHorn meta = (CraftMetaGoatHorn) super.clone();
        meta.instrument = this.instrument;
        return meta;
    }


    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasInstrument()) {
            builder.put(GOATHORN_INSTRUMENT.BUKKIT, instrument.getKey().toString());
        }

        return builder;
    }


    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    public boolean hasInstrument() {
        return instrument != null;
    }

    @Override
    public void setInstrument(@Nullable Instrument instrument) {
        Validate.notNull(instrument, "Instrument cannot be null");
        this.instrument = instrument;
    }

}

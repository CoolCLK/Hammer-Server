package org.bukkit.craftbukkit.legacy;

import static org.junit.jupiter.api.Assertions.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.support.AbstractTestingBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutoExpandMaxAbsorptionCompatibilityTest extends AbstractTestingBase {

    private CraftAttributeInstance attributeInstance;

    @BeforeEach
    public void setup() {
        attributeInstance = new CraftAttributeInstance(new AttributeModifiable(CraftAttribute.bukkitToMinecraftHolder(Attribute.GENERIC_MAX_ABSORPTION), x -> { }), Attribute.GENERIC_MAX_ABSORPTION);
    }

    @Test
    public void testNoModifiers() {
        test(20);
    }

    @Test
    public void testSingleAddNumber() {
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));

        test(20);
    }

    @Test
    public void testMultipleAddNumber() {
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 15.3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));

        test(30);
    }

    @Test
    public void testSingleAddScalarWithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 2, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));

        test(20);
    }

    @Test
    public void testMultipleAddScalarWithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 2, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -3.14, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -0.5, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 0.23, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));

        test(20);
    }

    @Test
    public void testSingleMultiplyScalar1WithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));

        test(20);
    }

    @Test
    public void testMultipleMultiplyScalar1WithBaseValue() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -3.14, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -0.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 0.23, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));

        test(20);
    }

    @Test
    public void testMixed() {
        attributeInstance.setBaseValue(5);
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 12.3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 2, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 2, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), 0.23, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -3.14, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), -0.14, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));

        test(99);
    }

    private void test(double target) {
        double toAdd = AutoExpandMaxAbsorptionCompatibility.calculateTarget(attributeInstance.getBaseValue(), target, attributeInstance.getModifiers());
        attributeInstance.addModifier(new AttributeModifier(NamespacedKey.randomKey(), toAdd, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));

        assertEquals(target, attributeInstance.getValue(), 0.0001f);
    }
}

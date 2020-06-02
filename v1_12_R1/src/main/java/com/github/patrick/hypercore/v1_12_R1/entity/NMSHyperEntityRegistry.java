/*
 * Copyright (C) 2020 PatrickKR
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.hypercore.v1_12_R1.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes", "SuspiciousMethodCalls"})
class NMSHyperEntityRegistry extends RegistryMaterials {

    private static NMSHyperEntityRegistry instance = null;

    private final BiMap<MinecraftKey, Class<? extends Entity>> customEntities = HashBiMap.create();
    private final BiMap<Class<? extends Entity>, MinecraftKey> customEntityClasses = customEntities.inverse();
    private final Map<Class<? extends Entity>, Integer> customEntityIds = new HashMap<>();

    private final RegistryMaterials wrapped;

    private NMSHyperEntityRegistry(RegistryMaterials original) {
        wrapped = original;
    }

    @Nullable
    public static NMSHyperEntityRegistry getInstance() {
        if (instance != null) return instance;

        instance = new NMSHyperEntityRegistry(EntityTypes.b);

        try {
            Field registryMaterialsField = EntityTypes.class.getField("b");
            registryMaterialsField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(registryMaterialsField, registryMaterialsField.getModifiers() & ~Modifier.FINAL);

            registryMaterialsField.set(null, instance);
        } catch (Exception e) {
            instance = null;

            throw new RuntimeException("Unable to override the old entity RegistryMaterials", e);
        }

        return instance;
    }

    public void putCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
        MinecraftKey minecraftKey = new MinecraftKey(entityName);

        customEntities.put(minecraftKey, entityClass);
        customEntityIds.put(entityClass, entityId);
    }

    @Override
    public Class<? extends Entity> get(Object key) {
        if (customEntities.containsKey(key))
            return customEntities.get(key);

        return (Class<? extends Entity>) wrapped.get(key);
    }

    @Override
    public int a(Object key) {
        if (customEntityIds.containsKey(key))
            return customEntityIds.get(key);

        return wrapped.a(key);
    }

    @Override
    public MinecraftKey b(Object value) {
        if (customEntityClasses.containsKey(value))
            return customEntityClasses.get(value);

        return (MinecraftKey) wrapped.b(value);
    }
}
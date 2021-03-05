package cf.pixelinc.entities

import cf.pixelinc.entities.pathfinders.PathfinderGoalAttackUninfected
import cf.pixelinc.entities.pathfinders.PathfinderGoalNearestAttackableUninfected
import net.minecraft.server.v1_16_R3.*
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.attribute.CraftAttributeMap
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity
import org.bukkit.entity.Entity
import java.lang.reflect.Field

/*
    A generic infected entity with some basic properties.
    They:
     - Have a name tag that says they're infected.
     - Follows around the player or passive mob (if they're not infected) and attacks them
 */
class InfectedEntity(loc: Location, entity : EntityTypes<out net.minecraft.server.v1_16_R3.Entity>) : EntityCreature(entity as EntityTypes<out EntityCreature>?, (loc.world as CraftWorld).handle) {
    private val attributeField: Field

    init {
        this.setPosition(loc.x, loc.y, loc.z)

        this.health = 20.0f
        this.customName = ChatComponentText("${ChatColor.RED}Infected")
        this.customNameVisible = true

        attributeField = AttributeMapBase::class.java.getDeclaredField("b")
        attributeField.isAccessible = true

        registerGenericAttribute(this.bukkitEntity, Attribute.GENERIC_ATTACK_DAMAGE)
        registerGenericAttribute(this.bukkitEntity, Attribute.GENERIC_FOLLOW_RANGE)
    }

    // Modifies an entity to include new attributes
    @Suppress("UNCHECKED_CAST")
    private fun registerGenericAttribute(entity : Entity, attribute : Attribute) {
        val mapBase : AttributeMapBase = (entity as CraftLivingEntity).handle.attributeMap

        val map = attributeField.get(mapBase) as MutableMap<AttributeBase, AttributeModifiable>
        val attributeBase = CraftAttributeMap.toMinecraft(attribute)
        val attributeModifiable = AttributeModifiable(attributeBase, AttributeModifiable::getAttribute)
        map[attributeBase] = attributeModifiable
    }

    override fun initPathfinder() {
        super.initPathfinder()

        this.attributeMap.b().add(AttributeModifiable(GenericAttributes.ATTACK_DAMAGE) { a -> a.value = 1.0 })
        this.attributeMap.b().add(AttributeModifiable(GenericAttributes.FOLLOW_RANGE) { a -> a.value = 1.0 })

        // target any living entity
        this.targetSelector.a(0, PathfinderGoalNearestAttackableUninfected(this, EntityHuman::class.java, true))
        this.targetSelector.a(1, PathfinderGoalNearestAttackableUninfected(this, EntityAnimal::class.java, true))

        //this.goalSelector.a(0, PathfinderGoalMeleeAttack(this, 1.0, false))
        this.goalSelector.a(0, PathfinderGoalMeleeAttack(this, 1.0, false))
    }
}
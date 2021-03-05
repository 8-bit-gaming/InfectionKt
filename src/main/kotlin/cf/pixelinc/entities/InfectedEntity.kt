package cf.pixelinc.entities

import cf.pixelinc.entities.pathfinders.PathfinderGoalAttackUninfected
import net.minecraft.server.v1_16_R3.*
import org.bukkit.ChatColor
import org.bukkit.Location
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
class InfectedEntity<M : EntityAgeable>(loc: Location, entityType : EntityTypes<out M>) : EntityAgeable(entityType, (loc.world as CraftWorld).handle) {
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
        this.attributeMap.b().add(AttributeModifiable(GenericAttributes.ATTACK_DAMAGE) { a -> a.value = 1.0 })
        this.attributeMap.b().add(AttributeModifiable(GenericAttributes.FOLLOW_RANGE) { a -> a.value = 1.0 })

        // target humans, animals and villagers
        this.targetSelector.a(0, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
        this.targetSelector.a(1, PathfinderGoalNearestAttackableTarget(this, EntityAnimal::class.java, true))
        this.targetSelector.a(2, PathfinderGoalNearestAttackableTarget(this, EntityVillager::class.java, true))

        //this.goalSelector.a(0, PathfinderGoalMeleeAttack(this, 1.0, false))
        this.goalSelector.a(0, PathfinderGoalAttackUninfected(this, 1.0, false))
        this.goalSelector.a(1, PathfinderGoalFloat(this))
        this.goalSelector.a(2, PathfinderGoalLookAtPlayer(this, EntityHuman::class.java, 8.0F))
        this.goalSelector.a(5, PathfinderGoalRandomStrollLand(this, 1.0))
        this.goalSelector.a(7, PathfinderGoalRandomLookaround(this))
    }

    @Suppress("UNCHECKED_CAST")
    override fun createChild(p0: WorldServer?, p1: EntityAgeable?): M {
        return (entityType.a(p0) as M)
    }
}
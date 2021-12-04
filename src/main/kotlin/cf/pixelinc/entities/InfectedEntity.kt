package cf.pixelinc.entities

import cf.pixelinc.entities.pathfinders.PathfinderGoalNearestAttackableUninfected

import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.ai.attributes.AttributeBase
import net.minecraft.world.entity.ai.attributes.AttributeMapBase
import net.minecraft.world.entity.ai.attributes.AttributeModifiable
import net.minecraft.world.entity.ai.attributes.GenericAttributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.animal.EntityAnimal
import net.minecraft.world.entity.player.EntityHuman
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.attribute.CraftAttributeMap
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity
import org.bukkit.entity.Entity
import java.lang.reflect.Field

/*
    A generic infected entity with some basic properties.
    They:
     - Have a name tag that says they're infected.
     - Follows around the player or passive mob (if they're not infected) and attacks them
 */
@Suppress("UNCHECKED_CAST")
class InfectedEntity(loc: Location, entity : EntityTypes<out net.minecraft.world.entity.Entity>) : EntityCreature(entity as EntityTypes<out EntityCreature>, (loc.world as CraftWorld).handle) {
    private val attributeField: Field

    init {
        // set position function
        this.o(loc.x, loc.y, loc.z)

        // set health function
        this.c(20.0f)
        this.bukkitEntity.customName = "${ChatColor.RED}Infected"
        this.bukkitEntity.isCustomNameVisible = true

        attributeField = AttributeMapBase::class.java.getDeclaredField("b")
        attributeField.isAccessible = true

        registerGenericAttribute(this.bukkitEntity, Attribute.GENERIC_ATTACK_DAMAGE)
        registerGenericAttribute(this.bukkitEntity, Attribute.GENERIC_FOLLOW_RANGE)
    }

    // Modifies an entity to include new attributes
     @Suppress("UNCHECKED_CAST")
     private fun registerGenericAttribute(entity : Entity, attribute : Attribute) {
         val mapBase : AttributeMapBase = (entity as CraftLivingEntity).handle.ep()
         val map = attributeField.get(mapBase) as MutableMap<AttributeBase, AttributeModifiable>
         val attributeBase = CraftAttributeMap.toMinecraft(attribute)
         val attributeModifiable = AttributeModifiable(attributeBase, AttributeModifiable::a)
         map[attributeBase] = attributeModifiable
     }

    // initPathfinder
    override fun u() {
        // attack damage
        this.getAttributeMap().b().add(AttributeModifiable(GenericAttributes.f) { a -> a.a(1.0) })

        // follow range
        this.getAttributeMap().b().add(AttributeModifiable(GenericAttributes.b) { a -> a.a(1.0) })

        this.getTargetSelector().a(1, PathfinderGoalNearestAttackableUninfected(this, EntityHuman::class.java, true))
        this.getTargetSelector().a(2, PathfinderGoalNearestAttackableUninfected(this, EntityAnimal::class.java, true))

        this.getGoalSelector().a(1, PathfinderGoalMeleeAttack(this, 1.0, false))
        this.getGoalSelector().a(2, PathfinderGoalFloat(this))
        this.getGoalSelector().a(3, PathfinderGoalRandomLookaround(this ))
        this.getGoalSelector().a(3, PathfinderGoalRandomStrollLand(this, 1.0))
        this.getGoalSelector().a(4, PathfinderGoalPanic(this, 1.25))
        this.getGoalSelector().a(5, PathfinderGoalLookAtPlayer(this, EntityHuman::class.java, 4.0f))
    }

    private fun getGoalSelector(): PathfinderGoalSelector {
        return this.bR
    }

    private fun getTargetSelector(): PathfinderGoalSelector {
        return this.bS
    }

    private fun getAttributeMap(): AttributeMapBase {
        return this.ep()
    }
}
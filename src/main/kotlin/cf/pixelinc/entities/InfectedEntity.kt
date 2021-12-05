package cf.pixelinc.entities

import cf.pixelinc.entities.pathfinders.PathfinderGoalNearestAttackableUninfected
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.player.Player
import org.bukkit.attribute.Attribute
import org.bukkit.ChatColor
import org.bukkit.Location
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
class InfectedEntity(loc: Location, entity : EntityType<out net.minecraft.world.entity.Entity>) : PathfinderMob(entity as EntityType<out PathfinderMob>, (loc.world as CraftWorld).handle) {
    private val attributeField: Field

    init {
        this.setPos(loc.x, loc.y, loc.z)
        this.yRot = loc.yaw
        this.xRot = loc.pitch
        this.health = 20.0f

        this.bukkitEntity.customName = "${ChatColor.RED}Infected"
        this.bukkitEntity.isCustomNameVisible = true

        attributeField = AttributeMap::class.java.getDeclaredField("b")
        attributeField.isAccessible = true

        registerGenericAttribute(this.bukkitEntity, Attribute.GENERIC_ATTACK_DAMAGE)
        registerGenericAttribute(this.bukkitEntity, Attribute.GENERIC_FOLLOW_RANGE)
    }

    // Modifies an entity to include new attributes
     @Suppress("UNCHECKED_CAST")
     private fun registerGenericAttribute(entity : Entity, attribute : Attribute) {
         val mapBase : AttributeMap = (entity as CraftLivingEntity).handle.attributes
         val map = attributeField.get(mapBase) as MutableMap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeInstance>
         val attributeBase = CraftAttributeMap.toMinecraft(attribute)
         val attributeModifiable = AttributeInstance(attributeBase, AttributeInstance::getAttribute)
         map[attributeBase] = attributeModifiable
     }

    override fun registerGoals() {
        this.getAttributeMap().add(AttributeInstance(Attributes.ATTACK_DAMAGE) { a -> a.baseValue = 1.0 })
        this.getAttributeMap().add(AttributeInstance(Attributes.FOLLOW_RANGE) { a -> a.baseValue = 1.0 })

        this.getTargetSelector().addGoal(1, PathfinderGoalNearestAttackableUninfected(this, Player::class.java, true))
        this.getTargetSelector().addGoal(2, PathfinderGoalNearestAttackableUninfected(this, Animal::class.java, true))

        this.getGoalSelector().addGoal(1, MeleeAttackGoal(this, 1.0, false))
        this.getGoalSelector().addGoal(2, FloatGoal(this))
        this.getGoalSelector().addGoal(3, RandomLookAroundGoal(this ))
        this.getGoalSelector().addGoal(3, RandomStrollGoal(this, 1.0))
        this.getGoalSelector().addGoal(4, PanicGoal(this, 1.25))
        this.getGoalSelector().addGoal(5, LookAtPlayerGoal(this, Player::class.java, 4.0f))
    }

    private fun getGoalSelector(): GoalSelector {
        return this.goalSelector
    }

    private fun getTargetSelector(): GoalSelector {
        return this.targetSelector
    }

    private fun getAttributeMap(): MutableCollection<AttributeInstance> {
        return this.attributes.syncableAttributes
    }
}
package org.ja13.eau.misc

import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11

enum class VoltageTier(val voltageLevel: String, val voltage: Double) {
    NEUTRAL("neutral", 0.0),
    TTL("ttl", 5.0),
    LOW("low", 12.0),
    LOW_HOUSEHOLD("low_household", 120.0),
    HIGH_HOUSEHOLD("high_household", 240.0),
    INDUSTRIAL("industrial", 480.0),
    SUBURBAN_GRID("suburban_grid", 13_200.0),
    DISTRIBUTION_GRID("distribution_grid", 55_000.0),
    HIGH_TENSION_GRID("high_tension_grid", 125_000.0);

}
class VoltageTierHelpers {
    companion object {
        fun drawIconBackground(type: IItemRenderer.ItemRenderType?, tier: VoltageTier) {
            if (type == null) return
            if (!org.ja13.eau.EAU.noVoltageBackground && type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.FIRST_PERSON_MAP) {
                UtilsClient.drawIcon(type, ResourceLocation("eau", "textures/voltages/${tier.voltageLevel}.png"))
            }
        }

        fun fromVoltage(voltage: Double): VoltageTier {
            return when {
                voltage <= VoltageTier.NEUTRAL.voltage -> VoltageTier.NEUTRAL
                voltage <= VoltageTier.TTL.voltage -> VoltageTier.TTL
                voltage <= VoltageTier.LOW.voltage -> VoltageTier.LOW
                voltage <= VoltageTier.LOW_HOUSEHOLD.voltage -> VoltageTier.LOW_HOUSEHOLD
                voltage <= VoltageTier.HIGH_HOUSEHOLD.voltage -> VoltageTier.HIGH_HOUSEHOLD
                voltage <= VoltageTier.INDUSTRIAL.voltage -> VoltageTier.INDUSTRIAL
                voltage <= VoltageTier.SUBURBAN_GRID.voltage -> VoltageTier.SUBURBAN_GRID
                voltage <= VoltageTier.DISTRIBUTION_GRID.voltage -> VoltageTier.DISTRIBUTION_GRID
                voltage <= VoltageTier.HIGH_TENSION_GRID.voltage -> VoltageTier.HIGH_TENSION_GRID
                else -> VoltageTier.NEUTRAL
            }
        }

        fun setGLColor(tier: VoltageTier) {
            when (tier) {
                VoltageTier.NEUTRAL -> GL11.glColor3f(0.9f, 0.9f, 0.9f)
                VoltageTier.TTL -> GL11.glColor3f(.80f, .87f, .82f)
                VoltageTier.LOW -> GL11.glColor3f(.80f, .87f, .82f)
                VoltageTier.LOW_HOUSEHOLD -> GL11.glColor3f(.96f, .80f, .56f)
                VoltageTier.HIGH_HOUSEHOLD -> GL11.glColor3f(.96f, .80f, .56f)
                VoltageTier.INDUSTRIAL -> GL11.glColor3f(.86f, .58f, .55f)
                else -> GL11.glColor3f(.55f, .74f, .85f)
            }
        }
    }
}

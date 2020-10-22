package org.ja13.eau.integration.waila

import cpw.mods.fml.common.Optional
import mcp.mobius.waila.api.IWailaRegistrar

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaRegistrar", modid = "Waila")
object WailaIntegration {

    @JvmStatic
    fun callbackRegister(registrar: IWailaRegistrar) {
        val transparentNodeProvider = TransparentNodeWailaProvider()
        val sixNodeProvider = SixNodeWailaProvider()
        val ghostNodeProvider = GhostNodeWailaProvider(transparentNodeProvider, sixNodeProvider)

        registrar.registerBodyProvider(transparentNodeProvider, org.ja13.eau.node.transparent.TransparentNodeBlock::class.java)

        registrar.registerHeadProvider(ghostNodeProvider, org.ja13.eau.ghost.GhostBlock::class.java)
        registrar.registerBodyProvider(ghostNodeProvider, org.ja13.eau.ghost.GhostBlock::class.java)
        registrar.registerStackProvider(ghostNodeProvider, org.ja13.eau.ghost.GhostBlock::class.java)

        registrar.registerHeadProvider(sixNodeProvider, org.ja13.eau.node.six.SixNodeBlock::class.java)
        registrar.registerBodyProvider(sixNodeProvider, org.ja13.eau.node.six.SixNodeBlock::class.java)
        registrar.registerStackProvider(sixNodeProvider, org.ja13.eau.node.six.SixNodeBlock::class.java)
    }
}

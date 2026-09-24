# FTB Fabricator art sources

- `ftb_fabricator.bbmodel`: GeckoLib block model and idle/working animations.
- `ftb_fabricator.aseprite`: layered 256×256 model atlas.
- `ftb_fabricator_gui.aseprite`: layered GUI atlas, with a 220×214 panel.

The Blockbench file embeds its texture. Runtime exports live in
`src/main/resources/assets/ftbevolutioncompanion/`: models and animations under
`geckolib/`, model textures under `textures/block/`, and the GUI under
`textures/gui/container/`. The code renders titles, gauges, and the projected item.

Geometry and all animation poses stay inside one block. The six named bones
control the housing, emitters, projector, and cooling fan. The texture uses twelve
colors from the Minecraft tech/glow palette library, with hard pixel edges and
upper-left highlights. The cyan glow mask is exported separately.

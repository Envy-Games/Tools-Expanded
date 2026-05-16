Tools Expanded
=======

Tools Expanded adds utility tools, paint tools, mining enchantments, and a throwable Potion of Weakening for temporarily mining bedrock.

Potion of Weakening
==========

The Splash Potion of Weakening temporarily weakens nearby blocks in the `egtools:weakenable_bedrock` block tag. While a tagged block is weakened, a player can mine it with a netherite pickaxe or Netherite Paxel. An unenchanted tool destroys the block without drops; Fortune has no effect; Silk Touch drops exactly one copy of the block during the weakening window.

Brewing recipe:

```
Awkward Potion + Crying Obsidian -> Potion of Weakening
Potion of Weakening + Gunpowder -> Splash Potion of Weakening
```

Weakening Tag Integration
==========

Other mods can opt their own bedrock-like blocks into the potion by adding them to the block tag:

```
egtools:weakenable_bedrock
```

Create a data file at `data/egtools/tags/block/weakenable_bedrock.json` in your mod or datapack:

```json
{
  "replace": false,
  "values": [
    "yourmod:your_bedrock_block"
  ]
}
```

Tagged blocks become temporarily mineable when hit by the potion. They require a vanilla netherite pickaxe or Tools Expanded Netherite Paxel, drop nothing without Silk Touch, ignore Fortune, and drop the block item from `Block#asItem()` when mined with Silk Touch.

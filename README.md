The "Space Distortion Mod" is a Minecraft mod destined be pretty awesome if I finish it.

###...Why?
The main concept is to indroduce 2 main things that will enhance you MC experience, both having to do with, well, distorting space. What does this
mean? Well, the first (and my personal favorite) is the addition of the [Stargate](http://en.wikipedia.org/wiki/Starga
te), interplanetary portals that can "dial" to each other and establish a short-lived, wormhole. The second would enable you to build structures
that are bigger on the inside, inspired by the [TARDIS](http://en.wikipedia.org/wiki/TARDIS).

###More about this mod's Stargate
- Each Stargate will have an "address" made up of 7 symbols based on the its coordinates
- Stargates will be one-way
- Will be able to stay open for a max of 512 seconds, or 8.53̅ minutes (as opposed to the 38 minutes in the TV series)
- Ancient ruins will possibly be implemented with 1 stargate, lots of loot, and a chance of a cartouche with many other valid gate addresses
etched onto it

###Building from source with Forge and MCP
First, get a working copy of Minecraft Forge (Forge includes MCP so don't worry about obtaining that for yourself); all you need to do is download
it and and run the decompile script. The rest will be done there for you.
Next, you need to create a folder in forge/mcp/src/minecraft called jw, then copy the git repo into that folder, making sure it is named
spacedistortion (no caps). You should now have the path: forge/mcp/src/minecraft/jw/spacedistortion/{modstuff}. But we're not quite done yet --
you need the assets. This involves either simply moving forge/mcp/src/minecraft/jw/spacedistortion/assets/ to forge/mcp/src/minecraft/assets/ (or
creating a symlink). Now, try opening the Eclipse workspace (forge/mcp/eclipse) and running the project!
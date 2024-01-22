# Replay Interpolator: A tool for large scale time-lapses
Replay interpolator is a tool to render multiple [Replay Mod](https://www.replaymod.com/) files in a final camera path preserving the in-game
time coherence between files.

## How to use it:
- Record your replays
- The camera path you want in the first replay (REMEMBER HOW LONG IT IS, YOU WILL NEED IT LATER)
- First, set up a folder in which you will be working
- Inside that folder, put the latest ReplayInterpolator.jar release and a folder called `replays`
- Put all the replays you want in the `replays` folder (The replays order of the final time-lapse will be in alphabetical order), have a copy of the replays because they may corrupt in the process
- Open the folder in terminal and run with `java -jar <name of the jar>`
- Insert the camera path duration of earlier
- All the time keyframes have been put and the replays are done for rendering
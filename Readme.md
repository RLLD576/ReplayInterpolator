# Replay Interpolator: A tool for large scale time-lapses
Replay interpolator is a tool to render multiple [Replay Mod](https://www.replaymod.com/)
files in a final camera path preserving the in-game
time coherence between files.

## How to use it:
- Record your replays
- The camera path you want in the first replay (REMEMBER HOW LONG IT IS, YOU WILL NEED IT LATER)
- First, set up a folder in which you will be working
- Inside that folder, put the latest ReplayInterpolator.jar release and a folder called `replays`
- Put all the replays you want in the `replays` folder (The replays order of the final time-lapse
- will be in alphabetical order), have a copy of the replays because they may corrupt in the process
- Open the folder in terminal and run with `java -jar <name of the jar>`
- Insert the camera path duration of earlier
- The program will output a txt file, save it for later, it will be the input for automatically merge the videos
- All the time keyframes have been put and the replays are done for rendering
- Go to minecraft and render all the videos as usual, it will output many videos with a lot of frozen
content
- For automatically edit the videos, setup in another folder all the videos, you will need ffmpeg installed, but as you are using replay mod you will have it installed
- Move the `out.txt` generated to that folder
- Now you have two options:
  - Run the command `ffmpeg -f concat -i out.txt out.mp4` on the cmd in the same folder of all the videos with the txt
  - Create another txt file with the command and rename it as a `.bat`, now you can execute the command by double-clicking the file
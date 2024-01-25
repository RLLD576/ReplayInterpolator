import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReplayInterpolator{
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Unzipping replays...");
        File recordingsFolder = new File("replays/");
        File[] replaysZips = recordingsFolder.listFiles((dir, name) -> name.endsWith(".mcpr"));
        assert replaysZips != null;
        Arrays.sort(replaysZips);
        for (File replay : replaysZips) {
                replay.renameTo(new File("replays/"+replay.getName()+".zip"));
                try{
                UnzipUtility.unzip(replay.getPath()+".zip",replay.getPath());
                if (!new File(replay.getPath()+".zip").delete())System.out.println("Cannot delete old files, idk why I'm writing this because later it will have many more errors");
                } catch (Exception e){System.out.println(e.getMessage());}
            }
        System.out.println("Insert final time-lapse duration in seconds:");
        Scanner scanner = new Scanner(System.in);
        long desiredFinalDuration = scanner.nextLong()*1000L;
        scanner.close();
        long summedReplayDurations = 0;
        Map<String,Long> durationsMap = new HashMap<>();
        JSONParser parser = new JSONParser();
        System.out.println("Getting replays durations...");
        for(File folder: Objects.requireNonNull(recordingsFolder.listFiles((dir, name) -> !name.endsWith(".zip")))){
            FileReader reader = new FileReader(folder.getPath() + "/metaData.json");
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            long duration = (long) jsonObject.get("duration")-1000L;
            reader.close();
            summedReplayDurations += duration;
            durationsMap.put(folder.getName(),duration);
        }
        File[] replays = recordingsFolder.listFiles((dir, name) -> !name.endsWith(".zip"));
        assert replays != null;
        Arrays.sort(replays);
        System.out.println("Replays Order used:");
        File first = replays[0];
        for(File replay : replays)System.out.println(replay.getName() + (replay.getName().equals(first.getName())?" <- First":""));
        JSONObject cameraTimeline = new JSONObject();
        try {
            FileReader reader = new FileReader(first.getPath() + "/timelines.json");
            JSONArray firstTimelinesJSON = (JSONArray) ((JSONObject) parser.parse(reader)).get("");
            reader.close();
            cameraTimeline = (JSONObject) firstTimelinesJSON.get(1);
        }catch (FileNotFoundException e){
            System.out.println("First file doesn't have any timeline");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        long timePassedS = 0;
        double previousout = 0;
        FileWriter textWriter = new FileWriter("out.txt");
        for(File replay : replays){
            if(new File(replay.getPath()+"/timelines.json").delete()&& !(replay.getName().equals(first.getName())))System.out.println("Replay "+replay.getName()+" had keyframes and had been deleted");
            JSONObject timeTimeline = new JSONObject();
            JSONArray content = new JSONArray();
            long replayDurationS = durationsMap.get(replay.getName());
            double equivalentSeconds = ((float)replayDurationS/(float)summedReplayDurations)*(float)desiredFinalDuration;
            long[][] data = {{timePassedS,1000},{(long) (timePassedS+equivalentSeconds), replayDurationS+1000}};
            if(!replay.getName().equals(first.getName()))textWriter.write("outpoint "+previousout/1000L+"\n");
            textWriter.write("file "+getNameWithoutExtension(replay.getName())+".mp4\n");
            if(!replay.getName().equals(first.getName()))textWriter.write("inpoint "+((double)timePassedS/1000L)+"\n");
            previousout = timePassedS+equivalentSeconds;
            timePassedS += (long) equivalentSeconds;
            for(long[] l : data)content.add(getKeyframeJson(l[0],l[1]));
            JSONArray array = new JSONArray();
            timeTimeline.put("keyframes",content);
            JSONArray segmentsContent = new JSONArray();
            segmentsContent.add(0);
            timeTimeline.put("segments",segmentsContent);
            JSONObject interpolatorsContent = (JSONObject) parser.parse("{\"type\":\"linear\",\"properties\":[\"timestamp\"]}");
            JSONArray interpolatorsArray = new JSONArray();
            interpolatorsArray.add(interpolatorsContent);
            timeTimeline.put("interpolators",interpolatorsArray);
            array.add(timeTimeline);
            array.add(cameraTimeline);
            JSONObject timelines = new JSONObject();
            timelines.put("",array);
            //System.out.println(timelines.toJSONString());
            FileWriter writer = new FileWriter(replay.getPath()+"/timelines.json");
            writer.write(timelines.toJSONString());
            writer.close();
            zipFilesInsideFolder(replay);
            deleteDirectory(replay);
            File finalReplay = new File("replays/"+replay.getName()+".zip");
            finalReplay.renameTo(replay);
        }
        textWriter.close();
    }
    public static String getNameWithoutExtension(String name) {
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex != -1) {
            return name.substring(0, lastIndex);
        } else {
            return name;
        }
    }
    static JSONObject getKeyframeJson(long time,long timestamp){
        JSONObject json = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse("{\"time\":"+time+",\"properties\":{\"timestamp\":"+timestamp+"}}");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return json;
    }
    static void zipFilesInsideFolder(File folder) throws IOException {
        File[] files = folder.listFiles();
        final FileOutputStream fos = new FileOutputStream(folder.getPath()+".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        assert files != null;
        for (File fileToZip : files) {
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

        zipOut.close();
        fos.close();
    }
    static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
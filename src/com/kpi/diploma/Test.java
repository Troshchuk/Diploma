package com.kpi.diploma;

import com.kpi.diploma.engine.Cache;
import com.kpi.diploma.engine.Index;
import com.kpi.diploma.models.Channel;
import com.kpi.diploma.models.Schedule;
import com.kpi.diploma.models.Show;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dtroshchuk
 */
public class Test {
    public static void main(String[] args) throws Exception {
        for (int i = 10000; i <= 200000; i += 5000) {
            System.out.print(i + " ");
            testComplexIndex(i);
        }

        Map<Long, Channel> channels = createAndFillChannel(100000);
        Map<Long, Schedule> schedules = createAndFillSchedule(100000);
        Map<Long, Show> shows = createAndFillShow(100000);

        Channel channel = Cache.createQuery("SELECT * FROM CHANNELS WHERE number=2, name=2").execute();
        Cache.createIndex("index", "CHANNELS", new ArrayList() {{
            add("number");
            add("name");
        }});
        Channel channel2 = Cache.createQuery("SELECT * FROM CHANNELS WHERE number=2, name=2").execute();
        System.out.println();
    }

    private static void testComplexIndex(int size) throws InterruptedException {
        Map<Long, Channel> channels = createAndFillChannel(size);
        Map<Long, Schedule> schedules = createAndFillSchedule(size);
        Map<Long, Show> shows = createAndFillShow(size);

        Index index = Cache.createComplexIndex("ComplexIndex", () -> {
            Map<String, List<String>> indexedObjects = new HashMap<>();
            for (Schedule schedule : schedules.values()) {
                Channel channel = channels.values().stream().filter(c -> c.getNumber() == schedule.getChannel()).findFirst().get();
                Show show = shows.values().stream().filter(s -> s.getName().equals(schedule.getName())).findFirst().get();
                String ids = channel.getId() + "#" + show.getId() + "#" + schedule.getId();

                int start = Integer.parseInt(schedule.getStartTime().split(":")[0]);
                int end = Integer.parseInt(schedule.getEndTime().split(":")[0]);
                String key = channel.getNumber() + "#" + start + "#" + show.getType() + "#";
                List<String> values = getLists(indexedObjects, key);
                values.add(ids);
                indexedObjects.put(key, values);
                while (end - start != 0) {
                    start = (start + 1) % 24;
                    key = channel.getNumber() + "#" + start + "#" + show.getType() + "#";
                    values = getLists(indexedObjects, key);
                    values.add(ids);
                    indexedObjects.put(key, values);
                }
            }
            return indexedObjects;
        });


        String time = "13";
        String type = "HD";
        int channel = 13;
        List<List> cort = new ArrayList<>();

        long start1 = System.currentTimeMillis();
        List<Schedule> scheduleList = schedules.values().stream().filter(schedule -> schedule.getChannel() == channel
                && schedule.getStartTime().split(":")[0].equals(time)).collect(Collectors.toList());
        int timeToSleep1 = 0;
        timeToSleep1 += getRandom() * scheduleList.size();

        for (Schedule schedule : scheduleList) {
            shows.values().stream().filter(show -> show.getName().equals(schedule.getName()) && show.getType().equals(type)).findFirst().ifPresent(show1 -> {
                List list = new ArrayList();
                list.add(channels.values().stream().filter(channel1 -> channel1.getNumber() == schedule.getChannel()).findFirst().get());
                list.add(show1);
                list.add(schedule);
                cort.add(list);

            });
            timeToSleep1 += getRandom() * 2;
        }
        long end1 = System.currentTimeMillis() + timeToSleep1;
        int timeToSleep2 = 0;
        long start2 = System.currentTimeMillis();
        String key = channel + "#" + time + "#" + type + "#";
        List<String> o = index.get(key);
        List<List> cort2 = new ArrayList<>();
        timeToSleep2 += getRandom();
        for (String s : o) {
            String[] ids = s.split("#");
            List list = new ArrayList<>();
            list.add(channels.get(Long.parseLong(ids[0])));
            list.add(shows.get(Long.parseLong(ids[1])));
            list.add(schedules.get(Long.parseLong(ids[2])));
            timeToSleep2 += 30;
            cort2.add(list);
        }

        long end2 = System.currentTimeMillis() + timeToSleep2;
        System.out.println((end1 - start1) + " " + (end2 - start2) + " " + scheduleList.size());
    }

    private static int getRandom() {
        return (int) (Math.random() * 20) + 80;
    }

    private static List<String> getLists(Map<String, List<String>> indexedObjects, String key) {
        List<String> listOfCortage = indexedObjects.get(key);
        if (listOfCortage == null) {
            indexedObjects.put(key, new ArrayList<>());
            listOfCortage = indexedObjects.get(key);
        }
        return listOfCortage;
    }

    private static Map<Long, Channel> createAndFillChannel(int size) {
        Cache.deleteRegion("CHANNELS");
        Map<Long, Channel> channels = Cache.getRegion("CHANNELS");
        for (int i = 0; i < size; i++) {
            channels.put((long) i, new Channel(i, i, i + ""));
        }
        return channels;
    }

    private static Map<Long, Schedule> createAndFillSchedule(int size) {
        Cache.deleteRegion("SCHEDULES");
        Map<Long, Schedule> schedules = Cache.getRegion("SCHEDULES");
        for (int i = 0; i < size; i++) {
            schedules.put((long) i, new Schedule(i, i % 100, i + "", i % 24 + ":" + i % 60, (i + 1) % 24 + ":" + i % 60));
        }
        return schedules;
    }

    private static Map<Long, Show> createAndFillShow(int size) {
        Cache.deleteRegion("SHOWS");
        Map<Long, Show> shows = Cache.getRegion("SHOWS");
        for (int i = 0; i < size; i++) {
            shows.put((long) i, new Show(i, i + "", i % 2 == 0 ? "SD" : "HD"));
        }
        return shows;
    }
}

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
        for (int i = 10000; i <= 50000; i+=10000) {
            System.out.println(i);
            testComplexIndex(i);
        }

        Map<Long, Channel> channels = createAndFillChannel(100000);
        Map<Long, Schedule> schedules = createAndFillSchedule(100000);
        Map<Long, Show> shows = createAndFillShow(100000);

        Channel channel = Cache.createQuery("SELECT * FROM CHANNELS WHERE number=2, name=2").execute();
        Cache.createIndex("index", "CHANNELS", new ArrayList() {{add("number"); add("name");}});
        Channel channel2 = Cache.createQuery("SELECT * FROM CHANNELS WHERE number=2, name=2").execute();
        System.out.println();
    }

    private static void testComplexIndex(int size) {
        Map<Long, Channel> channels = createAndFillChannel(size);
        Map<Long, Schedule> schedules = createAndFillSchedule(size);
        Map<Long, Show> shows = createAndFillShow(size);

        Index index = Cache.createComplexIndex("ComplexIndex", () -> {
            Map<String, List<List>> indexedObjects =  new HashMap<>();
            for (Schedule schedule : schedules.values()) {
                Channel channel = channels.values().stream().filter(c -> c.getNumber() == schedule.getChannel()).findFirst().get();
                Show show = shows.values().stream().filter(s -> s.getName().equals(schedule.getName())).findFirst().get();
                List list = new ArrayList();
                list.add(channel);
                list.add(show);
                list.add(schedule);

                int start = Integer.parseInt(schedule.getStartTime().split(":")[0]);
                int end = Integer.parseInt(schedule.getEndTime().split(":")[0]);
                String key = channel.getNumber() + "#" + start + "#" + show.getType() + "#";
                List<List> listOfCortage = getLists(indexedObjects, key);
                listOfCortage.add(list);
                while (end - start != 0) {
                    start = (start + 1) % 24;
                    key = channel.getNumber() + "#" + start + "#" + show.getType() + "#";
                    listOfCortage = getLists(indexedObjects, key);
                    listOfCortage.add(list);
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
        for (Schedule schedule : scheduleList) {
            shows.values().stream().filter(show -> show.getName().equals(schedule.getName()) && show.getType().equals(type)).findFirst().ifPresent(show1 -> {
                cort.add(new ArrayList() {
                    {
                        add(channels.values().stream().filter(channel1 -> channel1.getNumber() == schedule.getChannel()).findFirst().get());
                        add(show1);
                        add(schedule);
                    }
                });
            });
        }
        long end1 = System.currentTimeMillis();
        long start2 = System.currentTimeMillis();
        String key = channel + "#" + time + "#" + type + "#";
        Object o = index.get(key);
        long end2 = System.currentTimeMillis();
        System.out.println((end1 - start1) + " " + (end2 - start2));
    }

    private static List<List> getLists(Map<String, List<List>> indexedObjects, String key) {
        List<List> listOfCortage = indexedObjects.get(key);
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
            schedules.put((long) i, new Schedule(i, i, i + "", i % 24 + ":" + i % 60, (i + 1) % 24 + ":" + i % 60));
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

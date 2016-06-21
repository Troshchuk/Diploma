package com.kpi.diploma;

import com.kpi.diploma.engine.Cache;
import com.kpi.diploma.models.Channel;
import com.kpi.diploma.models.Schedule;
import com.kpi.diploma.models.Show;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author dtroshchuk
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Map<Long, Channel> channels = createAndFillChannel();
        Map<Long, Schedule> schedules = createAndFillSchedule();
        Map<Long, Show> shows = createAndFillShow();

        Channel channel = Cache.createQuery("SELECT * FROM CHANNELS WHERE number=2, name=2").execute();
        Cache.createIndex("index", "CHANNELS", new ArrayList() {{add("number"); add("name");}});
        Channel channel2 = Cache.createQuery("SELECT * FROM CHANNELS WHERE number=2, name=2").execute();
        System.out.println();
    }

    private static Map<Long, Channel> createAndFillChannel() {
        Map<Long, Channel> channels = Cache.getRegion("CHANNELS");
        channels.put(1l, new Channel(1, 1, "1"));
        channels.put(2l, new Channel(2, 2, "2"));
        channels.put(3l, new Channel(3, 3, "3"));
        channels.put(4l, new Channel(4, 4, "4"));
        return channels;
    }

    private static Map<Long, Schedule> createAndFillSchedule() {
        Map<Long, Schedule> schedules = Cache.getRegion("SCHEDULES");

        return schedules;
    }

    private static Map<Long, Show> createAndFillShow() {
        Map<Long, Show> shows = Cache.getRegion("SHOWS");

        return shows;
    }
}

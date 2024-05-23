package com.planner.util;

import com.planner.models.CheckList;
import com.planner.models.Task;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Handler of all core linker and utility functions for GoogleIO
 *
 * @author Andrew Roe
 */
public class GoogleCalendarUtil {

    public static Event formatTaskToEvent(Task task, double subTaskHours, int dayIdx, int dayHour, int dayMin) {
        Event event = new Event().setSummary(task.getName()); //todo need to display labels with given Task
        StringBuilder sb = new StringBuilder("Due: ");
        sb.append(task.getDueDate().get(Calendar.YEAR))
                .append("-")
                .append(task.getDueDate().get(Calendar.MONTH) + 1)
                .append("-")
                .append(task.getDueDate().get(Calendar.DAY_OF_MONTH))
                .append("\n\n");
        CheckList cl = task.getCheckList();
        if(cl != null) {
            sb.append(cl.getName()).append(":<ul>");
            for(CheckList.Item i1 : cl.getItems()) {
                sb.append("<li>")
                        .append(i1.getDescription())
                        .append("</li>");
            }
            sb.append("</ul>\n");
        }

        sb.append("Agile Planner\n\neb007aba6df2559a02ceb17ddba47c85b3e2b930");
        event.setDescription(sb.toString());

        Calendar now = Time.getFormattedCalendarInstance(dayIdx);
        now.add(Calendar.HOUR, dayHour);
        now.add(Calendar.MINUTE, dayMin);
        DateTime startDateTime = new DateTime(now.getTime());
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        now.add(Calendar.HOUR, (int) subTaskHours);
        if (subTaskHours % 1 == 0.5) now.add(Calendar.MINUTE, 30);
        DateTime endDateTime = new DateTime(now.getTime());
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);
        if (!task.getLabel().isEmpty()) {
            event.setColorId("" + task.getLabel().get(0).getColor());
        } else event.setColorId("7");

        return event;
    }

    public static List<String> formatEventsToTasks(List<Event> items) throws IOException {
        List<String> tasks = new ArrayList<>();
        for(Event i1 : items) {
            if(i1.getDescription() != null && i1.getDescription().contains("eb007aba6df2559a02ceb17ddba47c85b3e2b930")) {
                String title = i1.getSummary();
                String start = i1.getStart().toPrettyString();
                String end = i1.getEnd().toPrettyString();
                tasks.add("title=" + title + ", start=" + start + ", end=" + end);
            }
        }
        return tasks;
    }

}
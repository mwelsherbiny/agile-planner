package com.planner.schedule.day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.planner.models.Task;
import com.planner.models.Task.SubTask;
import com.planner.util.Time;

/**
 * Represents a single Day in the year
 *
 * @author Andrew Roe
 */
public class Day {

    /** Holds the date and time of the particular Day */
    private Calendar date;
    /** Number of hours possible for a given Day */
    private double capacity;
    /** Number of hours filled for a given Day */
    private double size;
    /** TreeSet of all SubTasks */
    private final List<SubTask> subtaskManager;
    /** ID for the specific Day */
    private int id;

    /**
     * Primary constructor for Day
     *
     * @param id ID specifier for Day
     * @param capacity total capacity for the day
     * @param incrementation number of days from present day (0=today, 1=tomorrow, ...)
     */
    public Day(int id, double capacity, int incrementation) {
        setId(id);
        setCapacity(capacity);
        setDate(incrementation);
        subtaskManager = new ArrayList<>();
    }

    public Day(int id, double capacity, Calendar date) {
        setId(id);
        setCapacity(capacity);
        this.date = date;
        subtaskManager = new ArrayList<>();
    }

    /**
     * Sets the capacity for the Day
     *
     * @param capacity total possible hours
     */
    private void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the total capacity for the Day
     *
     * @return total capacity for Day
     */
    public double getCapacity() { return this.capacity; }

    /**
     * Sets the due date for the Day
     *
     * @param incrementation number of days from present for the Date to be set
     */
    private void setDate(int incrementation) {
        this.date = Time.getFormattedCalendarInstance(incrementation);
    }

    public double getSize() {
        return size;
    }

    /**
     * Gets the Date from the Day
     *
     * @return Date from Day
     */
    public Calendar getDate() {
        return date;
    }

    public void addSubTask(Task task, double hours, boolean overflow) {
        Task.SubTask subtask = task.addSubTask(hours, overflow);
        subtaskManager.add(subtask);
        this.size += hours;
    }

    /**
     * Gets the parent task based on the specified subtask index value
     *
     * @param subtaskIndex index of the subtask
     * @return parent of subtask
     */
    public Task getParentTask(int subtaskIndex) {
        SubTask subtask = subtaskManager.get(subtaskIndex);
        return subtask.getParentTask();
    }

    /**
     * Adds a SubTask manually to the Day
     *
     * @param task  Task to be added
     * @param hours number of hours for the SubTask
     * @return boolean status for success of adding SubTask manually
     */
    public boolean addSubTaskManually(Task task, double hours) {
        if (hours <= 0) return false;
        boolean overflow = this.size + hours > this.capacity;
        SubTask subtask = task.addSubTask(hours, overflow);

        subtaskManager.add(subtask);
        this.size += hours;
        return this.size <= this.capacity;
    }

    public SubTask getSubTask(int subtaskIndex) {
        return subtaskManager.get(subtaskIndex);
    }

    /**
     * Gets the number of SubTasks possessed by the Day
     *
     * @return number of SubTasks possessed by the Day
     */
    public int getNumSubTasks() {
        return subtaskManager.size();
    }

    private void setId(int id) {
        this.id = id;
    }

    /**
     * Gets ID for Day
     *
     * @return ID for Day
     */
    public int getId() {
        return id;
    }

    /**
     * Gets spare hours from the Day
     *
     * @return number of free hours available for scheduling
     */
    public double getSpareHours() {
        return Math.max(capacity - size, 0);
    }

    /**
     * Gets the number of hours assigned for a given day
     *
     * @return number of hours assigned for day
     */
    public double getHoursFilled() {
        return this.size;
    }

    /**
     * Determines whether there are spare hours in the Day
     *
     * @return boolean value for opening in Day
     */
    public boolean hasSpareHours() {
        return getSpareHours() > 0;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return "Day [" + sdf.format(this.date.getTime()) + "]";
    }

    /**
     * Provides a formatted string for output purposes
     *
     * @return formatted String
     */
    public String formattedString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        StringBuilder sb = new StringBuilder(sdf.format(this.date.getTime()) + "\n");
        for(SubTask st : subtaskManager) {
            sb.append("-");
            sb.append(st.getParentTask().getName()).append(", ");
            sb.append(st.getSubTaskHours()).append("hr, Due ");
            sb.append(sdf.format(st.getParentTask().getDueDate().getTime()));
            if(st.isOverflow()) {
                sb.append(" OVERFLOW");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets the List of SubTasks for the given Day
     *
     * @return List of SubTasks
     */
    public Iterable<? extends SubTask> getSubTasks() {
        return subtaskManager;
    }
}
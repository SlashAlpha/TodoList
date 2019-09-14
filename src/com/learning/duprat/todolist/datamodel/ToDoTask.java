package com.learning.duprat.todolist.datamodel;

import java.time.LocalDate;

public class ToDoTask {
    String sticker;
    String info;
    double estimatedTime;
    LocalDate date;
    int order;
    String completed;
    int unexpected;
    double socialFactor;
    double difficulty;

    public ToDoTask(String sticker, String info, double estimatedTime, LocalDate date, int order, String completed, int unexpected, double socialFactor, double difficulty) {
        this.sticker = sticker;
        this.info = info;
        this.estimatedTime = estimatedTime;
        this.date = date;
        this.order = order;
        this.completed = completed;
        this.unexpected = unexpected;
        this.socialFactor = socialFactor;
        this.difficulty = difficulty;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public int getUnexpected() {
        return unexpected;
    }

    public void setUnexpected(int unexpected) {
        this.unexpected = unexpected;
    }

    public double getSocialFactor() {
        return socialFactor;
    }

    public void setSocialFactor(double socialFactor) {
        this.socialFactor = socialFactor;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }


}

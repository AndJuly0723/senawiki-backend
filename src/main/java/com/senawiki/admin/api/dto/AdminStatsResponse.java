package com.senawiki.admin.api.dto;

public class AdminStatsResponse {

    private long totalUsers;
    private long newUsersToday;
    private long totalVisitors;
    private long dailyVisitors;
    private long totalPosts;
    private long newPostsToday;
    private long totalUploads;
    private long newUploadsToday;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getNewUsersToday() {
        return newUsersToday;
    }

    public void setNewUsersToday(long newUsersToday) {
        this.newUsersToday = newUsersToday;
    }

    public long getTotalVisitors() {
        return totalVisitors;
    }

    public void setTotalVisitors(long totalVisitors) {
        this.totalVisitors = totalVisitors;
    }

    public long getDailyVisitors() {
        return dailyVisitors;
    }

    public void setDailyVisitors(long dailyVisitors) {
        this.dailyVisitors = dailyVisitors;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getNewPostsToday() {
        return newPostsToday;
    }

    public void setNewPostsToday(long newPostsToday) {
        this.newPostsToday = newPostsToday;
    }

    public long getTotalUploads() {
        return totalUploads;
    }

    public void setTotalUploads(long totalUploads) {
        this.totalUploads = totalUploads;
    }

    public long getNewUploadsToday() {
        return newUploadsToday;
    }

    public void setNewUploadsToday(long newUploadsToday) {
        this.newUploadsToday = newUploadsToday;
    }
}

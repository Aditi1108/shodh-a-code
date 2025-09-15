package com.shodhacode.constants;

public class ApplicationConstants {
    
    // Polling intervals in milliseconds
    public static final int SUBMISSION_POLLING_INTERVAL = 2500;  // 2.5 seconds
    public static final int LEADERBOARD_POLLING_INTERVAL = 20000; // 20 seconds
    
    // Execution limits
    public static final int DEFAULT_TIME_LIMIT = 2000; // 2 seconds in milliseconds
    public static final int DEFAULT_MEMORY_LIMIT = 256; // 256 MB
    
    // Queue processing
    public static final int QUEUE_PROCESSING_DELAY = 1000; // 1 second delay between queue processing
    
    // Docker configuration
    public static final String DEFAULT_DOCKER_IMAGE = "shodhacode-executor";
    public static final String DEFAULT_TEMP_DIR = "/tmp/shodhacode";
    
    // API paths
    public static final String API_PREFIX = "/api";
    public static final String SUBMISSIONS_PATH = "/submissions";
    public static final String CONTESTS_PATH = "/contests";
    public static final String USERS_PATH = "/users";
    public static final String CONFIG_PATH = "/config";
    
    // Contest defaults
    public static final int DEFAULT_PROBLEM_POINTS = 100;
    public static final int MAX_CODE_LENGTH = 10000;
    public static final int MAX_OUTPUT_LENGTH = 5000;
    
    private ApplicationConstants() {
        // Prevent instantiation
    }
}
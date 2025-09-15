package com.shodhacode.config;

import com.shodhacode.entity.*;
import com.shodhacode.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                  ContestRepository contestRepository,
                                  ProblemRepository problemRepository,
                                  TestCaseRepository testCaseRepository) {
        return args -> {
            log.info("Initializing sample data...");

            // Create sample users
            User user1 = new User();
            user1.setUsername("alice");
            user1.setEmail("alice@example.com");
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("bob");
            user2.setEmail("bob@example.com");
            userRepository.save(user2);

            User user3 = new User();
            user3.setUsername("charlie");
            user3.setEmail("charlie@example.com");
            userRepository.save(user3);

            // Create a sample contest
            Contest contest = new Contest();
            contest.setTitle("Weekly Coding Challenge #1");
            contest.setDescription("Test your programming skills with these algorithmic problems!");
            contest.setStartTime(LocalDateTime.now().minusHours(1));
            contest.setEndTime(LocalDateTime.now().plusHours(2));
            contest.setIsActive(true);
            Contest savedContest = contestRepository.save(contest);

            // Problem 1: Two Sum
            Problem problem1 = new Problem();
            problem1.setTitle("Two Sum");
            problem1.setDescription("Given an array of integers nums and an integer target, return the indices of the two numbers such that they add up to target.\n\n" +
                    "You may assume that each input would have exactly one solution, and you may not use the same element twice.\n\n" +
                    "You can return the answer in any order.");
            problem1.setInputFormat("First line: n (size of array) and target\nSecond line: n space-separated integers");
            problem1.setOutputFormat("Two space-separated indices");
            problem1.setSampleInput("4 9\n2 7 11 15");
            problem1.setSampleOutput("0 1");
            problem1.setTestInput("5 6\n3 2 4 1 5");
            problem1.setExpectedOutput("1 2");
            problem1.setPoints(100);
            problem1.setTimeLimit(2000);
            problem1.setMemoryLimit(256);
            problem1.setContest(savedContest);
            Problem savedProblem1 = problemRepository.save(problem1);

            // Add test cases for Problem 1
            TestCase tc1 = new TestCase();
            tc1.setInput("4 9\n2 7 11 15");
            tc1.setExpectedOutput("0 1");
            tc1.setIsHidden(false);
            tc1.setTimeLimit(2000);
            tc1.setMemoryLimit(256);
            tc1.setProblem(savedProblem1);
            testCaseRepository.save(tc1);

            TestCase tc2 = new TestCase();
            tc2.setInput("3 6\n3 2 3");
            tc2.setExpectedOutput("0 2");
            tc2.setIsHidden(true);
            tc2.setTimeLimit(2000);
            tc2.setMemoryLimit(256);
            tc2.setProblem(savedProblem1);
            testCaseRepository.save(tc2);

            // Problem 2: Palindrome Number
            Problem problem2 = new Problem();
            problem2.setTitle("Palindrome Number");
            problem2.setDescription("Given an integer x, return true if x is a palindrome, and false otherwise.\n\n" +
                    "An integer is a palindrome when it reads the same backward as forward.\n\n" +
                    "For example, 121 is a palindrome while 123 is not.");
            problem2.setInputFormat("A single integer x");
            problem2.setOutputFormat("true or false");
            problem2.setSampleInput("121");
            problem2.setSampleOutput("true");
            problem2.setTestInput("-121");
            problem2.setExpectedOutput("false");
            problem2.setPoints(100);
            problem2.setTimeLimit(1000);
            problem2.setMemoryLimit(128);
            problem2.setContest(savedContest);
            Problem savedProblem2 = problemRepository.save(problem2);

            // Add test cases for Problem 2
            TestCase tc3 = new TestCase();
            tc3.setInput("121");
            tc3.setExpectedOutput("true");
            tc3.setIsHidden(false);
            tc3.setTimeLimit(1000);
            tc3.setMemoryLimit(128);
            tc3.setProblem(savedProblem2);
            testCaseRepository.save(tc3);

            TestCase tc4 = new TestCase();
            tc4.setInput("10");
            tc4.setExpectedOutput("false");
            tc4.setIsHidden(true);
            tc4.setTimeLimit(1000);
            tc4.setMemoryLimit(128);
            tc4.setProblem(savedProblem2);
            testCaseRepository.save(tc4);

            // Problem 3: FizzBuzz
            Problem problem3 = new Problem();
            problem3.setTitle("FizzBuzz");
            problem3.setDescription("Given an integer n, return a string array answer (1-indexed) where:\n\n" +
                    "- answer[i] == \"FizzBuzz\" if i is divisible by 3 and 5.\n" +
                    "- answer[i] == \"Fizz\" if i is divisible by 3.\n" +
                    "- answer[i] == \"Buzz\" if i is divisible by 5.\n" +
                    "- answer[i] == i (as a string) if none of the above conditions are true.\n\n" +
                    "Print each element on a new line.");
            problem3.setInputFormat("A single integer n");
            problem3.setOutputFormat("n lines, each containing the appropriate FizzBuzz value");
            problem3.setSampleInput("3");
            problem3.setSampleOutput("1\n2\nFizz");
            problem3.setTestInput("5");
            problem3.setExpectedOutput("1\n2\nFizz\n4\nBuzz");
            problem3.setPoints(50);
            problem3.setTimeLimit(1000);
            problem3.setMemoryLimit(128);
            problem3.setContest(savedContest);
            Problem savedProblem3 = problemRepository.save(problem3);

            // Add test cases for Problem 3
            TestCase tc5 = new TestCase();
            tc5.setInput("15");
            tc5.setExpectedOutput("1\n2\nFizz\n4\nBuzz\nFizz\n7\n8\nFizz\nBuzz\n11\nFizz\n13\n14\nFizzBuzz");
            tc5.setIsHidden(false);
            tc5.setTimeLimit(1000);
            tc5.setMemoryLimit(128);
            tc5.setProblem(savedProblem3);
            testCaseRepository.save(tc5);

            TestCase tc6 = new TestCase();
            tc6.setInput("1");
            tc6.setExpectedOutput("1");
            tc6.setIsHidden(true);
            tc6.setTimeLimit(1000);
            tc6.setMemoryLimit(128);
            tc6.setProblem(savedProblem3);
            testCaseRepository.save(tc6);

            log.info("Sample data initialization completed!");
            log.info("Created contest with ID: {}", savedContest.getId());
            log.info("Created {} users", 3);
            log.info("Created {} problems", 3);
            log.info("Created {} test cases", 6);
        };
    }
}
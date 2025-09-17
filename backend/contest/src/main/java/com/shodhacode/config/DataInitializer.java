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

            // ============= SECOND ACTIVE CONTEST =============
            Contest oldContest = new Contest();
            oldContest.setTitle("Daily Challenge #1");
            oldContest.setDescription("Practice your problem-solving skills with these fundamental challenges!");
            oldContest.setStartTime(LocalDateTime.now().minusHours(2));
            oldContest.setEndTime(LocalDateTime.now().plusDays(1));
            oldContest.setIsActive(true);
            Contest savedOldContest = contestRepository.save(oldContest);

            // Old Contest - Problem 1: Reverse String
            Problem oldProblem1 = new Problem();
            oldProblem1.setTitle("Reverse String");
            oldProblem1.setDescription("Write a function that reverses a string. The input string is given as an array of characters.\n\n" +
                    "You must do this by modifying the input string in-place with O(1) extra memory.");
            oldProblem1.setInputFormat("First line: n (length of string)\nSecond line: n space-separated characters");
            oldProblem1.setOutputFormat("The reversed string as space-separated characters");
            oldProblem1.setConstraints("1 <= n <= 10^5\nString contains only printable ASCII characters");
            oldProblem1.setPoints(100);
            oldProblem1.setTimeLimit(1000);
            oldProblem1.setMemoryLimit(128);
            oldProblem1.setContest(savedOldContest);
            Problem savedOldProblem1 = problemRepository.save(oldProblem1);

            // Test cases for Reverse String
            TestCase otc1 = new TestCase();
            otc1.setInput("5\nh e l l o");
            otc1.setExpectedOutput("o l l e h");
            otc1.setIsHidden(false);
            otc1.setTimeLimit(1000);
            otc1.setMemoryLimit(128);
            otc1.setProblem(savedOldProblem1);
            testCaseRepository.save(otc1);

            TestCase otc2 = new TestCase();
            otc2.setInput("7\nH a n n a h !");
            otc2.setExpectedOutput("! h a n n a H");
            otc2.setIsHidden(true);
            otc2.setTimeLimit(1000);
            otc2.setMemoryLimit(128);
            otc2.setProblem(savedOldProblem1);
            testCaseRepository.save(otc2);

            // Old Contest - Problem 2: Valid Parentheses
            Problem oldProblem2 = new Problem();
            oldProblem2.setTitle("Valid Parentheses");
            oldProblem2.setDescription("Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.\n\n" +
                    "An input string is valid if:\n" +
                    "1. Open brackets must be closed by the same type of brackets.\n" +
                    "2. Open brackets must be closed in the correct order.\n" +
                    "3. Every close bracket has a corresponding open bracket of the same type.");
            oldProblem2.setInputFormat("A single line containing the string s");
            oldProblem2.setOutputFormat("true if the string is valid, false otherwise");
            oldProblem2.setConstraints("1 <= s.length <= 10^4\ns consists of parentheses only '()[]{}'");
            oldProblem2.setPoints(150);
            oldProblem2.setTimeLimit(1000);
            oldProblem2.setMemoryLimit(128);
            oldProblem2.setContest(savedOldContest);
            Problem savedOldProblem2 = problemRepository.save(oldProblem2);

            // Test cases for Valid Parentheses
            TestCase otc3 = new TestCase();
            otc3.setInput("()");
            otc3.setExpectedOutput("true");
            otc3.setIsHidden(false);
            otc3.setTimeLimit(1000);
            otc3.setMemoryLimit(128);
            otc3.setProblem(savedOldProblem2);
            testCaseRepository.save(otc3);

            TestCase otc4 = new TestCase();
            otc4.setInput("()[]{}");
            otc4.setExpectedOutput("true");
            otc4.setIsHidden(false);
            otc4.setTimeLimit(1000);
            otc4.setMemoryLimit(128);
            otc4.setProblem(savedOldProblem2);
            testCaseRepository.save(otc4);

            TestCase otc5 = new TestCase();
            otc5.setInput("(]");
            otc5.setExpectedOutput("false");
            otc5.setIsHidden(true);
            otc5.setTimeLimit(1000);
            otc5.setMemoryLimit(128);
            otc5.setProblem(savedOldProblem2);
            testCaseRepository.save(otc5);

            // Old Contest - Problem 3: Find Maximum
            Problem oldProblem3 = new Problem();
            oldProblem3.setTitle("Find Maximum in Array");
            oldProblem3.setDescription("Given an array of integers, find and return the maximum element.\n\n" +
                    "Write an efficient solution that handles large arrays.");
            oldProblem3.setInputFormat("First line: n (size of array)\nSecond line: n space-separated integers");
            oldProblem3.setOutputFormat("The maximum element in the array");
            oldProblem3.setConstraints("1 <= n <= 10^6\n-10^9 <= array[i] <= 10^9");
            oldProblem3.setPoints(50);
            oldProblem3.setTimeLimit(1000);
            oldProblem3.setMemoryLimit(256);
            oldProblem3.setContest(savedOldContest);
            Problem savedOldProblem3 = problemRepository.save(oldProblem3);

            // Test cases for Find Maximum
            TestCase otc6 = new TestCase();
            otc6.setInput("5\n3 7 2 9 1");
            otc6.setExpectedOutput("9");
            otc6.setIsHidden(false);
            otc6.setTimeLimit(1000);
            otc6.setMemoryLimit(256);
            otc6.setProblem(savedOldProblem3);
            testCaseRepository.save(otc6);

            TestCase otc7 = new TestCase();
            otc7.setInput("4\n-5 -2 -8 -1");
            otc7.setExpectedOutput("-1");
            otc7.setIsHidden(true);
            otc7.setTimeLimit(1000);
            otc7.setMemoryLimit(256);
            otc7.setProblem(savedOldProblem3);
            testCaseRepository.save(otc7);

            // ============= NEW CONTEST (ACTIVE) =============
            Contest activeContest = new Contest();
            activeContest.setTitle("Weekly Coding Challenge #2");
            activeContest.setDescription("Test your algorithmic skills with these challenging problems!");
            activeContest.setStartTime(LocalDateTime.now().minusHours(1));
            activeContest.setEndTime(LocalDateTime.now().plusDays(2));
            activeContest.setIsActive(true);
            Contest savedActiveContest = contestRepository.save(activeContest);

            // Active Contest - Problem 1: Two Sum
            Problem problem1 = new Problem();
            problem1.setTitle("Two Sum");
            problem1.setDescription("Given an array of integers nums and an integer target, return the indices of the two numbers such that they add up to target.\n\n" +
                    "You may assume that each input would have exactly one solution, and you may not use the same element twice.\n\n" +
                    "You can return the answer in any order.");
            problem1.setInputFormat("First line: n (size of array) and target\nSecond line: n space-separated integers");
            problem1.setOutputFormat("Two space-separated indices (0-based)");
            problem1.setConstraints("2 <= n <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9");
            problem1.setPoints(100);
            problem1.setTimeLimit(2000);
            problem1.setMemoryLimit(256);
            problem1.setContest(savedActiveContest);
            Problem savedProblem1 = problemRepository.save(problem1);

            // Test cases for Two Sum
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
            tc2.setIsHidden(false);
            tc2.setTimeLimit(2000);
            tc2.setMemoryLimit(256);
            tc2.setProblem(savedProblem1);
            testCaseRepository.save(tc2);

            TestCase tc3 = new TestCase();
            tc3.setInput("2 6\n3 3");
            tc3.setExpectedOutput("0 1");
            tc3.setIsHidden(true);
            tc3.setTimeLimit(2000);
            tc3.setMemoryLimit(256);
            tc3.setProblem(savedProblem1);
            testCaseRepository.save(tc3);

            // Active Contest - Problem 2: Palindrome Number
            Problem problem2 = new Problem();
            problem2.setTitle("Palindrome Number");
            problem2.setDescription("Given an integer x, return true if x is a palindrome, and false otherwise.\n\n" +
                    "An integer is a palindrome when it reads the same backward as forward.\n\n" +
                    "For example, 121 is a palindrome while 123 is not.");
            problem2.setInputFormat("A single integer x");
            problem2.setOutputFormat("true or false");
            problem2.setConstraints("-2^31 <= x <= 2^31 - 1");
            problem2.setPoints(100);
            problem2.setTimeLimit(5000); // 5 seconds for Java compilation and execution
            problem2.setMemoryLimit(128);
            problem2.setContest(savedActiveContest);
            Problem savedProblem2 = problemRepository.save(problem2);

            // Test cases for Palindrome Number
            TestCase tc4 = new TestCase();
            tc4.setInput("121");
            tc4.setExpectedOutput("true");
            tc4.setIsHidden(false);
            tc4.setTimeLimit(5000);
            tc4.setMemoryLimit(128);
            tc4.setProblem(savedProblem2);
            testCaseRepository.save(tc4);

            TestCase tc5 = new TestCase();
            tc5.setInput("-121");
            tc5.setExpectedOutput("false");
            tc5.setIsHidden(false);
            tc5.setTimeLimit(5000);
            tc5.setMemoryLimit(128);
            tc5.setProblem(savedProblem2);
            testCaseRepository.save(tc5);

            TestCase tc6 = new TestCase();
            tc6.setInput("10");
            tc6.setExpectedOutput("false");
            tc6.setIsHidden(true);
            tc6.setTimeLimit(5000);
            tc6.setMemoryLimit(128);
            tc6.setProblem(savedProblem2);
            testCaseRepository.save(tc6);

            // Active Contest - Problem 3: FizzBuzz
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
            problem3.setConstraints("1 <= n <= 10^4");
            problem3.setPoints(50);
            problem3.setTimeLimit(5000); // 5 seconds for Java compilation and execution
            problem3.setMemoryLimit(128);
            problem3.setContest(savedActiveContest);
            Problem savedProblem3 = problemRepository.save(problem3);

            // Test cases for FizzBuzz
            TestCase tc7 = new TestCase();
            tc7.setInput("3");
            tc7.setExpectedOutput("1\n2\nFizz");
            tc7.setIsHidden(false);
            tc7.setTimeLimit(5000);
            tc7.setMemoryLimit(128);
            tc7.setProblem(savedProblem3);
            testCaseRepository.save(tc7);

            TestCase tc8 = new TestCase();
            tc8.setInput("5");
            tc8.setExpectedOutput("1\n2\nFizz\n4\nBuzz");
            tc8.setIsHidden(false);
            tc8.setTimeLimit(5000);
            tc8.setMemoryLimit(128);
            tc8.setProblem(savedProblem3);
            testCaseRepository.save(tc8);

            TestCase tc9 = new TestCase();
            tc9.setInput("15");
            tc9.setExpectedOutput("1\n2\nFizz\n4\nBuzz\nFizz\n7\n8\nFizz\nBuzz\n11\nFizz\n13\n14\nFizzBuzz");
            tc9.setIsHidden(true);
            tc9.setTimeLimit(5000);
            tc9.setMemoryLimit(128);
            tc9.setProblem(savedProblem3);
            testCaseRepository.save(tc9);

            log.info("Sample data initialization completed!");
            log.info("Created 2 contests:");
            log.info("  - Old contest (ended): {} with 3 problems", savedOldContest.getTitle());
            log.info("  - Active contest: {} with 3 problems", savedActiveContest.getTitle());
            log.info("Created {} users", 3);
            log.info("Created {} problems total", 6);
            log.info("Created {} test cases total", 16);
        };
    }
}
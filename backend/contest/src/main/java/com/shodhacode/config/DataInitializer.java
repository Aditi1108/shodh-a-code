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
                                  TestCaseRepository testCaseRepository,
                                  ContestParticipantRepository contestParticipantRepository,
                                  SubmissionRepository submissionRepository) {
        return args -> {
            log.info("Initializing sample data...");

            // Create sample users
            User user1 = new User();
            user1.setUsername("alice");
            user1.setEmail("alice@example.com");
            user1.setFullName("Alice Johnson");
            user1.setScore(125); // Global score from ended contest
            user1.setProblemsSolved(2); // Count Vowels + Sum of Digits
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("bob");
            user2.setEmail("bob@example.com");
            user2.setFullName("Bob Williams");
            user2.setScore(75); // Global score from ended contest
            user2.setProblemsSolved(1); // Count Vowels only
            userRepository.save(user2);

            User user3 = new User();
            user3.setUsername("charlie");
            user3.setEmail("charlie@example.com");
            user3.setFullName("Charlie Brown");
            user3.setScore(50); // Global score from ended contest
            user3.setProblemsSolved(1); // Sum of Digits only
            userRepository.save(user3);

            User user4 = new User();
            user4.setUsername("dylan");
            user4.setEmail("dylan@example.com");
            user4.setFullName("Dylan Smith");
            user4.setScore(0); // No successful submissions
            user4.setProblemsSolved(0); // Failed all attempts
            userRepository.save(user4);

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

            // ============= ENDED CONTEST =============
            Contest endedContest = new Contest();
            endedContest.setTitle("Past Challenge #1");
            endedContest.setDescription("A previous contest that has now ended. You can view problems but cannot submit solutions.");
            endedContest.setStartTime(LocalDateTime.now().minusDays(7));
            endedContest.setEndTime(LocalDateTime.now().minusDays(2)); // Ended 2 days ago
            endedContest.setIsActive(false);
            Contest savedEndedContest = contestRepository.save(endedContest);

            // Ended Contest - Problem 1: Count Vowels
            Problem endedProblem1 = new Problem();
            endedProblem1.setTitle("Count Vowels");
            endedProblem1.setDescription("Given a string, count the number of vowels (a, e, i, o, u) in it.\n\n" +
                    "The string may contain uppercase and lowercase letters. Count both uppercase and lowercase vowels.");
            endedProblem1.setInputFormat("A single line containing the string");
            endedProblem1.setOutputFormat("An integer representing the count of vowels");
            endedProblem1.setConstraints("1 <= string.length <= 1000\nString contains only alphabetic characters");
            endedProblem1.setPoints(75);
            endedProblem1.setTimeLimit(1000);
            endedProblem1.setMemoryLimit(128);
            endedProblem1.setContest(savedEndedContest);
            Problem savedEndedProblem1 = problemRepository.save(endedProblem1);

            // Test cases for Count Vowels
            TestCase etc1 = new TestCase();
            etc1.setInput("Hello World");
            etc1.setExpectedOutput("3");
            etc1.setIsHidden(false);
            etc1.setTimeLimit(1000);
            etc1.setMemoryLimit(128);
            etc1.setProblem(savedEndedProblem1);
            testCaseRepository.save(etc1);

            TestCase etc2 = new TestCase();
            etc2.setInput("Programming");
            etc2.setExpectedOutput("3");
            etc2.setIsHidden(false);
            etc2.setTimeLimit(1000);
            etc2.setMemoryLimit(128);
            etc2.setProblem(savedEndedProblem1);
            testCaseRepository.save(etc2);

            TestCase etc3 = new TestCase();
            etc3.setInput("AEIOUaeiou");
            etc3.setExpectedOutput("10");
            etc3.setIsHidden(true);
            etc3.setTimeLimit(1000);
            etc3.setMemoryLimit(128);
            etc3.setProblem(savedEndedProblem1);
            testCaseRepository.save(etc3);

            // Ended Contest - Problem 2: Sum of Digits
            Problem endedProblem2 = new Problem();
            endedProblem2.setTitle("Sum of Digits");
            endedProblem2.setDescription("Given a positive integer, find the sum of its digits.\n\n" +
                    "For example, if the input is 123, the output should be 6 (1 + 2 + 3).");
            endedProblem2.setInputFormat("A single positive integer");
            endedProblem2.setOutputFormat("The sum of the digits");
            endedProblem2.setConstraints("1 <= n <= 10^9");
            endedProblem2.setPoints(50);
            endedProblem2.setTimeLimit(1000);
            endedProblem2.setMemoryLimit(128);
            endedProblem2.setContest(savedEndedContest);
            Problem savedEndedProblem2 = problemRepository.save(endedProblem2);

            // Test cases for Sum of Digits
            TestCase etc4 = new TestCase();
            etc4.setInput("123");
            etc4.setExpectedOutput("6");
            etc4.setIsHidden(false);
            etc4.setTimeLimit(1000);
            etc4.setMemoryLimit(128);
            etc4.setProblem(savedEndedProblem2);
            testCaseRepository.save(etc4);

            TestCase etc5 = new TestCase();
            etc5.setInput("9999");
            etc5.setExpectedOutput("36");
            etc5.setIsHidden(false);
            etc5.setTimeLimit(1000);
            etc5.setMemoryLimit(128);
            etc5.setProblem(savedEndedProblem2);
            testCaseRepository.save(etc5);

            TestCase etc6 = new TestCase();
            etc6.setInput("1000000");
            etc6.setExpectedOutput("1");
            etc6.setIsHidden(true);
            etc6.setTimeLimit(1000);
            etc6.setMemoryLimit(128);
            etc6.setProblem(savedEndedProblem2);
            testCaseRepository.save(etc6);

            // ============= CREATE CONTEST PARTICIPANTS AND SUBMISSIONS FOR ENDED CONTEST =============
            // Alice - rank 1 with 125 points (both problems solved)
            ContestParticipant aliceParticipant = new ContestParticipant();
            aliceParticipant.setUser(user1);
            aliceParticipant.setContest(savedEndedContest);
            aliceParticipant.setScore(125); // 75 + 50
            aliceParticipant.setProblemsSolved(2);
            contestParticipantRepository.save(aliceParticipant);

            // Alice's submissions
            Submission aliceSubmission1 = new Submission();
            aliceSubmission1.setUser(user1);
            aliceSubmission1.setProblem(savedEndedProblem1);
            aliceSubmission1.setCode("# Count vowels solution\nvowels = 'aeiouAEIOU'\ncount = sum(1 for char in input() if char in vowels)\nprint(count)");
            aliceSubmission1.setLanguage(ProgrammingLanguage.PYTHON3);
            aliceSubmission1.setStatus(SubmissionStatus.ACCEPTED);
            aliceSubmission1.setScore(75);
            aliceSubmission1.setTestCasesPassed(3);
            aliceSubmission1.setTotalTestCases(3);
            aliceSubmission1.setExecutionTime(45L);
            aliceSubmission1.setSubmittedAt(LocalDateTime.now().minusDays(3).minusHours(2));
            submissionRepository.save(aliceSubmission1);

            Submission aliceSubmission2 = new Submission();
            aliceSubmission2.setUser(user1);
            aliceSubmission2.setProblem(savedEndedProblem2);
            aliceSubmission2.setCode("# Sum of digits\nn = int(input())\ndigit_sum = sum(int(digit) for digit in str(n))\nprint(digit_sum)");
            aliceSubmission2.setLanguage(ProgrammingLanguage.PYTHON3);
            aliceSubmission2.setStatus(SubmissionStatus.ACCEPTED);
            aliceSubmission2.setScore(50);
            aliceSubmission2.setTestCasesPassed(3);
            aliceSubmission2.setTotalTestCases(3);
            aliceSubmission2.setExecutionTime(32L);
            aliceSubmission2.setSubmittedAt(LocalDateTime.now().minusDays(3));
            submissionRepository.save(aliceSubmission2);

            // Bob - rank 2 with 75 points (only first problem solved)
            ContestParticipant bobParticipant = new ContestParticipant();
            bobParticipant.setUser(user2);
            bobParticipant.setContest(savedEndedContest);
            bobParticipant.setScore(75); // Only solved Count Vowels
            bobParticipant.setProblemsSolved(1);
            contestParticipantRepository.save(bobParticipant);

            // Bob's submission
            Submission bobSubmission = new Submission();
            bobSubmission.setUser(user2);
            bobSubmission.setProblem(savedEndedProblem1);
            bobSubmission.setCode("import java.util.Scanner;\npublic class Solution {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        int count = 0;\n        for(char c : s.toCharArray()) {\n            if(\"aeiouAEIOU\".indexOf(c) != -1) count++;\n        }\n        System.out.println(count);\n    }\n}");
            bobSubmission.setLanguage(ProgrammingLanguage.JAVA);
            bobSubmission.setStatus(SubmissionStatus.ACCEPTED);
            bobSubmission.setScore(75);
            bobSubmission.setTestCasesPassed(3);
            bobSubmission.setTotalTestCases(3);
            bobSubmission.setExecutionTime(124L);
            bobSubmission.setSubmittedAt(LocalDateTime.now().minusDays(3).minusHours(1));
            submissionRepository.save(bobSubmission);

            // Charlie - rank 3 with 50 points (only second problem solved)
            ContestParticipant charlieParticipant = new ContestParticipant();
            charlieParticipant.setUser(user3);
            charlieParticipant.setContest(savedEndedContest);
            charlieParticipant.setScore(50); // Only solved Sum of Digits
            charlieParticipant.setProblemsSolved(1);
            contestParticipantRepository.save(charlieParticipant);

            // Charlie's submission
            Submission charlieSubmission = new Submission();
            charlieSubmission.setUser(user3);
            charlieSubmission.setProblem(savedEndedProblem2);
            charlieSubmission.setCode("#include <iostream>\nusing namespace std;\nint main() {\n    long long n;\n    cin >> n;\n    int sum = 0;\n    while(n > 0) {\n        sum += n % 10;\n        n /= 10;\n    }\n    cout << sum << endl;\n    return 0;\n}");
            charlieSubmission.setLanguage(ProgrammingLanguage.CPP);
            charlieSubmission.setStatus(SubmissionStatus.ACCEPTED);
            charlieSubmission.setScore(50);
            charlieSubmission.setTestCasesPassed(3);
            charlieSubmission.setTotalTestCases(3);
            charlieSubmission.setExecutionTime(15L);
            charlieSubmission.setSubmittedAt(LocalDateTime.now().minusDays(2).minusHours(5));
            submissionRepository.save(charlieSubmission);

            // Dylan - rank 4 with 0 points (attempted but failed)
            ContestParticipant dylanParticipant = new ContestParticipant();
            dylanParticipant.setUser(user4);
            dylanParticipant.setContest(savedEndedContest);
            dylanParticipant.setScore(0); // Failed attempts
            dylanParticipant.setProblemsSolved(0);
            contestParticipantRepository.save(dylanParticipant);

            // Dylan's failed submission
            Submission dylanSubmission = new Submission();
            dylanSubmission.setUser(user4);
            dylanSubmission.setProblem(savedEndedProblem1);
            dylanSubmission.setCode("// Incorrect vowel counting\nconst input = prompt();\nlet count = 0;\nfor(let char of input) {\n    if('aeiou'.includes(char)) count++; // Missing uppercase vowels\n}\nconsole.log(count);");
            dylanSubmission.setLanguage(ProgrammingLanguage.JAVASCRIPT);
            dylanSubmission.setStatus(SubmissionStatus.WRONG_ANSWER);
            dylanSubmission.setScore(0);
            dylanSubmission.setTestCasesPassed(0);
            dylanSubmission.setTotalTestCases(3);
            dylanSubmission.setExecutionTime(28L);
            dylanSubmission.setErrorMessage("All test cases failed: Missing uppercase vowel handling");
            dylanSubmission.setSubmittedAt(LocalDateTime.now().minusDays(2));
            submissionRepository.save(dylanSubmission);

            log.info("Sample data initialization completed!");
            log.info("Created 3 contests:");
            log.info("  - Active contest 1: {} with 3 problems", savedOldContest.getTitle());
            log.info("  - Active contest 2: {} with 3 problems", savedActiveContest.getTitle());
            log.info("  - Ended contest: {} with 2 problems and 4 participants", savedEndedContest.getTitle());
            log.info("Created {} users with global scores:", 4);
            log.info("  - alice: 125 points, 2 problems solved");
            log.info("  - bob: 75 points, 1 problem solved");
            log.info("  - charlie: 50 points, 1 problem solved");
            log.info("  - dylan: 0 points, 0 problems solved");
            log.info("Created {} problems total", 8);
            log.info("Created {} test cases total", 22);
            log.info("Created leaderboard for ended contest:");
            log.info("  1. Alice - 125 points (2 problems)");
            log.info("  2. Bob - 75 points (1 problem)");
            log.info("  3. Charlie - 50 points (1 problem)");
            log.info("  4. Dylan - 0 points (0 problems)");
        };
    }
}
-- Insert sample users
INSERT INTO users (username, score, problems_solved) VALUES
('alice', 0, 0),
('bob', 0, 0),
('charlie', 0, 0);

-- Insert sample contest
INSERT INTO contests (title, description, start_time, end_time, is_active) VALUES
('Weekly Coding Challenge',
 'Test your programming skills with these algorithmic challenges!',
 CURRENT_TIMESTAMP,
 DATEADD('HOUR', 2, CURRENT_TIMESTAMP),
 true);

-- Insert sample problems
INSERT INTO problems (title, description, input_format, output_format,
                     sample_input, sample_output, test_input, expected_output,
                     points, contest_id)
VALUES
('Two Sum',
 'Given two integers a and b, return their sum.',
 'Two space-separated integers a and b (-1000 ≤ a, b ≤ 1000)',
 'Single integer representing the sum of a and b',
 '3 5',
 '8',
 '10 20',
 '30',
 100, 1),

('Reverse String',
 'Given a string, reverse it and return the result.',
 'A single line containing a string (1 ≤ length ≤ 100)',
 'The reversed string',
 'hello',
 'olleh',
 'world',
 'dlrow',
 100, 1),

('Find Maximum',
 'Find the maximum number in an array of integers.',
 'First line: n (size of array, 1 ≤ n ≤ 100)\nSecond line: n space-separated integers',
 'The maximum number in the array',
 '5
1 5 3 9 2',
 '9',
 '4
10 20 5 15',
 '20',
 100, 1);
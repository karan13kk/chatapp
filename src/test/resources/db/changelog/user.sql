DROP TABLE IF EXISTS user_db;
CREATE TABLE IF NOT EXISTS user_db (
    user_id VARCHAR(64) PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    passcode VARCHAR(64) NOT NULL,
    is_logged_in BOOLEAN DEFAULT FALSE,
    last_logged_in_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Insert sample data
INSERT INTO user_db (user_id, user_name, passcode, is_logged_in, last_logged_in_time, created_at, updated_at)
VALUES
    ('88d93eaa1dcba8364bb1c797b943bf121b336904d12e5b26b151aa43fb3c9cb2', 'John Doe', 'eadd580ddf0bd2077fa7799d720608918f42333cb2d238ba870d09f5fa86505f', true, to_timestamp(609459200), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('9ab1eb130b0ce3b63eb484e1561adf509e4635eb37121f67e2e25525c60eddc5', 'karan', '8dd9ed1fe7510208510bbd0c75dd0f3fd1ee4965806f6df06ba5246834385e47', false, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
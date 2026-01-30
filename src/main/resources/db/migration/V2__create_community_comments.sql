CREATE TABLE community_comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES community_posts(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    author_type VARCHAR(20) NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    member_username VARCHAR(200),
    guest_password_hash VARCHAR(200),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_community_comments_post_id_created_at
    ON community_comments (post_id, created_at);

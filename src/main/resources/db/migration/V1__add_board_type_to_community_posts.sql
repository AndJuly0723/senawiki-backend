ALTER TABLE community_posts
    ADD COLUMN board_type VARCHAR(20);

UPDATE community_posts
SET board_type = 'COMMUNITY'
WHERE board_type IS NULL;

ALTER TABLE community_posts
    ALTER COLUMN board_type SET NOT NULL;

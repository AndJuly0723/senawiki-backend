alter table if exists email_verifications
    add column if not exists failed_attempts integer not null default 0;

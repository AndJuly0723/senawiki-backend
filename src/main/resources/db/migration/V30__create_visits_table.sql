create table if not exists visits (
    id bigserial primary key,
    visitor_key varchar(64) not null,
    visit_date date not null,
    created_at timestamp not null default now(),
    constraint uq_visits_day_key unique (visit_date, visitor_key)
);

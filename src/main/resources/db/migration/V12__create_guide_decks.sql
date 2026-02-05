create table if not exists guide_decks (
    id bigserial primary key,
    guide_type varchar(30) not null,
    author_user_id bigint not null,
    author_role varchar(20) not null,
    up_votes int not null default 0,
    down_votes int not null default 0,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    constraint fk_guide_decks_author_user
        foreign key (author_user_id) references users (id) on delete restrict,
    constraint chk_guide_decks_type
        check (guide_type in (
            'ADVENTURE',
            'RAID',
            'ARENA',
            'TOTAL_WAR',
            'GROWTH_DUNGEON',
            'SIEGE',
            'GUILD_WAR',
            'EXPEDITION'
        )),
    constraint chk_guide_decks_author_role
        check (author_role in ('ADMIN', 'MEMBER'))
);

create index if not exists idx_guide_decks_type on guide_decks (guide_type);
create index if not exists idx_guide_decks_author on guide_decks (author_user_id);

create table if not exists guide_deck_teams (
    id bigserial primary key,
    deck_id bigint not null,
    team_no int not null,
    team_size int not null,
    formation_type varchar(30) not null,
    constraint fk_guide_deck_teams_deck
        foreign key (deck_id) references guide_decks (id) on delete cascade,
    constraint uq_guide_deck_teams_team
        unique (deck_id, team_no),
    constraint chk_guide_deck_teams_size
        check (team_size in (3, 5))
);

create index if not exists idx_guide_deck_teams_deck on guide_deck_teams (deck_id);

create table if not exists guide_deck_slots (
    id bigserial primary key,
    team_id bigint not null,
    slot_no int not null,
    hero_id varchar(50),
    is_pet boolean not null default false,
    pet_name varchar(100),
    constraint fk_guide_deck_slots_team
        foreign key (team_id) references guide_deck_teams (id) on delete cascade,
    constraint fk_guide_deck_slots_hero
        foreign key (hero_id) references heroes (id) on delete restrict,
    constraint uq_guide_deck_slots_slot
        unique (team_id, slot_no),
    constraint chk_guide_deck_slots_owner
        check (
            (is_pet = true and pet_name is not null and hero_id is null)
            or
            (is_pet = false and hero_id is not null and pet_name is null)
        )
);

create index if not exists idx_guide_deck_slots_team on guide_deck_slots (team_id);

create table if not exists guide_deck_skill_orders (
    id bigserial primary key,
    team_id bigint not null,
    hero_id varchar(50) not null,
    skill_slot int not null,
    order_no int not null,
    constraint fk_guide_deck_skill_orders_team
        foreign key (team_id) references guide_deck_teams (id) on delete cascade,
    constraint fk_guide_deck_skill_orders_hero
        foreign key (hero_id) references heroes (id) on delete restrict,
    constraint uq_guide_deck_skill_orders_order
        unique (team_id, order_no),
    constraint chk_guide_deck_skill_orders_slot
        check (skill_slot in (1, 2))
);

create index if not exists idx_guide_deck_skill_orders_team on guide_deck_skill_orders (team_id);

create table if not exists guide_deck_hero_equipments (
    id bigserial primary key,
    team_id bigint not null,
    hero_id varchar(50) not null,
    equipment_json jsonb not null,
    constraint fk_guide_deck_hero_equipments_team
        foreign key (team_id) references guide_deck_teams (id) on delete cascade,
    constraint fk_guide_deck_hero_equipments_hero
        foreign key (hero_id) references heroes (id) on delete restrict,
    constraint uq_guide_deck_hero_equipments_hero
        unique (team_id, hero_id)
);

create index if not exists idx_guide_deck_hero_equipments_team on guide_deck_hero_equipments (team_id);

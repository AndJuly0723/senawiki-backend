# Guide Deck API (Guild War Counter Deck)

## Endpoints

- `POST /api/deck_create`
- `POST /api/guide-decks`
- `PUT /api/guide-decks/{deckId}`
- `GET /api/guide-decks`

## Counter Deck Definition

- Counter deck means a normal `guide_decks` row whose `counterParentDeckId` points to a source Guild War deck.
- Counter link is allowed only when `guideType = GUILD_WAR`.

## Create Request (`POST /api/deck_create`, `POST /api/guide-decks`)

### Counter-compatible input keys

Backend accepts all of the following:

- `counterParentDeckId` (standard)
- `parentDeckId`
- `counterOfDeckId`
- `sourceDeckId`
- `targetDeckId`
- `isCounter` (boolean)
- `counter` (boolean)

### Validation rules

- Counter parent deck id fields must not conflict (different ids across alias keys -> `400`).
- `isCounter=true` or `counter=true` requires parent deck id (`400` if missing).
- `isCounter=false`/`counter=false` with parent deck id is conflict (`400`).
- Parent deck must exist (`400`).
- Parent deck `guideType` must be `GUILD_WAR` (`400`).
- Counter link itself is allowed only for `guideType=GUILD_WAR` (`400` otherwise).
- Self-parent is blocked when request includes deck id-like field (`id` or `deckId`) and equals parent id (`400`).

### Create request example (counter deck)

```json
{
  "guideType": "GUILD_WAR",
  "counterParentDeckId": 101,
  "isCounter": true,
  "teams": [
    {
      "teamNo": 1,
      "teamSize": 3,
      "formationId": "line-front",
      "slots": [
        { "position": 1, "heroId": "hero-1" },
        { "position": 2, "heroId": "hero-2" },
        { "position": 3, "heroId": "hero-3" }
      ]
    }
  ]
}
```

## Update Request (`PUT /api/guide-decks/{deckId}`)

### Counter field policy (important)

- Counter keys **not provided**: keep existing `counterParentDeckId` unchanged.
- Counter keys **provided with null** parent id (`counterParentDeckId: null`, etc.): clear counter link.
- Counter keys **provided with `isCounter=false` or `counter=false`** and no parent id: clear counter link.
- Counter keys **provided with `isCounter=true` or `counter=true`**: parent id required.
- Same create-time validations apply (GUILD_WAR only, parent exists/type, conflict checks, self-parent block).

### Update request examples

Clear counter link:

```json
{
  "counterParentDeckId": null
}
```

Keep current link (no counter keys):

```json
{
  "raidId": "raid-2"
}
```

## List Response (`GET /api/guide-decks`)

### Query params

- Required: `type`
- Optional: `raidId`, `stageId`, `expeditionId`, `siegeDay`
- Optional: `counterParentDeckId` (for lightweight counter modal loading)

### Counter fields in each deck item

- `counterParentDeckId`: `null` for source deck, source deck id for counter deck
- `isCounterDeck`: `true` when `counterParentDeckId != null`

### Response example

```json
{
  "content": [
    {
      "id": 202,
      "guideType": "GUILD_WAR",
      "authorNickname": "user1",
      "authorRole": "MEMBER",
      "upVotes": 12,
      "downVotes": 1,
      "createdAt": "2026-02-24T21:00:00",
      "counterParentDeckId": 101,
      "isCounterDeck": true,
      "teams": []
    }
  ]
}
```

## Delete Cascade Behavior (DB FK Chain)

When a parent/source `guide_decks` row is deleted:

- child counter decks (`guide_decks.counter_parent_deck_id`) are deleted by FK cascade
- each deleted deck deletes:
  - `guide_deck_teams` (cascade)
  - `guide_deck_slots` (via teams cascade)
  - `guide_deck_skill_orders` (via teams cascade)
  - `guide_deck_hero_equipments` (via teams cascade)
  - `guide_deck_votes` (cascade)

Current FK source:

- `V11__create_guide_decks.sql`
- `V14__create_guide_deck_votes.sql`
- `V34__set_counter_parent_fk_on_delete_cascade.sql`

package com.senawiki.guide.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senawiki.guide.api.dto.GuideDeckCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckSlotCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckSummaryResponse;
import com.senawiki.guide.api.dto.GuideDeckTeamCreateRequest;
import com.senawiki.guide.domain.GuideAuthorRole;
import com.senawiki.guide.domain.GuideDeck;
import com.senawiki.guide.domain.GuideDeckHeroEquipmentRepository;
import com.senawiki.guide.domain.GuideDeckRepository;
import com.senawiki.guide.domain.GuideDeckSkillOrderRepository;
import com.senawiki.guide.domain.GuideDeckSlotRepository;
import com.senawiki.guide.domain.GuideDeckTeamRepository;
import com.senawiki.guide.domain.GuideDeckVoteRepository;
import com.senawiki.guide.domain.GuideType;
import com.senawiki.hero.domain.HeroRepository;
import com.senawiki.user.domain.Role;
import com.senawiki.user.domain.User;
import com.senawiki.user.domain.UserRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuideDeckServiceTest {

    @Mock
    private GuideDeckRepository deckRepository;
    @Mock
    private GuideDeckTeamRepository teamRepository;
    @Mock
    private GuideDeckSlotRepository slotRepository;
    @Mock
    private GuideDeckSkillOrderRepository skillOrderRepository;
    @Mock
    private GuideDeckHeroEquipmentRepository equipmentRepository;
    @Mock
    private GuideDeckVoteRepository voteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HeroRepository heroRepository;

    private GuideDeckService service;

    @BeforeEach
    void setUp() {
        service = new GuideDeckService(
            deckRepository,
            teamRepository,
            slotRepository,
            skillOrderRepository,
            equipmentRepository,
            voteRepository,
            userRepository,
            heroRepository,
            new ObjectMapper()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_rejectsCounterOnNonGuildWar() {
        mockAuthenticatedUser(10L);
        GuideDeckCreateRequest request = baseCreateRequest(GuideType.RAID);
        request.setCounterParentDeckId(100L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Counter deck is only for guild war guide", ex.getReason());
    }

    @Test
    void create_rejectsWhenParentDeckNotFound() {
        mockAuthenticatedUser(10L);
        GuideDeckCreateRequest request = baseCreateRequest(GuideType.GUILD_WAR);
        request.setCounterParentDeckId(999L);
        when(deckRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Counter parent deck not found", ex.getReason());
    }

    @Test
    void create_rejectsWhenParentDeckTypeIsNotGuildWar() {
        mockAuthenticatedUser(10L);
        GuideDeck parent = new GuideDeck();
        parent.setGuideType(GuideType.RAID);
        GuideDeckCreateRequest request = baseCreateRequest(GuideType.GUILD_WAR);
        request.setCounterParentDeckId(33L);
        when(deckRepository.findById(33L)).thenReturn(Optional.of(parent));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Counter parent must be a guild war deck", ex.getReason());
    }

    @Test
    void create_rejectsWhenCounterCompatKeysConflict() {
        mockAuthenticatedUser(10L);
        GuideDeckCreateRequest request = baseCreateRequest(GuideType.GUILD_WAR);
        request.setParentDeckId(1L);
        request.setSourceDeckId(2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Counter parent deck id fields conflict", ex.getReason());
    }

    @Test
    void create_rejectsWhenIsCounterTrueWithoutParentId() {
        mockAuthenticatedUser(10L);
        GuideDeckCreateRequest request = baseCreateRequest(GuideType.GUILD_WAR);
        request.setIsCounter(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Counter parent deck id is required", ex.getReason());
    }

    @Test
    void create_rejectsSelfParentWhenDeckIdProvided() {
        mockAuthenticatedUser(10L);
        GuideDeckCreateRequest request = baseCreateRequest(GuideType.GUILD_WAR);
        request.setDeckId(77L);
        request.setCounterParentDeckId(77L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Deck cannot be its own counter parent", ex.getReason());
    }

    @Test
    void update_keepsExistingParentWhenCounterFieldsNotProvided() {
        User user = mockAuthenticatedUser(10L);
        GuideDeck parent = deck(7L, GuideType.GUILD_WAR);
        GuideDeck deck = deck(11L, GuideType.GUILD_WAR);
        deck.setAuthorUser(user);
        deck.setCounterParentDeck(parent);
        when(deckRepository.findById(11L)).thenReturn(Optional.of(deck));

        GuideDeckCreateRequest request = new GuideDeckCreateRequest();
        request.setRaidId("raid-1");

        service.update(11L, request);

        assertEquals(parent, deck.getCounterParentDeck());
    }

    @Test
    void update_clearsParentWhenCounterFieldProvidedAsNull() {
        User user = mockAuthenticatedUser(10L);
        GuideDeck parent = deck(7L, GuideType.GUILD_WAR);
        GuideDeck deck = deck(11L, GuideType.GUILD_WAR);
        deck.setAuthorUser(user);
        deck.setCounterParentDeck(parent);
        when(deckRepository.findById(11L)).thenReturn(Optional.of(deck));

        GuideDeckCreateRequest request = new GuideDeckCreateRequest();
        request.setCounterParentDeckId(null);

        service.update(11L, request);

        assertNull(deck.getCounterParentDeck());
    }

    @Test
    void update_keepsDetailWhenDetailFieldNotProvided() {
        User user = mockAuthenticatedUser(10L);
        GuideDeck deck = deck(11L, GuideType.GUILD_WAR);
        deck.setAuthorUser(user);
        deck.setDetail("기존 비고");
        when(deckRepository.findById(11L)).thenReturn(Optional.of(deck));

        GuideDeckCreateRequest request = new GuideDeckCreateRequest();
        request.setRaidId("raid-1");

        service.update(11L, request);

        assertEquals("기존 비고", deck.getDetail());
    }

    @Test
    void update_clearsDetailWhenProvidedAsBlank() {
        User user = mockAuthenticatedUser(10L);
        GuideDeck deck = deck(11L, GuideType.GUILD_WAR);
        deck.setAuthorUser(user);
        deck.setDetail("기존 비고");
        when(deckRepository.findById(11L)).thenReturn(Optional.of(deck));

        GuideDeckCreateRequest request = new GuideDeckCreateRequest();
        request.setDetail("   ");

        service.update(11L, request);

        assertNull(deck.getDetail());
    }

    @Test
    void list_exposesCounterParentDeckIdAndIsCounterDeck() {
        GuideDeck parent = deck(1L, GuideType.GUILD_WAR);
        GuideDeck counter = deck(2L, GuideType.GUILD_WAR);
        User author = new User();
        author.setNickname("tester");
        counter.setAuthorUser(author);
        counter.setAuthorRole(GuideAuthorRole.MEMBER);
        counter.setCounterParentDeck(parent);
        counter.setDetail("상대 탱커 강타 대응");

        Page<GuideDeck> page = new PageImpl<>(List.of(counter));
        when(deckRepository.findAllByGuideType(eq(GuideType.GUILD_WAR), any())).thenReturn(page);
        when(teamRepository.findByDeckIdIn(List.of(2L))).thenReturn(List.of());
        when(slotRepository.findByTeamIdIn(List.of())).thenReturn(List.of());
        when(skillOrderRepository.findByTeamIdIn(List.of())).thenReturn(List.of());

        Page<GuideDeckSummaryResponse> response = service.list(
            GuideType.GUILD_WAR,
            null,
            null,
            null,
            null,
            null,
            PageRequest.of(0, 20)
        );

        GuideDeckSummaryResponse item = response.getContent().get(0);
        assertEquals(1L, item.getCounterParentDeckId());
        assertTrue(item.isCounterDeck());
        assertEquals("상대 탱커 강타 대응", item.getDetail());
    }

    @Test
    void list_filtersByCounterParentDeckIdWhenProvided() {
        when(deckRepository.findAllByGuideTypeAndCounterParentDeckId(eq(GuideType.GUILD_WAR), eq(15L), any()))
            .thenReturn(Page.empty());

        Page<GuideDeckSummaryResponse> response = service.list(
            GuideType.GUILD_WAR,
            null,
            null,
            null,
            null,
            15L,
            PageRequest.of(0, 20)
        );

        assertTrue(response.isEmpty());
        verify(deckRepository).findAllByGuideTypeAndCounterParentDeckId(eq(GuideType.GUILD_WAR), eq(15L), any());
    }

    private User mockAuthenticatedUser(Long id) {
        User user = new User();
        user.setEmail("tester@example.com");
        user.setNickname("tester");
        user.setName("tester");
        user.setRole(Role.USER);
        setField(user, "id", id);

        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), "pw", List.of()));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        return user;
    }

    private GuideDeckCreateRequest baseCreateRequest(GuideType guideType) {
        GuideDeckCreateRequest request = new GuideDeckCreateRequest();
        request.setGuideType(guideType);

        GuideDeckTeamCreateRequest team = new GuideDeckTeamCreateRequest();
        team.setTeamNo(1);
        team.setTeamSize(3);
        team.setFormationId("line-front");

        GuideDeckSlotCreateRequest slot1 = new GuideDeckSlotCreateRequest();
        slot1.setPosition(1);
        slot1.setHeroId("hero-1");
        GuideDeckSlotCreateRequest slot2 = new GuideDeckSlotCreateRequest();
        slot2.setPosition(2);
        slot2.setHeroId("hero-2");
        GuideDeckSlotCreateRequest slot3 = new GuideDeckSlotCreateRequest();
        slot3.setPosition(3);
        slot3.setHeroId("hero-3");
        team.setSlots(List.of(slot1, slot2, slot3));
        request.setTeams(List.of(team));

        return request;
    }

    private GuideDeck deck(Long id, GuideType guideType) {
        GuideDeck deck = new GuideDeck();
        setField(deck, "id", id);
        deck.setGuideType(guideType);
        deck.setUpVotes(0);
        deck.setDownVotes(0);
        return deck;
    }

    private static void setField(Object target, String fieldName, Object value) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }
}

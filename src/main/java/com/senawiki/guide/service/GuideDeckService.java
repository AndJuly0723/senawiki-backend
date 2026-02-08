package com.senawiki.guide.service;

import com.senawiki.guide.api.dto.GuideDeckCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckEquipmentResponse;
import com.senawiki.guide.api.dto.GuideDeckHeroEquipmentCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckSkillOrderCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckSlotCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckSummaryResponse;
import com.senawiki.guide.api.dto.GuideDeckTeamCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckVoteResponse;
import com.senawiki.guide.domain.GuideAuthorRole;
import com.senawiki.guide.domain.GuideDeck;
import com.senawiki.guide.domain.GuideDeckHeroEquipment;
import com.senawiki.guide.domain.GuideDeckHeroEquipmentRepository;
import com.senawiki.guide.domain.GuideDeckRepository;
import com.senawiki.guide.domain.GuideDeckSkillOrder;
import com.senawiki.guide.domain.GuideDeckSkillOrderRepository;
import com.senawiki.guide.domain.GuideDeckSlot;
import com.senawiki.guide.domain.GuideDeckSlotRepository;
import com.senawiki.guide.domain.GuideDeckTeam;
import com.senawiki.guide.domain.GuideDeckTeamRepository;
import com.senawiki.guide.domain.GuideDeckVote;
import com.senawiki.guide.domain.GuideDeckVoteRepository;
import com.senawiki.guide.domain.GuideDeckVoteType;
import com.senawiki.guide.domain.GuideType;
import com.senawiki.hero.domain.Hero;
import com.senawiki.hero.domain.HeroRepository;
import com.senawiki.user.domain.Role;
import com.senawiki.user.domain.User;
import com.senawiki.user.domain.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GuideDeckService {

    private final GuideDeckRepository deckRepository;
    private final GuideDeckTeamRepository teamRepository;
    private final GuideDeckSlotRepository slotRepository;
    private final GuideDeckSkillOrderRepository skillOrderRepository;
    private final GuideDeckHeroEquipmentRepository equipmentRepository;
    private final GuideDeckVoteRepository voteRepository;
    private final UserRepository userRepository;
    private final HeroRepository heroRepository;
    private final ObjectMapper objectMapper;

    public GuideDeckService(
        GuideDeckRepository deckRepository,
        GuideDeckTeamRepository teamRepository,
        GuideDeckSlotRepository slotRepository,
        GuideDeckSkillOrderRepository skillOrderRepository,
        GuideDeckHeroEquipmentRepository equipmentRepository,
        GuideDeckVoteRepository voteRepository,
        UserRepository userRepository,
        HeroRepository heroRepository,
        ObjectMapper objectMapper
    ) {
        this.deckRepository = deckRepository;
        this.teamRepository = teamRepository;
        this.slotRepository = slotRepository;
        this.skillOrderRepository = skillOrderRepository;
        this.equipmentRepository = equipmentRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.heroRepository = heroRepository;
        this.objectMapper = objectMapper;
    }

    public Long create(GuideDeckCreateRequest request) {
        User user = requireUser();
        List<GuideDeckTeamCreateRequest> teams = normalizeTeams(request);
        validateRequest(request, teams);

        GuideDeck deck = new GuideDeck();
        deck.setGuideType(request.getGuideType());
        deck.setAuthorUser(user);
        deck.setAuthorRole(user.getRole() == Role.ADMIN ? GuideAuthorRole.ADMIN : GuideAuthorRole.MEMBER);
        deck.setUpVotes(0);
        deck.setDownVotes(0);
        deck.setRaidId(request.getRaidId());
        deck.setStageId(request.getStageId());

        GuideDeck saved = deckRepository.save(deck);
        persistTeams(saved, request, teams);

        return saved.getId();
    }

    public void delete(Long deckId) {
        User user = requireUser();
        GuideDeck deck = deckRepository.findById(deckId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found"));
        requireOwnerOrAdmin(user, deck);
        deleteDeckChildren(deck.getId());
        deckRepository.delete(deck);
    }

    @Transactional(readOnly = true)
    public Page<GuideDeckSummaryResponse> list(GuideType guideType, String raidId, Pageable pageable) {
        Pageable sorted = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Order.desc("createdAt"))
        );
        Page<GuideDeck> decks;
        if (guideType == GuideType.RAID && raidId != null && !raidId.isBlank()) {
            decks = deckRepository.findAllByGuideTypeAndRaidId(guideType, raidId, sorted);
        } else {
            decks = deckRepository.findAllByGuideType(guideType, sorted);
        }
        List<Long> deckIds = decks.getContent().stream()
            .map(GuideDeck::getId)
            .toList();
        if (deckIds.isEmpty()) {
            return decks.map(deck -> new GuideDeckSummaryResponse());
        }

        List<GuideDeckTeam> teams = teamRepository.findByDeckIdIn(deckIds);
        Map<Long, List<GuideDeckTeam>> teamsByDeckId = teams.stream()
            .collect(Collectors.groupingBy(team -> team.getDeck().getId()));
        List<Long> teamIds = teams.stream()
            .map(GuideDeckTeam::getId)
            .toList();

        Map<Long, List<GuideDeckSlot>> slotsByTeamId = slotRepository.findByTeamIdIn(teamIds).stream()
            .collect(Collectors.groupingBy(slot -> slot.getTeam().getId()));
        Map<Long, List<GuideDeckSkillOrder>> skillsByTeamId = skillOrderRepository.findByTeamIdIn(teamIds).stream()
            .collect(Collectors.groupingBy(skill -> skill.getTeam().getId()));

        Map<String, Hero> heroMap = loadHeroMap(slotsByTeamId, skillsByTeamId);

        return decks.map(deck -> toSummary(deck, teamsByDeckId, slotsByTeamId, skillsByTeamId, heroMap));
    }

    @Transactional(readOnly = true)
    public GuideDeckEquipmentResponse getEquipment(Long deckId, String heroId) {
        GuideDeck deck = deckRepository.findById(deckId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found"));
        List<Long> teamIds = teamRepository.findByDeckIdIn(List.of(deck.getId())).stream()
            .map(GuideDeckTeam::getId)
            .toList();
        if (teamIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found");
        }
        GuideDeckHeroEquipment equipment = equipmentRepository.findByTeamIdInAndHeroId(teamIds, heroId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));

        GuideDeckEquipmentResponse response = new GuideDeckEquipmentResponse();
        response.setDeckId(deckId);
        response.setHeroId(heroId);
        response.setHeroName(resolveHeroName(heroId));
        applyEquipment(response, equipment.getEquipmentJson());
        return response;
    }

    private GuideDeckSummaryResponse toSummary(
        GuideDeck deck,
        Map<Long, List<GuideDeckTeam>> teamsByDeckId,
        Map<Long, List<GuideDeckSlot>> slotsByTeamId,
        Map<Long, List<GuideDeckSkillOrder>> skillsByTeamId,
        Map<String, Hero> heroMap
    ) {
        GuideDeckSummaryResponse response = new GuideDeckSummaryResponse();
        response.setId(deck.getId());
        response.setGuideType(deck.getGuideType().name());
        response.setAuthorNickname(resolveAuthorNickname(deck.getAuthorUser()));
        response.setAuthorRole(deck.getAuthorRole().name());
        response.setUpVotes(deck.getUpVotes());
        response.setDownVotes(deck.getDownVotes());
        response.setCreatedAt(deck.getCreatedAt());

        List<GuideDeckTeam> teams = teamsByDeckId.getOrDefault(deck.getId(), List.of());
        response.setTeams(teams.stream()
            .sorted(Comparator.comparingInt(GuideDeckTeam::getTeamNo))
            .map(team -> toTeamSummary(team, slotsByTeamId, skillsByTeamId, heroMap))
            .toList());
        return response;
    }

    private GuideDeckSummaryResponse.TeamSummary toTeamSummary(
        GuideDeckTeam team,
        Map<Long, List<GuideDeckSlot>> slotsByTeamId,
        Map<Long, List<GuideDeckSkillOrder>> skillsByTeamId,
        Map<String, Hero> heroMap
    ) {
        GuideDeckSummaryResponse.TeamSummary summary = new GuideDeckSummaryResponse.TeamSummary();
        summary.setTeamNo(team.getTeamNo());
        summary.setTeamSize(team.getTeamSize());
        summary.setFormationId(team.getFormationType());

        List<GuideDeckSlot> slots = slotsByTeamId.getOrDefault(team.getId(), List.of());
        String petId = slots.stream()
            .filter(GuideDeckSlot::isPet)
            .map(GuideDeckSlot::getPetName)
            .findFirst()
            .orElse(null);
        summary.setPetId(petId);
        summary.setSlots(slots.stream()
            .filter(slot -> !slot.isPet())
            .sorted(Comparator.comparingInt(GuideDeckSlot::getSlotNo))
            .map(slot -> toSlotSummary(slot, heroMap))
            .toList());

        List<GuideDeckSkillOrder> skills = skillsByTeamId.getOrDefault(team.getId(), List.of());
        summary.setSkillOrders(skills.stream()
            .sorted(Comparator.comparingInt(GuideDeckSkillOrder::getOrderNo))
            .map(skill -> toSkillSummary(skill, heroMap))
            .toList());

        return summary;
    }

    private GuideDeckSummaryResponse.SlotSummary toSlotSummary(GuideDeckSlot slot, Map<String, Hero> heroMap) {
        GuideDeckSummaryResponse.SlotSummary summary = new GuideDeckSummaryResponse.SlotSummary();
        summary.setPosition(slot.getSlotNo());
        summary.setPet(slot.isPet());
        summary.setPetName(slot.getPetName());
        summary.setHeroId(slot.getHeroId());
        if (slot.getHeroId() != null) {
            Hero hero = heroMap.get(slot.getHeroId());
            if (hero != null) {
                summary.setHeroName(hero.getName());
                summary.setHeroImage(hero.getImage());
            }
        }
        return summary;
    }

    private GuideDeckSummaryResponse.SkillOrderSummary toSkillSummary(
        GuideDeckSkillOrder skill,
        Map<String, Hero> heroMap
    ) {
        GuideDeckSummaryResponse.SkillOrderSummary summary = new GuideDeckSummaryResponse.SkillOrderSummary();
        summary.setHeroId(skill.getHeroId());
        summary.setSkill(skill.getSkillSlot());
        summary.setOrder(skill.getOrderNo());
        Hero hero = heroMap.get(skill.getHeroId());
        if (hero != null) {
            summary.setHeroName(hero.getName());
        }
        return summary;
    }

    private Map<String, Hero> loadHeroMap(
        Map<Long, List<GuideDeckSlot>> slotsByTeamId,
        Map<Long, List<GuideDeckSkillOrder>> skillsByTeamId
    ) {
        List<String> heroIds = slotsByTeamId.values().stream()
            .flatMap(List::stream)
            .map(GuideDeckSlot::getHeroId)
            .filter(id -> id != null && !id.isBlank())
            .toList();
        List<String> skillHeroIds = skillsByTeamId.values().stream()
            .flatMap(List::stream)
            .map(GuideDeckSkillOrder::getHeroId)
            .filter(id -> id != null && !id.isBlank())
            .toList();

        Map<String, Hero> heroMap = new HashMap<>();
        heroRepository.findAllById(heroIds).forEach(hero -> heroMap.put(hero.getId(), hero));
        heroRepository.findAllById(skillHeroIds).forEach(hero -> heroMap.put(hero.getId(), hero));
        return heroMap;
    }

    private String resolveAuthorNickname(User user) {
        String nickname = user.getNickname();
        if (nickname != null && !nickname.isBlank()) {
            return nickname;
        }
        String name = user.getName();
        if (name != null && !name.isBlank()) {
            return name;
        }
        return user.getEmail();
    }

    private String resolveHeroName(String heroId) {
        Optional<Hero> hero = heroRepository.findById(heroId);
        if (hero.isPresent()) {
            return hero.get().getName();
        }
        return heroId;
    }

    private User requireUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private List<GuideDeckTeamCreateRequest> normalizeTeams(GuideDeckCreateRequest request) {
        if (request.getTeams() != null && !request.getTeams().isEmpty()) {
            return request.getTeams();
        }
        if (request.getTeam() != null) {
            return List.of(request.getTeam());
        }
        return List.of();
    }

    private void validateRequest(GuideDeckCreateRequest request, List<GuideDeckTeamCreateRequest> teams) {
        if (request == null || request.getGuideType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide type is required");
        }
        if (teams.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teams are required");
        }
        for (GuideDeckTeamCreateRequest team : teams) {
            Integer teamSize = resolveTeamSize(team);
            if (teamSize != 3 && teamSize != 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid team size");
            }
            if (team.getFormationId() == null || team.getFormationId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formation type is required");
            }
            if (team.getSlots() != null) {
                for (GuideDeckSlotCreateRequest slot : team.getSlots()) {
                    if (slot.getHeroId() == null || slot.getHeroId().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hero is required");
                    }
                }
            }
            List<GuideDeckSkillOrderCreateRequest> skillOrders = resolveSkillOrders(request, team);
            if (skillOrders != null) {
                for (GuideDeckSkillOrderCreateRequest skill : skillOrders) {
                    if (skill.getHeroId() == null || skill.getHeroId().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skill hero is required");
                    }
                    if (skill.getSkill() != 1 && skill.getSkill() != 2) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid skill slot");
                    }
                }
            }
            List<GuideDeckHeroEquipmentCreateRequest> equipments = resolveEquipments(request, team);
            if (equipments != null) {
                for (GuideDeckHeroEquipmentCreateRequest equipment : equipments) {
                    if (equipment.getHeroId() == null || equipment.getHeroId().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipment hero is required");
                    }
                    if (equipment.getEquipmentSet() == null || equipment.getEquipmentSet().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipment set is required");
                    }
                    if (equipment.getRing() == null || equipment.getRing().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ring is required");
                    }
                    if (equipment.getSlots() == null || equipment.getSlots().isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipment is required");
                    }
                }
            }
        }
    }

    private int resolveTeamNo(GuideDeckTeamCreateRequest teamRequest, int teamIndex) {
        if (teamRequest.getTeamNo() != null) {
            return teamRequest.getTeamNo();
        }
        return teamIndex + 1;
    }

    private int resolveTeamSize(GuideDeckTeamCreateRequest teamRequest) {
        if (teamRequest.getTeamSize() != null) {
            return teamRequest.getTeamSize();
        }
        if (teamRequest.getSlots() != null && !teamRequest.getSlots().isEmpty()) {
            return teamRequest.getSlots().size();
        }
        return 0;
    }

    private List<GuideDeckSkillOrderCreateRequest> resolveSkillOrders(
        GuideDeckCreateRequest request,
        GuideDeckTeamCreateRequest teamRequest
    ) {
        if (teamRequest.getSkillOrders() != null) {
            return teamRequest.getSkillOrders();
        }
        return request.getSkillOrders();
    }

    private List<GuideDeckHeroEquipmentCreateRequest> resolveEquipments(
        GuideDeckCreateRequest request,
        GuideDeckTeamCreateRequest teamRequest
    ) {
        if (teamRequest.getHeroEquipments() != null) {
            return teamRequest.getHeroEquipments();
        }
        return request.getHeroEquipments();
    }

    private void persistTeams(
        GuideDeck deck,
        GuideDeckCreateRequest request,
        List<GuideDeckTeamCreateRequest> teams
    ) {
        int teamIndex = 0;
        for (GuideDeckTeamCreateRequest teamRequest : teams) {
            GuideDeckTeam team = new GuideDeckTeam();
            team.setDeck(deck);
            team.setTeamNo(resolveTeamNo(teamRequest, teamIndex));
            team.setTeamSize(resolveTeamSize(teamRequest));
            team.setFormationType(teamRequest.getFormationId());
            GuideDeckTeam savedTeam = teamRepository.save(team);
            teamIndex++;

            if (teamRequest.getPetId() != null && !teamRequest.getPetId().isBlank()) {
                GuideDeckSlot petSlot = new GuideDeckSlot();
                petSlot.setTeam(savedTeam);
                petSlot.setSlotNo(0);
                petSlot.setPet(true);
                petSlot.setPetName(teamRequest.getPetId());
                petSlot.setHeroId(null);
                slotRepository.save(petSlot);
            }

            if (teamRequest.getSlots() != null) {
                for (GuideDeckSlotCreateRequest slotRequest : teamRequest.getSlots()) {
                    GuideDeckSlot slot = new GuideDeckSlot();
                    slot.setTeam(savedTeam);
                    slot.setSlotNo(slotRequest.getPosition());
                    slot.setPet(false);
                    slot.setHeroId(slotRequest.getHeroId());
                    slot.setPetName(null);
                    slotRepository.save(slot);
                }
            }

            List<GuideDeckSkillOrderCreateRequest> skillOrders = resolveSkillOrders(request, teamRequest);
            if (skillOrders != null) {
                for (GuideDeckSkillOrderCreateRequest skillRequest : skillOrders) {
                    GuideDeckSkillOrder skillOrder = new GuideDeckSkillOrder();
                    skillOrder.setTeam(savedTeam);
                    skillOrder.setHeroId(skillRequest.getHeroId());
                    skillOrder.setSkillSlot(skillRequest.getSkill());
                    skillOrder.setOrderNo(skillRequest.getOrder());
                    skillOrderRepository.save(skillOrder);
                }
            }

            List<GuideDeckHeroEquipmentCreateRequest> equipments = resolveEquipments(request, teamRequest);
            if (equipments != null) {
                for (GuideDeckHeroEquipmentCreateRequest equipmentRequest : equipments) {
                    GuideDeckHeroEquipment equipment = new GuideDeckHeroEquipment();
                    equipment.setTeam(savedTeam);
                    equipment.setHeroId(equipmentRequest.getHeroId());
                    equipment.setEquipmentJson(toEquipmentJson(equipmentRequest));
                    equipmentRepository.save(equipment);
                }
            }
        }
    }

    private void deleteDeckChildren(Long deckId) {
        List<GuideDeckTeam> teams = teamRepository.findByDeckId(deckId);
        if (teams.isEmpty()) {
            return;
        }
        List<Long> teamIds = teams.stream()
            .map(GuideDeckTeam::getId)
            .toList();
        equipmentRepository.deleteByTeamIdIn(teamIds);
        skillOrderRepository.deleteByTeamIdIn(teamIds);
        slotRepository.deleteByTeamIdIn(teamIds);
        teamRepository.deleteAll(teams);
    }

    private void requireOwnerOrAdmin(User user, GuideDeck deck) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (deck.getAuthorUser() != null && deck.getAuthorUser().getId().equals(user.getId())) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to modify this deck");
    }

    private JsonNode toEquipmentJson(GuideDeckHeroEquipmentCreateRequest equipmentRequest) {
        return objectMapper.valueToTree(Map.of(
            "equipmentSet", equipmentRequest.getEquipmentSet(),
            "ring", equipmentRequest.getRing(),
            "slots", equipmentRequest.getSlots() == null ? List.of() : equipmentRequest.getSlots().stream()
                .map(slot -> Map.of(
                    "slotId", slot.getSlotId(),
                    "main", slot.getMain(),
                    "subs", slot.getSubs() == null ? List.of() : slot.getSubs()
                ))
                .toList()
        ));
    }

    private void applyEquipment(GuideDeckEquipmentResponse response, JsonNode equipment) {
        if (equipment == null || !equipment.isObject()) {
            response.setEquipmentSet(null);
            response.setRing(null);
            response.setSlots(List.of());
            return;
        }
        response.setEquipmentSet(asText(equipment.get("equipmentSet")));
        response.setRing(asText(equipment.get("ring")));
        JsonNode slots = equipment.get("slots");
        if (slots == null || !slots.isArray()) {
            response.setSlots(List.of());
            return;
        }
        List<GuideDeckEquipmentResponse.EquipmentSlot> mapped = new java.util.ArrayList<>();
        for (JsonNode slotNode : slots) {
            GuideDeckEquipmentResponse.EquipmentSlot slot = new GuideDeckEquipmentResponse.EquipmentSlot();
            slot.setSlotId(asText(slotNode.get("slotId")));
            slot.setMain(asText(slotNode.get("main")));
            List<String> subs = new java.util.ArrayList<>();
            JsonNode subsNode = slotNode.get("subs");
            if (subsNode != null && subsNode.isArray()) {
                for (JsonNode sub : subsNode) {
                    subs.add(asText(sub));
                }
            }
            slot.setSubs(subs);
            mapped.add(slot);
        }
        response.setSlots(mapped);
    }

    public GuideDeckVoteResponse vote(Long deckId, GuideDeckVoteType voteType) {
        User user = requireUser();
        if (voteType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vote type is required");
        }
        GuideDeck deck = deckRepository.findById(deckId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck not found"));
        if (voteRepository.existsByDeckIdAndUserId(deckId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already voted for this deck");
        }

        GuideDeckVote vote = new GuideDeckVote();
        vote.setDeck(deck);
        vote.setUser(user);
        vote.setVoteType(voteType);

        try {
            voteRepository.save(vote);
            if (voteType == GuideDeckVoteType.UP) {
                deckRepository.incrementUpVotes(deckId);
            } else {
                deckRepository.incrementDownVotes(deckId);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already voted for this deck");
        }

        GuideDeckVoteResponse response = new GuideDeckVoteResponse();
        response.setUpVotes(deck.getUpVotes() + (voteType == GuideDeckVoteType.UP ? 1 : 0));
        response.setDownVotes(deck.getDownVotes() + (voteType == GuideDeckVoteType.DOWN ? 1 : 0));
        return response;
    }

    private String asText(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }
}

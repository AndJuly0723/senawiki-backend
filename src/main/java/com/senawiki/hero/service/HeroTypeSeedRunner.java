package com.senawiki.hero.service;

import com.senawiki.hero.domain.Hero;
import com.senawiki.hero.domain.HeroRepository;
import com.senawiki.hero.domain.HeroType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.hero-type-seed.enabled", havingValue = "true")
public class HeroTypeSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HeroTypeSeedRunner.class);

    private final HeroRepository heroRepository;

    public HeroTypeSeedRunner(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, HeroType> nameToType = new LinkedHashMap<>();
        putAll(nameToType, HeroType.ATTACK, List.of(
                "아멜리아",
                "발리스타",
                "비담",
                "브란즈&브란셀",
                "캐티",
                "콜트",
                "델론즈",
                "파이",
                "헤브니아",
                "제인",
                "진",
                "쥬피",
                "카구라",
                "클라한",
                "카일",
                "레오",
                "메이",
                "풍연",
                "레이",
                "라이언",
                "세인",
                "스니퍼",
                "소이",
                "타카",
                "태오",
                "조운"
        ));
        putAll(nameToType, HeroType.MAGIC, List.of(
                "아리엘",
                "클레오",
                "데이지",
                "에스파다",
                "프레이야",
                "조커",
                "쥬리",
                "키리엘",
                "멜키르",
                "미호",
                "밀리아",
                "노호",
                "파스칼",
                "린",
                "루리",
                "세라",
                "실베스타",
                "실비아",
                "바네사",
                "벨리카",
                "연희",
                "유리",
                "유신"
        ));
        putAll(nameToType, HeroType.DEFENSE, List.of(
                "아킬라",
                "아라곤",
                "에반",
                "헬레니아",
                "리",
                "녹스",
                "룩",
                "루디"
        ));
        putAll(nameToType, HeroType.SUPPORT, List.of(
                "엘리스",
                "비스킷",
                "클로에",
                "초선",
                "카린",
                "카론",
                "리나",
                "루시",
                "오를리",
                "플라튼",
                "로지",
                "사라",
                "유이"
        ));
        putAll(nameToType, HeroType.ALLROUND, List.of(
                "에이스",
                "아수라",
                "백각",
                "챈슬러",
                "아일린",
                "엘리시아",
                "겔리두스",
                "제이브",
                "카르마",
                "크리스",
                "라니아",
                "니아",
                "팔라누스",
                "레이첼",
                "손오공",
                "스파이크",
                "트루드",
                "빅토리아",
                "지크"
        ));

        int updated = 0;
        for (Map.Entry<String, HeroType> entry : nameToType.entrySet()) {
            String name = entry.getKey();
            HeroType type = entry.getValue();
            Hero hero = heroRepository.findByName(name).orElse(null);
            if (hero == null) {
                log.warn("Hero not found: {}", name);
                continue;
            }
            if (hero.getType() == type) {
                continue;
            }
            hero.setType(type);
            hero.setTypeLabel(toLabel(type));
            heroRepository.save(hero);
            updated++;
        }
        log.info("Hero type seed complete. Updated={}", updated);
    }

    private static void putAll(Map<String, HeroType> nameToType, HeroType type, List<String> names) {
        for (String name : names) {
            nameToType.put(name, type);
        }
    }

    private static String toLabel(HeroType type) {
        return switch (type) {
            case ATTACK -> "공격형";
            case MAGIC -> "마법형";
            case DEFENSE -> "방어형";
            case SUPPORT -> "지원형";
            case ALLROUND -> "만능형";
            case UNKNOWN -> "미분류";
        };
    }
}

package org.example.yesodkimchijjimback.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.domain.Room;
import org.example.yesodkimchijjimback.domain.Rule;
import org.example.yesodkimchijjimback.dto.rule.MatchResponse;
import org.example.yesodkimchijjimback.dto.rule.RuleRequest;
import org.example.yesodkimchijjimback.repository.RoomRepository;
import org.example.yesodkimchijjimback.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RoomRepository roomRepository;
    private final RuleRepository ruleRepository;

    @Transactional
    public MatchResponse submitRule(String roomCode, RuleRequest ruleRequest){
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(()-> new IllegalArgumentException("찾는 방이 없습니다."));

        List<String> voteList = new ArrayList<>(ruleRequest.getRules()); // 사용자가 쓴 규칙을 List에 넣기

        String voteString = String.join(",", voteList); // 규칙 List를 하나의 String으로 받기

        room.addVote(voteString); // 하나의 String을 Vote 즉 투표함에 넣기

        int memberCount = room.getMembers().size(); //room에 있는 멤버 수 세기
        List<String> currentVote = room.getAllVote(); // room에 있는 투표함 가져오기
        if(currentVote.size()<memberCount){ // 멤버수보다 투표수가 적으면 프론트한테 WAITING 보내기
            return new MatchResponse("WAITING", null);
        }
        //모든 투표를 하나의 String 으로 만들고 다시 ,로 하나씩 나누기 split은 자르면 갯수가 정해지기 때문에 []로 받음
        String[] allVoteArray = String.join(",", currentVote).split(",");
        String Guitar = "Guitar"; //기타의견을 프론트한테 Guitar로 받고 이걸 받으면 바로 mismatch
        boolean isGuitar = false;

        Map<String, Integer> ruleCount = new HashMap<>(); //id, 갯수를 써주는 HashMap사용

        for (String vote : allVoteArray){ // vote에 하나씩 받기
            if(vote.equals(Guitar)){  //Guitar이면 isGuitar true 전환
                isGuitar = true;
            }
            // 규칙갯수 세주는 로직 (vote, 갯수)를 지정해줌 vote(ID)받아오고 뒤에는 그전에 있었는지를 확인해줌
            // 있었으면 불러와서 +1 해주고 없었으면 default값 0+1
            ruleCount.put(vote, ruleCount.getOrDefault(vote, 0)+1);
        }

        List<String> successRule = new ArrayList<>();

        for(String key : ruleCount.keySet()){ //map에서 쓰이는 고유한 keyset사용 (ruleCount의 String(ID)만 각각 가져옴
            if(ruleCount.get(key)==memberCount){ //key 즉 ID에 해당하는 갯수를 가져와서 member수와 같은지 확인함
                successRule.add(key); //같으면 successRule이라는 List에 더하기
            }
        }
        if (successRule.isEmpty() || isGuitar) { //즉 합의된 규칙이 없거나 기타의견이 있어요 라는 항목이 있을시 mismatch
            room.getAllVote().clear(); //mismatch를 주기전에 투표함을 다 비움

            List<String> allOpinion = new ArrayList<>(ruleCount.keySet()); //사람들이 선택한 의견들을 다 넣음
            Collections.sort(allOpinion); // 보기 좋게 정리함
            return new MatchResponse("MISMATCH", allOpinion); // MISMATCH와 선택한 의견들 리스폰
        }
        for(String agreeRule : successRule){ // 규칙들 저장하기
            Rule rule = Rule.builder()
                    .room(room) //방 정보
                    .rule(agreeRule) //동의된 룰
                    .build();
            ruleRepository.save(rule);
        }
        room.getAllVote().clear(); // 다음 문제 시작 전 투표함 비우기

        return new MatchResponse("MATCH", successRule); // 규칙이 합의 되면 MATCH와 successRule 주기
    }


    public MatchResponse redirect(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(()-> new IllegalArgumentException("찾는 방이 없습니다."));

        int memberCount = room.getMembers().size();
        List<String> currentVote = room.getAllVote();

        if(currentVote.size()<memberCount){
            return new MatchResponse("WAITING", null);
        }

        List<Rule> rules = ruleRepository.findByRoomCode(roomCode);

        if(rules.isEmpty()){
            return new MatchResponse("MISMATCH", null);
        }else{
            List<String> successRule = rules.stream().map(Rule::getRule).toList();
            return new MatchResponse("MATCH", successRule);
        }
    }
}
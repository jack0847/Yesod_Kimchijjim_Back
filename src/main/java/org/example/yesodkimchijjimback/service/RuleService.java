package org.example.yesodkimchijjimback.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.domain.Room;
import org.example.yesodkimchijjimback.domain.RoomMember;
import org.example.yesodkimchijjimback.domain.Rule;
import org.example.yesodkimchijjimback.domain.User;
import org.example.yesodkimchijjimback.dto.rule.MatchResponse;
import org.example.yesodkimchijjimback.dto.rule.RuleRequest;
import org.example.yesodkimchijjimback.dto.rule.RuleSummaryResponse;
import org.example.yesodkimchijjimback.dto.rule.UpdateRuleResponse;
import org.example.yesodkimchijjimback.repository.RoomMemberRepository;
import org.example.yesodkimchijjimback.repository.RoomRepository;
import org.example.yesodkimchijjimback.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.tomcat.util.http.parser.HttpParser.isNumeric;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RoomRepository roomRepository;
    private final RuleRepository ruleRepository;
    private final RoomMemberRepository roomMemberRepository;


    @Transactional
    public MatchResponse submitRule(String roomCode, RuleRequest ruleRequest, Long userId) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        if (!room.getAllVote().isEmpty() && room.getAllVote().get(0).matches("\\d+")) { //matches("\\d+") 이 코드가 투표함 안에 다 숫자인지 확인하고 숫자면 true 아니면 false 반환
            room.getAllVote().clear();
        }

        List<String> voteList = new ArrayList<>(ruleRequest.getOpinion()); // 사용자가 쓴 규칙을 List에 넣기
        String voteString = String.join(",", voteList); // 규칙 List를 하나의 String으로 받기
        room.addVote(voteString); // 하나의 String을 Vote 즉 투표함에 넣기


        //수정
        boolean amIHost = isHost(room, userId); // 방장인지 확인

        int memberCount = room.getMembers().size(); //room에 있는 멤버 수 세기
        List<String> currentVote = room.getAllVote(); // room에 있는 투표함 가져오기
        int currentVotes = currentVote.size();

        if (currentVote.size() < memberCount) { // 멤버수보다 투표수가 적으면 프론트한테 WAITING 보내기
            return new MatchResponse(currentVotes, "WAITING", null, amIHost, "category");
        }
        //모든 투표를 하나의 String 으로 만들고 다시 ,로 하나씩 나누기 split은 자르면 갯수가 정해지기 때문에 []로 받음
        String[] allVoteArray = String.join(",", currentVote).split(",");
        String Guitar = "다른 의견이 있어요"; //기타의견을 프론트한테 다른 의견이 있어요를 받고 이걸 받으면 바로 mismatch
        boolean isGuitar = false;
        Map<String, Integer> ruleCount = new HashMap<>(); //id, 갯수를 써주는 HashMap사용

        for (String vote : allVoteArray) { // vote에 하나씩 받기
            if (vote.equals(Guitar)) {  //Guitar이면 isGuitar true 전환
                isGuitar = true;
            }
            // 규칙갯수 세주는 로직 (vote, 갯수)를 지정해줌 vote(ID)받아오고 뒤에는 그전에 있었는지를 확인해줌
            // 있었으면 불러와서 +1 해주고 없었으면 default값 0+1
            ruleCount.put(vote, ruleCount.getOrDefault(vote, 0) + 1);
        }

        List<String> successRule = new ArrayList<>();

        for (String key : ruleCount.keySet()) { //map에서 쓰이는 고유한 keyset사용 (ruleCount의 String(ID)만 각각 가져옴
            if (ruleCount.get(key) == memberCount) { //key 즉 ID에 해당하는 갯수를 가져와서 member수와 같은지 확인함
                successRule.add(key); //같으면 successRule이라는 List에 더하기
            }
        }
        if (successRule.isEmpty() || isGuitar) { //즉 합의된 규칙이 없거나 기타의견이 있어요 라는 항목이 있을시 mismatch

            List<String> allOpinion = new ArrayList<>(ruleCount.keySet()); //사람들이 선택한 의견들을 다 넣음
            Collections.sort(allOpinion); // 보기 좋게 정리함
            return new MatchResponse(currentVotes, "MISMATCH", allOpinion, amIHost, "category"); // MISMATCH와 선택한 의견들 리스폰
        }
        for (String agreeRule : successRule) { // 규칙들 저장하기
            Rule rule = Rule.builder()
                    .room(room) //방 정보
                    .rule(agreeRule)//동의된 룰
                    .questionId(ruleRequest.getQuestionId()) //수정함
                    .build();
            ruleRepository.save(rule);
        }

        return new MatchResponse(currentVotes, "MATCH", successRule, amIHost, "category"); // 규칙이 합의 되면 MATCH와 successRule 주기
    }

    public MatchResponse redirect(String roomCode, Long userId) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        if (!room.getAllVote().isEmpty() && room.getAllVote().get(0).matches("\\d+")) { //matches("\\d+") 이 코드가 투표함 안에 다 숫자인지 확인하고 숫자면 true 아니면 false 반환
            room.getAllVote().clear(); // 그 전 startNext에서 있었던 userId나 count들 지움
        }

        boolean amIHost = isHost(room, userId);

        int memberCount = room.getMembers().size();
        List<String> currentVote = room.getAllVote();
        int currentVotes = currentVote.size();

        for (String hostAdd : currentVote) {
            if (hostAdd.startsWith("GO_AFTER_MISMATCH")) {
                String debateRule = hostAdd.replace("GO_AFTER_MISMATCH", "");

                List<String> afterRule = new ArrayList<>();
                afterRule.add(debateRule);

                return new MatchResponse(currentVotes, "AFTER_MISMATCH", afterRule, amIHost, "category");
            }
        }

        if (currentVotes < memberCount) {
            return new MatchResponse(currentVotes, "WAITING", null, amIHost, "category");
        }
        //합의된 룰이 없을때 투표함 열어서 리스트 만들기
        String[] allVoteArray = String.join(",", currentVote).split(",");
        String Guitar = "다른 의견이 있어요";
        boolean isGuitar = false;
        Map<String, Integer> ruleCount = new HashMap<>();

        for (String voteGuitar : allVoteArray) {
            if (voteGuitar.equals(Guitar)) isGuitar = true;
            ruleCount.put(voteGuitar, ruleCount.getOrDefault(voteGuitar, 0) + 1);
        }

        List<String> successRule = new ArrayList<>();

        for (String key : ruleCount.keySet()) { //map에서 쓰이는 고유한 keyset사용 (ruleCount의 String(ID)만 각각 가져옴
            if (ruleCount.get(key) == memberCount) { //key 즉 ID에 해당하는 갯수를 가져와서 member수와 같은지 확인함
                successRule.add(key); //같으면 successRule이라는 List에 더하기
            }
        }

        if (successRule.isEmpty() || isGuitar) {
            List<String> allOpinion = new ArrayList<>(ruleCount.keySet());
            Collections.sort(allOpinion);
            return new MatchResponse(currentVotes, "MISMATCH", allOpinion, amIHost, "category");
        } else {
            // MATCH일 때 투표함이 비워지지 않았으므로 여기로 들어옴
            room.getAllVote().clear();
            return new MatchResponse(currentVotes, "MATCH", successRule, amIHost, "category");
        }
    }

    @Transactional
    public String startNext(String roomCode, Long userId) { // 전원 확인했어요 누를 시 넘어가기
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방이 없습니다."));

        if (!room.getAllVote().isEmpty() && !room.getAllVote().get(0).matches("\\d+")) { //matches("\\d+") 이 코드가 투표함 안에 다 숫자인지 확인하고 숫자면 true 아니면 false 반환
            room.getAllVote().clear(); //이건 그 전 문자열 다 지우기 투표할때 문자열 넣으니까
        }
        String userVote = String.valueOf(userId);

        if (!room.getAllVote().contains(userVote)) {
            room.addVote(userVote);
        }
        int memberCount = room.getMembers().size();
        int confirmCount = room.getAllVote().size();

        if (confirmCount >= memberCount) {
            return "PASS";
        } else {
            return "WAITING";
        }
    }

    @Transactional
    public void confirmRule(String roomCode, RuleRequest ruleRequest, Long userId) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방 코드입니다."));

        if (!isHost(room, userId)) {
            throw new IllegalArgumentException("방장만 규칙을 확정할 수 있습니다!");
        }
        String debateRule = ruleRequest.getOpinion().get(0);
        Rule rule = Rule.builder()
                .room(room)
                .rule(debateRule)
                .questionId(ruleRequest.getQuestionId())
                .build();
        ruleRepository.save(rule);

        room.getAllVote().clear();
        room.addVote("GO_AFTER_MISMATCH"+debateRule);
    }

    public boolean isHost(Room room, Long userId) {
        return roomMemberRepository.findByRoomAndUserId(room, userId)
                .map(RoomMember::isHost)
                .orElseThrow(()->new IllegalArgumentException("찾는 사람이 없습니다."));
    }

    @Transactional
    public void addRule(String roomCode, Long userId, RuleRequest request) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        if (!isHost(room, userId)) {
            throw new IllegalArgumentException("방장만 규칙을 추가할 수 있습니다.");
        }
        Rule newRule = Rule.builder()
                .room(room)
                .questionId(request.getQuestionId())
                .rule(request.getOpinion().get(0)) // 리스트에서 내용 하나 꺼내기
                .build();

        ruleRepository.save(newRule);
    }

    @Transactional
    public void updateRule(String roomCode, Long userId, Long ruleId, RuleRequest request) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        if (!isHost(room, userId)) {
            throw new IllegalArgumentException("방장만 규칙을 수정 할 수 있습니다.");
        }
        Rule rule = ruleRepository.findByIdAndRoom(ruleId, room)
                .orElseThrow(() -> new IllegalArgumentException("해당 규칙이 존재하지 않습니다."));
        rule.updateRule(request.getOpinion().get(0));
    }

    @Transactional
    public void statusComplete(String roomCode, Long userId) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        if (!isHost(room, userId)) {
            throw new IllegalArgumentException("방장만 누를 수 있습니다.");
        }
        room.updateStatus("COMPLETE");
    }

    public UpdateRuleResponse getRuleSummary(String roomCode, Long userId, RuleRequest request) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("찾는 방이 없습니다."));

        boolean amIHost = isHost(room, userId);

        List<RuleSummaryResponse> rules = ruleRepository.findAllByRoom(room).stream()
                .map(rule -> RuleSummaryResponse.builder()
                        .id(rule.getId())
                        .rule(rule.getRule())
                        .questionId(rule.getQuestionId())
                        .build())
                .toList();


        String category = request.getCategory();

        return UpdateRuleResponse.builder()
                .status(room.getStatus()) // 방 상태 (MODIFYING, COMPLETE 등)
                .amIHost(amIHost)
                .data(rules)
                .category(category)// DB에서 가져온 규칙 리스트 그대로 전달
                .build();
    }


}
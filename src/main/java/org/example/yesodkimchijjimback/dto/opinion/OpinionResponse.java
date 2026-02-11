package org.example.yesodkimchijjimback.dto.opinion;

import lombok.Builder;
import lombok.Data;
import org.example.yesodkimchijjimback.domain.Opinion;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class OpinionResponse {
    private Long id;
    private Long authorId;
    private String content;
    private String nickname;
    private String createdDate;
    private String createdTime;

    public static OpinionResponse fromResponse(Opinion opinion){
        return OpinionResponse.builder()
                .id(opinion.getId())
                .authorId(opinion.getUser().getId())
                .content(opinion.getContent())
                .nickname(opinion.getNickname())
                .createdDate(opinion.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .createdTime(opinion.getCreatedDate().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }
}

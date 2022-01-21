package scra.qnaboard.web.dto.tag.search;

import lombok.Getter;
import scra.qnaboard.domain.entity.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TagSearchResultDTO {

    private String searchKeyword;
    private List<TagSimpleDTO> tags = new ArrayList<>();

    public static TagSearchResultDTO from(List<Tag> tags, String searchKeyword) {
        TagSearchResultDTO dto = new TagSearchResultDTO();

        dto.tags = tags.stream()
                .map(TagSimpleDTO::from)
                .collect(Collectors.toList());
        dto.searchKeyword = searchKeyword;

        return dto;
    }
}

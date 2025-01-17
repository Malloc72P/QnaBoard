package scra.qnaboard.web.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import scra.qnaboard.configuration.auth.SecurityConfig;
import scra.qnaboard.configuration.auth.SessionUser;
import scra.qnaboard.service.QuestionService;
import scra.qnaboard.service.SearchInputParserService;
import scra.qnaboard.dto.question.edit.QuestionWithTagDTO;
import scra.qnaboard.dto.question.detail.QuestionDetailDTO;
import scra.qnaboard.dto.question.list.QuestionSummaryDTO;
import scra.qnaboard.dto.question.search.ParsedSearchQuestionDTO;
import scra.qnaboard.dto.question.search.SearchQuestionDTO;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = QuestionController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private SearchInputParserService parserService;

    @Test
    @WithMockUser
    @DisplayName("질문_목록조회_테스트")
    void testQuestionSearch () throws Exception {
        //given
        QuestionSummaryDTO questionSummaryDTO = QuestionSummaryDTO.builder()
                .title("question-title-1")
                .authorName("author-name-1")
                .build();

        //given
        List<QuestionSummaryDTO> list = new ArrayList<>();
        list.add(questionSummaryDTO);

        //given
        given(parserService.parse(new SearchQuestionDTO("")))
                .willReturn(new ParsedSearchQuestionDTO());

        //given
        given(questionService.searchQuestions(new ParsedSearchQuestionDTO(), 0, 20))
                .willReturn(new PageImpl<>(list));

        //when
        ResultActions resultActions = mockMvc.perform(get("/questions"));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpectAll(
                        content().string(Matchers.containsString(questionSummaryDTO.getTitle())),
                        content().string(Matchers.containsString(questionSummaryDTO.getAuthorName()))
                );
    }

    @Test
    @WithMockUser
    void 질문_상세조회_테스트() throws Exception {
        //given
        QuestionDetailDTO detailDTO = QuestionDetailDTO.builder()
                .title("question-title-1")
                .content("question-content-1")
                .authorName("authorName")
                .build();
        given(questionService.questionDetail(1L))
                .willReturn(detailDTO);

        //when
        ResultActions resultActions = mockMvc.perform(get("/questions/" + 1L));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpectAll(
                        content().string(Matchers.containsString(detailDTO.getTitle())),
                        content().string(Matchers.containsString(detailDTO.getContent())),
                        content().string(Matchers.containsString(detailDTO.getAuthorName()))
                );
    }

    @Test
    @WithMockUser
    void 질문_생성폼_테스트() throws Exception {
        //given
        //when
        ResultActions perform = mockMvc.perform(get("/questions/form"));
        //then
        perform.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 질문_수정폼_테스트() throws Exception {
        //given
        QuestionWithTagDTO questionWithTagDTO = QuestionWithTagDTO.builder()
                .title("title-1")
                .content("content-1")
                .tags(new ArrayList<>())
                .build();
        given(questionService.questionWithTag(1L))
                .willReturn(questionWithTagDTO);

        //when
        ResultActions perform = mockMvc.perform(get("/questions/1/editForm"));
        //then
        perform.andExpect(status().isOk())
                .andExpectAll(
                        content().string(Matchers.containsString(questionWithTagDTO.getTitle())),
                        content().string(Matchers.containsString(questionWithTagDTO.getContent()))
                );
    }

    @Test
    @WithMockUser
    void 질문_생성_테스트() throws Exception {
        //given
        long newQuestionId = 1L;
        given(questionService.createQuestion(1L, "title-1", "content-1", new ArrayList<>()))
                .willReturn(newQuestionId);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/questions")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "title-1")
                        .param("content", "content-1")
                        .param("tags", "")
                        .with(csrf())
                        .sessionAttr("user", new SessionUser(1L, "", ""))
        );

        //then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/questions/" + newQuestionId));
    }

    @Test
    @WithMockUser
    void 질문_삭제_테스트() throws Exception {
        //given
        long requesterId = 1L;
        long questionId = 2L;

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/questions/" + questionId + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .sessionAttr("user", new SessionUser(requesterId, "", ""))
        );

        //then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/notify?title={.+}&content={.+}"));
    }

    @Test
    @WithMockUser
    void 질문_수정_테스트() throws Exception {
        //given
        long requesterId = 1L;
        long questionId = 2L;

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/questions/" + questionId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "title-1")
                        .param("content", "content-1")
                        .param("tags", "")
                        .with(csrf())
                        .sessionAttr("user", new SessionUser(requesterId, "", ""))
        );

        //then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/questions/" + questionId));
    }
}

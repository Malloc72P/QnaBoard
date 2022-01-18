package scra.qnaboard.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import scra.qnaboard.configuration.auth.SecurityConfig;
import scra.qnaboard.configuration.auth.SessionUser;
import scra.qnaboard.domain.entity.member.Member;
import scra.qnaboard.service.MemberService;
import scra.qnaboard.web.dto.member.MemberDTO;
import scra.qnaboard.web.dto.member.MemberListDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockitoSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource message;

    @MockBean
    private MemberService memberService;

    @Test
    @WithMockUser(roles = "USER")
    void 회원목록조회테스트() throws Exception {
        //given
        List<MemberDTO> memberDTOS = new ArrayList<>();
        memberDTOS.add(new MemberDTO(1L, "member-1"));
        memberDTOS.add(new MemberDTO(2L, "member-2"));
        //given
        given(memberService.findAllMember()).willReturn(new MemberListDTO(memberDTOS));

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/members")
                        .with(csrf())
                        .secure(true));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpectAll(
                        content().string(containsString(memberDTOS.get(0).getMemberName())),
                        content().string(containsString(memberDTOS.get(1).getMemberName()))
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    void 회원탈퇴하면_알림페이지로_리다이렉션해야함() throws Exception {
        //given
        long memberId = 1L;
        //given
        Locale locale = Locale.KOREA;
        String titleMessage = this.message.getMessage("ui.notify.members.delete.title", null, locale);
        String contentMessage = this.message.getMessage("ui.notify.members.delete.content", null, locale);
        //given
        titleMessage = URLEncoder.encode(titleMessage, StandardCharsets.UTF_8.toString());
        contentMessage = URLEncoder.encode(contentMessage, StandardCharsets.UTF_8.toString());

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/members/sign-out")
                        .with(csrf())
                        .sessionAttr("user", new SessionUser(memberId, "name", "email"))
                        .secure(true));

        //then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notify?title=" + titleMessage + "&content=" + contentMessage));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 로그아웃하면_질문목록조회페이지로_리다이렉션해야함() throws Exception {
        //given
        long memberId = 1L;

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/members/log-out")
                        .with(csrf())
                        .sessionAttr("user", new SessionUser(memberId, "name", "email"))
                        .secure(true));

        //then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/questions"));
    }
}

package scra.qnaboard.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import scra.qnaboard.domain.entity.Tag;
import scra.qnaboard.domain.entity.member.Member;
import scra.qnaboard.domain.entity.member.MemberRole;
import scra.qnaboard.domain.entity.post.Question;
import scra.qnaboard.domain.repository.MemberRepository;
import scra.qnaboard.domain.repository.tag.TagRepository;
import scra.qnaboard.service.exception.member.MemberNotFoundException;
import scra.qnaboard.service.exception.question.AlreadyDeletedQuestionException;
import scra.qnaboard.service.exception.question.delete.QuestionDeleteFailedException;
import scra.qnaboard.service.exception.question.edit.QuestionEditFailedException;
import scra.qnaboard.service.exception.question.edit.UnauthorizedQuestionEditException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * QuestionService와 Repository를 통합해서 테스트함.
 */
@SpringBootTest
@Transactional
class QuestionServiceIntegrationTest {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TagRepository tagRepository;

    @Test
    void 질문생성_테스트() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //when
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);
        Question findQuestion = questionService.findQuestion(questionId);

        //then
        assertThat(findQuestion.getTitle()).isEqualTo(title);
        assertThat(findQuestion.getContent()).isEqualTo(content);
        assertThat(findQuestion.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(findQuestion.getQuestionTags().size()).isEqualTo(tagIds.size());
    }

    @Test
    void 질문생성_실패_테스트() {
        //given
        String title = "title-1";
        String content = "content-1";
        List<Long> tagIds = new ArrayList<>();

        //when & then
        assertThatThrownBy(() -> questionService.createQuestion(1L, title, content, tagIds))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 질문삭제_테스트() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //given
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);

        //when
        questionService.deleteQuestion(author.getId(), questionId);

        //then
        assertThatThrownBy(() -> questionService.findQuestion(questionId))
                .isInstanceOf(AlreadyDeletedQuestionException.class);

    }

    @Test
    void 질문삭제_실패_테스트_작성자및관리자아님() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));
        Member anotherAuthor = memberRepository.save(new Member("nickname", "email", MemberRole.USER));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //given
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);

        //when & then
        assertThatThrownBy(() -> questionService.deleteQuestion(anotherAuthor.getId(), questionId))
                .isInstanceOf(QuestionDeleteFailedException.class);
    }

    @Test
    void 질문삭제_테스트_관리자는_성공해야함() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));
        Member anotherAuthor = memberRepository.save(new Member("nickname", "email", MemberRole.ADMIN));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //given
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);

        //when
        questionService.deleteQuestion(anotherAuthor.getId(), questionId);

        //then
        assertThatThrownBy(() -> questionService.findQuestion(questionId))
                .isInstanceOf(AlreadyDeletedQuestionException.class);
    }

    @Test
    void 질문수정_테스트() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //given
        String newTitle = "new-title-1";
        String newContent = "new-content-1";

        //given
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);

        //when
        questionService.editQuestion(author.getId(), questionId, newTitle, newContent, tagIds);
        Question findQuestion = questionService.findQuestion(questionId);

        //then
        assertThat(findQuestion.getTitle()).isEqualTo(newTitle);
        assertThat(findQuestion.getContent()).isEqualTo(newContent);
        assertThat(findQuestion.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(findQuestion.getQuestionTags().size()).isEqualTo(tagIds.size());
    }

    @Test
    void 질문수정_실패_테스트_작성자및관리자아님() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));
        Member anotherAuthor = memberRepository.save(new Member("nickname", "email", MemberRole.USER));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //given
        String newTitle = "new-title-1";
        String newContent = "new-content-1";

        //given
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);

        //when & then
        assertThatThrownBy(() ->
                questionService.editQuestion(anotherAuthor.getId(), questionId, newTitle, newContent, tagIds))
                .isInstanceOf(QuestionEditFailedException.class)
                .isInstanceOf(UnauthorizedQuestionEditException.class);
        Question findQuestion = questionService.findQuestion(questionId);

        //then
        assertThat(findQuestion.getTitle()).isEqualTo(title);
        assertThat(findQuestion.getContent()).isEqualTo(content);
        assertThat(findQuestion.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(findQuestion.getQuestionTags().size()).isEqualTo(tagIds.size());
    }

    @Test
    void 질문수정_테스트_관리자는_성공해야함() throws Exception {
        //given
        Member author = memberRepository.save(new Member("nickname", "email", MemberRole.USER));
        Member anotherAuthor = memberRepository.save(new Member("nickname", "email", MemberRole.ADMIN));

        //given
        String title = "title-1";
        String content = "content-1";

        //given
        Tag tag = new Tag(author, "tag", "tag-description");
        tagRepository.save(tag);

        //given
        List<Long> tagIds = new ArrayList<>();
        tagIds.add(tag.getId());

        //given
        String newTitle = "new-title-1";
        String newContent = "new-content-1";

        //given
        long questionId = questionService.createQuestion(author.getId(), title, content, tagIds);

        //when & then
        questionService.editQuestion(anotherAuthor.getId(), questionId, newTitle, newContent, tagIds);
        Question findQuestion = questionService.findQuestion(questionId);

        //then
        assertThat(findQuestion.getTitle()).isEqualTo(newTitle);
        assertThat(findQuestion.getContent()).isEqualTo(newContent);
        assertThat(findQuestion.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(findQuestion.getQuestionTags().size()).isEqualTo(tagIds.size());
    }
}

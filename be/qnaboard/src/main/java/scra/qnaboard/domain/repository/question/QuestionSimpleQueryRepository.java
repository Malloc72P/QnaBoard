package scra.qnaboard.domain.repository.question;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import scra.qnaboard.domain.entity.QTag;
import scra.qnaboard.domain.entity.post.QQuestion;
import scra.qnaboard.domain.entity.post.Question;
import scra.qnaboard.domain.entity.questiontag.QQuestionTag;

import java.util.Optional;

import static scra.qnaboard.domain.entity.QMember.member;
import static scra.qnaboard.domain.entity.QTag.tag;
import static scra.qnaboard.domain.entity.post.QQuestion.question;
import static scra.qnaboard.domain.entity.questiontag.QQuestionTag.questionTag;

/**
 * JPQL을 직접 작성해야하지만, 복잡한 쿼리는 아닌경우 여기에서 처리함
 */
@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionSimpleQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QuestionBooleanExpressionSupplier expressionSupplier;

    /**
     * 질문 엔티티와 작성자를 함께 조회해서(패치조인) 반환한다
     *
     * @param questionId 조회할 질문게시글의 아이디
     * @return 질문 엔티티(작성자 포함)
     */
    public Optional<Question> questionWithAuthor(long questionId) {
        Question findQuestion = queryFactory.selectFrom(question)
                .innerJoin(question.author, member).fetchJoin()
                .where(expressionSupplier.questionNotDeletedAndEqualsId(questionId))
                .fetchOne();

        return Optional.ofNullable(findQuestion);
    }

    public Optional<Question> questionWithTag(long questionId) {
        Question question = queryFactory.selectFrom(QQuestion.question)
                .leftJoin(QQuestion.question.questionTags, questionTag).fetchJoin()
                .innerJoin(questionTag.tag, tag).fetchJoin()
                .where(QQuestion.question.id.eq(questionId))
                .fetchOne();

        return Optional.ofNullable(question);
    }
}

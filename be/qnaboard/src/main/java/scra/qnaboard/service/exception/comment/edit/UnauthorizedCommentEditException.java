package scra.qnaboard.service.exception.comment.edit;

public class UnauthorizedCommentEditException extends CommentEditFailedException {
    private static final String UNAUTHORIZED = "댓글을 수정할 권한이 없습니다. 댓글의 작성자가 아니거나 관리자가 아닙니다";

    public UnauthorizedCommentEditException(long commentId, long requesterId) {
        super(UNAUTHORIZED + " commentId : " + commentId + " requesterId : " + requesterId);
    }

    @Override
    public String descriptionMessageCode() {
        return "ui.error.page-desc-edit-failed-comment-unauthorized";
    }
}

package sample.dto;


public class GetSavedBoardResponse {
    private BoardDto boardDto;

    @java.beans.ConstructorProperties({"boardDto"})
    private GetSavedBoardResponse(BoardDto boardDto) {
        this.boardDto = boardDto;
    }

    public GetSavedBoardResponse() {
    }

    public static GetSavedBoardResponse of(BoardDto boardDto) {
        return new GetSavedBoardResponse(boardDto);
    }

    public BoardDto getBoardDto() {
        return this.boardDto;
    }

    public void setBoardDto(BoardDto boardDto) {
        this.boardDto = boardDto;
    }
}

package sample.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SaveBoardRequest {
    public SaveBoardRequest(BoardDto boardDto) {
        this.boardDto = boardDto;
    }

    public BoardDto getBoardDto() {
        return boardDto;
    }

    public void setBoardDto(BoardDto boardDto) {
        this.boardDto = boardDto;
    }

    private BoardDto boardDto;

}

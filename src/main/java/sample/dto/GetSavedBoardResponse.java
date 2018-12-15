package sample.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@AllArgsConstructor(staticName = "of")
public class GetSavedBoardResponse {
    public BoardDto getBoardDto() {
        return boardDto;
    }

    private BoardDto boardDto;
}

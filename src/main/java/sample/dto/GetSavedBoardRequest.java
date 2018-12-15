package sample.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class GetSavedBoardRequest {
    private String name;

    public String getName() {
        return name;
    }
}

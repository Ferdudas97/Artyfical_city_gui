package sample.dto;

import java.util.Set;

public class BoardDto {
    private String boardName;
    private Set<NodeDto> nodeDtos;

    @java.beans.ConstructorProperties({"boardName", "nodeDtos"})
    private BoardDto(String boardName, Set<NodeDto> nodeDtos) {
        this.boardName = boardName;
        this.nodeDtos = nodeDtos;
    }

    public BoardDto() {
    }

    public static BoardDto of(String boardName, Set<NodeDto> nodeDtos) {
        return new BoardDto(boardName, nodeDtos);
    }

    public String getBoardName() {
        return this.boardName;
    }

    public Set<NodeDto> getNodeDtos() {
        return this.nodeDtos;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public void setNodeDtos(Set<NodeDto> nodeDtos) {
        this.nodeDtos = nodeDtos;
    }
}

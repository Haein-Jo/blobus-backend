package project.blobus.Backend.community;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "posts") // posts 테이블에 매핑
public class CommunityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성
    private Long id;

    @Column(nullable = false, length = 255) // 제목 컬럼
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT") // 본문 내용 컬럼
    private String content;

    @Column(nullable = false) // 작성자 ID
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // 게시글 유형
    private BoardType boardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // 작성자 유형
    private UserType userType;

    @Builder.Default
    @Column(nullable = false, updatable = false) // 생성 일자
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false) // 수정 일자
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "communityEntity", cascade = CascadeType.ALL, orphanRemoval = true) // 댓글과의 관계
    private List<CommunityComment> comments = new ArrayList<>();

    public CommunityEntity(Long id, String title, String content, Long authorId, BoardType boardType, UserType userType, LocalDateTime createdAt, LocalDateTime updateAt, List<CommunityComment> comments) {
    }

    @PreUpdate // 엔티티가 업데이트되기 직전에 호출
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 게시판 유형 Enum
    public enum BoardType {
        FREE,       // 자유 게시판
        SUGGESTION  // 건의 게시판
    }

    // 작성자 유형 Enum
    public enum UserType {
        YOUTH,      // 청년
        CORPORATE   // 기업
    }

    public CommunityEntity() {

    }
}
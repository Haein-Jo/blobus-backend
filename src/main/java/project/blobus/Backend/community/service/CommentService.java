package project.blobus.Backend.community.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.blobus.Backend.common.dto.PageRequestDTO;
import project.blobus.Backend.common.dto.PageResponseDTO;
import project.blobus.Backend.community.dto.CommentDTO;
import project.blobus.Backend.community.entity.CommuntiyComment;
import project.blobus.Backend.community.repository.CommentRepository;
import project.blobus.Backend.community.repository.PostRepository;
import project.blobus.Backend.community.util.CommunityMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final CommentRepository commentRepository;

    // 댓글 조회
    public PageResponseDTO<CommentDTO> getList(PageRequestDTO pageRequestDTO, Long postId) {
        log.info("Comment Get List");

        List<CommentDTO> commentList = commentRepository.findAllByPost_Id(postId).stream()
                .map(CommunityMapper::commentEntityToDto)
                .collect(Collectors.toList());

        commentList = commentList.stream()
                .sorted((b1, b2) -> b2.getId().compareTo(b1.getId()))
                .collect(Collectors.toList());

        int totalCount = commentList.size();

        int startIndex = (pageRequestDTO.getPage() - 1) * pageRequestDTO.getSize();
        int endIndex = Math.min(startIndex + pageRequestDTO.getSize(), totalCount);
        List<CommentDTO> dtoList = commentList.subList(startIndex, endIndex);

        return PageResponseDTO.<CommentDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }

    // 댓글 등록
    public void register(CommentDTO dto) {
        log.info("Comment Register");

        CommuntiyComment comment = CommunityMapper.commentDtoToEntity(dto);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setPost(postRepository.findById(dto.getPostId()).orElseThrow());
        commentRepository.save(comment);
    }

    // 댓글 수정
    public void modify(CommentDTO dto) {
        log.info("Comment Modify");

        CommuntiyComment comment = commentRepository.findById(dto.getId()).orElseThrow();

        comment.setId(dto.getId());
        comment.setContent(dto.getContent());
        comment.setVisibility(dto.isVisibility());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void remove(Long id) {
        log.info("Comment Remove");

        commentRepository.deleteById(id);
    }
}

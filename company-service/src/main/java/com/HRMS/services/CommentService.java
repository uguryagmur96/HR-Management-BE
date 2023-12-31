package com.HRMS.services;

import com.HRMS.dto.request.AddCommentRequestDto;
import com.HRMS.dto.request.UpdateCommentRequestDto;
import com.HRMS.exceptions.CompanyException;
import com.HRMS.exceptions.ErrorType;
import com.HRMS.mapper.ICommentMapper;
import com.HRMS.mapper.ICompanyMapper;
import com.HRMS.repository.ICommentRepository;
import com.HRMS.repository.ICompanyRepository;
import com.HRMS.repository.entity.Comment;
import com.HRMS.repository.entity.Company;
import com.HRMS.repository.enums.EStatus;
import com.HRMS.utils.JwtTokenManager;
import com.HRMS.utils.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService extends ServiceManager<Comment, String> {

    private final ICommentRepository repository;
    private final JwtTokenManager tokenManager;
    private final CompanyService companyService;
    private final ICompanyRepository companyRepository;

    public CommentService(ICommentRepository repository,JwtTokenManager tokenManager,CompanyService companyService,
                          ICompanyRepository companyRepository) {
        super(repository);
        this.repository = repository;
        this.tokenManager=tokenManager;
        this.companyService=companyService;
        this.companyRepository=companyRepository;
    }

    public Boolean addComment(AddCommentRequestDto requestDto,String token) {
       Optional<Long> id=tokenManager.getByIdFromToken(token);
       if (id.isEmpty()) {
        throw new CompanyException(ErrorType.USER_NOT_FOUND);
       }
       Optional<String> role=tokenManager.getRoleFromToken(token.toString());
       if(role.isPresent() && role.get().equals("EMPLOYEE")){
         Optional<Company>company =companyRepository.findByCompanyNameEqualsIgnoreCase(requestDto.getCompanyName());
         if (company.get().getSubPackageType().equals("SILVER")||company.get().getSubPackageType().equals("GOLD")){
             Comment comment = ICommentMapper.INSTANCE.toCommentFromDto(requestDto);
             if (company.get().getSubPackageType().equals("GOLD")){
                 comment.setStatus(EStatus.PENDING);
                 comment.setCompanyId(company.get().getId());
                 save(comment);
                 return true;
             }
             comment.setStatus(EStatus.PENDING);
             comment.setCompanyId(company.get().getId());
             comment.setRate(null);
             save(comment);
             return true;
         }
        throw new CompanyException(ErrorType.UNAUTHORIZED_PACKAGE);
       }
        throw new CompanyException(ErrorType.UNAUTHORIZED_USER);
    }

    public Boolean updateComment(UpdateCommentRequestDto requestDto, String token) {
        Optional<String> role = tokenManager.getRoleFromToken(token.toString());
        if (role.isEmpty()) {
            throw new CompanyException(ErrorType.UNAUTHORIZED_USER);
        }
        Comment comment = repository.findOptionalById(requestDto.getId()).orElseThrow(() -> new CompanyException(ErrorType.COMMENT_NOT_FOUND));
        if (comment.getStatus() == EStatus.PENDING) {
            if (requestDto.getState() == true) {
                comment.setStatus(EStatus.APPROVED);
            } else {
                comment.setStatus(EStatus.REJECTED);
            }
            update(comment);
            return true;
        }
        throw new CompanyException(ErrorType.BAD_REQUEST_ERROR);
    }

    public List<Comment> getAllPendingComment() {
        List<Comment> comments = repository.findAll();
        List<Comment> pendingComments = comments.stream().filter(comment -> comment.getStatus().equals(EStatus.PENDING)).toList();
        return pendingComments;
    }
    public List<Comment> getAllApprovedComment(String companyId) {
        Optional<List<Comment>> commentsOptional = repository.findAllByCompanyId(companyId);

        if (commentsOptional.isPresent()) {
            List<Comment> comments = commentsOptional.get();
            List<Comment> approvedComments = comments.stream()
                    .filter(comment -> comment.getStatus().equals(EStatus.APPROVED))
                    .toList();
            return approvedComments;
        } else {
            return Collections.emptyList();
        }
    }
}

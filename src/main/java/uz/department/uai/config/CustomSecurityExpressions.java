package uz.department.uai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.department.uai.organization.domain.Department;
import uz.department.uai.user.domain.User;
//import uz.department.uai.repository.DocumentRepository; // Buni o'zingiz yaratasiz

@Service("customSecurity")
@RequiredArgsConstructor
public class CustomSecurityExpressions {

//    private final DocumentRepository documentRepository;
//
//    public boolean canAccessDocument(Long documentId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return false;
//        }
//
//        User currentUser = (User) authentication.getPrincipal();
//        Department userDepartment = currentUser.getEmployee().getEmployment().getDepartment();
//
//        return documentRepository.findById(documentId)
//                .map(document -> document.getDepartment().equals(userDepartment))
//                .orElse(false);
//    }
}
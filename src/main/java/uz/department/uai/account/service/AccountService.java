package uz.department.uai.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.department.uai.account.dto.AccountDTO;
import uz.department.uai.auth.domain.Authority;
import uz.department.uai.organization.domain.Employment;
import uz.department.uai.user.domain.Employee;
import uz.department.uai.user.domain.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    public AccountDTO getAccountDetails(User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Current user not found");
        }
        return convertToDto(currentUser);
    }

    private AccountDTO convertToDto(User user) {
        Employee employee = user.getEmployee();
        Employment employment = (employee != null) ? employee.getEmployment() : null;

        String position = (employment != null) ? employment.getPositionUzUz() : null;
        String department = (employment != null && employment.getDepartment() != null)
                ? employment.getDepartment().getNameUzUz() : null;
        String branch = (employment != null && employment.getDepartment() != null && employment.getDepartment().getBranch() != null)
                ? employment.getDepartment().getBranch().getNameUzUz() : null;

        return AccountDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getLastName() + " " + user.getFirstName()) // Sizning formatga moslang
                .positionName(position)
                .departmentName(department)
                .branchName(branch)
                .userPhotoUrl(null) // Hozircha rasm yo'q
                .permissions(parsePermissions(user.getDomainAuthorities())) // Huquqlarni ajratamiz
                .build();
    }

    /**
     * "SUBJECT_ACTION" formatdagi huquqlar ro'yxatini
     * Map<"Subject", Set<"Action">> formatiga o'giradi.
     */
    private Map<String, Set<String>> parsePermissions(Set<Authority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Set<String>> permissionMap = new HashMap<>();

        for (Authority authority : authorities) {
            String[] parts = authority.getName().split("_");
            if (parts.length > 1) {
                String subject = parts[0].toLowerCase();
                String action = parts[1].toUpperCase();

                permissionMap.computeIfAbsent(subject, k -> new HashSet<>()).add(action);
            }
        }
        return permissionMap;
    }
}
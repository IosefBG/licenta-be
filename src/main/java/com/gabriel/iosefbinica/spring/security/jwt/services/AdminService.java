package com.gabriel.iosefbinica.spring.security.jwt.services;

import com.gabriel.iosefbinica.spring.security.jwt.domains.*;
import com.gabriel.iosefbinica.spring.security.jwt.models.ERole;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.UserWithMissingRoles;
import com.gabriel.iosefbinica.spring.security.jwt.models.dtos.UserWithRolesDTO;
import com.gabriel.iosefbinica.spring.security.jwt.repository.*;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RoleRepository roleRepository;
    private final UserProjectRepository userProjectRepository;
    private final TimesheetRepository timesheetRepository;

    public AdminService(UserRepository userRepository, ProjectRepository projectRepository,
                        RoleRepository roleRepository, UserProjectRepository userProjectRepository,
                        TimesheetRepository timesheetRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
        this.userProjectRepository = userProjectRepository;
        this.timesheetRepository = timesheetRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersWithUserRole() {
        return userRepository.findByRolesName(ERole.ROLE_USER);
    }

    public void addProject(String projectName, Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Error: Manager not found"));

        Project project = new Project();
        project.setProjectName(projectName);
        project.setManager(manager);

        projectRepository.save(project);
    }

    public List<User> getManagers() {
        return userRepository.findByRolesName(ERole.ROLE_MANAGER);
    }

    public List<Map<String, Object>> getManagersWithProjects() {
        List<User> managers = userRepository.findByRolesName(ERole.ROLE_MANAGER);
        List<Map<String, Object>> managersWithProjects = new ArrayList<>();

        for (User manager : managers) {
            List<Project> projects = projectRepository.findByManagerId(manager.getId());
            List<Map<String, Object>> projectList = new ArrayList<>();

            for (Project project : projects) {
                Map<String, Object> projectData = new HashMap<>();
                projectData.put("projectId", project.getId());
                projectData.put("projectName", project.getProjectName());
                projectData.put("managerId", manager.getId());
                projectData.put("managerUsername", manager.getUsername());
                projectList.add(projectData);
            }

            managersWithProjects.addAll(projectList);
        }

        return managersWithProjects;
    }

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public List<UserWithMissingRoles> getAllUsersWithMissingRoles() {
        List<User> users = userRepository.findAll();
        List<UserWithMissingRoles> usersWithMissingRoles = new ArrayList<>();

        for (User user : users) {
            List<Role> allRoles = roleRepository.findAll();
            Set<Role> userRoles = user.getRoles();

            List<Role> missingRoles = allRoles.stream()
                    .filter(role -> !userRoles.contains(role))
                    .collect(Collectors.toList());

            UserWithMissingRoles userWithMissingRoles = new UserWithMissingRoles(user, missingRoles);
            usersWithMissingRoles.add(userWithMissingRoles);
        }

        return usersWithMissingRoles;
    }


    public void addRoleForUserId(Long userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void deleteRoleForUserId(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Error: Role not found"));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public List<UserWithRolesDTO> getUsersWithRoles() {
        List<User> users = userRepository.findAll();
        List<UserWithRolesDTO> usersWithRoles = new ArrayList<>();

        for (User user : users) {
            UserWithRolesDTO userWithRoles = new UserWithRolesDTO(user);
            usersWithRoles.add(userWithRoles);
        }

        return usersWithRoles;
    }

    public UserProject addUserProject(Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long projectId = payload.get("projectId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ErrorMessage.USER_NOT_FOUND"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("ErrorMessage.PROJECT_NOT_FOUND"));

        // Check if the user is already added to the project
        UserProject existingUserProject = userProjectRepository.findByUserAndProject(user, project);
        if (existingUserProject != null) {
            throw new RuntimeException("ErrorMessage.USER_ALREADY_ADDED");
        }

        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);

        return userProjectRepository.save(userProject);
    }


    public List<UserProject> getUsersProjects() {
        return userProjectRepository.findAll();
    }

    public byte[] generateTimesheetReport(LocalDate fromDate, LocalDate toDate) throws IOException {
        List<Timesheet> timesheets = retrieveTimesheets(fromDate, toDate);

        Workbook workbook = generateTimesheetWorkbook(timesheets);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private List<Timesheet> retrieveTimesheets(LocalDate fromDate, LocalDate toDate) {
        return timesheetRepository.findBySelectedDateBetween(fromDate, toDate);
    }

    private Workbook generateTimesheetWorkbook(List<Timesheet> timesheets) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Timesheets");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("User Name");
        headerRow.createCell(2).setCellValue("User Email");
        headerRow.createCell(3).setCellValue("User Id");
        headerRow.createCell(4).setCellValue("Project Name");
        headerRow.createCell(5).setCellValue("Project Manager");
        headerRow.createCell(6).setCellValue("Hours");
        headerRow.createCell(7).setCellValue("Selected Date");
        headerRow.createCell(8).setCellValue("From Date");
        headerRow.createCell(9).setCellValue("To Date");
        headerRow.createCell(10).setCellValue("Status");

        // Populate data rows
        int rowNum = 1;
        for (Timesheet timesheet : timesheets) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(timesheet.getId());
            row.createCell(1).setCellValue(timesheet.getUser().getUsername());
            row.createCell(2).setCellValue(timesheet.getUser().getEmail());
            row.createCell(3).setCellValue(timesheet.getUser().getId());
            row.createCell(4).setCellValue(timesheet.getProject().getProject().getProjectName());
            row.createCell(5).setCellValue(timesheet.getProject().getProject().getManager().getUsername());
            row.createCell(6).setCellValue(timesheet.getHours());
            row.createCell(7).setCellValue(timesheet.getSelectedDate().toString());
            row.createCell(8).setCellValue(timesheet.getFromDate().toString());
            row.createCell(9).setCellValue(timesheet.getToDate().toString());
            row.createCell(10).setCellValue(timesheet.getStatus());
        }

        return workbook;
    }

}

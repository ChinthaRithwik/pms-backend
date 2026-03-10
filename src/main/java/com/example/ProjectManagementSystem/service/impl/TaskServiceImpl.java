package com.example.ProjectManagementSystem.service.impl;

import com.example.ProjectManagementSystem.dto.taskDtos.*;
import com.example.ProjectManagementSystem.entity.Project;
import com.example.ProjectManagementSystem.entity.Task;
import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import com.example.ProjectManagementSystem.exception.ResourceNotFoundException;
import com.example.ProjectManagementSystem.repository.ProjectRepository;
import com.example.ProjectManagementSystem.repository.TaskRepository;
import com.example.ProjectManagementSystem.repository.UserRepository;
import com.example.ProjectManagementSystem.service.TaskService;
import com.example.ProjectManagementSystem.specification.TaskSpecification;
import com.example.ProjectManagementSystem.security.SecurityUtils;
import com.example.ProjectManagementSystem.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static com.example.ProjectManagementSystem.entity.enums.StatusTypes.CANCELLED;
import static com.example.ProjectManagementSystem.entity.enums.StatusTypes.COMPLETED;
import static com.example.ProjectManagementSystem.helper.AllowedTransitions.allowedTransitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private static final Logger logger =LoggerFactory.getLogger(TaskServiceImpl.class);

    @Override
    @Transactional
    public TaskResponse createNewTask(CreateTaskRequest request, Long projectId) {
        logger.info("Fetching project with id: {} for task creation",projectId);
        Project project=projectRepository.findById(projectId).orElseThrow(()-> {
            logger.warn("Project not found with id: {}", projectId);
            return new ResourceNotFoundException("project not found with ID:" + projectId);
        });
        if (project.getStatus() == StatusTypes.COMPLETED
                || project.getStatus() == StatusTypes.CANCELLED) {
            logger.warn("Cannot add tasks to a closed project");
            throw new IllegalStateException("Cannot add tasks to a closed project");
        }
        verifyProjectOwnershipOrAdmin(project);
        User user = null;
        if (request.getAssignedUserId() != null) {
            user = userRepository.findById(request.getAssignedUserId()).orElseThrow(() ->{
               logger.warn("User not found with id: {}", request.getAssignedUserId());
               return new ResourceNotFoundException("User not found with ID:" + request.getAssignedUserId());
                    });
        }
        logger.info("Creating new task with title: {}", request.getTitle());
        Task task=new Task();
        task.setTitle(request.getTitle());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.TODO);
        task.setAssignedUser(user);
        project.addTask(task);
        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with id: {}", savedTask.getId());
        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    public Page<TaskResponse> getAllTasks(Long projectId ,TaskStatus status, Pageable pageable) {
        logger.info("Validating project existence with id: {}", projectId);
        if(!projectRepository.existsById(projectId)){
            logger.warn("project not found with ID:"+projectId);
            throw new ResourceNotFoundException("project not found with ID:"+projectId);
        }
       logger.info("Fetching all projects with page number: {}, page size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Task> tasks;
        if(status!=null){
            tasks=taskRepository.findByProjectIdAndStatus(projectId,status,pageable);
        }else{
            tasks=taskRepository.findByProjectId(projectId,pageable);
        }
        return tasks.map(this::mapToResponse);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        logger.info("Fetching task with id: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.warn("Task not found with id: {}", taskId);
                    return new ResourceNotFoundException("Task not found with id: " + taskId);
                });
        return mapToResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTask(UpdateTaskRequest request, Long taskId) {
        logger.info("Fetching task with id: {} for update", taskId);
        Task task=taskRepository.findById(taskId).orElseThrow(()-> {
            logger.warn("Task not found with id: {}", taskId);
            return new ResourceNotFoundException("Task not found with ID:" + taskId);
        });
        if(task.getProject().getStatus()==StatusTypes.COMPLETED){
            logger.warn("Cannot modify task of Completed Project");
            throw new IllegalStateException("Cannot modify task of Completed Project");
        }
        if(task.getProject().getStatus()==StatusTypes.CANCELLED){
            logger.warn("Cannot modify task of Cancelled Project");
            throw new IllegalStateException("Cannot modify task of Cancelled Project");
        }
        verifyTaskOwnershipOrAdmin(task);
        logger.info("Updating task with id: {}", taskId);
        modelMapper.map(request,task);
        taskRepository.save(task);
        logger.info("Task updated successfully with id: {}", taskId);
        return mapToResponse(task);
    }

    @Transactional
    @Override
    public void deleteTaskById(Long taskId) {
        logger.info("Fetching task with id: {} for deletion", taskId);
        Task task=taskRepository.findById(taskId).orElseThrow(()-> {
            logger.warn("Task not found with id: {}", taskId);
            return new ResourceNotFoundException("Task not found with ID:" + taskId);
        });
        if(task.getProject().getStatus()== StatusTypes.COMPLETED || task.getProject().getStatus() == StatusTypes.CANCELLED){
            logger.warn("Task cannot be deleted from a completed or cancelled Project");
            throw new IllegalStateException("Task cannot be deleted from a completed or cancelled Project");
        }
        verifyTaskOwnershipOrAdmin(task);
        if(task.getStatus()==TaskStatus.IN_PROGRESS || task.getStatus()==TaskStatus.COMPLETED){
            logger.warn("Task cannot be deleted");
            throw new IllegalStateException("Task cannot be deleted");
        }
        logger.info("Deleting task with id: {}", taskId);
        task.getProject().removeTask(task);
        logger.info("Task deleted successfully with id: {}", taskId);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        logger.info("Fetching task with id: {} for status update", taskId);
        Task task=taskRepository.findById(taskId).orElseThrow(()-> {
            logger.warn("Task not found with id: {}", taskId);
            return new ResourceNotFoundException("Task not found with ID:" + taskId);
        });
        if (task.getProject().getStatus() == StatusTypes.ON_HOLD || task.getProject().getStatus() == CANCELLED) {
            logger.warn("Cannot update task when project is not active");
            throw new IllegalStateException("Cannot update task when project is not active");
        }
        verifyTaskOwnershipOrAdmin(task);
        logger.info("Updating task's status with id: {}", taskId);
        TaskStatus current=task.getStatus();
        TaskStatus targetStatus=request.getStatus();
        if (targetStatus == null) {
            logger.warn("Status cannot be null");
            throw new IllegalArgumentException("Status cannot be null");
        }
        Set<TaskStatus> validNextStates=allowedTransitions.get(current);
        if(validNextStates==null || !validNextStates.contains(targetStatus)){
            logger.warn("Invalid status transition from {} to {}. Valid transitions: {}", current, targetStatus, validNextStates);
            throw new IllegalStateException("Invalid status transition from " + current + " to " + targetStatus);
        }
        task.setStatus(targetStatus);
        updateProjectStatusBasedOnTasks(task.getProject());
        logger.info("Task updated successfully with id: {}", taskId);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTasksAssignedUser(Long taskId, UpdateAssignedUserRequest request) {
        logger.info("Fetching task with id: {} for reassignment", taskId);
        Task task=taskRepository.findById(taskId).orElseThrow(()-> {
            logger.warn("Task not found with id: {}", taskId);
            return new ResourceNotFoundException("Task not found with ID:" + taskId);
        });
        if (task.getStatus() == TaskStatus.COMPLETED) {
            logger.warn("Cannot reassign task that is completed");
            throw new IllegalStateException("Cannot reassign task that is completed");
        }
        verifyTaskOwnershipOrAdmin(task);
        logger.info("Reassigning task with id:{} to new user with id:{}",taskId,request.getAssignedUserId());
        if(request.getAssignedUserId()!=null){
            User newUser=userRepository.findById(request.getAssignedUserId()).orElseThrow(() -> {
                logger.warn("User not found with id: {}", request.getAssignedUserId());
                return new ResourceNotFoundException("User not found with ID:" + request.getAssignedUserId());
            });
            task.setAssignedUser(newUser);
        }else{
            task.setAssignedUser(null);
        }
        logger.info("Task is reassigned to new user successfully");
        return mapToResponse(task);
    }

    @Override
    public Page<TaskResponse> getOverDueTasks(Pageable pageable) {
        logger.info("Fetching overdue tasks");
        User currentUser = getAuthenticatedUser();
        Page<Task> tasks;
        if (currentUser.getRole() == Role.ADMIN) {
            tasks = taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.COMPLETED, pageable);
        } else {
            tasks = taskRepository.findByDueDateBeforeAndStatusNotAndProjectOwner(LocalDate.now(), TaskStatus.COMPLETED, currentUser, pageable);
        }
        if(tasks.isEmpty()){
            logger.info("No overdue tasks found");
        }
        return tasks.map(this::mapToResponse);
    }
    public Page<TaskResponse> searchTasks(
            Long projectId,
            TaskStatus status,
            Long assignedUserId,
            LocalDate dueBefore,
            Pageable pageable) {

        User currentUser = getAuthenticatedUser();
        Specification<Task> spec = TaskSpecification.hasProject(projectId)
                .and(TaskSpecification.hasStatus(status))
                .and(TaskSpecification.hasAssignedUser(assignedUserId))
                .and(TaskSpecification.dueBefore(dueBefore));

        if (currentUser.getRole() != Role.ADMIN) {
            spec = spec.and(TaskSpecification.hasProjectOwner(currentUser.getId()));
        }

        Page<Task> tasks = taskRepository.findAll(spec, pageable);

        return tasks.map(this::mapToResponse);
    }

    private void updateProjectStatusBasedOnTasks(Project project) {
        if(project.getStatus()==StatusTypes.ON_HOLD || project.getStatus()== StatusTypes.CANCELLED){
            return;
        }
        long countTasks=taskRepository.countByProjectId(project.getId());
        if(countTasks == 0){
            project.setStatus(StatusTypes.PLANNED);
            return;
        }
        long incompleteTasks= taskRepository.countByProjectIdAndStatusNot(project.getId(),TaskStatus.COMPLETED);
        if(incompleteTasks==0){
            project.setStatus(StatusTypes.COMPLETED);
        }else{
            project.setStatus(StatusTypes.IN_PROGRESS);
        }
    }

    public java.util.Optional<User> getCurrentUser() {
        String email = securityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email);
    }

    private User getAuthenticatedUser() {
        return getCurrentUser().orElseThrow(() ->
                new ResourceNotFoundException("Logged-in user not found"));
    }

    private void verifyProjectOwnershipOrAdmin(Project project) {
        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() != Role.ADMIN && !project.getOwner().getId().equals(currentUser.getId())) {
             throw new IllegalStateException("You do not have permission to modify tasks in this project");
        }
    }

    private void verifyTaskOwnershipOrAdmin(Task task) {
        verifyProjectOwnershipOrAdmin(task.getProject());
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = modelMapper.map(task, TaskResponse.class);
        response.setProjectId(task.getProject().getId());
        response.setAssignedUserId(
                task.getAssignedUser() != null ? task.getAssignedUser().getId() : null
        );
        return response;
    }
}

//******************************************************************************************************
// TaskService.java
//
// Task service according to redfish specification.
//
//Copyright (C) 2022, PICMG.
//
//        This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <https://www.gnu.org/licenses/>.
//*******************************************************************************************************


package com.redfishserver.Redfish_Server.services;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ResourceHealth;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.Task_TaskState;
import com.redfishserver.Redfish_Server.RFmodels.custom.TaskMonitor;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.Task_Task;
import com.redfishserver.Redfish_Server.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import javax.persistence.EntityExistsException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RedfishErrorResponseService redfishErrorResponseService;

    public TaskService() {

    }


    public List<Task_Task> getAllTaskDetails(){
        List<Task_Task> taskList = taskRepository.findAll();
        return taskList;
    }

    public Task_Task getTask(String Id) {
        Task_Task task = taskRepository.getById(Id);
        return task;
    }

    public TaskMonitor getTaskMonitor(String Id) throws ChangeSetPersister.NotFoundException {
        Task_Task task = taskRepository.getById(Id);
        if(task == null || task.getTaskMonitor() == null)
            throw new ChangeSetPersister.NotFoundException();
        return task.getTaskMonitor();
    }

    public Task_Task addTask(Task_Task task) throws EntityExistsException {
        Task_Task newTask = taskRepository.getByName(task.getName());
        if(newTask != null)
            throw new EntityExistsException();
        Integer newTaskId = getMaxTaskCount();
        task.setId(newTaskId.toString());
        task.setAtOdataId("/redfish/v1/TaskService/Tasks/" + newTaskId.toString());
        task.setAtOdataType("#Task.v1_6_1.Task");
        task = taskRepository.save(task);
        return task;
    }

    public Boolean deleteTask(Task_Task task) {
        Task_Task existingTask = taskRepository.getByName(task.getName());
        if(existingTask == null)
            return false;
        taskRepository.delete(existingTask);
        return true;
    }

    public void updateTaskState(String id, Task_TaskState taskState, Object asyncResponse) {
        Task_Task task = new Task_Task();
        task.setId(id);
        task.setTaskState(taskState);
        if(taskState == Task_TaskState.COMPLETED)
            task.setEndTime(OffsetDateTime.now());
        updateTask(task, asyncResponse);
    }

    public Boolean updateTask(Task_Task task, Object asyncResponse) {
        Task_Task existingTask = taskRepository.getById(task.getId());
        if(existingTask == null)
            return false;
        if(task.getName() != null)
            existingTask.setName(task.getName());
        if(task.getTaskStatus() != null)
            existingTask.setTaskStatus(task.getTaskStatus());
        if(task.getTaskState() != null)
            existingTask.setTaskState(task.getTaskState());
        if(task.getAtOdataType() != null)
            existingTask.setAtOdataType(task.getAtOdataType());
        if(task.getStartTime() != null)
            existingTask.setStartTime(task.getStartTime());
        if(task.getEndTime() != null)
            existingTask.setEndTime(task.getEndTime());
        if(task.getAtOdataId() != null)
            existingTask.setAtOdataId(task.getAtOdataId());
        if(existingTask.getTaskMonitor()!=null && asyncResponse!=null) {
            existingTask.getTaskMonitor().setTaskResponse(asyncResponse);
            existingTask.getTaskMonitor().setTaskState(task.getTaskState().toString());
            existingTask.getTaskMonitor().setMessage(redfishErrorResponseService.getTaskCompletedOKResponse(task.getId()));
        }

        taskRepository.save(existingTask);
        return true;
    }

    public Task_Task createTaskForAsyncOperation(OffsetDateTime startTime, Integer newTaskId, String uri) {
        Task_Task newTask = new Task_Task();
        newTask.setId(newTaskId.toString());
        newTask.setName("Task " + newTaskId.toString());
        newTask.setTaskState(Task_TaskState.RUNNING);
        newTask.setStartTime(startTime);
        newTask.setTaskStatus(ResourceHealth.valueOf("OK"));
        newTask.setAtOdataId("/redfish/v1/TaskService/Tasks/" + newTaskId.toString());
        newTask.setAtOdataType("#Task.v1_6_1.Task");

        TaskMonitor taskMonitor = new TaskMonitor();
        taskMonitor.setUri(uri);
        taskMonitor.setTaskState(String.valueOf(Task_TaskState.RUNNING));
        taskMonitor.setMessage(redfishErrorResponseService.getTaskStartedResponse(newTaskId));

        newTask.setTaskMonitor(taskMonitor);

        taskRepository.save(newTask);

        return newTask;
    }

    public Integer getMaxTaskCount() {
        Integer max_count = 0;
        List<Task_Task> taskList = taskRepository.findAll();
        for(Task_Task task: taskList) {
            max_count = Math.max(max_count, Integer.parseInt(task.getId()));
        }
        return max_count+1;
    }

    public String getTaskServiceURI()  {
        return "/redfish/v1/TaskService/Task/";
    }
}
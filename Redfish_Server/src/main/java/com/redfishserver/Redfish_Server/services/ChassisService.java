//******************************************************************************************************
// ChassisService.java
//
// Chassis service according to redfish specification.
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

import com.redfishserver.Redfish_Server.RFmodels.AllModels.*;
import com.redfishserver.Redfish_Server.repository.chassis.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class ChassisService {

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    @Autowired
    TaskService taskService;

    @Autowired
    ChassisCollectionRepository chassisCollectionRepository;

    @Autowired
    ChassisRepository chassisRepository;

    @Autowired
    ControlCollectionRepository controlCollectionRepository;

    @Autowired
    PowerLimitRepository powerLimitRepository;

    @Autowired
    SensorCollectionRepository sensorCollectionRepository;

    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    EnvironmentMetricsRepository environmentMetricsRepository;

    @Autowired
    PowerRepository powerRepository;

    @Autowired
    PowerSubsystemRepository powerSubsystemRepository;

    @Autowired
    PowerSuppliesCollectionRepository powerSuppliesCollectionRepository;

    @Autowired
    BatteryCollectionRepository batteryCollectionRepository;

    @Autowired
    AssemblyRepository assemblyRepository;

    @Autowired
    PowerSupplyMetricsRepository powerSupplyMetricsRepository;
    @Autowired
    ThermalSubsystemRepository thermalSubsystemRepository;
    @Autowired
    ThermalMetricsRepository thermalMetricsRepository;

    @Autowired
    FanCollectionRepository fanCollectionRepository;

    @Autowired
    FanRepository fanRepository;

    @Autowired
    BatteryRepository batteryRepository;

    @Autowired
    BatteryMetricsRepository batteryMetricsRepository;

    @Autowired
    CPU1FreqRepository cpu1FreqRepository;

    @Autowired
    TrustedComponentCollectionRepository trustedComponentCollectionRepository;

    @Autowired
    TrustedComponentRepository trustedComponentRepository;

    @Autowired
    CertificateCollectionRepository certificateCollectionRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    PowerSupplyRepository powerSupplyRepository;
    @Async
    public Future<TrustedComponent_TrustedComponent> getTrustedComponent(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String trustedComponentObjectId){
        List<TrustedComponent_TrustedComponent> trustedComponentList = trustedComponentRepository.getById(trustedComponentObjectId);
        TrustedComponent_TrustedComponent obj = null;
        for(TrustedComponent_TrustedComponent trustedComponent : trustedComponentList){
            if(trustedComponent.getAtOdataId().contains(chassisObjectId)){
                obj = trustedComponent;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<TrustedComponent_TrustedComponent>(obj);
    }
    @Async
    public  Future<Certificate_Certificate> getCertificate(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String trustedComponentObjectId, String certificateObjectId){
        List<Certificate_Certificate> certificateList = certificateRepository.findAll();
        Certificate_Certificate obj = null;
        for(Certificate_Certificate certificate : certificateList){
            if(certificate.getAtOdataId() != null && certificate.getAtOdataId().contains(chassisObjectId) && certificate.getAtOdataId().contains(trustedComponentObjectId) && certificate.getAtOdataId().contains(certificateObjectId)){
                obj = certificate;
                break;

            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<Certificate_Certificate>(obj);
    }
    @Async
    public Future<CertificateCollection> getCertificateCollection(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String trustedComponentObjectId){
        List<CertificateCollection> certificateCollectionList = certificateCollectionRepository.findAll();
        CertificateCollection obj = null;
        for(CertificateCollection certificateCollection : certificateCollectionList){
            if(certificateCollection.getAtOdataId().contains(chassisObjectId) && certificateCollection.getAtOdataId().contains(trustedComponentObjectId)){
                obj = certificateCollection;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<CertificateCollection>(obj);
    }
    @Async
    public Future<TrustedComponentCollection> getTrustedComponentCollection(OffsetDateTime startTime, Integer taskId,String chassisObjectId){
        List<TrustedComponentCollection> trustedComponentCollectionList = trustedComponentCollectionRepository.findAll();
        TrustedComponentCollection obj = null;
        for(TrustedComponentCollection trustedComponentCollection : trustedComponentCollectionList){
            if(trustedComponentCollection.getAtOdataId().contains(chassisObjectId)){
                obj = trustedComponentCollection;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<TrustedComponentCollection>(obj);
    }

    //public Control getCPUFreq(String controlObjectId){ return cpu1FreqRepository.getById(controlObjectId);}
    @Async
    public Future<BatteryMetrics_BatteryMetrics> getBatteryMetrics(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String batteryObjectId){
        List<BatteryMetrics_BatteryMetrics> batteryMetricsList = batteryMetricsRepository.findAll();
        BatteryMetrics_BatteryMetrics obj = null;
        for(BatteryMetrics_BatteryMetrics batteryMetrics : batteryMetricsList){
            if(batteryMetrics.getAtOdataId().contains(chassisObjectId) && batteryMetrics.getAtOdataId().contains(batteryObjectId)){
                obj = batteryMetrics;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<BatteryMetrics_BatteryMetrics>(obj);
    }
    @Async
    public Future<Battery_Battery> getBattery(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String batteryObjectId){
        List<Battery_Battery> batteryList = batteryRepository.getById(batteryObjectId);
        Battery_Battery obj = null;
        for(Battery_Battery battery : batteryList){
            if(battery.getAtOdataId().contains(chassisObjectId)){
                obj = battery;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<Battery_Battery>(obj);
    }
    @Async
    public Future<Fan_Fan> getFan(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String fanObjectId){
        List<Fan_Fan> fanList = fanRepository.getById(fanObjectId);
        Fan_Fan obj = null;
        for(Fan_Fan fan : fanList){
            if(fan.getAtOdataId().contains(chassisObjectId)){
                obj = fan;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<Fan_Fan>(obj);
    }
    @Async
    public Future<FanCollection> getFanCollection(OffsetDateTime startTime, Integer taskId,String chassisObjectId){
        List<FanCollection> fanCollectionList = fanCollectionRepository.findAll();
        FanCollection obj = null;
        for(FanCollection fanCollection : fanCollectionList){
            if(fanCollection.getAtOdataId().contains(chassisObjectId)){
                obj = fanCollection;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<FanCollection>(obj);
    }
    @Async
    public Future<ThermalMetrics_ThermalMetrics> getThermalMetrics(OffsetDateTime startTime, Integer taskId,String chassisObjectId){
        List<ThermalMetrics_ThermalMetrics> thermalMetricsList = thermalMetricsRepository.findAll();
        ThermalMetrics_ThermalMetrics obj = null;
        for(ThermalMetrics_ThermalMetrics thermalMetrics : thermalMetricsList){
            if(thermalMetrics.getAtOdataId().contains(chassisObjectId)){
                obj = thermalMetrics;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }

        }
        return new AsyncResult<ThermalMetrics_ThermalMetrics>(obj);
    }
    @Async
    public Future<ThermalSubsystem_ThermalSubsystem> getThermalSubsystem(OffsetDateTime startTime, Integer taskId,String chassisObjectId){
        List<ThermalSubsystem_ThermalSubsystem> thermalSubsystemList = thermalSubsystemRepository.findAll();
        ThermalSubsystem_ThermalSubsystem obj = null;
        for(ThermalSubsystem_ThermalSubsystem thermalSubsystem : thermalSubsystemList){
            if(thermalSubsystem.getAtOdataId().contains(chassisObjectId)){
                obj = thermalSubsystem;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<ThermalSubsystem_ThermalSubsystem>(obj);
    }
    @Async
    public Future<PowerSupplyMetrics_PowerSupplyMetrics> getPowerSupplyMetrics(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String powerSupplyObjectId){
        List<PowerSupplyMetrics_PowerSupplyMetrics> powerSupplyMetricsList = powerSupplyMetricsRepository.findAll();
        PowerSupplyMetrics_PowerSupplyMetrics obj = null;
        for(PowerSupplyMetrics_PowerSupplyMetrics powerSupplyMetrics : powerSupplyMetricsList){
            if(powerSupplyMetrics.getAtOdataId().contains(chassisObjectId) && powerSupplyMetrics.getAtOdataId().contains(powerSupplyObjectId)) {
                obj = powerSupplyMetrics;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<PowerSupplyMetrics_PowerSupplyMetrics>(obj);
    }
    @Async
    public Future<Assembly_Assembly> getAssembly(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String powerSupplyObjectId){
        List<Assembly_Assembly> assemblyList = assemblyRepository.findAll();
        Assembly_Assembly obj = null;
        for(Assembly_Assembly assembly : assemblyList){
            if(assembly.getAtOdataId().contains(chassisObjectId) && assembly.getAtOdataId().contains(powerSupplyObjectId)){
                obj = assembly;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<Assembly_Assembly>(obj);
    }
    @Async
    public Future<BatteryCollection> getBatteryCollection(OffsetDateTime startTime, Integer taskId,String chassisObjectId){
        List<BatteryCollection> batteryCollectionList = batteryCollectionRepository.findAll();
        BatteryCollection obj = null;
        for(BatteryCollection batteryCollection : batteryCollectionList){
            if(batteryCollection.getAtOdataId().contains(chassisObjectId)){
                obj = batteryCollection;
                break;
            }
            if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
            }
        }
        return new AsyncResult<BatteryCollection>(obj);
    }
    @Async
    public Future<PowerSupply_PowerSupply> getPowerSupply(OffsetDateTime startTime, Integer taskId,String chassisObjectId, String powerSupplyObjectId){
        List<PowerSupply_PowerSupply> powerSupplyList = powerSupplyRepository.getById(powerSupplyObjectId);
        PowerSupply_PowerSupply obj = null;
        for(PowerSupply_PowerSupply powerSupply : powerSupplyList){
            if(powerSupply.getAtOdataId().contains(chassisObjectId)) {
                obj = powerSupply;
                break;
            }
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
        }
        return new AsyncResult<PowerSupply_PowerSupply>(obj);
    }

    @Async
    public Future<List<ChassisCollection>> getAllChassisCollection(OffsetDateTime startTime, Integer taskId) throws InterruptedException {
        List<ChassisCollection> chassisCollectionList = chassisCollectionRepository.findAll();
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, chassisCollectionList);
        }
        return new AsyncResult<List<ChassisCollection>> (chassisCollectionList);
    }

    @Async
    public Future<List<Chassis_Chassis>> getChassis(OffsetDateTime startTime, Integer taskId, String chassisObjectId) {
        List<Chassis_Chassis> chassis_chassisList = chassisRepository.getById(chassisObjectId);
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, chassis_chassisList);
        }
        return new AsyncResult<List<Chassis_Chassis>> (chassis_chassisList);
    }

    @Async
    public Future<ControlCollection> getControlCollection(OffsetDateTime startTime, Integer taskId, String chassisObjectId) {
        List<ControlCollection> controlCollectionList = controlCollectionRepository.findAll();
        ControlCollection controlColl = null;
        for(ControlCollection controlCollection : controlCollectionList) {
            if (controlCollection.getAtOdataId().contains(chassisObjectId)) {
                controlColl = controlCollection;
                break;
            }
        }

        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, controlColl);
        }
        return new AsyncResult<ControlCollection>(controlColl);
    }

    @Async
    public Future<Control_Control> getPowerLimit(OffsetDateTime startTime, Integer taskId,String Id, String controlObjectId) {
        List<Control_Control> controlList = powerLimitRepository.getById(controlObjectId);
        Control_Control cntrl = null;
        for(Control_Control control : controlList){
            if(control.getAtOdataId().contains(Id)) {
                cntrl = control;
                break;
            }
        }

        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, cntrl);
        }
        return new AsyncResult<Control_Control>(cntrl);
    }

    public Future<SensorCollection> getSensorCollection(OffsetDateTime startTime, Integer taskId, String controlObjectId) {
        List<SensorCollection> sensorCollectionList = sensorCollectionRepository.findAll();
        SensorCollection collection = null;
        for(SensorCollection sensorCollection : sensorCollectionList){
            System.out.println(sensorCollection.getAtOdataId());
            if(sensorCollection.getAtOdataId().contains(controlObjectId)) {
                collection = sensorCollection;
                break;
            }
        }
        return new AsyncResult<SensorCollection>(collection);
    }

    @Async
    public Future<Sensor_Sensor> getSensor(OffsetDateTime startTime, Integer taskId, String chassisObjectId, String sensorObjectId) {
        List<Sensor_Sensor> sensorList = sensorRepository.getById(sensorObjectId);
        Sensor_Sensor snsr = null;
        for(Sensor_Sensor sensor : sensorList){
            if(sensor.getAtOdataId().contains(chassisObjectId)) {
                snsr = sensor;
                break;
            }
        }

        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, snsr);
        }
        return new AsyncResult<Sensor_Sensor>(snsr);
    }

    @Async
    public Future<EnvironmentMetrics_EnvironmentMetrics> getEnvironmentMetrics(OffsetDateTime startTime, Integer taskId,String chassisObjectId) {
        List<EnvironmentMetrics_EnvironmentMetrics> environmentMetricsList = environmentMetricsRepository.findAll();
        EnvironmentMetrics_EnvironmentMetrics obj = null;
        for(EnvironmentMetrics_EnvironmentMetrics environmentMetrics : environmentMetricsList){
            if(environmentMetrics.getAtOdataId().contains(chassisObjectId)) {
                obj = environmentMetrics;
                break;
            }
        }

        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
        }
        return new AsyncResult<EnvironmentMetrics_EnvironmentMetrics>(obj);
    }
    @Async
    public Future<Power_Power> getPower(OffsetDateTime startTime, Integer taskId,String chassisObjectId) {
        List<Power_Power> powerList = powerRepository.findAll();
        Power_Power obj = null;
        for(Power_Power power : powerList){
            if(power.getAtOdataId().contains(chassisObjectId)) {
                obj = power;
                break;
            }
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
        }
        return new AsyncResult<Power_Power>(obj);
    }
    @Async
    public Future<PowerSubsystem_PowerSubsystem> getPowerSubsystem(OffsetDateTime startTime, Integer taskId,String chassisObjectId) {
        List<PowerSubsystem_PowerSubsystem> powerSubsystemList = powerSubsystemRepository.findAll();
        PowerSubsystem_PowerSubsystem obj = null;
        for(PowerSubsystem_PowerSubsystem powerSubsystem : powerSubsystemList){
            if(powerSubsystem.getAtOdataId().contains(chassisObjectId)) {
                obj = powerSubsystem;
                break;
            }
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
        }
        return new AsyncResult<PowerSubsystem_PowerSubsystem>(obj);
    }
    @Async
    public Future<PowerSupplyCollection> getPowerSupplyCollection(OffsetDateTime startTime, Integer taskId,String chassisObjectId) {
        List<PowerSupplyCollection> powerSupplyCollectionList = powerSuppliesCollectionRepository.findAll();
        PowerSupplyCollection obj = null;
        for(PowerSupplyCollection powerSupplyCollection : powerSupplyCollectionList){
            if(powerSupplyCollection.getAtOdataId().contains(chassisObjectId)) {
                obj = powerSupplyCollection;
                break;
            }
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, obj);
        }
        return new AsyncResult<PowerSupplyCollection>(obj);
    }

    public Integer getTaskId() {
        return taskService.getMaxTaskCount();
    }

    public String getTaskServiceURI(String newTaskId) {
        return taskService.getTaskServiceURI() + newTaskId + "/monitor";
    }

    public void createTaskForOperation(OffsetDateTime startTime, Integer newTaskId, String uri) {
        taskService.createTaskForAsyncOperation(startTime, newTaskId, uri);
    }

    public Task_Task getTaskResource(String Id) {
        return taskService.getTask(Id);
    }
}


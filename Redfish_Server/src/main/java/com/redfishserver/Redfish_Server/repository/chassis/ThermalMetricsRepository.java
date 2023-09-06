//******************************************************************************************************
// ThermalMetricsRepository.java
//
// Interface for ThermalMetricsRepository.
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


package com.redfishserver.Redfish_Server.repository.chassis;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ThermalMetrics_ThermalMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThermalMetricsRepository extends MongoRepository<ThermalMetrics_ThermalMetrics, Object> {
    ThermalMetrics_ThermalMetrics getById(String Id);
}
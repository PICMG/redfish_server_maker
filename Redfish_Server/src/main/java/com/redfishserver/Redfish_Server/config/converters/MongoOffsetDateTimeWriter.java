//******************************************************************************************************
// MongoOffsetDateTimeWriter.java
//
// Mongo Offset DateTime Writer file.
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


package com.redfishserver.Redfish_Server.config.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.OffsetDateTime;
import java.util.Date;

@WritingConverter
public class MongoOffsetDateTimeWriter implements Converter<OffsetDateTime, String> {
    public static final String DATE_FIELD = "dateTime";
    public static final String OFFSET_FIELD = "offset";

    @Override
    public String convert(final OffsetDateTime offsetDateTime) {
//        final Document document = new Document();
//        document.put(DATE_FIELD, Date.from(offsetDateTime.toInstant()));
//        document.put(OFFSET_FIELD, offsetDateTime.getOffset().toString());
        return offsetDateTime.toString();
    }
}

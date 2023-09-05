//******************************************************************************************************
// APIService.java
//
// API service according to redfish specification.
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

import com.redfishserver.Redfish_Server.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class APIServices {

    String responseString = null;

    public String callGETAPI(String stringURL, String authToken) throws IOException {
        URL url = new URL(stringURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(Utils.API_REQUEST_TYPE_GET);

        con.setRequestProperty("Content-Type", "application/json");
        if(authToken != null)
            con.setRequestProperty("Authorization", "Bearer " +  authToken);
        BufferedReader in = new BufferedReader(new InputStreamReader( con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseString = inputLine;
        }
        return responseString;
    }

    public JSONObject parseAPIResponse(HttpURLConnection connection) throws JSONException {
        String responseText = null;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), Utils.API_CHARACTER_SET_NAME))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            responseText= String.valueOf(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject responseJson = new JSONObject(responseText);
        return responseJson;
    }

    public String callPOSTAPI(String stringURL, String body, String authToken) throws IOException {
        URL url = new URL(stringURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(Utils.API_REQUEST_TYPE_POST);

        con.setRequestProperty("Content-Type", "application/json");
//        try(OutputStream os = con.getOutputStream()) {
//            byte[] input = body.getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
        if(authToken != null)
            con.setRequestProperty("Authorization", "Bearer " + authToken);
        BufferedReader in = new BufferedReader(new InputStreamReader( con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseString = inputLine;
        }
        return responseString;
    }
}

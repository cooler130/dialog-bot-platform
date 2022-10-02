package com.cooler.ai.platform.facade.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class DMRequest implements java.io.Serializable{
    private ClientInfo clientInfo;
    private UserInfo userInfo;
    private LocationInfo locationInfo;

    private String queryType;
    private String query;
    private String sessionId;
    private String debugModel;
    private NLUData nluData;
    private Map<String, String> metaData;

    private DomainTaskData domainTaskData;

    public DMRequest(ClientInfo clientInfo, UserInfo userInfo, LocationInfo locationInfo, String queryType, String query, Map<String, String> metaData, long timestamp, String sessionId, NLUData nluData, String debugModel) {
        this.clientInfo = clientInfo;
        this.userInfo = userInfo;
        this.locationInfo = locationInfo;
        this.queryType = queryType;
        this.query = query;
        this.metaData = metaData;
        this.sessionId = sessionId;
        this.nluData = nluData;
        this.debugModel = debugModel;
    }

}

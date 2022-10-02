package com.cooler.ai.platform.entity;

import com.cooler.ai.platform.model.EntityConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    private Integer id;

    private String actionName;

    private Integer actionType;

    private String processCode;

    private String defaultReply;

    private String msg;

    private static Action defaultAction = new Action(EntityConstant.DEFAULT_ACTION_ID, "全局兜底动作", 2, EntityConstant.DEFAULT_ACTION_PROCESSCODE,  "", "全局兜底动作");
    private static Action inquiryAction = new Action(EntityConstant.INQUIRY_ACTION_ID, "全局询问动作", 2, EntityConstant.INQUIRY_ACTION_PROCESSCODE, "", "全局询问动作");

    public static Action getInquiryAction(){
        return inquiryAction;
    }
    public static Action getDefaultAction(){
        return defaultAction;
    }

}
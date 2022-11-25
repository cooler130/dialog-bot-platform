package com.cooler.ai.platform.entity2;

import com.cooler.ai.platform.model.EntityConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action implements Comparable<Action>{
    private Integer id;

    private String actionName;

    private Integer policyId;

    private Integer actionType;

    private Integer groupNum;

    private String actionContent;

    private Integer enable;

    private String msg;

    private static Action defaultAction = new Action(
            EntityConstant.DEFAULT_ACTION_ID,
            "default_action",
            EntityConstant.DEFAULT_POLICY_ID,
            2,
            1,
            "抱歉！没有理解您的意思，请换个说法再说一次吧！",
            1,
            "全局兜底动作"
    );


    public static Action getDefaultAction(){
        return defaultAction;
    }

    @Override
    public int compareTo(Action o) {
        if(this.groupNum > o.groupNum) return 1;
        else if(this.groupNum < o.groupNum) return -1;
        return 0;
    }
}
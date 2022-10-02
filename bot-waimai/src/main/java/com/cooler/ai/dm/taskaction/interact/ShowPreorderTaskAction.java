package com.cooler.ai.dm.taskaction.interact;

import com.cooler.ai.dm.constant.BC;
import com.cooler.ai.nlg.entity.NlgTemplateInfo;
import com.cooler.ai.platform.action.BaseInteractiveTaskAction;
import com.cooler.ai.platform.facade.constance.Constant;
import com.cooler.ai.platform.facade.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2018/12/25
 **/
@Component("showPreorderTaskAction")
public class ShowPreorderTaskAction extends BaseInteractiveTaskAction {
    private Logger logger = LoggerFactory.getLogger(ShowPreorderTaskAction.class);

    private final String PREVIEW_ORDER_DATA_VALUED = "preview_order_data_valued";

    private final String PAGE_COUNT = "page_count";

    @Override
    protected Message createReplyMessage() {
        String bubbleTxt = null;

        Map<String, String> paramKvs = new HashMap<>();
        String preOrderDataInfoJS = getBizDataValue(BC.PREVIEW_ORDER_DATA_INFOS);
        if(!preOrderDataInfoJS.equals(Constant.NONE_VALUE)){
            String lastOrderPageNumStr = getBizDataValue(BC.CURRENT_ORDER_PAGE_NUM);
            int lastOrderPageNum = Integer.parseInt(lastOrderPageNumStr);
            int currentOrderPageNum = lastOrderPageNum + 1;

            String orderSizeStr = getBizDataValue(BC.ORDER_SIZE);
            int orderSize = Integer.parseInt(orderSizeStr);

            String orderCountPerPageStr = getBizDataValue(BC.ORDER_COUNT_PER_PAGE);
            int orderCountPerPage = Integer.parseInt(orderCountPerPageStr);

            int moreOrderCount = orderSize % orderCountPerPage;
            int pageCount = orderSize / orderCountPerPage + (moreOrderCount == 0 ? 0 : 1);

            //当前是第 ${current_order_page_num} 页，您有 ${order_size} 个订单，每页展示 ${order_count_per_page} 项，共 ${page_count} 页，您可以点击下面的历史订单项，将订单的商品重新加入购物车再次购买，如果调整数量，请对我说'查看购物车'。
            paramKvs.put(PREVIEW_ORDER_DATA_VALUED, "true");
            paramKvs.put(BC.CURRENT_ORDER_PAGE_NUM, currentOrderPageNum + "");
            paramKvs.put(BC.ORDER_SIZE, orderSizeStr);
            paramKvs.put(BC.ORDER_COUNT_PER_PAGE, orderCountPerPageStr);
            paramKvs.put(PAGE_COUNT, pageCount + "");
        }else{
            //已经最后一页了啊！
            paramKvs.put(PREVIEW_ORDER_DATA_VALUED, "false");
        }
        NlgTemplateInfo nlgTemplateInfo = getNlgTemplateInfoOfDefaultTheme(this.getClass(), Constant.NONE_VALUE, Constant.NONE_VALUE, paramKvs);
        bubbleTxt = nlgTemplateInfo.getNlgTemplate();

        Message bubbleMsg = new Message(Constant.MSG_TEXT, bubbleTxt);
        return bubbleMsg;
    }
}

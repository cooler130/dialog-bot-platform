package com.cooler.ai.dm.constant;

/**
 * @Author zhangsheng
 * @Description
 * @Date 2019/10/14
 **/

/**
 * 此类定义了所有系统的业务数据，可以跟SC里面的数据有交集，业务数据两类用途：1.对外展示 2.参与下轮对话的计算过程
 */
public class BC {

    public static final String FAQ_SEARCH_PARAMS = "faq_search_params";                     //FAQ的搜索条件

    public static final String FAQ_ANSWER = "faq_answer";                                   //FAQ的答案


    public static final String BRAND = "brand";                                             //所选的品牌

    public static final String MORE_THAN_PRICE = "more_than_price";                         //价格区间的下限

    public static final String LESS_THAN_PRICE = "less_than_price";                         //价格区间的上线

    public static final String ORDER_BY = "control_object";                                 //所用的排序对象

    public static final String SORT = "sort_dim";                                           //所用的排序方式


    public static final String SEARCH_POI_PARAMS = "search_poi_params";                     //查询Poi所用的参数集

    public static final String POI_INFOS = "poi_infos";                                     //查询出的poi集合（true）

    public static final String CURRENT_POI_PAGE_NUM = "current_poi_page_num";               //当前poi列表页展示到的页数（true）

    public static final String POI_ID = "poi_id";                                           //当前选择的poi_id（true）

    public static final String POI_NAME = "poi_name";                                       //（选定的）餐厅的名称，用来搜索（true）


    public static final String SEARCH_SKU_PARAMS = "search_sku_params";                     //查询sku所用的参数集

    public static final String SKU_INFOS = "sku_infos";                                     //查询出的sku集合（true）

    public static final String CURRENT_SKU_PAGE_NUM = "current_sku_page_num";               //当前sku列表页展示到的页数（true）

    public static final String SKU_ID = "sku_id";                                           //选择的sku编号（true）

    public static final String SKU_NAME = "sku_name";                                       //选择的sku的名称（true）


    public static final String OPTION_NUMBER = "option_number";                             //所选商家或商品的序号

    public static final String SHOPPING_CART_INFO = "shopping_cart_info";                   //产生的购物车内商品的数据体


    public static final Float DELIVERY_PRICE = 60.0f;                                       //起送价

    public static final String IS_BEYOND_DELIVERY_PRICE = "is_beyond_delivery_price";          //是否超过起送价数据

    public static final String SELECTED_HISTORY_ORDER_ID = "select_history_orderId";          //再来一单时所选择的订单Id数据集



    public static final String CURRENT_ORDER_PAGE_NUM = "current_order_page_num";             //当前poi列表页展示到的页数（true）

    public static final String CURRENT_ORDER_STATE = "current_order_state";                 //所查询订单的状态条件

    public static final String ORDER_DATA_INFOS_MAP = "order_data_infos_map";                 //查询出来的订单数据集

    public static final String PREVIEW_ORDER_DATA_INFOS = "preview_order_data_infos";         //预览的订单数据（ONE）

    public static final String SELECTED_ORDER_DATA_INFOS = "selected_order_data_infos";       //选定的订单数据（true）


    public static final String ORDER_SIZE = "order_size";                                     //当前用户拥有的已存订单数量

    public static final String ORDER_COUNT_PER_PAGE = "order_count_per_page";                 //每页显示的订单数量

    public static final String ORDER_COUNT_THIS_PAGE = "order_count_this_page";               //本页显示的订单数量


    public static final String ADDRESS = "address";                                           //用户地址（true）

    public static final String QR_CODE = "qr_code";                                           //支付二维码



}

package com.cooler.ai.dm.external;

import com.cooler.ai.platform.service.external.TagsRecommendService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//import com.sankuai.ai.kg.jinnang.api.thrift.SkuStruct;

@Service("tagsRecommendService")
public class TagsRecommendServiceImpl implements TagsRecommendService<Object> {

    public String getLabels(List<Object> resultlist, Map<String, List<String>> slotvalus) {
        return null;
//        String res = new String();
//        if (resultlist == null || resultlist.size() == 0) {
//            return res;
//        }
//        List<Pair<String, String>> my_list = new ArrayList<>();
//        Set<String> ms = new HashSet<>();
//        int skuname_count = 0;                                     //增加商品名字，但是通过前端图文显示了前10个，所以推荐标签推荐后面的菜品
//        for (SkuStruct sku : resultlist) {
//            skuname_count = skuname_count + 1;                     //
//            String feature_value = sku.getSkuCate();               //外卖和商品的类别，为了构造话术和识别
//            String cate_String;
//            if (StringUtils.isEmpty(feature_value)) {
//                cate_String = new String();
//            } else {
//                cate_String = new String(feature_value);
//            }
//            if (skuname_count > 10) {                                //推荐name规则，推荐不显示的其他name
//                feature_value = sku.getSkuName();
//                if (!StringUtils.isEmpty(feature_value)) {
//                    ms.add(feature_value);
//                    my_list.add((new Pair<>("name", feature_value)));
//                }
//            }
//
//            feature_value = sku.getSkuTaste();                     //味道，"香蕉味的披萨"
//            if (!StringUtils.isEmpty(feature_value)) {
//                if (!ms.contains(feature_value)) {
//                    ms.add(feature_value);
//                    my_list.add(new Pair<>("taste", feature_value + "味的" + cate_String));
//                }
//            }
//
//            feature_value = sku.getSkuMaterial();                  //原材料，"火腿,肉松原材料的"，等
//            if (!StringUtils.isEmpty(feature_value)) {
//                String[] material_str = feature_value.split("_");
//                feature_value = "";
//                for (int i = 0; i < material_str.length; i++) {
//                    feature_value = "带" + material_str[i] + "的" + cate_String;
//                    if (!ms.contains(feature_value)) {
//                        ms.add(feature_value);
//                        my_list.add(new Pair<>("material", feature_value));
//                    }
//                }
//            }
//
//            feature_value = sku.getSkuBrand();                      //商品品牌
//            if (!StringUtils.isEmpty(feature_value)) {
//                if (feature_value.indexOf(cate_String) == -1) {
//                    feature_value = feature_value + cate_String;
//                }
//                if (!ms.contains(feature_value)) {
//                    ms.add(feature_value);
//                    my_list.add(new Pair<>("brand", feature_value));
//                }
//            }
//
//            feature_value = sku.getSkuSpecification();              //商品包装
//            if (!StringUtils.isEmpty(feature_value)) {
//                feature_value = feature_value + "的" + cate_String;
//                if (!ms.contains(feature_value)) {
//                    ms.add(feature_value);
//                    my_list.add(new Pair<>("spec", feature_value));
//                }
//            }
//
//            feature_value = sku.getSkuTags();                       //商品标签,外卖标签还未知，等有数据的时候加
//            if (!StringUtils.isEmpty(feature_value)) {
//                JSONArray jsonArray = JSONArray.parseArray(feature_value);
//                for (Object json : jsonArray) {
//                    JSONObject obj = JSONObject.parseObject(json.toString());
//                    for (String key : obj.keySet()) {
//                        if (key.equals("skucakebottom") || key.equals("skuassemble")) {    //如[{"skucakebottom":"厚底"},{"skuassemble":"2拼"}]
//                            feature_value = obj.get(key).toString() + "的" + cate_String;
//                            if (!ms.contains(feature_value)) {
//                                ms.add(feature_value);
//                                my_list.add(new Pair<>("tag", feature_value));
//                            }
//                        }
//                        if (key.equals("售卖商家") || key.equals("产地") || key.equals("营养成分") || key.equals("酒精度")) {
//                            feature_value = obj.get(key).toString() + "的" + cate_String;
//                            if (!ms.contains(feature_value)) {
//                                ms.add(feature_value);
//                                my_list.add(new Pair<>("tag", feature_value));
//                            }
//                        }
//                        if (key.equals("保质期") || key.equals("折扣率")) {
//                            feature_value = key + obj.get(key).toString() + "的" + cate_String;
//                            if (!ms.contains(feature_value)) {
//                                ms.add(feature_value);
//                                my_list.add(new Pair<>("tag", feature_value));
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        int count = my_list.size();
//        List<ShowPair> listRes = new ArrayList<>();
//        for (int i = 0; i < 3 && i < count; i++) {
//            int rand_cn = new Random().nextInt(count);
//            Pair<String, String> tmp = my_list.get(rand_cn);
//            my_list.set(rand_cn, my_list.get(count - 1));
//            my_list.set(count - 1, tmp);
//            count = count - 1;
//            ShowPair showPair = new ShowPair();
//            showPair.setKey(tmp.getKey());
//            showPair.setVal(tmp.getValue());
//            listRes.add(showPair);
//        }
//        ShowPair nextShop = new ShowPair();
//        nextShop.setKey("next_shop");
//        nextShop.setVal("换一家");
//
//        ShowPair goSuperMarket = new ShowPair();
//        goSuperMarket.setKey("goSuperMarket");
//        goSuperMarket.setVal("我要逛超市");
//
//        ShowPair goWaimai = new ShowPair();
//        goWaimai.setKey("goWaimai");
//        goWaimai.setVal("我要点外卖");
//
//        listRes.add(nextShop);
//        listRes.add(goSuperMarket);
//        listRes.add(goWaimai);
//
//        if (listRes.size() == 0) {
//            return new String();
//        }
//        JSONArray jsonRes = new JSONArray();
//        for (int i = 0; i < listRes.size(); i++) {
//            Object obj = JSONObject.toJSON(listRes.get(i));
//            jsonRes.add(obj);
//        }
//        res = jsonRes.toString();
//        return res;
    }

    public static void main(String[] args) {
        TagsRecommendServiceImpl tagsRecommendService = new TagsRecommendServiceImpl();

    }
}

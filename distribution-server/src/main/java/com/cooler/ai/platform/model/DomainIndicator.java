package com.cooler.ai.platform.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DomainIndicator implements Comparable<DomainIndicator>{

    private String domainIndex = null;              //领域编号 domainId_historyDialogStateId

    private String lastNluDomain = null;            //上一轮NLU领域名称

    private String lastDomain = null;               //上一轮领域名称（和上面的lastNluDomain可能不一样，上面可能是control）

    private String domainName = null;               //领域名称

    private String intentName = null;               //意图名称

    private double nluDomainScore = 0d;             //NLU的评分

    private int valuedNecessarySlotCount = 0;       //有值的必须槽位数量

    private int valuedImportantSlotCount = 0;       //有值的重要槽位数量

    private float totalImportanceDegree = 0f;       //有值的重要槽位重要度之和

    private int totalValuedSlotCount = 0;           //有值槽位的数量

    private int turnNum = -1;

    public List<String> compareLogRecords = new ArrayList<>();         //两个领域对比的log集合

    public DomainIndicator(String domainIndex, String lastNluDomain, String lastDomain, String domainName, String intentName, double nluDomainScore, int valuedNecessarySlotCount, int valuedImportantSlotCount, float totalImportanceDegree, int totalValuedSlotCount, int turnNum) {
        this.domainIndex = domainIndex;
        this.lastNluDomain = lastNluDomain;
        this.lastDomain = lastDomain;
        this.domainName = domainName;
        this.intentName = intentName;
        this.nluDomainScore = nluDomainScore;
        this.valuedNecessarySlotCount = valuedNecessarySlotCount;
        this.valuedImportantSlotCount = valuedImportantSlotCount;
        this.totalImportanceDegree = totalImportanceDegree;
        this.totalValuedSlotCount = totalValuedSlotCount;
        this.turnNum = turnNum;
    }

    @Override
    public int compareTo(DomainIndicator another) {
        String compareTwoDomain = String.format("(%s)(%s)   VS   (%s)(%s)", this.getDomainName(), this.getIntentName(), another.getDomainName(), another.getIntentName());

        //1.比较NLU的值，哪个值为1，另一个不为1，则选择那个为1的领域（跟伟鹏商议的）     (原本判断score是否==1d的，但double类型不能直接比较，进而不能判断score是否为1d，只能比较两个score谁大，超过0.05d，并且距离1.0d不超过0.0001d)
        String format = "%s ---> 1.score为1的胜 ( %3f, %3f )";
        if(this.getNluDomainScore() - another.getNluDomainScore() > 0.05d && Math.abs(this.getNluDomainScore() - 1) <= 0.0001d){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getNluDomainScore(), another.getNluDomainScore()));
            return 1;
        } else if(another.getNluDomainScore() - this.getNluDomainScore() > 0.05d && Math.abs(another.getNluDomainScore() - 1) <= 0.0001d) {
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getNluDomainScore(), another.getNluDomainScore()));
            return -1;
        }

        //2.比较有值的必须槽位个数
        format = "%s ---> 2.有值的必须槽位个数 valuedNecessarySlotCount 多胜 ( %d, %d )";
        if(this.getValuedNecessarySlotCount() > another.getValuedNecessarySlotCount()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getValuedNecessarySlotCount(), another.getValuedNecessarySlotCount()));
            return 1;
        } else if(this.getValuedNecessarySlotCount() < another.getValuedNecessarySlotCount()) {
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getValuedNecessarySlotCount(), another.getValuedNecessarySlotCount()));
            return -1;
        }

        //3.比较有值的重要槽位个数
        format = "%s ---> 3.有值的重要槽位个数 valuedImportantSlotCount 多胜 ( %d, %d )";
        if(this.getValuedImportantSlotCount() > another.getValuedImportantSlotCount()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getValuedImportantSlotCount(), another.getValuedImportantSlotCount()));
            return 1;
        } else if(this.getValuedImportantSlotCount() < another.getValuedImportantSlotCount()) {
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getValuedImportantSlotCount(), another.getValuedImportantSlotCount()));
            return -1;
        }

        //4.比较重要度之和大小
        format = "%s ---> 4.重要度之和 totalImportanceDegree 大的胜 ( %3f, %3f )";
        if(this.getTotalImportanceDegree() > another.getTotalImportanceDegree()){
            compareLogRecords.add(String.format(format, compareTwoDomain,  this.getTotalImportanceDegree(), another.getTotalImportanceDegree()));
            return 1;
        }else if(this.getTotalImportanceDegree() < another.getTotalImportanceDegree()){
            compareLogRecords.add(String.format(format, compareTwoDomain,  this.getTotalImportanceDegree(), another.getTotalImportanceDegree()));
            return -1;
        }

        //5.比较有值槽位数量
        format = "%s ---> 5.有值槽位数量 totalValuedSlotCount 多的胜 ( %d, %d )";
        if(this.getTotalValuedSlotCount() > another.getTotalValuedSlotCount()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getTotalValuedSlotCount(), another.getTotalValuedSlotCount()));
            return 1;
        }else if(this.getTotalValuedSlotCount() < another.getTotalValuedSlotCount()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getTotalValuedSlotCount(), another.getTotalValuedSlotCount()));
            return -1;
        }

        //6.如果上轮领域和哪一个领域相同，则选择哪一个领域（不可能是control）
        String lastDomain = this.getLastDomain();
        format = "%s ---> 6.上轮Domain：" + lastDomain + "，跟上轮领域相同的胜 ( %b, %b )";
        if(lastDomain != null){
            if(lastDomain.equals(this.getDomainName()) && !lastDomain.equals(another.getDomainName())){
                compareLogRecords.add(String.format(format, compareTwoDomain, lastDomain.equals(this.getDomainName()), lastDomain.equals(another.getDomainName())));
                return 1;
            }else if(!lastDomain.equals(this.getDomainName()) && lastDomain.equals(another.getDomainName())){
                compareLogRecords.add(String.format(format, compareTwoDomain, lastDomain.equals(this.getDomainName()), lastDomain.equals(another.getDomainName())));
                return -1;
            }
        }

        //7.比较NLU的解析的Domain的Score
        format = "%s ---> 7.NLU解析的Domain分数 nluDomainScore 高胜 ( %3f, %3f )";
        if(this.getNluDomainScore() > another.getNluDomainScore()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getNluDomainScore(), another.getNluDomainScore()));
            return 1;
        } else if(this.getNluDomainScore() < another.getNluDomainScore()) {
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getNluDomainScore(), another.getNluDomainScore()));
            return -1;
        }

        //8.如果上轮领域和哪一个领域相同，则选择哪一个领域（有可能NLU领域为control，则哪个都跟它不一样）

        String lastNluDomain = this.getLastNluDomain();
        format = "%s ---> 8.上轮NLUDomain: " + lastNluDomain + "，跟上轮NLU领域相同的胜 ( %b, %b )";
        if(lastNluDomain != null){
            if(lastNluDomain.equals(this.getDomainName()) && !lastNluDomain.equals(another.getDomainName())){
                compareLogRecords.add(String.format(format, compareTwoDomain, lastNluDomain.equals(this.getDomainName()), lastNluDomain.equals(another.getDomainName())));
                return 1;
            }else if(!lastNluDomain.equals(this.getDomainName()) && lastNluDomain.equals(another.getDomainName())){
                compareLogRecords.add(String.format(format, compareTwoDomain, lastNluDomain.equals(this.getDomainName()), lastNluDomain.equals(another.getDomainName())));
                return -1;
            }
        }

        //9.比较轮数，谁的轮数更加大，使用谁的轮数
        format = "%s ---> 9.轮数 turnNum 大的胜 ( %d, %d )";
        if(this.getTurnNum() > another.getTurnNum()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getTurnNum(), another.getTurnNum()));
            return 1;
        }else if(this.getTurnNum() < another.getTurnNum()){
            compareLogRecords.add(String.format(format, compareTwoDomain, this.getTurnNum(), another.getTurnNum()));
            return -1;
        }

        return 0;
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

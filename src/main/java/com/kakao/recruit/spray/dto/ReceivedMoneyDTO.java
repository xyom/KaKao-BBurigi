package com.kakao.recruit.spray.dto;

public class ReceivedMoneyDTO {
    private String userId;
    private int allocatedMoney;

    public ReceivedMoneyDTO(){}
    public ReceivedMoneyDTO(String userId, int allocatedMoney){
        this.userId=userId;
        this.allocatedMoney=allocatedMoney;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAllocatedMoney() {
        return allocatedMoney;
    }

    public void setAllocatedMoney(int allocatedMoney) {
        this.allocatedMoney = allocatedMoney;
    }
}

package com.kakao.recruit.spray.dto;

public class UserMappedSprayDTO {
    String id;
    String token;
    String userId;
    int allocatedMoney;
    int received;

    public UserMappedSprayDTO(){}
    public UserMappedSprayDTO(String token, int allocatedMoney){
        this.token = token;
        this.allocatedMoney = allocatedMoney;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

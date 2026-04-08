package com.habit.interfaces;

import java.util.List;

public interface RewardOperations {
    void addReward(String name, String description, int xpCost);
    List<String> getAvailableRewards();
    boolean redeemReward(int userId, int rewardId);
    List<String> getUserRedeemedRewards(int userId);
}

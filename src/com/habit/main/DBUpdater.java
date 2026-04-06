package com.habit.main;

import com.habit.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DBUpdater {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            
            // Clean up the badges to be more practical
            String updateBadge1 = "UPDATE BADGE SET BadgeName='7-Day Streak', Description='Complete a habit 7 days in a row', BadgeType='Streak', BadgeCategory='Consistency', CriteriaValue=7 WHERE BadgeID=1";
            String updateBadge2 = "UPDATE BADGE SET BadgeName='30-Day Streak', Description='Maintain a 30-day continuous streak', BadgeType='Streak', BadgeCategory='Consistency', CriteriaValue=30 WHERE BadgeID=2";
            String updateBadge3 = "UPDATE BADGE SET BadgeName='Habit Veteran', Description='Log 100 completions for any habit', BadgeType='Completion', BadgeCategory='Milestone', CriteriaValue=100 WHERE BadgeID=3";
            String updateBadge4 = "UPDATE BADGE SET BadgeName='Starter Milestone', Description='Reach 10 total completions', BadgeType='Completion', BadgeCategory='Milestone', CriteriaValue=10 WHERE BadgeID=4";
            String updateBadge5 = "UPDATE BADGE SET BadgeName='Consistency Master', Description='Maintain a 50-day streak', BadgeType='Streak', BadgeCategory='Consistency', CriteriaValue=50 WHERE BadgeID=5";

            PreparedStatement p1 = conn.prepareStatement(updateBadge1); p1.executeUpdate(); p1.close();
            PreparedStatement p2 = conn.prepareStatement(updateBadge2); p2.executeUpdate(); p2.close();
            PreparedStatement p3 = conn.prepareStatement(updateBadge3); p3.executeUpdate(); p3.close();
            PreparedStatement p4 = conn.prepareStatement(updateBadge4); p4.executeUpdate(); p4.close();
            PreparedStatement p5 = conn.prepareStatement(updateBadge5); p5.executeUpdate(); p5.close();
            
            System.out.println("Badges updated successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

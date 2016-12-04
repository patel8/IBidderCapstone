package edu.uwm.ibidder;

import edu.uwm.ibidder.dbaccess.models.UserModel;

/**
 * Created by Brandon on 12/2/16.
 * Manages the display of rewards in {@link edu.uwm.ibidder.Activities.UserProfileActivity}.
 */
public class RewardManager {

    // Bounds floor
    private final int UNKNOWN = 0, NOVICE = 5, REVERED = 12, MASTER = 25, ELITE = 50;

    private int createTaskCount;
    private int bidTaskCount;

    public RewardManager(UserModel user){
        createTaskCount = user.getTasksCompleted();
        bidTaskCount = user.getBidsCompleted();
    }

    private int findImageID(int count, String type){
        int imageID;
        if(type.equals("creator")){
            if(count == UNKNOWN){
                imageID = R.drawable.bb_reward_taskunknown;
            } else if(count > UNKNOWN && count <= NOVICE){
                imageID = R.drawable.bb_reward_tasknovice;
            } else if(count > NOVICE && count <= REVERED){
                imageID = R.drawable.bb_reward_taskrevered;
            } else if(count > REVERED && count <= MASTER){
                imageID = R.drawable.bb_reward_taskmaster;
            } else if(count > MASTER && count <= ELITE){
                imageID = R.drawable.bb_reward_taskelite;
            } else{
                imageID = R.drawable.bb_reward_taskemperor;
            }
        } else { // bidder
            if(count == UNKNOWN){
                imageID = R.drawable.bb_reward_bidunknown;
            } else if(count > UNKNOWN && count <= NOVICE){
                imageID = R.drawable.bb_reward_bidnovice;
            } else if(count > NOVICE && count <= REVERED){
                imageID = R.drawable.bb_reward_bidrevered;
            } else if(count > REVERED && count <= MASTER){
                imageID = R.drawable.bb_reward_bidmaster;
            } else if(count > MASTER && count <= ELITE){
                imageID = R.drawable.bb_reward_bidelite;
            } else{
                imageID = R.drawable.bb_reward_bidemperor;
            }
        }

        return imageID;
    }

    /**
     * Determines the users creator image
     * @return drawable image id of reward
     */
    public int getCreatorImage(){
        return findImageID(createTaskCount, "creator");
    }

    /**
     * Determines the users bidder image
     * @return drawable image id of reward
     */
    public int getBidderImage(){
        return findImageID(bidTaskCount, "bidder");
    }

    private int findTitleID(int count, String type){
        int titleID;
        if(type.equals("creator")){
            if(createTaskCount == UNKNOWN){
                titleID = R.string.reward_unknown;
            } else if(createTaskCount > UNKNOWN && createTaskCount <= NOVICE){
                titleID = R.string.reward_novice;
            } else if(createTaskCount > NOVICE && createTaskCount <= REVERED){
                titleID = R.string.reward_revered;
            } else if(createTaskCount > REVERED && createTaskCount <= MASTER){
                titleID = R.string.reward_master;
            } else if(createTaskCount > MASTER && createTaskCount <= ELITE){
                titleID = R.string.reward_elite;
            } else{
                titleID = R.string.reward_emperor;
            }
        } else{ // bidder
            if(bidTaskCount == UNKNOWN){
                titleID = R.string.reward_unknown;
            } else if(bidTaskCount > UNKNOWN && bidTaskCount <= NOVICE){
                titleID = R.string.reward_novice;
            } else if(bidTaskCount > NOVICE && bidTaskCount <= REVERED){
                titleID = R.string.reward_revered;
            } else if(bidTaskCount > REVERED && bidTaskCount <= MASTER){
                titleID = R.string.reward_master;
            } else if(bidTaskCount > MASTER && bidTaskCount <= ELITE){
                titleID = R.string.reward_elite;
            } else{
                titleID = R.string.reward_emperor;
            }
        }

        return titleID;
    }

    /**
     * Determines the users creator title
     * @return string title id of reward
     */
    public int getCreatorTitle(){
        return findTitleID(createTaskCount, "creator");
    }

    /**
     * Determines the users bidder title
     * @return string title id or reward
     */
    public int getBidderTitle(){
        return findTitleID(bidTaskCount, "bidder");
    }

    public String getCreatorCount(){
        return "(" + Integer.toString(createTaskCount) + ")";
    }

    public String getBidderCount(){
        return "(" + Integer.toString(bidTaskCount) + ")";
    }

}

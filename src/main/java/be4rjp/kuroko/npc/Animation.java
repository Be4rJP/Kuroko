package be4rjp.kuroko.npc;

public enum Animation {
    SWING_MAIN_ARM(0),
    TAKE_DAMAGE(1),
    LEAVE_BED(2),
    SWING_OFF_ARM(3),
    CRITICAL_EFFECT(4),
    MAGIC_CRITICAL_EFFECT(5),
    
    POSE_STANDING("STANDING"),
    POSE_FALL_FLYING("FALL_FLYING"),
    POSE_SLEEPING("SLEEPING"),
    POSE_SWIMMING("SWIMMING"),
    POSE_SPIN_ATTACK("SPIN_ATTACK"),
    POSE_CROUCHING("CROUCHING"),
    POSE_DYING("DYING");
    
    
    private final String entityPoseEnumName;
    
    private final int animationNumber;
    
    private final boolean isPose;
    
    Animation(String entityPoseEnumName){
        this.entityPoseEnumName = entityPoseEnumName;
        this.animationNumber = -1;
        this.isPose = true;
    }
    
    Animation(int animationNumber){
        this.entityPoseEnumName = "NULL";
        this.animationNumber = animationNumber;
        this.isPose = false;
    }
    
    public String getEntityPoseEnumName() {return entityPoseEnumName;}
    
    public int getAnimationNumber() {return animationNumber;}
    
    public boolean isPose() {return isPose;}
    
}

package cn.edu.sdu.java.server.models;

// 定义一个枚举类表示荣誉类型
public enum HonorType {
    SCHOLARSHIP("奖学金"),
    TITLE("荣誉称号"),
    COMPETITION_AWARD("竞赛奖项"),
    SOCIAL_PRACTICE("社会实践奖项"),
    OTHER("其他");

    private final String description;

    HonorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 可以根据需要添加根据 description 获取枚举成员的方法
    public static HonorType fromDescription(String description) {
        for (HonorType type : HonorType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("没有奖项：" + description);
    }
}
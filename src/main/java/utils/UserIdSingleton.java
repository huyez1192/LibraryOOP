package utils;

public class UserIdSingleton {
    private static UserIdSingleton instance;
    private int userId;

    private UserIdSingleton() {
    }
    public static UserIdSingleton getInstance() {
        if (instance == null) {
            instance = new UserIdSingleton();
        }
        return instance;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
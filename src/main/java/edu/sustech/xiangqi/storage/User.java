package edu.sustech.xiangqi.storage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
public class User implements Serializable {
    private String username;
    private String password;
    private LocalTime createtime;

    public User() {}
    public User(String username, String password, LocalTime createtime) {
        this.username = username;
        this.password = password;
        this.createtime = LocalTime.from(LocalDateTime.now());
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalTime getCreatetime() {
        return createtime;
    }
    public void setCreatetime(LocalTime createtime) {
        this.createtime = createtime;
    }
}

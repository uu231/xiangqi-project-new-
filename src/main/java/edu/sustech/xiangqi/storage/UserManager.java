package edu.sustech.xiangqi.storage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_DATA_DIR ="data/users/";
    private static final String USER_FILE_EXTENSION=".user";

    static{
        new File(USER_DATA_DIR).mkdirs();
    }

    public static boolean registerUser(String username,String password){
        if(username==null||username.trim().isEmpty()||password==null||password.trim().isEmpty()){
            return false;
        }

        if(userExists(username)){
            System.out.println("用户名已存在："+username);
            return false;
        }
        User user = new User(username,password);
        return saveUserToFile(user);
    }

    public static User login(String username,String password){
        User user=loadUserFromFile(username);
        if(user!=null&&user.getPassword().equals(password)){
            return user;
        }
        return null;
    }

    public static boolean userExists(String username){
        File userFile = new File(USER_DATA_DIR+username+USER_FILE_EXTENSION);
        return userFile.exists();
    }
    public static boolean saveUserToFile(User user){
        String filename=USER_DATA_DIR+user.getUsername()+USER_FILE_EXTENSION;

        try(ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))){
            oos.writeObject(user);
            return true;
        }catch(IOException e){
            System.err.println("保存用户信息失败："+e.getMessage());
            return false;
        }
    }

    private static User loadUserFromFile(String username){
        String filename = USER_DATA_DIR+username+USER_FILE_EXTENSION;
        File file = new File(filename);
        if(!file.exists()){
            return null;
        }
        try(ObjectInputStream ois= new ObjectInputStream(
                new FileInputStream(filename))){
            return (User)ois.readObject();
        } catch (IOException |ClassNotFoundException e) {
            System.err.println("加载用户信息失败"+e.getMessage());
            return null;

        }
    }
}

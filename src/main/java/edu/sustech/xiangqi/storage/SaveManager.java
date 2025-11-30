package edu.sustech.xiangqi.storage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_DATA_DIR="data/saves/";
    private static final String SAVE_FILE_EXTENSION=".save";

    static{
        new File(SAVE_DATA_DIR).mkdirs();
    }

    public static boolean saveGame(String saveName,String username,String boardState,String currentPlayer){
        if(saveName==null||saveName.trim().isEmpty()||username==null||username.trim().isEmpty()){
            return false;
        }
        GameSave save =new GameSave(saveName,username,boardState,currentPlayer);
        return saveGameToFile(save);
    }

    public static List<GameSave> getUserSaves(String username){
        List<GameSave> saves=new ArrayList<>();
        File saveDir=new File(SAVE_DATA_DIR);
        if(saveDir.exists()&&saveDir.isDirectory()){
            File[] saveFiles=saveDir.listFiles((dir,name)->name.startsWith(username+"_")&&name.endsWith(SAVE_FILE_EXTENSION));

            if(saveFiles!=null){
                for (File saveFile:saveFiles){
                    GameSave save=loadGameFromFile(saveFile);
                    if(save!=null){
                        saves.add(save);
                    }
                }
            }
        }
        return saves;
    }
    public static GameSave loadSave(String username,String saveName) {
    String fileName=SAVE_DATA_DIR+username+"_"+SAVE_FILE_EXTENSION;
    File saveFile=new File(fileName);
    if(saveFile.exists()){
        return null;
    }
    return loadGameFromFile(saveFile);
    }
    public static boolean deleteSave(String username,String saveName){
        String fileName=SAVE_DATA_DIR+username+"_"+saveName+SAVE_FILE_EXTENSION;
        File saveFile=new File(fileName);
        if(saveFile.exists()){
            return saveFile.delete();
        }
        return false;
    }
    private static boolean saveGameToFile(GameSave save){
        String filename = SAVE_DATA_DIR+save.getUsername()+"_"+save.getSaveName()+SAVE_FILE_EXTENSION;

        try(ObjectOutputStream oos=new ObjectOutputStream(
                new FileOutputStream(filename))){
            oos.writeObject(save);
            return true;
        } catch (IOException e) {
            System.err.println("保存游戏失败："+e.getMessage());
            return false;
        }
    }
    private static GameSave loadGameFromFile(File saveFile){
        try(ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(saveFile))){
            return (GameSave) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("加载存档失败："+e.getMessage());
            return null;
        }
    }
}


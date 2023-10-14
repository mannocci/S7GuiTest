
import net.contentobjects.jnotify.JNotifyListener;


  class Listener implements JNotifyListener {
    @Override
    public void fileRenamed(int wd, String rootPath, String oldName,
        String newName) {
      print("renamed " + rootPath + " : " + oldName + " -> " + newName);
    }
    @Override
    public void fileModified(int wd, String rootPath, String name) {
      print("modified " + rootPath + " : " + name);
    }
    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
      print("deleted " + rootPath + " : " + name);
    }
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
      print("created " + rootPath + " : " + name);
    }
    void print(String msg) {
      System.err.println(msg);
    }
  }


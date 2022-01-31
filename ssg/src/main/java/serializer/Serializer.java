package serializer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Optional;

/**
 * System to save and load objects.
 * objects -> bytes : save
 * bytes -> objects : load
 * @author Lucian Petic
 */
public class Serializer {

    private Serializer(){}

    /**
     * Save an object
     * @param data
     * @param path
     * @return
     */
    public static boolean save(final Serializable data, final String path){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(data);
            oos.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Load an object
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Optional<T> load(final String path){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            T data = (T) ois.readObject();
            ois.close();
            return Optional.of(data);
        }catch (Exception e){
            //e.printStackTrace();
        }
        return Optional.empty();
    }
}

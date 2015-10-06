package langeditor;

import langoperations.LanguageOperations;
import langoperations.TurkishOperations;
import langoperations.PolishOperations;
import langoperations.DummyOperations;
import java.awt.Container;
import java.util.HashMap;
import utils.MsgTextPane;

public class LanguageContext {

    static LanguageOperations ops = null;
    static LanguageEditorFrame frame = null;
    
    static HashMap<String, LanguageOperations> opsMap = new HashMap<>();

    public static LanguageOperations get() {
        return ops;
    }
    
    public static LanguageEditorFrame getFrame(){
        return frame;
    }

    public static void set(LanguageEditorFrame f, String language, String origin) {
        frame=f;
        String s; if (f==null) s="null"; else s=f.editorLanguage;
//        System.out.println(" set language context to " + s+"/"+language + "  (" + origin + ")");

        ops = opsMap.get(language);
        if (ops == null) {
            if (language.equals("tur")) {
                ops = new TurkishOperations();
                ops.initialize();
                opsMap.put(language, ops);
            } else 
            if (language.equals("pol")) {
                ops = new PolishOperations();
                ops.initialize();
                opsMap.put(language, ops);
            } else {
                ops = new DummyOperations();
                ops.initialize();
                opsMap.put(language, ops);              
            }
        }

    }
}

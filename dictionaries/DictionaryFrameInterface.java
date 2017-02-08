/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionaries;

/**
 *
 * @author erikv
 */
public interface DictionaryFrameInterface {

    public void setVisible(boolean b);
    public void isModified(boolean b);

    public void close();

    public void writeSelectDictArea(String str);

    public void manualSelectDictArea(int position, int length);

    public void writeDictArea(String s, boolean bold);

    public void scrollEnd();

}

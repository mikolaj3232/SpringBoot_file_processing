package com.rarToZip.RarToZip.myDictinary;

import com.rarToZip.RarToZip.Controllers.FileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Dictionary <T,V>{
    private Logger log =  LoggerFactory.getLogger(FileController.class);
    private List<DictionaryItem> items;

    public Dictionary() {
        items = new LinkedList<>();
    }
    public void insertItem(T key, V value) throws Exception {
        for(DictionaryItem item : items){
            if(item.getKey().equals(key)){
                log.info("key input :"+key+" current item "+item.getKey());
                throw new Exception("Key value must be unique");
            }
        }
        items.add(new DictionaryItem<>(key,value));
    }
    public void deleteItemByKey(T key){
        for(DictionaryItem item : items){
            if(item.getKey().equals(key)){
                items.remove(item);
            }
        }
    }
    public Object getItemByKey(T key){
        Object tmp = null;
        for(DictionaryItem item : items){
            if(item.getKey().equals(key)){
                tmp = (Object) item.getValue();
            }
        }
        return tmp;
    }
    public Set<T> getKeySet(){
        Set<T> tmp = new LinkedHashSet<>();
        for(DictionaryItem item : items){
            tmp.add((T)item.getKey());
        }
        return tmp;
    }
    public int getSize(){
        return items.size();
    }


}

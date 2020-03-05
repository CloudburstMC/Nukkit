package cn.nukkit.utils;

import java.util.*;

import lombok.*;

public class IocContainer {

    private Map<String, Object> dictionary = new Hashtable<String, Object>();

    public <K,V extends K> void Register(Class<K> key, V value)
    {
        String className = key.getName();
        // Class<K> key = ;
        // Class<V> clazz = clazz
        // String className = key.getClass().getName();
        dictionary.put(className, value);        
    }

    @SuppressWarnings("unchecked")
    public <K, V extends K> K Retrieve(Class<K> obj){
        String interFaceName = obj.getName();
        
        V objClass = (V)dictionary.get(interFaceName);
        return (K)objClass;
    }

}